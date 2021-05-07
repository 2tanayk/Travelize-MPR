package com.myapp.travelize.main.mainscreen

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.myapp.travelize.R
import com.myapp.travelize.main.MainHostActivity2.Companion.CHAT_GROUP_KEY

class ChatFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_chat, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val bundle = arguments
        if (bundle != null) {
            Toast.makeText(activity,"${bundle.getInt(CHAT_GROUP_KEY, -1)}",Toast.LENGTH_SHORT).show()
        }else{
            Log.e("bundle","is null")
        }
    }
}