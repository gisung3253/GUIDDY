package com.flius.guiddy

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide

class FoPostListAdapter(private val context: Context, private val postList: ArrayList<Pair<Profile, Intro>>):
    RecyclerView.Adapter<FoPostListAdapter.PostViewHolder>() {

    class PostViewHolder(itemView: View): RecyclerView.ViewHolder(itemView) {
        val nickname: TextView = itemView.findViewById(R.id.profile_nikname)
        val free: TextView = itemView.findViewById(R.id.profile_free)
        val pimage: ImageView = itemView.findViewById(R.id.profile_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.post_layout, parent, false)
        return PostViewHolder(view)
    }

    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
        val (profile, intro) = postList[position]
        holder.nickname.text = profile.nickname
        holder.free.text = intro.free
        // 이미지 로드 (Glide 예시)
        Glide.with(context).load(profile.profileImageUrl).into(holder.pimage)

        // 아이템 클릭 이벤트
        holder.itemView.setOnClickListener {
            val intent = Intent(context, FoPostDetailActivity::class.java)
            intent.putExtra("nickname", profile.nickname)
            intent.putExtra("free", intro.free)
            intent.putExtra("image", profile.profileImageUrl)
            intent.putExtra("uid", profile.uid)
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return postList.size
    }
}
