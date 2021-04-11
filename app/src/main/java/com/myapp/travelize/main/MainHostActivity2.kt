package com.myapp.travelize.main

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.myapp.travelize.R

class MainHostActivity2 : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_host2)
        supportActionBar?.hide()
    }
}