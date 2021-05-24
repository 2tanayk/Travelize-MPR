package com.myapp.travelize.adapters.utils

import androidx.recyclerview.widget.DiffUtil
import com.myapp.travelize.models.Chat

class ChatDiffUtil : DiffUtil.ItemCallback<Chat>() {
    override fun areItemsTheSame(oldItem: Chat, newItem: Chat): Boolean {
        return oldItem.id.equals(newItem.id)
    }

    override fun areContentsTheSame(oldItem: Chat, newItem: Chat): Boolean {
        return oldItem.name.equals(newItem.name) &&
                oldItem.lastUnreadMsg.equals(newItem.lastUnreadMsg) &&
                oldItem.lastUnreadMsgTimestamp.equals(newItem.lastUnreadMsgTimestamp) &&
                oldItem.url.equals(newItem.url) &&
                oldItem.unreadMsgCt.equals(newItem.unreadMsgCt)

    }
}