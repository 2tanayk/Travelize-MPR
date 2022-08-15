package com.myapp.travelize.adapters

import android.text.Layout
import android.text.Spannable
import android.text.SpannableString
import android.text.style.AlignmentSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.firebase.ui.firestore.FirestoreRecyclerAdapter
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.myapp.travelize.R
import com.myapp.travelize.abstractclasses.MessageViewHolder
import com.myapp.travelize.models.Message
import java.text.SimpleDateFormat
import java.util.*


class MessageAdapter(
    options: FirestoreRecyclerOptions<Message>,
    val listener: OnItemClickListener
) :
    FirestoreRecyclerAdapter<Message, MessageViewHolder<*>>(options) {
    companion object {
        const val TEXT_MESSAGE = 0
        const val TYPE_MY_TEXT_MESSAGE = 0
        const val TYPE_OTHERS_TEXT_MESSAGE = 1
        const val IMG_MESSAGE = 1
        const val TYPE_MY_IMG_MESSAGE = 2
        const val TYPE_OTHERS_IMG_MESSAGE = 3
    }

    val currentUid = FirebaseAuth.getInstance().currentUser.uid

    interface OnMessageSendListener {
        fun onMessageSend(position: Int, snapshot: DocumentSnapshot)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder<*> {
        val context = parent.context
        return when (viewType) {
            TYPE_MY_TEXT_MESSAGE -> {
                val view =
                    LayoutInflater.from(context).inflate(R.layout.my_chat_item, parent, false)
                MyMessageViewHolder(view)
            }
            TYPE_OTHERS_TEXT_MESSAGE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.other_chat_item, parent, false)
                OtherMessageViewHolder(view)
            }
            TYPE_MY_IMG_MESSAGE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.my_image_item, parent, false)
                MyImageMessageViewHolder(view)
            }
            TYPE_OTHERS_IMG_MESSAGE -> {
                val view = LayoutInflater.from(parent.context)
                    .inflate(R.layout.other_image_item, parent, false)
                OtherImageMessageViewHolder(view)
            }
            else -> throw IllegalArgumentException("Invalid view type")
        }
    }

    override fun onBindViewHolder(holder: MessageViewHolder<*>, position: Int, model: Message) {
        Log.e("model", model.toString())
        Log.e("timestamp", model.sent.toString())
        val tempTime: Date = model.sent ?: Date()
        val styledMsgTimestamp = SpannableString(SimpleDateFormat("HH:mm").format(tempTime))
        styledMsgTimestamp.setSpan(
            AlignmentSpan.Standard(Layout.Alignment.ALIGN_OPPOSITE),
            0,
            styledMsgTimestamp.length,
            Spannable.SPAN_EXCLUSIVE_EXCLUSIVE
        )
        when (holder) {
            is MyMessageViewHolder -> {
                holder.outgoingMsgTextView.text = model.msg + "\n"
                holder.outgoingMsgTextView.append(styledMsgTimestamp)
            }
            is OtherMessageViewHolder -> {
                holder.incomingMsgTextView.text = model.senderName + "\n"
                holder.incomingMsgTextView.append(model.msg + "\n")
                holder.incomingMsgTextView.append(styledMsgTimestamp)
            }
            is MyImageMessageViewHolder -> {
                Glide.with(holder.outgoingImgMsgTextView.context)
                    .load(model.msg)
                    .placeholder(R.drawable.blankplaceholder)
                    .error(R.drawable.brokenplaceholder)
                    .fallback(R.drawable.brokenplaceholder)
                    .into(holder.outgoingImgMsgTextView)

                holder.timeStampTxt.text = styledMsgTimestamp
            }
            is OtherImageMessageViewHolder -> {
                holder.senderIdTxt.text = model.senderName

                Glide.with(holder.incomingImgMsgTextView.context)
                    .load(model.msg)
                    .placeholder(R.drawable.blankplaceholder)
                    .error(R.drawable.brokenplaceholder)
                    .fallback(R.drawable.brokenplaceholder)
                    .into(holder.incomingImgMsgTextView)

                holder.timeStampTxt.text = styledMsgTimestamp
            }
            else -> throw IllegalArgumentException()
        }
    }

    override fun getItemViewType(position: Int): Int {
        if (currentUid == snapshots[position].senderUid && snapshots[position].msgType == TEXT_MESSAGE) {
            return TYPE_MY_TEXT_MESSAGE
        } else if (currentUid != snapshots[position].senderUid && snapshots[position].msgType == TEXT_MESSAGE) {
            return TYPE_OTHERS_TEXT_MESSAGE
        } else if (currentUid == snapshots[position].senderUid && snapshots[position].msgType == IMG_MESSAGE) {
            return TYPE_MY_IMG_MESSAGE
        }
        return TYPE_OTHERS_IMG_MESSAGE
    }

    inner class MyMessageViewHolder(val view: View) : MessageViewHolder<Message>(view) {
        val outgoingMsgTextView: TextView = view.findViewById(R.id.outgoing_msg_txt)
    }

    inner class OtherMessageViewHolder(val view: View) : MessageViewHolder<Message>(view),
        View.OnClickListener {
        val incomingMsgTextView: TextView = view.findViewById(R.id.incoming_msg_txt)

        init {
            view.setOnClickListener(this)
        }

        override fun onClick(v: View?) {
            Log.e("others message", "clicked!")
            val position: Int = adapterPosition
            if (position != RecyclerView.NO_POSITION) {
                listener.onMessageClick(position, getItem(position))
            }
        }
    }

    inner class MyImageMessageViewHolder(val view: View) : MessageViewHolder<Message>(view) {
        val outgoingImgMsgTextView: ImageView = view.findViewById(R.id.outgoing_img)
        val timeStampTxt: TextView = view.findViewById(R.id.outgoing_time_stamp_txt)
    }

    inner class OtherImageMessageViewHolder(val view: View) : MessageViewHolder<Message>(view) {
        val senderIdTxt: TextView = view.findViewById(R.id.incoming_sender_id_txt)
        val incomingImgMsgTextView: ImageView = view.findViewById(R.id.incoming_img)
        val timeStampTxt: TextView = view.findViewById(R.id.incoming_time_stamp_txt)
    }

    interface OnItemClickListener {
        fun onMessageClick(position: Int, item: Message)
    }
}