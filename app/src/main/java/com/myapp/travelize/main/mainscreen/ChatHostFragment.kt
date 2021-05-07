package com.myapp.travelize.main.mainscreen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.myapp.travelize.Constants.Companion.ACTION_CHAT_GROUP_SELECTED
import com.myapp.travelize.Constants.Companion.ACTION_KEY
import com.myapp.travelize.R
import com.myapp.travelize.adapters.ChatAdapter
import com.myapp.travelize.interfaces.FragmentActionListener
import com.myapp.travelize.main.MainHostActivity2.Companion.CHAT_GROUP_KEY
import com.myapp.travelize.models.Chat


class ChatHostFragment : Fragment(), ChatAdapter.OnItemClickListener {
    lateinit var chatsAdapter: ChatAdapter
    lateinit var chatsRecyclerView: RecyclerView
    lateinit var fragmentActionListener: FragmentActionListener
    val chatList = mutableListOf<Chat>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        chatList.add(Chat("", "Place1", "", "hello", "3", "11:40"))
        chatList.add(Chat("", "Place2", "", "hello", "3", "11:40"))
        chatList.add(Chat("", "Place3", "", "hello", "3", "11:40"))
        chatList.add(Chat("", "Place4", "", "hello", "3", "11:40"))
        chatList.add(Chat("", "Place5", "", "hello", "3", "11:40"))
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

        chatsAdapter.submitList(chatList)
    }

    override fun onItemClick(position: Int) {
        Log.e("chat group", "clicked ${position}")
        if(this::fragmentActionListener.isInitialized){
            val bundle=Bundle()
            bundle.putInt(ACTION_KEY, ACTION_CHAT_GROUP_SELECTED)
            bundle.putInt(CHAT_GROUP_KEY,position)
            fragmentActionListener.onActionCallBack(bundle)
        }else{
            Log.e("fragmentActionListener","is not initialized")
        }
    }
}