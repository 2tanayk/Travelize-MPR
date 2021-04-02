package com.myapp.travelize.onboarding

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import com.myapp.travelize.R
import com.myapp.travelize.onboarding.screens.OnboardingFragment1
import com.myapp.travelize.onboarding.screens.OnboardingFragment2
import com.myapp.travelize.onboarding.screens.OnboardingFragment3

class ViewPagerActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_pager)
        supportActionBar?.hide()
        val pager = findViewById<ViewPager2>(R.id.onboardingViewPager)
        val tabLayout = findViewById<TabLayout>(R.id.tab_layout)


        val onboardingFragmentList = arrayListOf<Fragment>(
            OnboardingFragment1(),
            OnboardingFragment2(),
            OnboardingFragment3()
        )

        val viewPagerAdapter =
            ViewPagerAdapter(onboardingFragmentList, supportFragmentManager, lifecycle)

        pager.adapter = viewPagerAdapter

        TabLayoutMediator(tabLayout, pager) { tab, position ->
            //Some implementation
//            tab.=R.drawable.selected_dot
        }.attach()


    }//onCreate ends
}//class ends