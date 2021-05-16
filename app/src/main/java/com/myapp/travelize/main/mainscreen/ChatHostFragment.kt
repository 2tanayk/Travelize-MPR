package com.myapp.travelize.main.mainscreen

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.myapp.travelize.R
import com.myapp.travelize.adapters.ChatAdapter
import com.myapp.travelize.adapters.PlaceAdapter
import com.myapp.travelize.models.Chat
import com.myapp.travelize.models.Place


class ChatHostFragment : Fragment(), ChatAdapter.OnItemClickListener {
    lateinit var chatsAdapter: ChatAdapter
    lateinit var chatsRecyclerView: RecyclerView
    val chatList = mutableListOf<Chat>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chat_host, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatsRecyclerView = view.findViewById(R.id.chats_recycler_view)
        chatsAdapter = ChatAdapter(this)
        chatsRecyclerView.setHasFixedSize(true)
        chatsRecyclerView.adapter = chatsAdapter
//        chatList.add(Chat("", "Place1", "", "hello", "3", "11:40"))
//        chatList.add(Chat("", "Place2", "", "hello", "3", "11:40"))
//        chatList.add(Chat("", "Place3", "", "hello", "3", "11:40"))
//        chatList.add(Chat("", "Place4", "", "hello", "3", "11:40"))
//        chatList.add(Chat("", "Place5", "", "hello", "3", "11:40"))

//        chatsAdapter.submitList(chatList)
    }

    override fun onItemClick(position: Int) {
        Log.e("chat group", "clicked ${position}")
    }
}