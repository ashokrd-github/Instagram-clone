package com.yaduvanshi_ashok_rd.likee.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yaduvanshi_ashok_rd.likee.AccountSettingActivity
import com.yaduvanshi_ashok_rd.likee.Activity.ProfileDetail
import com.yaduvanshi_ashok_rd.likee.Adapter.MyPostAdapter
import com.yaduvanshi_ashok_rd.likee.Adapter.MyReelAdapter
import com.yaduvanshi_ashok_rd.likee.Adapter.ReelAdapter
import com.yaduvanshi_ashok_rd.likee.Model.Post
import com.yaduvanshi_ashok_rd.likee.Model.Reel
import com.yaduvanshi_ashok_rd.likee.Model.User
import com.yaduvanshi_ashok_rd.likee.R
import com.yaduvanshi_ashok_rd.likee.ShowUserActivity
import java.util.Collections

private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProfileFragment: Fragment() {
    private lateinit var profileId: String
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var edit_profile_Button:Button
    private lateinit var total_followers: TextView
    private lateinit var total_following:TextView
    private lateinit var follow:LinearLayout
    private lateinit var following:LinearLayout
    private lateinit var total_posts:TextView
    private lateinit var total_reels:TextView
    private lateinit var profile_toolbar_username:TextView
    private lateinit var fullname_in_profile:TextView
    private lateinit var username_in_profile:TextView
    private lateinit var bio_profile:TextView
    private lateinit var profile_image_profile:ImageView




    var postList:List<Post>?=null
    var myPostAdapter: MyPostAdapter?=null


    var postListSaved:List<Post>?=null
    var myImagesAdapterSavedImg:MyPostAdapter?=null
    var mySavedImg:List<String>?=null

    var reelList:List<Reel>?=null
    var reelAdapter:MyReelAdapter?=null
    var reelSaved:List<String>?=null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_profile, container, false)
        edit_profile_Button = view.findViewById(R.id.edit_profile_Button)
        total_followers = view.findViewById(R.id.total_followers)
        total_following = view.findViewById(R.id.total_following)
        total_posts = view.findViewById(R.id.total_posts)
        total_reels = view.findViewById(R.id.total_reels)
        fullname_in_profile = view.findViewById(R.id.fullname_in_profile)
        profile_toolbar_username = view.findViewById(R.id.profile_toolbar_username)
        username_in_profile = view.findViewById(R.id.username_in_profile)
        bio_profile = view.findViewById(R.id.bio_profile)
        profile_image_profile = view.findViewById(R.id.profile_image_profile)


        follow = view.findViewById(R.id.Followers)
        following = view.findViewById(R.id.Following)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null) {
            this.profileId = pref.getString("profileId", "none")!!
        }

        if (profileId == firebaseUser.uid) {
            edit_profile_Button.text = "Edit Profile"
        } else if (profileId != firebaseUser.uid) {
            checkFollowOrFollowingButtonStatus()
        }
        //to call account profile setting activity
       edit_profile_Button.setOnClickListener {
            val getButtontext = edit_profile_Button.text.toString()
            when {
                getButtontext == "Edit Profile" -> startActivity(
                    Intent(
                        context,
                        AccountSettingActivity::class.java
                    )
                )

                getButtontext == "Follow" -> {

                    firebaseUser.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1)
                            .child("Following").child(profileId)
                            .setValue(true)

                        pushNotification()
                    }

                    firebaseUser.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(it1)
                            .setValue(true)
                    }
                }

                getButtontext == "Following" -> {

                    firebaseUser.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1)
                            .child("Following").child(profileId)
                            .removeValue()
                    }

                    firebaseUser.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(profileId)
                            .child("Followers").child(it1)
                            .removeValue()
                    }
                }
            }
        }

        profile_image_profile.setOnClickListener {
            val intent = Intent(context, ProfileDetail::class.java)
            intent.putExtra("name", username_in_profile.text)
            intent.putExtra("username", fullname_in_profile.text)
//            intent.putExtra("image", profile_image_profile.)
//            intent.putExtra("uid", firebaseUser.uid)
            startActivity(intent)

        }





        follow.setOnClickListener {
            val intent = Intent(context, ShowUserActivity::class.java)
            intent.putExtra("id",profileId)
            intent.putExtra("title","followers")
            startActivity(intent)
        }
        following.setOnClickListener {
            val intent = Intent(context, ShowUserActivity::class.java)
            intent.putExtra("id", profileId)
            intent.putExtra("title","following")
            startActivity(intent)
        }
        //to get own feeds
        var recyclerView: RecyclerView?=null
        recyclerView=view.findViewById(R.id.recyclerview_profile)
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = GridLayoutManager(context,3,GridLayoutManager.VERTICAL,false)
        postList=ArrayList()
        myPostAdapter= context?.let { MyPostAdapter(it, postList as ArrayList<Post>) }
        recyclerView.adapter=myPostAdapter


        //Adding recycler view for saved posts
        val recyclerViewSavedImages:RecyclerView
        recyclerViewSavedImages=view.findViewById(R.id.recycler_view_saved_pic)
        recyclerViewSavedImages.setHasFixedSize(true)
        val linearLayoutManager2: LinearLayoutManager =GridLayoutManager(context,3)
        recyclerViewSavedImages.layoutManager=linearLayoutManager2

        //adding recycler view for reel post
        var recyclerViewReels:RecyclerView?=null
        recyclerViewReels = view.findViewById(R.id.recycler_view_reel)
        recyclerViewReels.setHasFixedSize(true)
        recyclerViewReels.layoutManager = GridLayoutManager(context, 3, GridLayoutManager.VERTICAL, false)
        reelList = ArrayList()
        reelAdapter = context?.let { MyReelAdapter(it, reelList as ArrayList<Reel>) }
        recyclerViewReels.adapter = reelAdapter


        postListSaved=ArrayList()
        myImagesAdapterSavedImg=context?.let { MyPostAdapter( it,postListSaved as ArrayList<Post>) }
        recyclerViewSavedImages.adapter=myImagesAdapterSavedImg

        // saved reels


        recyclerViewReels.visibility = View.GONE
        recyclerViewSavedImages.visibility=View.GONE
        recyclerView.visibility=View.VISIBLE

        //To view savedimages button function
        val uploadedImagesBtn: ImageButton
        uploadedImagesBtn=view.findViewById(R.id.postGrid)
        uploadedImagesBtn.setOnClickListener{
            recyclerViewSavedImages.visibility=View.GONE
            recyclerView.visibility=View.VISIBLE
            recyclerViewReels.visibility=View.GONE
        }
        /// reel for reel
        val GridReel:ImageButton
        GridReel=view.findViewById(R.id.reelGrid)
        GridReel.setOnClickListener {
            recyclerViewReels.visibility = View.VISIBLE
            recyclerView.visibility = View.GONE
            recyclerViewSavedImages.visibility = View.GONE
        }


        //To view uploadedimages button function
        val savedImagesBtn: ImageButton
        savedImagesBtn=view.findViewById(R.id.images_save_btn)
        savedImagesBtn.setOnClickListener{
            recyclerViewSavedImages.visibility=View.VISIBLE
            recyclerView.visibility=View.GONE
            recyclerViewReels.visibility = View.GONE
        }



        //to fill in data in profile page
        getFollowers()
        getFollowing()
        getNoofPosts()
        getNoOfReels()

        getUserInfo(view)
        myPosts()
        mySaves()
        myReel()

        return view
    }

    private fun mySaves() {

        mySavedImg=ArrayList()
        val savesRef=FirebaseDatabase.getInstance().reference.child("Saves").child(firebaseUser.uid)
        savesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()){
                    for(pO in snapshot.children)
                    {
                        ( mySavedImg as ArrayList<String>).add(pO.key!!)
                    }

                    readSavedImagesData()//Following is thr function to get the details of the saved posts
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    private fun readSavedImagesData() {

        val PostsRef=FirebaseDatabase.getInstance().reference.child("Posts")
        PostsRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(datasnapshot: DataSnapshot) {
                if(datasnapshot.exists())
                {
                    (postListSaved as ArrayList<Post>).clear()

                    for(snapshot in datasnapshot.children)
                    {
                        val post=snapshot.getValue(Post::class.java)

                        for(key in mySavedImg!!)
                        {
                            if (post!!.getPostId()==key)
                            {
                                (postListSaved as ArrayList<Post>).add(post)
                            }
                        }
                    }
                    myImagesAdapterSavedImg!!.notifyDataSetChanged()
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })

    }

    private fun checkFollowOrFollowingButtonStatus() {

        val followingRef = firebaseUser.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString())
                .child("Following")
        }

        if (followingRef != null) {
            followingRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {
                }

                override fun onDataChange(p0: DataSnapshot) {

                    if (p0.child(profileId).exists()) {
                        edit_profile_Button.text = "Following"
                    } else {
                        edit_profile_Button.text = "Follow"
                    }
                }
            })
        }
    }

    private fun getFollowers() {
        val followersRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId)
            .child("Followers")

        followersRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    total_followers.text = snapshot.childrenCount.toString()
                }
            }
        })
    }

    private fun getFollowing() {
        val followingsRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(profileId)
            .child("Following")

        followingsRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                   total_following.text = snapshot.childrenCount.toString()
                }
            }
        })
    }

    private fun getNoofPosts() {
        val postRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                var i:Int=0
                for(snapshot in p0.children)
                {
                    val post=snapshot.getValue(Post::class.java)
                    if(post!!.getPublisher().equals(profileId))
                    {
                        i=i+1
                    }
                }
                total_posts.text = ""+i
            }
        })
    }

    // count of reel

    private fun getNoOfReels() {
        val postRef = FirebaseDatabase.getInstance().reference.child("Reels")

        postRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                var i:Int=0
                for(snapshot in p0.children)
                {
                    val post=snapshot.getValue(Post::class.java)
                    if(post!!.getPublisher().equals(profileId))
                    {
                        i=i+1
                    }
                }
                total_reels.text = ""+i
            }
        })
    }
    // count of reel

    private fun myPosts() {
        val postRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                (postList as ArrayList<Post>).clear()
                for(snapshot in p0.children)
                {
                    val post=snapshot.getValue(Post::class.java)
                    if(post!!.getPublisher().equals(profileId))
                        (postList as ArrayList<Post>).add(post)
                }
                Collections.reverse(postList)
                myPostAdapter!!.notifyDataSetChanged()
            }
        })
    }

    // reel for reel

    private fun myReel() {
        val postRef = FirebaseDatabase.getInstance().reference.child("Reels")

        postRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                (reelList as ArrayList<Reel>).clear()
                for(snapshot in p0.children)
                {
                    val reel=snapshot.getValue(Reel::class.java)
                    if(reel!!.getPublisher().equals(profileId))
                        (reelList as ArrayList<Reel>).add(reel)
                }
                Collections.reverse(reelList)
                myPostAdapter!!.notifyDataSetChanged()
            }
        })
    }

    // reel for reel

    private fun pushNotification() {

        val ref = FirebaseDatabase.getInstance().reference.child("Notification").child(profileId)

        val notifyMap = HashMap<String, Any>()
        notifyMap["userid"] = FirebaseAuth.getInstance().currentUser!!.uid
        notifyMap["text"] = "➱Started following you "
        notifyMap["postid"] = ""
        notifyMap["ispost"] = true

        ref.push().setValue(notifyMap)
    }


    private fun getUserInfo(view: View) {
        val usersRef = FirebaseDatabase.getInstance().reference.child("Users").child(profileId)
        usersRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {

                if (snapshot.exists()) {
                    val user = snapshot.getValue<User>(User::class.java)
//                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profile_image_profile)
                    Glide.with(context!!).load(user!!.getImage()).placeholder(R.drawable.profile).into(profile_image_profile)
                    profile_toolbar_username.text =user.getUsername()
                    fullname_in_profile.text = user.getFullname()
                    username_in_profile.text = user.getUsername()
                    bio_profile.text = user.getBio()

                }
            }
        })
    }

    override fun onStop() {
        super.onStop()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onPause() {
        super.onPause()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }

    override fun onDestroy() {
        super.onDestroy()

        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)?.edit()
        pref?.putString("profileId", firebaseUser.uid)
        pref?.apply()
    }
}