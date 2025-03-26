package com.yaduvanshi_ashok_rd.likee.Adapter

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.squareup.picasso.Picasso
import com.yaduvanshi_ashok_rd.likee.Activity.postDetailActivity
import com.yaduvanshi_ashok_rd.likee.Fragments.PostDetailsFragment
import com.yaduvanshi_ashok_rd.likee.Model.Post
import com.yaduvanshi_ashok_rd.likee.R

class MyPostAdapter(private val mContext: Context, private  val mPost:List<Post>): RecyclerView.Adapter<MyPostAdapter.ViewHolder>() {

    inner class ViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView)
    {
        var postedImg: ImageView
        init
        {
            postedImg = itemView.findViewById(R.id.my_posted_picture)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view= LayoutInflater.from(mContext).inflate(R.layout.my_post_layout,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return mPost.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val post=mPost[position]

     Glide.with(mContext).load(post.getPostImage()).into(holder.postedImg)
        holder.postedImg.setOnClickListener {

            val pref=mContext.getSharedPreferences("PREFS",Context.MODE_PRIVATE).edit()
            pref.putString("postid",post.getPostId())
            pref.apply()

            val intent = Intent(mContext    , postDetailActivity::class.java).apply {
                putExtra("postid",  post.getPostId() )
            }
            mContext.startActivity(intent)
//            (mContext as FragmentActivity).supportFragmentManager.beginTransaction()
//                .replace(R.id.fragment_container, PostDetailsFragment()).commit()
        }
    }
}