package com.example.red_v1.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.red_v1.R
import com.example.red_v1.listeners.RedListener
import com.example.red_v1.util.Red
import com.example.red_v1.util.getDate
import com.example.red_v1.util.loadUrl
import com.google.android.material.tabs.TabLayout.OnTabSelectedListener


class RedListAdapter(val userId:String, val reds:ArrayList<Red>):RecyclerView.Adapter<RedListAdapter.RedViewHolder>() {

    private var listener:RedListener? =null
    fun setListener(listener:RedListener?){
        this.listener = listener
    }

    fun updateReds(newReds:List<Red>){
        reds.clear()
        reds.addAll(newReds)
        notifyDataSetChanged()
    }

    class RedViewHolder(v: View): RecyclerView.ViewHolder(v) {
        private val layout = v.findViewById<ViewGroup>(R.id.redLayout)
        private val username = v.findViewById<TextView>(R.id.redUsername)
        private val text = v.findViewById<TextView>(R.id.redText)
        private val image = v.findViewById<ImageView>(R.id.redImage)
        private val date = v.findViewById<TextView>(R.id.redDate)
        private val like = v.findViewById<ImageView>(R.id.redLike)
        private val likeCount = v.findViewById<TextView>(R.id.redLikeCount)
        private val repost = v.findViewById<ImageView>(R.id.redRepost)
        private val repostCount = v.findViewById<TextView>(R.id.redRepostCount)

        fun bind(userId: String, red: Red, listener: RedListener?) {
            username.text = red.username
            text.text = red.text
            if(red.imageUrl.isNullOrEmpty()){
                image.visibility = View.GONE
            }else{
                image.visibility = View.VISIBLE
                image.loadUrl(red.imageUrl)
            }

            date.text = getDate(red.timestamp)
            likeCount.text = red.likes?.size.toString()
            repostCount.text = red.userIds?.size?.minus(1).toString()

            layout.setOnClickListener{listener?.onLayoutClick(red)}
            like.setOnClickListener{listener?.onLike(red)}
            repost.setOnClickListener{listener?.onRepost(red)}
            if(red.likes?.contains(userId) == true){
                like.setImageDrawable(ContextCompat.getDrawable(like.context,R.drawable.like))
            }else{
                like.setImageDrawable(ContextCompat.getDrawable(like.context,R.drawable.like_inactive))
            }

            if(red.userIds?.get(0).equals(userId)){
                repost.setImageDrawable(ContextCompat.getDrawable(like.context, R.drawable.original))
                repost.isClickable= false
            }else if(red.userIds?.contains(userId) == true){
                repost.setImageDrawable(ContextCompat.getDrawable(like.context, R.drawable.retweet))
            }else{
                repost.setImageDrawable(ContextCompat.getDrawable(like.context, R.drawable.retweet_inactive))
            }



        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) = RedViewHolder(
        LayoutInflater.from(parent.context).inflate(R.layout.item_red,parent,false)
    )
    override fun getItemCount() = reds.size

    override fun onBindViewHolder(holder: RedViewHolder, position: Int) {
        holder.bind(userId,reds[position],listener)
    }
}