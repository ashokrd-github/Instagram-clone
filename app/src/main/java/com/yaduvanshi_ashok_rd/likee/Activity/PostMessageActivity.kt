package com.yaduvanshi_ashok_rd.likee.Activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.FirebaseDatabase
import com.yaduvanshi_ashok_rd.likee.R

class PostMessageActivity : AppCompatActivity() {
    private lateinit var messagePhoto:ImageView
    private lateinit var firebaseUser: FirebaseUser

    var receiverRoom: String? = null
    var senderRoom: String? = null
    var senderUid: String? = null
    var receiverUid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_message)

        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT


        messagePhoto = findViewById(R.id.message_post_photos)
        setSupportActionBar(findViewById(R.id.post_message_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        senderUid = FirebaseAuth.getInstance().currentUser!!.uid


        senderRoom = receiverUid + senderUid
        receiverRoom = senderUid + receiverUid
        val photo = intent.getStringExtra("image")
        receiverUid = intent.getStringExtra("uid")

        Glide.with(this).load(photo).placeholder(R.drawable.loading).into(messagePhoto)

    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.photo_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId){
            R.id.delete->{
                FirebaseDatabase.getInstance().reference.child("chats").child(senderRoom!!).child("messages").removeValue()
            }
            R.id.save->{
                Toast.makeText(this, "save is clicked", Toast.LENGTH_SHORT).show()
            }
        }
        
        return true
        
    }
}