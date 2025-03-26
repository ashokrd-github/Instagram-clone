package com.yaduvanshi_ashok_rd.likee.Activity

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.CalendarContract.Colors
import android.view.View
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yaduvanshi_ashok_rd.likee.Adapter.NotificationAdapter
import com.yaduvanshi_ashok_rd.likee.Model.Notification
import com.yaduvanshi_ashok_rd.likee.R
import java.util.Collections

class NotificationActivity : AppCompatActivity() {
    private var notificationAdapter: NotificationAdapter?=null
    private var notificationList:MutableList<Notification>?=null
    private  var firebaseUser: FirebaseUser?=null
    private lateinit var notification_shimmer:ShimmerFrameLayout
    private lateinit var dataView: LinearLayout
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notification)
        setSupportActionBar(findViewById(R.id.notification_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT

        notification_shimmer = findViewById(R.id.notification_shimmer)
        dataView = findViewById(R.id.data_view)

        firebaseUser = FirebaseAuth.getInstance().currentUser!!
        var recyclerView: RecyclerView?=null
        recyclerView= findViewById(R.id.recyclerview_notification)

//        dataView.visibility = View.VISIBLE
//        notification_shimmer.startShimmer()
//
//        Handler(Looper.getMainLooper()).postDelayed({
//            dataView.visibility = View.GONE
//            notification_shimmer.stopShimmer()
//            notification_shimmer.visibility = View.VISIBLE
//
//        }, 1000)

        recyclerView.setHasFixedSize(true)
        val linearLayoutManager: LinearLayoutManager = LinearLayoutManager(this)
        recyclerView.layoutManager=linearLayoutManager

        notificationList=ArrayList()
        notificationAdapter= this.let { NotificationAdapter(it,notificationList as ArrayList<Notification>) }
        recyclerView.adapter=notificationAdapter

//        readNotification()
        Handler(Looper.getMainLooper()).postDelayed({
            readNotification()
        }, 3000)

    }
    private fun readNotification() {

        val postRef= FirebaseDatabase.getInstance().reference.child("Notification").child(firebaseUser!!.uid)
        postRef.addValueEventListener(object : ValueEventListener
        {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(p0: DataSnapshot)
            {
                notificationList?.clear()
                for (snapshot in p0.children)
                {
                    val notification: Notification? = snapshot.getValue(Notification::class.java)
                    notificationList!!.add(notification!!)
                    notification_shimmer.stopShimmer()
                    notification_shimmer.visibility = View.GONE
                }
                Collections.reverse(notificationList)
                notificationAdapter!!.notifyDataSetChanged()

            }
        })
    }
}
