package com.myapp.travelize.abstractclasses

import android.view.View
import androidx.recyclerview.widget.RecyclerView

abstract class MessageViewHolder<in T>(itemView: View) : RecyclerView.ViewHolder(itemView)