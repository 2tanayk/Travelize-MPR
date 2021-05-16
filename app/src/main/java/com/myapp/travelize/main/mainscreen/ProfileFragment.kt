package com.myapp.travelize.main.mainscreen

import android.annotation.SuppressLint
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.myapp.travelize.R

class ProfileFragment : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    @SuppressLint("LongLogTag")
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view=inflater.inflate(R.layout.fragment_profile, container, false)
        Log.e("ProfileFragment", "created")
        Log.e("ProfileFragment onCreateView","called!")
        return view
    }

    @SuppressLint("LongLogTag")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        Log.e("ProfileFragment onViewCreated","called!")
    }

    @SuppressLint("LongLogTag")
    override fun onDestroyView() {
        super.onDestroyView()
        Log.e("ProfileFragment onDestroyView","called!")
    }
}