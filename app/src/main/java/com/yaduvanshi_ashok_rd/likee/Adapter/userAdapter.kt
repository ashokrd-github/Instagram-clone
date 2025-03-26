package com.yaduvanshi_ashok_rd.likee.Adapter

import android.content.Context
import android.content.Intent
import android.provider.ContactsContract.Profile
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yaduvanshi_ashok_rd.likee.Activity.GalleryActivity
import com.yaduvanshi_ashok_rd.likee.Activity.ProfileDetail
import com.yaduvanshi_ashok_rd.likee.MainActivity
import com.yaduvanshi_ashok_rd.likee.Model.User
import com.yaduvanshi_ashok_rd.likee.R
import de.hdodenhof.circleimageview.CircleImageView
import java.util.Date


class userAdapter (private var mContext:Context,
                   private var mUser:List<User>,
                   private var isFragment:Boolean=false):RecyclerView.Adapter<userAdapter.ViewHolder>(){

    private val firebaseUser: FirebaseUser?= FirebaseAuth.getInstance().currentUser

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): userAdapter.ViewHolder {
        //to make user item available in search item
        val view=LayoutInflater.from(mContext).inflate(R.layout.user_item_layout,parent,false)

        return userAdapter.ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mUser.size
    }

    override fun onBindViewHolder(holder: userAdapter.ViewHolder, position: Int) {
        //to display the user data
        val user=mUser[position]
        holder.userNameTextView.text=user.getUsername()
        holder.userFullnameTextView.text=user.getFullname()
        Glide.with(mContext).load(user.getImage()).placeholder(R.drawable.profile).into(holder.userProfileImage) //add picasso dependency for image caching and downloading
        checkFollowingStatus(user.getUid(),holder.followButton)

        //to go to searched user's profile

        holder.userProfileImage.setOnClickListener {
            val intent = Intent(mContext, ProfileDetail::class.java)
            intent.putExtra("name", user.getFullname())
            intent.putExtra("username", user.getUsername())
            intent.putExtra("image", user.getImage())
            intent.putExtra("uid", user.getUid())
            mContext.startActivity(intent)
        }


        holder.useritem.setOnClickListener {

            val pref=mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
            pref.putString("PUBLISHER_ID",user.getUid())
            pref.apply()

            val intent = Intent(mContext, MainActivity::class.java).apply {
                putExtra("PUBLISHER_ID", user.getUid())
            }
            mContext.startActivity(intent)
        }

        holder.followButton.setOnClickListener {
            if(holder.followButton.text.toString()=="Follow") {
                firebaseUser?.uid.let { it1 ->
                    FirebaseDatabase.getInstance().reference
                        .child("Follow").child(it1.toString())
                        .child("Following").child(user.getUid())
                        .setValue(true).addOnCompleteListener { task ->
                            if (task.isSuccessful) {

                                firebaseUser?.uid.let { it1 ->
                                    FirebaseDatabase.getInstance().reference
                                        .child("Follow").child(user.getUid())
                                        .child("Followers").child(it1.toString())
                                        .setValue(true).addOnCompleteListener { task ->
                                            if (task.isSuccessful)
                                            {
                                                pushNotification(user.getUid())
                                            }
                                        }
                                }
                            }
                        }
                }
            }
            else
            {
                if(holder.followButton.text.toString()=="Following") {
                    firebaseUser?.uid.let { it1 ->
                        FirebaseDatabase.getInstance().reference
                            .child("Follow").child(it1.toString())
                            .child("Following").child(user.getUid())
                            .removeValue().addOnCompleteListener { task -> //reversing following action
                                if (task.isSuccessful) {
                                    firebaseUser?.uid.let { it1 ->
                                        FirebaseDatabase.getInstance().reference
                                            .child("Follow").child(user.getUid())
                                            .child("Followers").child(it1.toString())
                                            .removeValue().addOnCompleteListener { task ->
                                                if (task.isSuccessful) {
                                                }
                                            }
                                    }
                                }
                            }
                    }
                }
            }
        }
    }

    class ViewHolder(@NonNull itemView: View): RecyclerView.ViewHolder(itemView)
    {
        var userNameTextView:TextView=itemView.findViewById(R.id.userName)
        var useritem: ConstraintLayout =itemView.findViewById(R.id.userItem)
        var userFullnameTextView:TextView=itemView.findViewById(R.id.fullName)
        var userProfileImage: CircleImageView =itemView.findViewById(R.id.userProfile)
        var followButton: Button =itemView.findViewById(R.id.btnFollow)
    }

    private fun pushNotification(userid:String) {

        val ref = FirebaseDatabase.getInstance().reference.child("Notification").child(userid)

        val notifyMap = HashMap<String, Any>()
        notifyMap["userid"] = FirebaseAuth.getInstance().currentUser!!.uid
        notifyMap["text"] = "started following you"
        notifyMap["postid"] = ""
        notifyMap["ispost"] = false
        notifyMap["timestamp"] = Date().time

        ref.push().setValue(notifyMap)
    }

    private fun checkFollowingStatus(uid:String,followButton: Button) {
        val followingRef = firebaseUser?.uid.let { it1 ->
            FirebaseDatabase.getInstance().reference
                .child("Follow").child(it1.toString())
                .child("Following")
        }

        followingRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(datasnapshot: DataSnapshot) {
                if (datasnapshot.child(uid).exists()) {
                    followButton.text = "Following"
                }
                else {
                    followButton.text = "Follow"
                }
            }
        })
    }
}