package com.myapp.travelize.main.mainscreen

import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.firebase.ui.firestore.FirestoreRecyclerOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.FirebaseFirestore
import com.myapp.travelize.Constants.Companion.ACTION_CHAT_GROUP_SELECTED
import com.myapp.travelize.Constants.Companion.ACTION_KEY
import com.myapp.travelize.R
import com.myapp.travelize.adapters.ChatAdapter
import com.myapp.travelize.interfaces.FragmentActionListener
import com.myapp.travelize.main.MainHostActivity2.Companion.CHAT_DOC_REF
import com.myapp.travelize.main.MainHostActivity2.Companion.CHAT_GROUP_KEY
import com.myapp.travelize.models.Chat


class ChatHostFragment : Fragment(), ChatAdapter.OnItemClickListener {
    lateinit var chatsAdapter: ChatAdapter
    lateinit var chatsRecyclerView: RecyclerView
    lateinit var chatsProgressBar: ProgressBar
    lateinit var fragmentActionListener: FragmentActionListener
    val db = FirebaseFirestore.getInstance()
    val firebaseAuth = FirebaseAuth.getInstance()
    val chatList = mutableListOf<Chat>()
//fragment state for progress bar needs to be handled here
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
            setUpChatHostRecyclerView()
//        chatList.add(Chat("", "Place1", "", "hello", "3", "11:40"))
//        chatList.add(Chat("", "Place2", "", "hello", "3", "11:40"))
//        chatList.add(Chat("", "Place3", "", "hello", "3", "11:40"))
//        chatList.add(Chat("", "Place4", "", "hello", "3", "11:40"))
//        chatList.add(Chat("", "Place5", "", "hello", "3", "11:40"))
    }

    private fun setUpChatHostRecyclerView() {
        val query=db.collectionGroup("chats").whereArrayContains("members",firebaseAuth.currentUser.uid)
        query.get().addOnSuccessListener {
            for(document in it)
            {
                Log.e("doc value", document.data.toString())
            }
        }.addOnFailureListener {
            it.printStackTrace()
            Log.e("CollectionGroup",it.toString())
        }
        val options: FirestoreRecyclerOptions<Chat> = FirestoreRecyclerOptions.Builder<Chat>()
            .setQuery(query, Chat::class.java)
            .build()
        chatsAdapter = ChatAdapter(this,options)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_chat_host, container, false)
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        chatsRecyclerView = view.findViewById(R.id.chats_recycler_view)
        chatsProgressBar=view.findViewById(R.id.chats_progress_bar)
        chatsRecyclerView.setHasFixedSize(true)
        chatsRecyclerView.adapter = chatsAdapter
        chatsProgressBar.visibility=View.GONE
    }

    override fun onItemClick(position: Int, snapshot: DocumentSnapshot) {
        Log.e("chat group", "clicked ${position}")
        Log.e("ChatGroupDoc", snapshot.data.toString())

        if(this::fragmentActionListener.isInitialized){
            val bundle=Bundle()
            bundle.putInt(ACTION_KEY, ACTION_CHAT_GROUP_SELECTED)
            bundle.putInt(CHAT_GROUP_KEY,position)
            bundle.putString(CHAT_DOC_REF, snapshot.reference.path)
            fragmentActionListener.onActionCallBack(bundle)
        }else{
            Log.e("fragmentActionListener","is not initialized in chat host fragment")
        }
    }

    override fun onStart() {
        super.onStart()
        chatsAdapter.startListening()
    }

    override fun onStop() {
        super.onStop()
        chatsAdapter.stopListening()
    }
}