package com.yaduvanshi_ashok_rd.likee.Activity

import android.content.Context
import android.content.pm.ActivityInfo
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import android.widget.Toolbar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yaduvanshi_ashok_rd.likee.Adapter.PostAdapter
import com.yaduvanshi_ashok_rd.likee.Model.Post
import com.yaduvanshi_ashok_rd.likee.R

class postDetailActivity : AppCompatActivity() {
private lateinit var postDetail:Toolbar
    private var postAdapter: PostAdapter?=null
    private var postList:MutableList<Post>?=null
    private var postid:String?=""
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_post_detail)

//        window.statusBarColor = Color.TRANSPARENT
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        
        setSupportActionBar(findViewById(R.id.search_toolbar))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)
        auth = FirebaseAuth.getInstance()

        val pref = getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        postid=pref?.getString("postid","none")

        var recyclerView: RecyclerView?=null
        recyclerView= findViewById(R.id.recyclerview_postdetail)
        val linearLayoutManager: LinearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager=linearLayoutManager

        postList=ArrayList()
        postAdapter= this.let { PostAdapter(it,postList as ArrayList<Post>) }
        recyclerView.adapter=postAdapter

        readPosts(postid)

    }

    private fun readPosts(postid: String?) {
        val postRef= FirebaseDatabase.getInstance().reference.child("Posts").child(postid!!)

        Log.d("Post id",postid)
        postRef.addValueEventListener(object : ValueEventListener
        {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot)
            {
                postList?.clear()
                val post: Post? = p0.getValue(Post::class.java)
                postList!!.add(post!!)
                postAdapter!!.notifyDataSetChanged()
            }
        })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.gallery_nav, menu)
        return super.onCreateOptionsMenu(menu)

    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.delete->{
              FirebaseDatabase.getInstance().reference.child("Posts").child(postid!!).removeValue()
                  .addOnCompleteListener { task->
                      if (task.isSuccessful){
                          Toast.makeText(this, "post deleted successfully", Toast.LENGTH_SHORT).show()
                      }
                      else{
                          Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show()
                      }
                  }


//                ref.removeValue().addOnCompleteListener { task->
//                    if (task.isSuccessful){
//                        Toast.makeText(this, "deleted successfully", Toast.LENGTH_SHORT).show()
//
//                    }
//                    else{
//                        Toast.makeText(this, "something went wrong", Toast.LENGTH_SHORT).show()
//                    }
//                }
            }
        }
        return true
    }
}