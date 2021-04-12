package com.myapp.travelize

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.widget.Toast
import com.myapp.travelize.authentication.MainActivity
import com.myapp.travelize.onboarding.ViewPagerActivity

class SplashScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        setTheme(R.style.Theme_Travelize)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        //Toast.makeText(this, "OnSplashScreen", Toast.LENGTH_SHORT).show()
        supportActionBar?.hide()

        Handler(Looper.getMainLooper()).postDelayed({
            if (onBoardingFinished()) {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            } else {
                val intent = Intent(this, ViewPagerActivity::class.java)
                startActivity(intent)
                finish()
            }
        }, 1000)
    }

    private fun onBoardingFinished(): Boolean {
        val sharedPref = getSharedPreferences("onboarding", MODE_PRIVATE)
        return sharedPref.getBoolean("finished", false)
    }
}