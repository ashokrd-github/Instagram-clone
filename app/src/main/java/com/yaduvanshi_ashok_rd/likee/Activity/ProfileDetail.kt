package com.yaduvanshi_ashok_rd.likee.Activity

import android.content.pm.ActivityInfo
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
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
import com.squareup.picasso.Picasso
import com.yaduvanshi_ashok_rd.likee.Adapter.MyPostAdapter
import com.yaduvanshi_ashok_rd.likee.Model.Post
import com.yaduvanshi_ashok_rd.likee.Model.User
import com.yaduvanshi_ashok_rd.likee.R

class ProfileDetail : AppCompatActivity() {
    private lateinit var user_detail_name:TextView
    private lateinit var post_detail_image:ImageView
    private lateinit var post_detail_full_name:TextView
    private lateinit var firebaseUser: FirebaseUser

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        var postList: List<Post>? = null
        var myPostAdapter: MyPostAdapter? = null


        var postListSaved: List<Post>? = null
        var myImagesAdapterSavedImg: MyPostAdapter? = null
        var mySavedImg: List<String>? = null

        setContentView(R.layout.activity_profile_detail)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT


        setSupportActionBar(findViewById(R.id.profileToolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        user_detail_name = findViewById(R.id.user_detail_Name)
        post_detail_image = findViewById(R.id.post_detail_image)
        post_detail_full_name = findViewById(R.id.user_detail_full_Name)
        firebaseUser = FirebaseAuth.getInstance().currentUser!!



            val profile = intent.getStringExtra("image")
            val name = intent.getStringExtra("name")
            val username = intent.getStringExtra("username")
            val uid = intent.getStringExtra("uid")


            user_detail_name.text = name
            post_detail_full_name.text =username
            Glide.with(this).load(profile).placeholder(R.drawable.profile).into(post_detail_image)


    getUserData()
    }


 private fun getUserData(){
     val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(firebaseUser.uid)
     userRef.addValueEventListener(object : ValueEventListener{
         override fun onDataChange(snapshot: DataSnapshot) {
             if (snapshot.exists()){
                 val user = snapshot.getValue(User::class.java)
                 Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(post_detail_image)
//                 user_detail_name.text = user.getUsername()
//                 post_detail_full_name.text = user.getFullname()
             }
         }

         override fun onCancelled(error: DatabaseError) {}

     })
 }
}