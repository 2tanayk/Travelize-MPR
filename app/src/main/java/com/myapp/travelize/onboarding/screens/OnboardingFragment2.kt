package com.myapp.travelize.onboarding.screens

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.viewpager2.widget.ViewPager2
import com.myapp.travelize.authentication.MainActivity
import com.myapp.travelize.R
import com.myapp.travelize.onboarding.ViewPagerActivity

class OnboardingFragment2 : Fragment() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_onboarding2, container, false)
        val nextBtn2 = view.findViewById<Button>(R.id.onboardingBtn2)
        val skipBtn2 = view.findViewById<Button>(R.id.skipBtn2)
        val viewPager = activity?.findViewById<ViewPager2>(R.id.onboardingViewPager)

        nextBtn2.setOnClickListener {
            viewPager?.currentItem = 2
        }

        skipBtn2.setOnClickListener {
            val sharedPref = activity?.getSharedPreferences("onboarding", Context.MODE_PRIVATE)
            val editor = sharedPref?.edit()
            editor?.putBoolean("finished", true)
            editor?.apply()

            val intent = Intent(activity, MainActivity::class.java)
            val parentActivity = activity as? ViewPagerActivity
            Log.e("Result of safe cast", parentActivity.toString())
            parentActivity?.startActivity(intent)
            parentActivity?.finish()
        }
        return view
    }
}