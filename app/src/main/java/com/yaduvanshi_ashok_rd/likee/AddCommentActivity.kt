package com.yaduvanshi_ashok_rd.likee

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.emoji2.emojipicker.EmojiPickerView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Visibility
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.vanniktech.ui.hideKeyboard
import com.yaduvanshi_ashok_rd.likee.Adapter.CommentAdapter
import com.yaduvanshi_ashok_rd.likee.Model.Comment
import com.yaduvanshi_ashok_rd.likee.Model.User
import org.kodein.emoji.Emoji
import java.util.Date

class AddCommentActivity : AppCompatActivity() {

    private var firebaseUser: FirebaseUser?=null
    private var commentAdapter: CommentAdapter?=null
    private var commentList:MutableList<Comment>?=null

    private lateinit var add_comment: EditText
    private lateinit var post_image_comment: ImageView
    private lateinit var user_profile_image: ImageView
    private lateinit var post_comment:ImageView
    private lateinit var emojiPicker: EmojiPickerView
    private lateinit var emoji: ImageView
    private lateinit var keyboard: ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_add_comment)

        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        add_comment = findViewById(R.id.add_comment)
        post_image_comment = findViewById(R.id.post_image_comment)
        user_profile_image = findViewById(R.id.user_profile_image)
        post_comment = findViewById(R.id.post_comment)
        emojiPicker = findViewById(R.id.emoji_picker)
        emoji = findViewById(R.id.emoji)
        keyboard = findViewById(R.id.keyboard)

        val toolbar=findViewById<androidx.appcompat.widget.Toolbar>( R.id.comments_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar!!.title = "Comments"
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener(View.OnClickListener {
            finish()
        })



        var recyclerView: RecyclerView?=null
        recyclerView=findViewById(R.id.recyclerview_comments)
        recyclerView.setHasFixedSize(true)
        val linearLayoutManager: LinearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager=linearLayoutManager

        commentList=ArrayList()
        commentAdapter= this.let { CommentAdapter(it,commentList as ArrayList<Comment>) }
        recyclerView.adapter=commentAdapter


        firebaseUser= FirebaseAuth.getInstance().currentUser

        val add_comment=findViewById<EditText>(R.id.add_comment)
        val post_comment=findViewById<ImageView>(R.id.post_comment)
        val postid = intent.getStringExtra("POST_ID")

        getImage()
        readComments(postid!!)
        getPostImage(postid)


        post_comment.setOnClickListener {
            if(add_comment.text.toString().equals(""))
            {
                Toast.makeText(this,"You can't send an empty comment",Toast.LENGTH_SHORT).show()


            }
            else
            {
                postComment(postid)

            }

        }

        emoji.setOnClickListener {
            emojiPicker.visibility = View.VISIBLE
            emoji.visibility = View.GONE
            keyboard.visibility = View.VISIBLE
            hideKeyboard()
        }
        add_comment.setOnClickListener{
            emojiPicker.visibility = View.GONE
            keyboard.visibility =View.GONE
            emoji.visibility =View.VISIBLE
        }


        emojiPicker.setOnEmojiPickedListener{
            add_comment.append(it.emoji)
        }

    }

    private fun postComment(postid:String) {

        val commentRef : DatabaseReference = FirebaseDatabase.getInstance().reference.child("Comment").child(postid)

        val commentMap = HashMap<String, Any>()
        commentMap["publisher"] = firebaseUser!!.uid
        commentMap["comment"] = add_comment.text.toString()
        commentMap["timestamp"] = Date().time

        commentRef.push().setValue(commentMap)
        pushNotification(postid)
        add_comment.setText("")
        Toast.makeText(this, "posted!!", Toast.LENGTH_LONG).show()
    }

    private fun getImage() {
        val ref : DatabaseReference = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser!!.uid)

        ref.addValueEventListener(object:ValueEventListener{
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    val user = snapshot.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(user_profile_image)
                }
            }
        })
    }

    private fun pushNotification(postid: String) {

        val ref = FirebaseDatabase.getInstance().reference.child("Notification").child(firebaseUser!!.uid)

        val notifyMap = HashMap<String, Any>()
        notifyMap["userid"] = FirebaseAuth.getInstance().currentUser!!.uid
        notifyMap["text"] = "commented :"+add_comment.text.toString()
        notifyMap["postid"] = postid
        notifyMap["ispost"] = true
        notifyMap["timestamp"] = Date().time

        ref.push().setValue(notifyMap)
    }

    private fun readComments(postid: String) {
        val ref: DatabaseReference =
            FirebaseDatabase.getInstance().reference.child("Comment").child(postid)

        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }

            override fun onDataChange(p0: DataSnapshot) {

                commentList?.clear()
                for (snapshot in p0.children) {
                    val cmnt: Comment? = snapshot.getValue(Comment::class.java)
                    commentList!!.add(cmnt!!)
                }
                commentAdapter!!.notifyDataSetChanged()
            }
        })
    }

    private fun getPostImage(postid: String){
        val postRef = FirebaseDatabase.getInstance()
            .reference.child("Posts")
            .child(postid).child("postimage")

        postRef.addValueEventListener(object: ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if(p0.exists())
                {
                    val image = p0.value.toString()

                    Picasso.get().load(image).placeholder(R.drawable.profile).into(post_image_comment)
                }
            }
        })
    }
    }
