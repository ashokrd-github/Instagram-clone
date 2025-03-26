package com.yaduvanshi_ashok_rd.likee.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yaduvanshi_ashok_rd.likee.Activity.ProfileDetail
import com.yaduvanshi_ashok_rd.likee.Activity.chatActivity
import com.yaduvanshi_ashok_rd.likee.Model.Message
import com.yaduvanshi_ashok_rd.likee.Model.User
import com.yaduvanshi_ashok_rd.likee.R

class ChatUserAdapter(
    private val context: Context,
    private val user: ArrayList<User>,
//    senderRoom: String,
//    receiverRoom: String,
//    senderRoom: String,
//    receiverRoom: String,
) : RecyclerView.Adapter<ChatUserAdapter.viewHolder>() {
    private lateinit var firebaseUser: FirebaseUser
//    var receiverRoom: String?=null
//    var senderRoom: String?=null
    var senderUid: String? = null
    var receiverUid: String? = null


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.chat_user_layout, parent, false)
        return viewHolder(view)
    }

    override fun getItemCount(): Int {
        return user.size
    }


    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val currentUser = user[position]
        Glide.with(context).load(currentUser.getImage()).placeholder(R.drawable.profile)
            .into(holder.userProfile)
        holder.userName.text = currentUser.getUsername()
//        holder.fullName.text = currentUser.getFullname()
        getStatus(holder.viewStatus, currentUser.getUid())
        getLast(currentUser.getUid(), holder.fullName)



        holder.chatUserItem.setOnClickListener {
            val intent = Intent(context, chatActivity::class.java)
            intent.putExtra("name", currentUser.getUsername())
            intent.putExtra("image", currentUser.getImage())
            intent.putExtra("username", currentUser.getFullname())
            intent.putExtra("uid", currentUser.getUid())

            context.startActivity(intent)

        }
        holder.userProfile.setOnClickListener {
            val intent = Intent(context, ProfileDetail::class.java)
            intent.putExtra("name", currentUser.getUsername())
            intent.putExtra("image", currentUser.getImage())
            intent.putExtra("username", currentUser.getFullname())
            intent.putExtra("uid", currentUser.getUid())
            context.startActivity(intent)
        }


    }
//    val senderUid = FirebaseAuth.getInstance().currentUser!!.uid
//    var receiverUid = user
//    val senderRoom = senderUid + receiverUid
//    val receiverRoom = receiverUid + senderUid


    class viewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userProfile: ImageView = itemView.findViewById(R.id.userProfile)
        val userName: TextView = itemView.findViewById(R.id.userName)
        val fullName: TextView = itemView.findViewById(R.id.fullName)
        val chatUserItem: ConstraintLayout = itemView.findViewById(R.id.chat_user_item)
        val viewStatus: TextView = itemView.findViewById(R.id.user_status)


    }

    private fun getStatus(viewStatus: TextView, userId: String) {
        val userRef = FirebaseDatabase.getInstance().reference.child("Presence").child(userId)
        userRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val status = snapshot.getValue(String::class.java)
                    if (status == "Offline") {
                        viewStatus.setText(status)
                    viewStatus.visibility = View.GONE
                    } else {
                        viewStatus.setText(status)
                        viewStatus.visibility = View.VISIBLE
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }
    private fun getLast(userId: String, lastMessage:TextView){
        val dRef = FirebaseDatabase.getInstance().reference.child("Presence").child(userId)
        dRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val lastmsg = snapshot.getValue(String::class.java)
                    if (lastmsg == ""){

                    }else{
                        lastMessage.setText(lastmsg)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
            }

        })
    }

}
