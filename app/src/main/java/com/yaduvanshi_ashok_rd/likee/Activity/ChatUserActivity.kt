package com.yaduvanshi_ashok_rd.likee.Activity

import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.NotificationCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yaduvanshi_ashok_rd.likee.Adapter.ChatUserAdapter
import com.yaduvanshi_ashok_rd.likee.Model.User
import com.yaduvanshi_ashok_rd.likee.R
import kotlinx.datetime.toKotlinLocalDateTime
import java.time.LocalDateTime

class ChatUserActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: ChatUserAdapter
    private lateinit var userList: ArrayList<User>
    private lateinit var search: EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var chatShimmer:ShimmerFrameLayout
    private lateinit var dataView:LinearLayout
//    private lateinit var viewStatus:TextView
    private lateinit var firebaseUser: FirebaseUser
    var receiverRoom: String? = null
    var senderRoom: String? = null
    var senderUid: String? = null
    var receiverUid: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_chat_user)
        enableEdgeToEdge()
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT


//        receiverUid = intent.getStringExtra("uid")
////        senderUid = FirebaseAuth.getInstance().uid
//
//        senderUid = FirebaseAuth.getInstance().currentUser?.uid
//
//        senderRoom = senderUid+receiverUid
//        receiverRoom=receiverUid+senderUid

        chatShimmer = findViewById(R.id.chat_shimmer)
        dataView = findViewById(R.id.data_view)
        auth = Firebase.auth
        userList = ArrayList()
        userAdapter = ChatUserAdapter(this, userList)
        recyclerView = findViewById(R.id.chat_rv_user)
        search = findViewById(R.id.search)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = userAdapter
//        viewStatus = findViewById(R.id.user_status)
        receiverUid = FirebaseAuth.getInstance().currentUser!!.uid


        val userRef = FirebaseDatabase.getInstance().reference.child("Presence").child(receiverUid!!)
        userRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val status = snapshot.getValue(String::class.java)
                    if (status == "Offline"){
//
//                        intent.putExtra("online", firebaseUser.uid)
                    }else{
//                    intent.putExtra("offline", firebaseUser.uid)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}

        })

        FirebaseDatabase.getInstance().getReference().child("Users")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userList.clear()
                    for (dataSnapshot in snapshot.children){
                        val currentUser = dataSnapshot.getValue(User::class.java)
                        if (auth.currentUser?.uid != currentUser?.getUid()){
                            userList.add(currentUser!!)
                            chatShimmer.stopShimmer()
                            chatShimmer.visibility = View.GONE
                        }
                    }
                    userAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {}

            })

        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (search.text.toString() == ""){

                }else{
                    recyclerView.visibility = View.VISIBLE
                    searchUser(s.toString().toLowerCase())
                }
            }

        })


    }
    private fun searchUser(input:String) {

        FirebaseDatabase.getInstance().reference
            .child("Users")
            .orderByChild("username")
            .startAt(input)
            .endAt(input + "\uf8ff").addValueEventListener(object:ValueEventListener
            {
                override fun onCancelled(error: DatabaseError) {

                }
                override fun onDataChange(datasnapshot: DataSnapshot) {
                    userList!!.clear()

                    for(snapshot in datasnapshot.children)
                    {
                        //searching all users
                        val user=snapshot.getValue(User::class.java)
                        if(user!=null)
                        {
                            userList.add(user)
                        }
                    }
                    userAdapter.notifyDataSetChanged()
                }
            })
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
        val startOfDay = now.toKotlinLocalDateTime()
        val currentId = FirebaseAuth.getInstance().uid
        FirebaseDatabase.getInstance().reference.child("Presence")
            .child(currentId!!).setValue("$startOfDay")
    }
    

}