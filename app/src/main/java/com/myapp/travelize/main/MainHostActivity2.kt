package com.myapp.travelize.main

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.myapp.travelize.R
import com.myapp.travelize.authentication.MainActivity.Companion.FIRESTORE_SHARED_PREF
import com.myapp.travelize.authentication.MainActivity.Companion.USER_EMAIL
import com.myapp.travelize.authentication.MainActivity.Companion.USER_NAME
import com.myapp.travelize.main.MainHostActivity.Companion.PROFILE_PIC_URL
import com.myapp.travelize.main.MainHostActivity.Companion.USER_DOB
import com.myapp.travelize.main.MainHostActivity.Companion.USER_GENDER
import com.myapp.travelize.main.MainHostActivity.Companion.USER_INSTITUTE_NAME
import com.myapp.travelize.main.MainHostActivity.Companion.USER_PASSIONS
import com.myapp.travelize.models.User

class MainHostActivity2 : AppCompatActivity() {
    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val collectionRef = db.collection("Users")
    val docRef = collectionRef.document(auth.getCurrentUser().getUid())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main_host2)
        supportActionBar?.hide()

        val i = intent
        val isNewUser = i.getBooleanExtra("New User", false)
        Log.e("Info", "MainHostActivity2 and New User Status:$isNewUser")
        if (isNewUser) {
            createUserProfile()
        }
    }

    private fun createUserProfile() {
        val sharedPref = getSharedPreferences(FIRESTORE_SHARED_PREF, MODE_PRIVATE)
        val name = sharedPref.getString(USER_NAME, null)
        val email = sharedPref.getString(USER_EMAIL, null)
        val dob = sharedPref.getString(USER_DOB, null)
        val gender = sharedPref.getString(USER_GENDER, null)
        val tempSet: Set<String> =
            HashSet<String>(sharedPref.getStringSet(USER_PASSIONS, HashSet<String>()))
        val passions: List<String> = tempSet.toList()
        val imageURL = sharedPref.getString(PROFILE_PIC_URL, null)
        val institute = sharedPref.getString(USER_INSTITUTE_NAME, null)

        val user = User(
            name,
            email,
            dob,
            gender,
            institute,
            passions,
            imageURL,
            null,
            null,
            null,
            null,
            null
        )

        docRef
            .set(user).addOnSuccessListener {
                Log.e("MainHostActivity2", "DocumentSnapshot successfully written!");
            }
            .addOnFailureListener {
                it.printStackTrace()
                Log.e("MainHostActivity2", "Failure to write document")
            }
    }
}