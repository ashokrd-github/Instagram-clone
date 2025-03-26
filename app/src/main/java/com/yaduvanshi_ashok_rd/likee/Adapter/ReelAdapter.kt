package com.yaduvanshi_ashok_rd.likee.Adapter

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.MediaController
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.Toast
import android.widget.VideoView
import androidx.recyclerview.widget.RecyclerView
import androidx.transition.Visibility
import com.bumptech.glide.Glide
import com.google.common.io.Files
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.squareup.picasso.Picasso
import com.yaduvanshi_ashok_rd.likee.Activity.ReelCommentActivity
import com.yaduvanshi_ashok_rd.likee.AddCommentActivity
import com.yaduvanshi_ashok_rd.likee.Model.Reel
import com.yaduvanshi_ashok_rd.likee.Model.User
import com.yaduvanshi_ashok_rd.likee.R
import java.io.File
import java.util.Date

class ReelAdapter(private val context: Context,private val reelList: List<Reel>): RecyclerView.Adapter<ReelAdapter.ViewHolder>() {

    private var firebaseUser: FirebaseUser? = null


    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val userName: TextView = itemView.findViewById(R.id.userName)
        val reelCaption: TextView = itemView.findViewById(R.id.reelCaption)
        val videoView: VideoView = itemView.findViewById(R.id.videoView)
        val progressBar: ProgressBar = itemView.findViewById(R.id.progressbar)
        val userProfile: ImageView = itemView.findViewById(R.id.userProfile)
        val reelLike:ImageView = itemView.findViewById(R.id.like_reel)
        val reelComment:ImageView = itemView.findViewById(R.id.comment_reel)
        val shareReel:ImageView = itemView.findViewById(R.id.share_reel)
        val saveReel:ImageView = itemView.findViewById(R.id.save_reel)
        val reelLikeCount:TextView = itemView.findViewById(R.id.reel_like_count)
        val reelCommentCount:TextView = itemView.findViewById(R.id.reel_comment_count)
//        val reelShareCount:TextView = itemView.findViewById(R.id.reel_share_count)
        val playbtn:ImageView = itemView.findViewById(R.id.play_btn)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.reel_layout, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return reelList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        firebaseUser = FirebaseAuth.getInstance().currentUser
        val currentReel = reelList[position]
        val reelid = currentReel.getReelId()
        holder.reelCaption.text = currentReel.getCaption()
        publisherInfo(holder.userProfile, holder.userName, currentReel.getPublisher())
        isLiked(currentReel.getReelId(), holder.reelLike, holder.videoView)
        getLikesOfCount(currentReel.getReelId(), holder.reelLikeCount)
        isSaved(currentReel.getReelId(), holder.saveReel)
        getCommentOfCount(currentReel.getReelId(), holder.reelCommentCount)

        holder.userName.text = currentReel.getPublisher()
        holder.videoView.setVideoPath(currentReel.getReels())
        holder.videoView.setOnPreparedListener {
            holder.progressBar.visibility = View.GONE
            holder.videoView.start()
            holder.videoView.onStartTemporaryDetach()

        }
   holder.videoView.setOnClickListener {
       if (holder.videoView.isPlaying){
           holder.videoView.pause()
           holder.playbtn.visibility = View.VISIBLE
       }
       else{
           holder.videoView.start()
           holder.playbtn.visibility = View.GONE
       }
   }




        holder.shareReel.setOnClickListener {
            val reels = currentReel.getReels() // Assume this returns a List<String> of file paths
            val imageUris = ArrayList<Uri>()

//            for (reelPath in reels) {
//                val file = File(reelPath)
//                val uri = Uri.fromFile(file)
//                imageUris.add(uri)
//            }

            val intent = Intent(Intent.ACTION_SEND_MULTIPLE)
            intent.type = "image/*" //or "video/*" or "*/*", depending on file type.
            intent.putParcelableArrayListExtra(Intent.EXTRA_STREAM, imageUris)
            context.startActivity(Intent.createChooser(intent, "Share Reels"))
        }
        holder.reelLike.setOnClickListener {
            if (holder.reelLike.tag.toString() == "like") {
                FirebaseDatabase.getInstance().reference.child("ReelLikes")
                    .child(currentReel.getReelId())
                    .child(firebaseUser!!.uid)
                    .setValue(true)
                pushNotification(currentReel.getReelId(), currentReel.getPublisher())
            } else {
                FirebaseDatabase.getInstance().reference.child("ReelLikes")
                    .child(currentReel.getReelId())
                    .child(firebaseUser!!.uid)
                    .removeValue()
            }
        }
        holder.reelComment.setOnClickListener {
            val intent = Intent(context, ReelCommentActivity::class.java).apply {
                putExtra("reelid", reelid)
            }
            context.startActivity(intent)
        }
        
//        holder.saveReel.setOnClickListener {
//            if (holder.saveReel.tag == "Save"){
//                FirebaseDatabase.getInstance().reference.child("SaveReels").child(firebaseUser!!.uid)
//                    .child(currentReel.getReelId()).setValue(true)
//                Toast.makeText(context, "Saved your reel!!", Toast.LENGTH_SHORT).show()
//            }else{
//                FirebaseDatabase.getInstance().reference.child("SaveReels").child(firebaseUser!!.uid)
//                    .child(currentReel.getReelId()).removeValue()
//                Toast.makeText(context, "unsaved your reel !!", Toast.LENGTH_SHORT).show()
//            }
//        }

        holder.saveReel.setOnClickListener {
            if (holder.saveReel.tag == "Save"){
                FirebaseDatabase.getInstance().reference.child("ReelSaves").child(firebaseUser!!.uid)
                    .child(currentReel.getReelId()).setValue(true)
                Toast.makeText(context, "saved!!", Toast.LENGTH_SHORT).show()
            }else{
                FirebaseDatabase.getInstance().reference.child("ReelSaves").child(firebaseUser!!.uid)
                    .child(currentReel.getReelId()).removeValue()
                Toast.makeText(context, "Unsaved!!", Toast.LENGTH_SHORT).show()
            }
        }

    }
    private fun getCommentOfCount(reelid: String, commentNo:TextView){
        val userRef = FirebaseDatabase.getInstance().reference.child("ReelsComment").child(reelid)
        userRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                commentNo.text = snapshot.childrenCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {}

        })

    }


        private fun pushNotification(reelid: String, userid: String) {

            val ref = FirebaseDatabase.getInstance().reference.child("Notification").child(userid)

            val notifyMap = HashMap<String, Any>()
            notifyMap["userid"] = FirebaseAuth.getInstance().currentUser!!.uid
            notifyMap["text"] = "♥liked your reel♥"
            notifyMap["reelid"] = reelid
            notifyMap["ispost"] = true
            notifyMap["timestamp"] = Date().time
            ref.push().setValue(notifyMap)
        }


        private fun isLiked(reelid: String, postedView:ImageView, postedVideo:VideoView) {

            firebaseUser = FirebaseAuth.getInstance().currentUser
            val postRef = FirebaseDatabase.getInstance().reference.child("ReelLikes").child(reelid)

            postRef.addValueEventListener(object : ValueEventListener {
                override fun onCancelled(error: DatabaseError) {

                }

                override fun onDataChange(datasnapshot: DataSnapshot) {
                    if (datasnapshot.child(firebaseUser!!.uid).exists()) {
                        postedView.setImageResource(R.drawable.fill_heart)
                        postedVideo.tag = " liked"
                        postedView.tag = "liked"
                    } else {
                        postedView.setImageResource(R.drawable.reel_favourite)
                        postedVideo.tag = "like"
                        postedView.tag = "like"
                    }
                }
            })
        }
    private fun getLikesOfCount(reelid: String, likesNo:TextView){
        val userRef = FirebaseDatabase.getInstance().reference.child("ReelLikes").child(reelid)
        userRef.addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                likesNo.text = snapshot.childrenCount.toString()
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }
    
    private fun isSaved(reelid: String, reelSave:ImageView){
        val userRef = FirebaseDatabase.getInstance().reference.child("ReelSaves").child(firebaseUser!!.uid)
        userRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.child(reelid).exists()){
                    reelSave.setImageResource(R.drawable.save_filled)
                    reelSave.tag = "Saved"
                }else{
                    reelSave.setImageResource(R.drawable.save)
                    reelSave.tag = "Save"
                }
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }


    }
    private fun publisherInfo(
        userProfile:ImageView,
        userName:TextView,
        publisherId:String,
    ){
        val userRef = FirebaseDatabase.getInstance().reference.child("Users").child(publisherId)
        userRef.addValueEventListener(object:ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()){
                    val user = snapshot.getValue(User::class.java)
                    Picasso.get().load(user!!.getImage()).placeholder(R.drawable.profile).into(userProfile)
                    userName.text = user.getUsername()
                }
            }

            override fun onCancelled(error: DatabaseError) {}

        })
    }
