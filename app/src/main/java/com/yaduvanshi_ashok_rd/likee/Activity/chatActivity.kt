package com.yaduvanshi_ashok_rd.likee.Activity

import android.app.ProgressDialog
import android.content.Intent
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.EmojiConsistency
import android.text.TextWatcher
import android.view.Menu
import android.view.MenuItem
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.PopupWindow
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.emoji.text.EmojiCompat
import androidx.emoji.text.EmojiMetadata
import androidx.emoji.text.FontRequestEmojiCompatConfig
import androidx.emoji.widget.EmojiButton
import androidx.emoji.widget.EmojiEditText
import androidx.emoji.widget.EmojiTextView
import androidx.emoji2.emojipicker.EmojiPickerView
import androidx.emoji2.emojipicker.EmojiViewItem
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Visibility
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.storage.FirebaseStorage
import com.vanniktech.emoji.EmojiPopup
import com.vanniktech.ui.hideKeyboard
import com.yaduvanshi_ashok_rd.likee.AccountSettingActivity
import com.yaduvanshi_ashok_rd.likee.Adapter.MessageAdapter
import com.yaduvanshi_ashok_rd.likee.Model.Message
import com.yaduvanshi_ashok_rd.likee.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ensureActive
import kotlinx.coroutines.withContext
import org.kodein.emoji.Emoji
import org.kodein.emoji.EmojiTemplateCatalog
import org.kodein.emoji.all
import org.kodein.emoji.allOf
import org.kodein.emoji.objects.office.Calendar
import org.kodein.emoji.people_body.hand_fingers_open.WavingHand
import org.kodein.emoji.smileys_emotion.face_smiling.GrinSweat
import java.time.LocalDateTime
import java.util.Calendar
import java.util.Date


class chatActivity : AppCompatActivity() {
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var userProfile: ImageView
    private lateinit var profile_second:LinearLayout
    private lateinit var userName: TextView
    private lateinit var fullName:TextView
    private lateinit var messageBox: EditText
    private lateinit var sentButton: ImageView
    private lateinit var viewOnline: TextView
    private lateinit var chatRecyclerview: RecyclerView
    private lateinit var leftArrow: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageEmoji: ImageView
    private lateinit var emojiPickerView: EmojiPickerView

//    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDbRef: DatabaseReference
    private lateinit var dialog: ProgressDialog
    var receiverRoom: String? = null
    var senderRoom: String? = null
    var senderUid: String? = null
    var receiverUid: String? = null
    var messageList:ArrayList<Message>?=null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        window.statusBarColor = Color.rgb(92, 50, 199)
        userName = findViewById(R.id.user_name)
        userProfile = findViewById(R.id.option_profile)
        chatRecyclerview = findViewById(R.id.charRecyclerview)
        sentButton = findViewById(R.id.sentButton)
        messageBox = findViewById(R.id.messageBox)
        leftArrow = findViewById(R.id.left_arrow)
        viewOnline = findViewById(R.id.view_online)
        messageEmoji = findViewById(R.id.emoji)
        emojiPickerView = findViewById(R.id.emoji_picker)
        fullName = findViewById(R.id.full_name)
        profile_second = findViewById(R.id.profile_112)
        setSupportActionBar(findViewById(R.id.chat_profile_toolbar))

        firebaseUser = FirebaseAuth.getInstance().currentUser!!


        val name = intent.getStringExtra("name")
        val username = intent.getStringExtra("username")
        val profile = intent.getStringExtra("image")
         receiverUid = intent.getStringExtra("uid")
//        senderUid = FirebaseAuth.getInstance().uid

         senderUid = FirebaseAuth.getInstance().currentUser?.uid
        mDbRef = FirebaseDatabase.getInstance().getReference()
        dialog = ProgressDialog(this)
        dialog.setTitle("Post")
        dialog.setMessage("Please wait...")
        dialog.setCancelable(true)


        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid
        userName.text = name
        fullName.text = username
        Glide.with(this).load(profile).placeholder(R.drawable.profile).into(userProfile)

        profile_second.setOnClickListener {
            val intent = Intent(this, ProfileDetail::class.java)
            intent.putExtra("name", userName.text)
            intent.putExtra("image", profile)
            intent.putExtra("uid", senderUid)
            intent.putExtra("username", fullName.text)
            this.startActivity(intent)

        }



        leftArrow.setOnClickListener { finish() }

        // online or offline
        mDbRef.child("Presence").child(receiverUid!!)
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        val status = snapshot.getValue(String::class.java)
                        if (status == "Offline") {
                            viewOnline.visibility = View.GONE
                        } else {
                            viewOnline.setText(status)
                            viewOnline.visibility = View.VISIBLE
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {}

            })
// online offline

        messageList = ArrayList()
        messageAdapter = MessageAdapter(this@chatActivity, messageList, senderRoom!!, receiverRoom!!)
        chatRecyclerview.layoutManager = LinearLayoutManager(this)
        chatRecyclerview.adapter = messageAdapter
        // logic for adding data to recyclerview
        mDbRef.child("chats").child(senderRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    messageList!!.clear()
                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        message!!.messageId = postSnapshot.key
                        messageList!!.add(message)

                    }
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}

            })


        sentButton.setOnClickListener {
            val message = messageBox.text.toString()
            val date = Date()
            val messageObject = Message(message, senderUid, date.time)
            if (message.isEmpty()) {
                Toast.makeText(this, "Please write here something!!", Toast.LENGTH_SHORT).show()
            } else {
                val randomKey = mDbRef.push().key
                val lastMsgObj = HashMap<String, Any>()
                lastMsgObj["lastMsg"] = message
                lastMsgObj["lastMsg"] = date.time
                mDbRef.child("chats").child(senderRoom!!).child("messages").child(randomKey!!)
                    .setValue(messageObject).addOnSuccessListener {
                        mDbRef.child("chats").child(receiverRoom!!).child("messages").child(randomKey)
                            .setValue(messageObject)
                    }
                messageBox.setText("")

                // last message
                // last message
            }

        }

        messageEmoji.setOnClickListener {
            emojiPickerView.visibility = View.VISIBLE
            hideKeyboard()
        }
        messageBox.setOnClickListener {
            emojiPickerView.visibility = View.GONE

        }
//
      emojiPickerView.setOnEmojiPickedListener{
          messageBox.append(it.emoji)
      }



// online or offline
        val Handler = Handler()
        messageBox.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                mDbRef.child("Presence").child(senderUid!!).setValue("typing...")
                Handler.removeCallbacksAndMessages(null)
                Handler.postDelayed(userStopTyping, 1000)

            }

            var userStopTyping = Runnable {
                mDbRef.child("Presence")
                    .child(senderUid!!)
                    .setValue("Online")

            }

        })
        supportActionBar?.setDisplayHomeAsUpEnabled(false)
    }
// image for image //
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 101) {
            if (data != null) {
                if (data.data != null) {
                val selectedImage = data.data
                    val calender = Calendar.getInstance()
                    val reference = FirebaseStorage.getInstance().reference.child("chats")
                        .child(calender.timeInMillis.toString()+"jpg")
                    dialog.show()
                    reference.putFile(selectedImage!!).addOnCompleteListener { task->
                        if (task.isSuccessful){
                            reference.downloadUrl.addOnSuccessListener { uri->
                                val filePath = uri.toString()
                                val messageText = messageBox.text.toString()
                                val date = Date()
                                val message = Message(messageText, senderUid, date.time)
                                message.message = "photo"
                                message.imageUrl = filePath
                                messageBox.setText("")
                                val randomKey = mDbRef.push().key
                                val lastMsgObj = java.util.HashMap<String, Any>()
                                lastMsgObj["lastMsg"] = message.message!!
                                lastMsgObj["lastMsgTime"] = date.time

                                mDbRef.child("chats").child(senderRoom!!).child("messages").child(randomKey!!)
                                    .setValue(message).addOnSuccessListener {
                                        mDbRef.child("chats").child(receiverRoom!!).child("messages").child(randomKey)
                                            .setValue(message)
                                    }
                                dialog.dismiss()

                            }
                        }
                    }
                }
            }

        }



    if (requestCode == 102) {
        if (data != null) {
            if (data.data != null) {
                val selectedVideo = data.data
                val calender = Calendar.getInstance()
                val reference = FirebaseStorage.getInstance().reference.child("chats")
                    .child(calender.timeInMillis.toString()+"Mp4")
                dialog.show()
                reference.putFile(selectedVideo!!).addOnCompleteListener { task->
                    if (task.isSuccessful){
                        reference.downloadUrl.addOnSuccessListener { uri->
                            val videoPath = uri.toString()
                            val messageText = messageBox.text.toString()
                            val date = Date()
                            val message = Message(messageText, senderUid, date.time)
                            message.message = "video"
                            message.videoUrl = videoPath
                            messageBox.setText("")
                            val randomKey = mDbRef.push().key
                            val lastMsgObj = java.util.HashMap<String, Any>()
                            lastMsgObj["lastMsg"] = message.message!!
                            lastMsgObj["lastMsgTime"] = date.time
                            mDbRef.child("chats").child(senderRoom!!).child("messages").child(randomKey!!)
                                .setValue(message).addOnSuccessListener {
                                    mDbRef.child("chats").child(receiverRoom!!).child("messages").child(randomKey)
                                        .setValue(message)
                                }
                            dialog.dismiss()

                        }
                    }
                }
            }
        }

    }

}
    // image for image ///



    override fun onResume() {
        super.onResume()
        val currentId = FirebaseAuth.getInstance().uid
        mDbRef.child("Presence")
            .child(currentId!!)
            .setValue("Online")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPause() {
        super.onPause()
        val now = LocalDateTime.now()
        val startOfDay = now
        val currentId = FirebaseAuth.getInstance().uid
        mDbRef.child("Presence").child(currentId!!)
            .setValue("$startOfDay")
    }
    // online or offline
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.message_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.setting ->{
                startActivity(Intent(this, AccountSettingActivity::class.java))
            }
            R.id.video->{
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.action
                intent.type = "video/*"
                startActivityForResult(intent, 102)
            }
            R.id.profile->{
                val intent = Intent(this, ProfileDetail::class.java)
                intent.putExtra("name", userName.text)
//                intent.putExtra("image", userProfile.id.green)
                intent.putExtra("uid", senderUid)
                this.startActivity(intent)
            }
            R.id.block ->{
                Toast.makeText(this, "Block is not active", Toast.LENGTH_SHORT).show()
            }
            R.id.photo->{
                val intent = Intent(Intent.ACTION_GET_CONTENT)
                intent.action
                intent.type = "image/*"
                startActivityForResult(intent, 101)
            }
            R.id.camera ->{
                val intent = Intent(Intent.ACTION_CAMERA_BUTTON)
                intent.action
                intent.type = "camera/*"
                startActivityForResult(intent, 103)
            }
            R.id.clear ->{
                mDbRef.child("chats").child(senderRoom!!).removeValue()
            }
        }

        return true
    }

}