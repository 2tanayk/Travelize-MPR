package com.myapp.travelize.authentication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import com.google.firebase.auth.FirebaseAuth
import com.myapp.travelize.R
import com.myapp.travelize.main.MainHostActivity
import com.myapp.travelize.main.MainHostActivity2

class MainActivity : AppCompatActivity() {
    companion object {
        const val USER_NAME: String = "name"
        const val USER_EMAIL: String = "email"
        const val FIRESTORE_SHARED_PREF: String = "userDocument"
    }

    private lateinit var auth: FirebaseAuth
    var isProfileCreated: Boolean = true
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        supportActionBar?.hide()
        auth = FirebaseAuth.getInstance()
        isProfileCreated =
            getSharedPreferences("profile", MODE_PRIVATE).getBoolean("profile_created", true)

    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (!isProfileCreated) {
            startActivity(Intent(this, MainHostActivity::class.java))
            finish()
            return
        }
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

    fun saveSharedPrefs(name: String, email: String) {
        val sharedPref = getSharedPreferences(FIRESTORE_SHARED_PREF, MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(USER_NAME, name)
        editor.putString(USER_EMAIL, email)
        editor.apply()
        Toast.makeText(this, "name and email saved!", Toast.LENGTH_SHORT).show()
    }
}