package com.yaduvanshi_ashok_rd.likee.Adapter

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.app.NotificationCompat
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.Timestamp
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.yaduvanshi_ashok_rd.likee.Activity.NotificationActivity
import com.yaduvanshi_ashok_rd.likee.Activity.postDetailActivity
import com.yaduvanshi_ashok_rd.likee.Fragments.PostDetailsFragment
import com.yaduvanshi_ashok_rd.likee.Fragments.ProfileFragment
import com.yaduvanshi_ashok_rd.likee.Model.Notification
import com.yaduvanshi_ashok_rd.likee.Model.Post
import com.yaduvanshi_ashok_rd.likee.Model.User
import com.yaduvanshi_ashok_rd.likee.R
import de.hdodenhof.circleimageview.CircleImageView
import me.leolin.shortcutbadger.ShortcutBadger
import java.util.Date
import kotlin.time.TimeSource


class NotificationAdapter (
    private var mContext: Context,
    private var mNotification:List<Notification>)
    : RecyclerView.Adapter<NotificationAdapter.ViewHolder>() {

    class ViewHolder( itemView: View) : RecyclerView.ViewHolder(itemView) {

        var username: TextView = itemView.findViewById(R.id.notification_username)
        var notifyText: TextView = itemView.findViewById(R.id.notification_text)
        var profileimage: CircleImageView = itemView.findViewById(R.id.notification_image_profile)
        var postimg: ImageView = itemView.findViewById(R.id.posted_image)
        var notificationTime:TextView = itemView.findViewById(R.id.notification_time)
        var deleteNotification:RelativeLayout = itemView.findViewById(R.id.notification_delete)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(mContext).inflate(R.layout.notification_item,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mNotification.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val notification = mNotification[position]
        holder.notifyText.text=notification.getText()
        holder.notificationTime.text = java.sql.Timestamp(notification.timestamp).toString()
        holder.notificationTime.visibility = View.VISIBLE


        publisherInfo(holder.profileimage,holder.username,notification.getUserId())
        if(notification.getIsPost())
        {
            holder.postimg.visibility=View.VISIBLE
            getPostedImg(holder.postimg,notification.getPostId())
        }
        else
        {
            holder.postimg.visibility=View.GONE
        }

        holder.postimg.setOnClickListener {
            if(notification.getIsPost()) {
                val pref = mContext.getSharedPreferences("PREFS", Context.MODE_PRIVATE).edit()
                pref.putString("postid", notification.getPostId())
                pref.apply()


//                (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
//                    .replace(R.id.fragment_container, PostDetailsFragment()).commit()
                val intent = Intent(mContext, postDetailActivity::class.java).apply {
                    putExtra("postid", notification.getPostId())
                }
                mContext.startActivity(intent)
            }
            else
            {

                val pref=mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
                pref.putString("profileid",notification.getUserId())
                pref.apply()

                (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_container, ProfileFragment()).commit()
            }
        }
        // delete notification
        holder.deleteNotification.setOnLongClickListener {
            val alertDialog = AlertDialog.Builder(mContext).create()
            alertDialog.setMessage("Do you want delete Notification?")
            alertDialog.window?.setBackgroundDrawableResource(R.color.colorBlack)
            alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "No"){dialog, which->
                dialog.dismiss()

            }
            alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "Yes"){dialog, which->
                FirebaseDatabase.getInstance().reference.child("Users").child("Notification")

            }


            alertDialog.show()
            return@setOnLongClickListener false

        }
        // delete notification
    }


    private fun publisherInfo(imgView: CircleImageView, username: TextView, publisherid: String) {

        val userRef= FirebaseDatabase.getInstance().reference.child("Users").child(publisherid)
        userRef.addValueEventListener(object : ValueEventListener
        {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
//                val user = snapshot.getValue(User::class.java)
                val user = snapshot.getValue(User::class.java)

//                Glide.with(mContext).load(user!!.getImage()).placeholder(R.drawable.profile).into(imgView)
               Picasso.get().load(user?.getImage()).placeholder(R.drawable.profile).into(imgView)
                username.text =(user?.getUsername())
            }
        })
    }

    private fun getPostedImg(postimg:ImageView, postid:String?) {

        val postRef= FirebaseDatabase.getInstance().reference.child("Posts").child(postid!!)

        postRef.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(error: DatabaseError) {
            }
            override fun onDataChange(snapshot: DataSnapshot)
            {
                val post = snapshot.getValue(Post::class.java)
                Glide.with(mContext).load(post!!.getPostImage()).placeholder(R.drawable.loading).into(postimg)
//                Picasso.get().load(post?.getPostId()).into(postimg)

            }
        })
    }


}