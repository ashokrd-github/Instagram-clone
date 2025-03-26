package com.yaduvanshi_ashok_rd.likee.Adapter

import android.content.Context
import android.media.AudioAttributes
import android.media.AudioManager
import android.media.audiofx.AudioEffect
import android.net.Uri
import android.os.Build
import android.os.Handler
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.MediaController
import android.widget.ProgressBar
import android.widget.VideoView
import androidx.annotation.RequiresApi
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.yaduvanshi_ashok_rd.likee.Model.Reel
import com.yaduvanshi_ashok_rd.likee.R
import com.yaduvanshi_ashok_rd.likeee.fragments.ReelsFragment

class MyReelAdapter(
    private val mContext: Context,
    private val reelList:List<Reel>): RecyclerView.Adapter<MyReelAdapter.viewHolder>() {

    class viewHolder(itemView:View): RecyclerView.ViewHolder(itemView){
        val reelVideoView:VideoView = itemView.findViewById(R.id.my_posted_reel)
        val progressBar:ProgressBar = itemView.findViewById(R.id.p_reel)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): viewHolder {
        val view = LayoutInflater.from(mContext).inflate(R.layout.my_reel_layout, parent, false)
        return viewHolder(view)
    }

    override fun getItemCount(): Int {
        return reelList.size
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: viewHolder, position: Int) {
        val reel = reelList[position]
        holder.reelVideoView.setVideoPath(reel.getReels())
        holder.reelVideoView.setOnPreparedListener {
            holder.progressBar.visibility = View.GONE
            holder.reelVideoView.start()
            holder.reelVideoView.postDelayed({
                holder.reelVideoView.pause()
            }, 1500)

        }

        holder.reelVideoView.setOnClickListener {

            (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, ReelsFragment()).commit()

        }

    }
}