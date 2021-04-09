package com.myapp.travelize.main.createprofile

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import com.myapp.travelize.R

class CreateProfileFragment3 : Fragment() {
    lateinit var uploadBtn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_create_profile3, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        uploadBtn = view.findViewById(R.id.uploadImageBtn)
        uploadBtn.setOnClickListener {
//            Log.e("CreateProfileFragment3", "clicked!")

        }
    }
}