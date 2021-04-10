package com.myapp.travelize.main

import android.Manifest
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.myapp.travelize.R
import com.myapp.travelize.authentication.RegisterFragment

class MainHostActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_host)
        supportActionBar?.hide()

        if (ContextCompat.checkSelfPermission(this@MainHostActivity, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this@MainHostActivity,
                arrayOf(Manifest.permission.CAMERA), 1)
        }
    }

    fun replaceFragment(fragment: Fragment) {
        val fm = supportFragmentManager
        fm.beginTransaction().replace(R.id.main_fragment_container, fragment).commit()
    }
}