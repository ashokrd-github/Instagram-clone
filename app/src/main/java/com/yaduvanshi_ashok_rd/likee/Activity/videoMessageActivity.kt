package com.yaduvanshi_ashok_rd.likee.Activity

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.os.Message
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.MediaController
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.VideoView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yaduvanshi_ashok_rd.likee.R

class videoMessageActivity : AppCompatActivity() {

    private lateinit var user: TextView
    private lateinit var messageVideo: VideoView
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var progressBar:ProgressBar
    private lateinit var playButton:ImageView
    private var senderRoom:String?=null
    private var receiverRoom:String?=null
    private var receiverUid:String?=null
    private var senderUid:String?=null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_message)
//        window.statusBarColor = Color.TRANSPARENT
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setSupportActionBar(findViewById(R.id.message_toolbar_post_video))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        playButton = findViewById(R.id.play_button)
        messageVideo = findViewById(R.id.message_post_video)
        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        progressBar = findViewById(R.id.message_post_video_progressbar)

        senderUid = FirebaseAuth.getInstance().currentUser!!.uid

        senderRoom = senderUid+receiverUid
        receiverRoom = receiverUid+senderUid

        val video = intent.getStringExtra("video")
        receiverUid = intent.getStringExtra("uid")
        messageVideo.setVideoPath(video)
        messageVideo.setOnPreparedListener {
            messageVideo.start()
            progressBar.visibility = View.GONE
        }
        messageVideo.setOnClickListener {
            if (messageVideo.isPlaying){
                messageVideo.pause()
                playButton.visibility = View.VISIBLE
            }else{
                messageVideo.start()
                playButton.visibility = View.GONE
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.video_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
       when(item.itemId){
           R.id.delete->{
               FirebaseDatabase.getInstance().reference.child("chats").child(senderRoom!!).child(firebaseUser.uid)
                   .removeValue()
           }
       }

        return true
    }
}