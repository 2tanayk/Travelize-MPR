package com.myapp.travelize.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.firestore.DocumentSnapshot
import com.myapp.travelize.Keys
import com.myapp.travelize.R
import com.myapp.travelize.models.Chat

class ChatAdapter(val listener: OnItemClickListener, options: FirestoreRecyclerOptions<Chat>) :
    FirestoreRecyclerAdapter<Chat, ChatAdapter.ChatViewHolder>(options) {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatViewHolder {
        val chatItemView = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.chat_item, parent, false)
        return ChatViewHolder(chatItemView)
    }

    override fun onBindViewHolder(holder: ChatViewHolder, position: Int,model: Chat) {
        Glide.with(holder.groupIconImg.getContext())
            .load("https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=${model.url.toString()}&key=${Keys.apiKey()}")
            .placeholder(R.drawable.blankplaceholder)
            .error(R.drawable.brokenplaceholder)
            .fallback(R.drawable.brokenplaceholder)
            .into(holder.groupIconImg)
        holder.groupNameTxt.text = model.name
        holder.unreadMsgTxt.text = model.lastUnreadMsg ?: ""
        holder.unreadCtTxt.text = model.unreadMsgCt ?: ""
        holder.timestampTxt.text = model.lastUnreadMsgTimestamp ?: ""
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
                listener.onItemClick(position, snapshots.getSnapshot(position))
            }
        }
    }

    interface OnItemClickListener {
        fun onItemClick(position: Int, snapshot: DocumentSnapshot)
    }
}