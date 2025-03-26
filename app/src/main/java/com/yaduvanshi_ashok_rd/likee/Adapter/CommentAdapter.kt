package com.yaduvanshi_ashok_rd.likee.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.yaduvanshi_ashok_rd.likee.MainActivity
import com.yaduvanshi_ashok_rd.likee.Model.Comment
import com.yaduvanshi_ashok_rd.likee.Model.User
import com.yaduvanshi_ashok_rd.likee.R
import de.hdodenhof.circleimageview.CircleImageView

class CommentAdapter (private var mContext: Context,
                      private var mComment:List<Comment>): RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    private var firebaseUser: FirebaseUser? = null

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var profileImage: CircleImageView
        var publisher: TextView
        var publisher_comment: TextView
        var commentTime:TextView


        init {
            profileImage = itemView.findViewById(R.id.publisher_image_profile)
            publisher = itemView.findViewById(R.id.publisher_username)
            publisher_comment = itemView.findViewById(R.id.publisher_caption)
            commentTime = itemView.findViewById(R.id.comment_time)
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(mContext).inflate(R.layout.comment_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mComment.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        firebaseUser = FirebaseAuth.getInstance().currentUser
        val comment = mComment[position]

        if(comment.getComment()!="")
            holder.publisher_comment.text=(comment.getComment())
            holder.commentTime.text = java.sql.Timestamp(comment.timestamp).toString()
            holder.commentTime.visibility = View.VISIBLE

        publisherInfo(holder.profileImage, holder.publisher, comment.getPublisher())

        holder.publisher.setOnClickListener {

            val intent = Intent(mContext, MainActivity::class.java).apply {
                putExtra("PUBLISHER_ID", comment.getPublisher())
            }
            mContext.startActivity(intent)
        }

        holder.profileImage.setOnClickListener {

            val intent = Intent(mContext, MainActivity::class.java).apply {
                putExtra("PUBLISHER_ID", comment.getPublisher())
            }
            mContext.startActivity(intent)
        }
    }

    private fun publisherInfo(profileImage: CircleImageView, username: TextView, publisherID: String) {

        val userRef= FirebaseDatabase.getInstance().reference.child("Users").child(publisherID)
        userRef.addValueEventListener(object : ValueEventListener
        {
            override fun onCancelled(error: DatabaseError) {

            }

            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists())
                {
                    val user = snapshot.getValue<User>(User::class.java)

                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(profileImage)
                    username.text =(user.getUsername())
                }
            }

        })
    }
}