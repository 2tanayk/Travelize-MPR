package com.myapp.travelize

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.MenuItem
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.myapp.travelize.authentication.LoginFragment
import com.myapp.travelize.authentication.RegisterFragment

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
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