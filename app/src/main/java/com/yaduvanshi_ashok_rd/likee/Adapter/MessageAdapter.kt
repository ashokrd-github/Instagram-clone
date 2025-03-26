package com.yaduvanshi_ashok_rd.likee.Adapter

import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import android.widget.VideoView
import androidx.annotation.RequiresApi
import androidx.core.graphics.toColor
import androidx.core.graphics.toColorInt
import androidx.databinding.adapters.ViewBindingAdapter.OnViewDetachedFromWindow
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.yaduvanshi_ashok_rd.likee.Activity.AddStoryActivity
import com.yaduvanshi_ashok_rd.likee.Activity.PostMessageActivity
import com.yaduvanshi_ashok_rd.likee.Activity.StoryActivity
import com.yaduvanshi_ashok_rd.likee.Activity.videoMessageActivity
import com.yaduvanshi_ashok_rd.likee.Model.Message
import com.yaduvanshi_ashok_rd.likee.Model.User
import com.yaduvanshi_ashok_rd.likee.R
import com.yaduvanshi_ashok_rd.likee.databinding.DeleteForReceiveBinding
import com.yaduvanshi_ashok_rd.likee.databinding.DeleteLayoutBinding
import kotlinx.datetime.toLocalDate

class MessageAdapter(
    var context: Context,
    messageList: ArrayList<Message>?,
    senderRoom: String,
    receiverRoom: String,

    ) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private var firebaseUser: FirebaseUser? = null
    lateinit var messageList: ArrayList<Message>
    var senderRoom: String
    var receiverRoom: String

    val ITEM_SENT = 1
    val ITEM_RECEVIE = 2


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType == 1) {
            // inflate receive
            val view = LayoutInflater.from(context).inflate(R.layout.reciever_layout, parent, false)
            return ReceiveViewHolder(view)
        } else {
            // inflate sent
            val view = LayoutInflater.from(context).inflate(R.layout.sender_layout, parent, false)
            return SentViewHolder(view)
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val currentMessage = messageList[position]
        firebaseUser = FirebaseAuth.getInstance().currentUser
        if (holder.javaClass == SentViewHolder::class.java) {
            //do the stuff for sent view holder
            val viewHolder = holder as SentViewHolder

            if (currentMessage.message.equals("photo")) {
                holder.sentImage.visibility = View.VISIBLE
                holder.sentMessage.visibility = View.GONE
                holder.receiveVideo.visibility = View.GONE
                Glide.with(context).load(currentMessage.imageUrl).placeholder(R.drawable.loading)
                    .into(holder.sentImage)

            }
            if (currentMessage.message.equals("video")) {
                holder.sentImage.visibility = View.GONE
                holder.sentMessage.visibility = View.GONE
                holder.receiveTIme.visibility = View.GONE
                holder.receiveFrame.visibility = View.VISIBLE
                holder.receiveVideo.setVideoPath(currentMessage.videoUrl)
                holder.receiveVideo.setOnPreparedListener { mp ->
                    mp.isLooping = true
                    mp.stop()
                    holder.RpReel.visibility = View.GONE
                }

            }
            holder.receiveVideo.setOnClickListener {
                val intent = Intent(context, videoMessageActivity::class.java)
                intent.putExtra("video", currentMessage.videoUrl)
                intent.putExtra("uid", currentMessage.senderId)
                context.startActivity(intent)
            }
            holder.sentImage.setOnClickListener {
                val intent = Intent(context, PostMessageActivity::class.java)
                intent.putExtra("image", currentMessage.imageUrl)
                intent.putExtra("uid", currentMessage.senderId)
                context.startActivity(intent)
            }

            holder.sentMessage.text = currentMessage.message
            holder.receiveTIme.text = java.sql.Timestamp(currentMessage.timestamp).toString()
            holder.receiveTIme.visibility = View.VISIBLE

            viewHolder.itemView.setOnLongClickListener {
                val view = LayoutInflater.from(context).inflate(R.layout.delete_for_receive, null)
                val binding: DeleteForReceiveBinding = DeleteForReceiveBinding.bind(view)
                val dialog = AlertDialog.Builder(context)
                    .setTitle("Are you want to delete message ?")
                    .setView(binding.root)
                    .create()
                binding.delete.setOnClickListener {
                    currentMessage.messageId?.let { it1 ->
                        FirebaseDatabase.getInstance().reference.child("chats")
                            .child(senderRoom).child("messages").child(it1).setValue(null)
                    }
                    dialog.dismiss()
                }
                binding.cancelReceive.setOnClickListener {
                    dialog.dismiss()
                }

                dialog.show()
                false
            }

        } else {
            val viewHolder = holder as ReceiveViewHolder
            if (currentMessage.message.equals("photo")) {
                holder.receiveImage.visibility = View.VISIBLE
                holder.receiveMessage.visibility = View.GONE
                holder.sentVideo.visibility = View.GONE
                Glide.with(context).load(currentMessage.imageUrl).placeholder(R.drawable.loading)
                    .into(holder.receiveImage)
            }
            if (currentMessage.message.equals("video")) {
                holder.receiveImage.visibility = View.GONE
                holder.receiveMessage.visibility = View.GONE
                holder.sentTime.visibility = View.GONE
                holder.sentFrame.visibility = View.VISIBLE
                holder.sentVideo.setVideoPath(currentMessage.videoUrl)
                holder.sentVideo.setOnPreparedListener {

                    holder.sentPreel.visibility = View.GONE
                }
            }
            holder.receiveMessage.text = currentMessage.message
            holder.sentTime.text = java.sql.Timestamp(currentMessage.timestamp).toString()
            holder.sentTime.visibility = View.VISIBLE

            holder.sentVideo.setOnClickListener {
                val intent = Intent(context, videoMessageActivity::class.java)
                intent.putExtra("video", currentMessage.videoUrl)
                intent.putExtra("uid", currentMessage.senderId)
                context.startActivity(intent)
            }
            holder.receiveImage.setOnClickListener {
                val intent = Intent(context, PostMessageActivity::class.java)
                intent.putExtra("image", currentMessage.imageUrl)
                intent.putExtra("uid", currentMessage.senderId)
                context.startActivity(intent)
            }

            viewHolder.itemView.setOnLongClickListener {
                val view = LayoutInflater.from(context).inflate(R.layout.delete_layout, null)
                val binding: DeleteLayoutBinding = DeleteLayoutBinding.bind(view)
                val dialog = AlertDialog.Builder(context)
                    .setTitle("Delete message ?")
                    .setView(binding.root)
                    .create()
                binding.everyone.setOnClickListener {
//                    currentMessage.message =
                    currentMessage.messageId?.let { it1 ->
                        currentMessage.message = ("🚫 You deleted this message")
                        FirebaseDatabase.getInstance().reference.child("chats")
                            .child(senderRoom)
                            .child("messages")
                            .child(it1).setValue(currentMessage)
                    }
                    currentMessage.messageId.let { it1 ->
                        currentMessage.message = ("🚫 This message was deleted")
                        FirebaseDatabase.getInstance().reference.child("chats")
                            .child(receiverRoom)
                            .child("messages")
                            .child(it1!!).setValue(currentMessage)
                    }
                    dialog.dismiss()

                }
                binding.delete.setOnClickListener {
                    currentMessage.messageId.let { it1 ->
                        FirebaseDatabase.getInstance().reference.child("chats")
                            .child(senderRoom)
                            .child("messages")
                            .child(it1!!).setValue(null)
                    }
                    dialog.dismiss()
                }
                binding.cancel.setOnClickListener {
                    dialog.dismiss()
                }
                dialog.show()
                false
            }
        }


    }

    override fun getItemViewType(position: Int): Int {
        val currentMessage = messageList[position]
        if (FirebaseAuth.getInstance().currentUser!!.uid.equals(currentMessage.senderId)) {
            return ITEM_SENT
        } else {
            return ITEM_RECEVIE
        }

    }

    override fun getItemCount(): Int {
        return messageList.size
    }


    inner class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sentMessage = itemView.findViewById<TextView>(R.id.sent_message)
        val receiveTIme: TextView = itemView.findViewById(R.id.receiveTime)
        val sentImage: ImageView = itemView.findViewById(R.id.receive_image)
        val receiveVideo: VideoView = itemView.findViewById(R.id.receive_videoView)
        val receiveFrame: FrameLayout = itemView.findViewById(R.id.receiveFrame)
        val RpReel: ProgressBar = itemView.findViewById(R.id.Rp_reel)


    }

    inner class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receiveMessage = itemView.findViewById<TextView>(R.id.receive_message)
        val sentTime: TextView = itemView.findViewById(R.id.sentTime)
        val receiveImage: ImageView = itemView.findViewById(R.id.sent_image)
        val sentVideo: VideoView = itemView.findViewById(R.id.sent_videoView)
        val sentFrame: FrameLayout = itemView.findViewById(R.id.sent_frame)
        val sentPreel: ProgressBar = itemView.findViewById(R.id.sentP_reel)
    }

    init {
        if (messageList != null) {
            this.messageList = messageList
        }
        this.senderRoom = senderRoom
        this.receiverRoom = receiverRoom
    }

}