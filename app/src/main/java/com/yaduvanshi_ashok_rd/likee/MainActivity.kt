package com.yaduvanshi_ashok_rd.likee

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.ActivityInfo
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Audio.Albums
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.storage.StorageReference
import com.yaduvanshi_ashok_rd.likee.Activity.AddStoryActivity
import com.yaduvanshi_ashok_rd.likee.Activity.ReelActivity
import com.yaduvanshi_ashok_rd.likee.Activity.SearchActivity
import com.yaduvanshi_ashok_rd.likee.Activity.StoryActivity
import com.yaduvanshi_ashok_rd.likee.Activity.postActivity
import com.yaduvanshi_ashok_rd.likee.Fragments.HomeFragments
import com.yaduvanshi_ashok_rd.likee.Fragments.ProfileFragment
import com.yaduvanshi_ashok_rd.likee.Model.Reel
import com.yaduvanshi_ashok_rd.likee.Model.User
import com.yaduvanshi_ashok_rd.likeee.fragments.ReelsFragment
import java.time.LocalDateTime

class MainActivity : AppCompatActivity() {
//    private val SELECT_IMAGE = 101
    private var uri: Uri? =null
    private lateinit var firebaseUser: FirebaseUser
    var receiverRoom: String? = null
    var senderRoom: String? = null
    var senderUid: String? = null
    var receiverUid: String? = null

    private val onNavigationItemSelectedListener =
        BottomNavigationView.OnNavigationItemSelectedListener { item ->

            when (item.itemId) {
                R.id.home -> {
                    moveToFragment(HomeFragments())
                    return@OnNavigationItemSelectedListener true
                }

                R.id.search -> {
                    startActivity(Intent(this@MainActivity, SearchActivity::class.java))
//                moveToFragment(searchFragmetns())
                    return@OnNavigationItemSelectedListener true
                }

                R.id.add -> {

//                    moveToFragment(AddFragment())
                    val alertDialog= android.app.AlertDialog.Builder(this).create()
                    alertDialog.window?.setBackgroundDrawableResource(R.color.colorBlack)
                    alertDialog.setTitle("Add your new post !!")
                    alertDialog.setButton(android.app.AlertDialog.BUTTON_NEUTRAL,"Add Photos")
                    {
                            dialog, which ->
                        val intent=Intent(this, postActivity::class.java)
                        this.startActivity(intent)
                        dialog.dismiss()
                    }
                    alertDialog.setButton(android.app.AlertDialog.BUTTON_POSITIVE,"Add Reels")
                    {
                            dialog, which ->
                        val intent=Intent(this, ReelActivity::class.java)
                       startActivity(intent)
                        dialog.dismiss()
                    }
                    alertDialog.show()
                    return@OnNavigationItemSelectedListener true

                }


                R.id.reel -> {
                    moveToFragment(ReelsFragment())
//                    startActivity(Intent(this@MainActivity,NotificationActivity::class.java))
                    return@OnNavigationItemSelectedListener true

                }

                R.id.profile -> {
                    moveToFragment(ProfileFragment())
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        FirebaseMessaging.getInstance().subscribeToTopic("notification")
        receiverUid = FirebaseAuth.getInstance().currentUser!!.uid

        val userRef = FirebaseDatabase.getInstance().reference.child("Presence").child(receiverUid!!)
        userRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val status = snapshot.getValue(String::class.java)
                    if (status == "Offline"){
                        // online
                    }else{
                        ///
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
            }

        })



        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val navView: BottomNavigationView = findViewById(R.id.nav_view)
        navView.setOnNavigationItemSelectedListener(onNavigationItemSelectedListener)

        val publisher = intent.getStringExtra("PUBLISHER_ID")
        if (publisher != null) {
            val prefs: SharedPreferences.Editor? =
                getSharedPreferences("PREFS", Context.MODE_PRIVATE)
                    .edit().apply { putString("profileId", publisher); apply() }

            moveToFragment(ProfileFragment())
        } else
        //to call fragments
            moveToFragment(HomeFragments())
    }

    private fun moveToFragment(fragment: Fragment) {
        val fragmentTrans = supportFragmentManager.beginTransaction()
        fragmentTrans.replace(R.id.fragment_container, fragment)
        fragmentTrans.commit()
    }

    override fun onResume() {
        super.onResume()
        val currentId = FirebaseAuth.getInstance().uid
        FirebaseDatabase.getInstance().reference.child("Presence").child(currentId!!)
            .setValue("Online")
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onPause() {
        super.onPause()
        val now = LocalDateTime.now()
        val startOfDay = now
        val currentId = FirebaseAuth.getInstance().uid
        FirebaseDatabase.getInstance().reference.child("Presence").child(currentId!!)
            .setValue("$startOfDay")
    }

}
