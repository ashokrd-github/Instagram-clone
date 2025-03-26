package com.yaduvanshi_ashok_rd.likee.Activity

import android.content.pm.ActivityInfo
import android.graphics.Color
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.LinearLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.facebook.shimmer.ShimmerFrameLayout
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.firebase.ktx.Firebase
import com.yaduvanshi_ashok_rd.likee.Adapter.userAdapter
import com.yaduvanshi_ashok_rd.likee.Model.User
import com.yaduvanshi_ashok_rd.likee.R

class SearchActivity : AppCompatActivity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var userAdapter: userAdapter
    private lateinit var userList: ArrayList<User>
    private lateinit var searchItem: EditText
    private lateinit var auth: FirebaseAuth
    private lateinit var searchShimmer:ShimmerFrameLayout
    private lateinit var dataView:LinearLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_search)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        setSupportActionBar(findViewById(R.id.search_toolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        searchShimmer = findViewById(R.id.search_shimmer)
        dataView = findViewById(R.id.data_view)
        auth = Firebase.auth
        userList = ArrayList()
        userAdapter = userAdapter(this, userList)
        recyclerView = findViewById(R.id.recyclerview_search)
        searchItem = findViewById(R.id.searchItem)
        recyclerView.layoutManager = LinearLayoutManager(this)
        recyclerView.adapter = userAdapter


        FirebaseDatabase.getInstance().getReference().child("Users")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    userList.clear()
                    for (dataSnapshot in snapshot.children){
                        val currentUser = dataSnapshot.getValue(User::class.java)
                        if (auth.currentUser?.uid != currentUser?.getUid()){
                            userList.add(currentUser!!)
                            searchShimmer.stopShimmer()
                            searchShimmer.visibility = View.GONE
                        }
                    }
                    userAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {
//                    TODO("Not yet implemented")
                }

            })

        searchItem.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}

            override fun afterTextChanged(s: Editable?) {
                if (searchItem.text.toString() == ""){

                }else{
                    recyclerView.visibility = View.VISIBLE
                    searchUser(s.toString().toLowerCase())
                }
            }

        })


    }
    private fun searchUser(input:String) {

      val query =   FirebaseDatabase.getInstance().reference
            .child("Users")
            .orderByChild("username")
            .startAt(input)
            .endAt(input + "\uf8ff")
            query.addValueEventListener(object:ValueEventListener
            {
                override fun onCancelled(error: DatabaseError) {

                }
                override fun onDataChange(datasnapshot: DataSnapshot) {
                    userList.clear()

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

}
