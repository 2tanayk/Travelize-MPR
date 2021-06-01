package com.myapp.travelize.adapters

import android.text.Layout
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AlignmentSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.myapp.travelize.R
import com.myapp.travelize.abstractclasses.MessageViewHolder
import com.myapp.travelize.models.Message
import java.text.SimpleDateFormat


class MessageAdapter(options: FirestoreRecyclerOptions<Message>) :
    FirestoreRecyclerAdapter<Message, MessageViewHolder<*>>(options) {
    companion object{
        const val TYPE_MY_TEXT_MESSAGE=0
        const val TYPE_OTHERS_TEXT_MESSAGE=1
        const val TEXT_MESSAGE=0
    }
    val currentUid = FirebaseAuth.getInstance().currentUser.uid
    interface OnMessageSendListener {
        fun onMessageSend(position: Int, snapshot: DocumentSnapshot)
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder<*> {
        val context=parent.context
        return when (viewType) {
            TYPE_MY_TEXT_MESSAGE -> {
                val view = LayoutInflater.from(context).inflate(R.layout.my_chat_item, parent, false)
                MyMessageViewHolder(view)
            }
            TYPE_OTHERS_TEXT_MESSAGE -> {
                val view = LayoutInflater.from(parent.context).inflate(R.layout.other_chat_item, parent, false)
                OtherMessageViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder<*>, position: Int, model: Message) {
        Log.e("model",model.toString())
        val styledMsgTimestamp = SpannableString(SimpleDateFormat("HH:mm").format(model.sent))
        styledMsgTimestamp.setSpan(AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE),0,styledMsgTimestamp.length,Spannable.SPAN_EXCLUSIVE_EXCLUSIVE)
        when (holder) {
            is MyMessageViewHolder ->{
                holder.outgoingMsgTextView.text=model.msg+"\n"
                holder.outgoingMsgTextView.append(styledMsgTimestamp)
            }
            is OtherMessageViewHolder -> {
                holder.incomingMsgTextView.text=model.senderName+"\n"
                holder.incomingMsgTextView.append(model.msg+"\n")
                holder.incomingMsgTextView.append(styledMsgTimestamp)
            }
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemViewType(position: Int): Int {
        if(currentUid==snapshots[position].senderUid && snapshots[position].msgType== TEXT_MESSAGE) {
            return TYPE_MY_TEXT_MESSAGE
        }
        return TYPE_OTHERS_TEXT_MESSAGE
    }

    inner class MyMessageViewHolder(val view: View) : MessageViewHolder<Message>(view) {
        val outgoingMsgTextView: TextView = view.findViewById(R.id.outgoing_msg_txt)
    }

    inner class OtherMessageViewHolder(val view: View) : MessageViewHolder<Message>(view) {
        val incomingMsgTextView: TextView = view.findViewById(R.id.incoming_msg_txt)
    }
}