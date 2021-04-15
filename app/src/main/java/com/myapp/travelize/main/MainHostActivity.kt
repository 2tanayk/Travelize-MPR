package com.myapp.travelize.main

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.myapp.travelize.R
import com.myapp.travelize.authentication.MainActivity

class MainHostActivity : AppCompatActivity() {
    companion object {
        const val USER_DOB: String = "dob"
        const val USER_GENDER: String = "gender"
        const val USER_INSTITUTE_NAME: String = "institute"
        const val USER_PASSIONS: String = "passions"
        const val PROFILE_PIC_URL: String = "downloadURL"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_host)
        supportActionBar?.hide()
        val sharedPref = getSharedPreferences("profile", MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("profile_created", false)
        editor.apply()

        if (ContextCompat.checkSelfPermission(
                this@MainHostActivity,
                Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            ActivityCompat.requestPermissions(
                this@MainHostActivity,
                arrayOf(Manifest.permission.CAMERA), 1
            )
        }
    }

    fun replaceFragment(fragment: Fragment) {
        val fm = supportFragmentManager
        fm.beginTransaction().replace(R.id.main_fragment_container, fragment).commit()
    }

    fun saveInfo() {
        val sharedPref = getSharedPreferences("profile", MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putBoolean("profile_created", true)
        editor.apply()
    }

    fun saveSharedPrefs(dateString: String, gender: String, instituteName: String) {
        val sharedPref = getSharedPreferences(MainActivity.FIRESTORE_SHARED_PREF, MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(USER_DOB, dateString)
        editor.putString(USER_GENDER, gender)
        editor.putString(USER_INSTITUTE_NAME, instituteName)
        editor.apply()
        Toast.makeText(this, "dob,gender and inst saved!", Toast.LENGTH_SHORT).show()
    }

    fun saveSharedPrefs(passionSet: MutableSet<String>) {
        val sharedPref = getSharedPreferences(MainActivity.FIRESTORE_SHARED_PREF, MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putStringSet(USER_PASSIONS, passionSet)
        editor.apply()
        Toast.makeText(this, "Passions saved", Toast.LENGTH_SHORT).show()
    }

    fun saveSharedPrefs(firebaseProfilePicUrl: String) {
        val sharedPref = getSharedPreferences(MainActivity.FIRESTORE_SHARED_PREF, MODE_PRIVATE)
        val editor = sharedPref.edit()
        editor.putString(PROFILE_PIC_URL, firebaseProfilePicUrl)
        editor.commit()
        Log.e("All prefs", sharedPref.all.toString())
        Toast.makeText(this, "URL saved!", Toast.LENGTH_SHORT).show()
    }

}