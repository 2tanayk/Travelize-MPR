package com.myapp.travelize.authentication

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.google.firebase.auth.FirebaseAuth
import com.myapp.travelize.R
import com.myapp.travelize.main.MainHostActivity
import com.myapp.travelize.main.MainHostActivity2

class MainActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        auth = FirebaseAuth.getInstance()

    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            startActivity(Intent(this, MainHostActivity2::class.java))
            finish()
        }
    }

    fun signupInstead() {
        val fm = supportFragmentManager
        fm.commit {
            replace<RegisterFragment>(R.id.auth_fragment_container)
            setReorderingAllowed(true)
            addToBackStack(null)
        }
    }
}