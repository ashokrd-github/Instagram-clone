package com.yaduvanshi_ashok_rd.likee.Fragments

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yaduvanshi_ashok_rd.likee.Activity.ChatUserActivity
import com.yaduvanshi_ashok_rd.likee.Activity.NotificationActivity
import com.yaduvanshi_ashok_rd.likee.Adapter.PostAdapter
import com.yaduvanshi_ashok_rd.likee.Adapter.StoryAdapter
import com.yaduvanshi_ashok_rd.likee.Model.Notification
import com.yaduvanshi_ashok_rd.likee.Model.Post
import com.yaduvanshi_ashok_rd.likee.Model.Story
import com.yaduvanshi_ashok_rd.likee.R


class HomeFragments : Fragment() {
    private lateinit var profileId: String
    private lateinit var message: ImageView
    private lateinit var notification:ImageView
//    private lateinit var welcome_text: TextView
    private lateinit var home_shimmer:ShimmerFrameLayout
    private lateinit var dataLayout: LinearLayout
    private lateinit var storyShimmer:ShimmerFrameLayout
    private lateinit var storyView:LinearLayout
    private var postAdapter: PostAdapter? = null
    private var postList: MutableList<Post>? = null
    private var followingList: MutableList<String>? = null
    private var storyAdapter: StoryAdapter? = null
    private var storyList: MutableList<Story>? = null
    private lateinit var notificationCounter: TextView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_home_fragments, container, false)
        message = view.findViewById(R.id.message)
        notification = view.findViewById(R.id.notification)
        home_shimmer = view.findViewById(R.id.home_shimmer)
        dataLayout = view.findViewById(R.id.data_view)
        notificationCounter = view.findViewById(R.id.notificationCounter)
//        storyShimmer = view.findViewById(R.id.story_shimmer)
//        storyView = view.findViewById(R.id.StoryView)
//        notification.setOnClickListener {
//            (context as FragmentActivity).supportFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, NotificationFragment()).commit()
//        }
        val pref = context?.getSharedPreferences("PREFS", Context.MODE_PRIVATE)
        if (pref != null) {
            this.profileId = pref.getString("profileId", "none")!!
        }



        message.setOnClickListener{
            startActivity(Intent(requireContext(), ChatUserActivity::class.java))
        }

        notification.setOnClickListener {
            startActivity(Intent(requireContext(), NotificationActivity::class.java))
        }

        var recyclerView: RecyclerView? = null
        var recyclerViewStory: RecyclerView? = null

        recyclerView = view.findViewById(R.id.recycler_view_home)
        val linearlayoutManager = LinearLayoutManager(context)
        linearlayoutManager.reverseLayout = true
        //New posts at top
        linearlayoutManager.stackFromEnd = true
        recyclerView.layoutManager = linearlayoutManager
        //For Posts
        postList = ArrayList()
        postAdapter = context?.let { PostAdapter(it, postList as ArrayList<Post>) }
        recyclerView.adapter = postAdapter
//        home_shimmer.startShimmer()


        recyclerViewStory = view.findViewById(R.id.recycler_view_story)
        recyclerViewStory.setHasFixedSize(true)
        val linearlayoutManager2 =
            LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        recyclerViewStory.layoutManager = linearlayoutManager2
        ///For Stories
        storyList = ArrayList()
        storyAdapter = context?.let { StoryAdapter(it, storyList as ArrayList<Story>) }
        recyclerViewStory.adapter = storyAdapter

//        code for counting no of items in recycler view

//         if (postAdapter!!.itemCount == 0){
//             welcome_text.text = "Welcome to Likee"
//         }
//         else
//         {
//             welcome_text.visibility=View.INVISIBLE
//         }
        noOfNotification()
        checkFollowings()
        return view
    }

    private fun noOfNotification() {
        val notificationRef = FirebaseDatabase.getInstance().reference.child("Notification")
        notificationRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(p0: DataSnapshot) {
                var count = 0 // Improved variable name and direct initialization

                for (snapshot in p0.children) {
                    val notification = snapshot.getValue(Notification::class.java)

                    notification?.let { // Safe call using let
                        if (it.getText() == profileId) { // Safe property access and equals comparison
                            count++
                        }
                    }
                }

                notificationCounter.text = "$count" // String templates for efficiency
            }

            override fun onCancelled(error: DatabaseError) {
//                Log.e("NotificationError", "Database error: ${error.message}") // Proper error logging
            }
        })
    }

//    Notification counter initialize

    private fun checkFollowings() {
        followingList = ArrayList()

        val followingRef = FirebaseDatabase.getInstance().reference
            .child("Follow").child(FirebaseAuth.getInstance().currentUser!!.uid)
            .child("Following")

        followingRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    (followingList as ArrayList<String>).clear() //to get previous data
                    for (snapshot in p0.children) {
                        snapshot.key?.let { (followingList as ArrayList<String>).add(it) }
                    }
                    retrieveStories()
                        retrievePosts()

                }
            }
        })

    }

    private fun retrievePosts() {
        val postRef = FirebaseDatabase.getInstance().reference.child("Posts")

        postRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot) {
                if (p0.exists()) {
                    postList?.clear()
                    for (snapshot in p0.children) {
                        val post = snapshot.getValue(Post::class.java)

                        for (id in (followingList as ArrayList<String>)) {
                            if (post!!.getPublisher() == id) {
                                postList!!.add(post)
                                home_shimmer.stopShimmer()
                                home_shimmer.visibility = View.GONE
                            }
                            postAdapter!!.notifyDataSetChanged()
                        }
                    }
                }
            }

        })
    }

    private fun retrieveStories() {
        val storyRef = FirebaseDatabase.getInstance().reference.child("Story")

        storyRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(datasnapshot: DataSnapshot) {
                val timeCurrent = System.currentTimeMillis()

                (storyList as ArrayList<Story>).clear()

                (storyList as ArrayList<Story>).add(
                    Story(
                        "",
                        0,
                        0,
                        "",
                        FirebaseAuth.getInstance().currentUser!!.uid
                    )
                )

                for (id in followingList!!) {
                    var countStory = 0


                    var story: Story? = null
                    for (snapshot in datasnapshot.child(id).children) {
                        story = snapshot.getValue(Story::class.java)

                        if (timeCurrent > story!!.getTimeStart() && timeCurrent < story.getTimeEnd()) {
                            countStory++
                        }
                    }
                    if (countStory > 0) {
                        (storyList as ArrayList<Story>).add(story!!)

                    }
                }
                storyAdapter!!.notifyDataSetChanged()


            }

            override fun onCancelled(error: DatabaseError) {

            }
        })


    }
}