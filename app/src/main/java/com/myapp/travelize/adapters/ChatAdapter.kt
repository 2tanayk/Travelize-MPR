package com.myapp.travelize.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.myapp.travelize.Keys
import com.myapp.travelize.R
import com.myapp.travelize.adapters.utils.ChatDiffUtil
import com.myapp.travelize.models.Chat

class ChatAdapter(val listener: OnItemClickListener) :
    ListAdapter<Chat, ChatAdapter.ChatViewHolder>(ChatDiffUtil()) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val chatItemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.chat_item, parent, false)
        return ChatViewHolder(chatItemView)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int) {
        val chat = getItem(position)
        Glide.with(holder.groupIconImg.getContext())
            .load(R.drawable.blankuser)
            .placeholder(R.drawable.blankplaceholder)
            .error(R.drawable.brokenplaceholder).centerInside()
            .fallback(R.drawable.brokenplaceholder).centerInside()
            .into(holder.groupIconImg)
        holder.groupNameTxt.text=chat.name
        holder.unreadMsgTxt.text=chat.lastUnreadMsg
        holder.unreadCtTxt.text=chat.unreadMsgCt
        holder.timestampTxt.text=chat.lastUnreadMsgTimestamp
    }

    inner class ChatViewHolder(private val view: View) : RecyclerView.ViewHolder(view),
        View.OnClickListener {
        val groupIconImg: ShapeableImageView = view.findViewById(R.id.chat_icon_img)
        val groupNameTxt: TextView = view.findViewById(R.id.chat_title_txt)
        val unreadMsgTxt: TextView = view.findViewById(R.id.unread_msg_txt)
        val timestampTxt: TextView = view.findViewById(R.id.chat_timestamp_txt)
        val unreadCtTxt: TextView = view.findViewById(R.id.unread_ct_txt)

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            val position: Int = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onItemClick(position)
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }
}