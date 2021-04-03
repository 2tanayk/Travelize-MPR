package com.myapp.travelize.authentication

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import android.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.myapp.travelize.MainActivity
import com.myapp.travelize.R
import com.myapp.travelize.main.MainHostActivity
import com.myapp.travelize.onboarding.ViewPagerActivity

class RegisterFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    lateinit var emailEditText: TextInputEditText
    lateinit var nameEditText: TextInputEditText
    lateinit var passwordEditText: TextInputEditText
    lateinit var confirmPasswordEditText: TextInputEditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }//onCreate ends

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_register, container, false)
        val toolbar = view.findViewById<Toolbar>(R.id.backToolBar)
        val signupBtn = view.findViewById<Button>(R.id.signupBtn)
        auth = FirebaseAuth.getInstance()
        nameEditText = view.findViewById(R.id.nameEditText)
        emailEditText = view.findViewById(R.id.emailEditText)
        passwordEditText = view.findViewById(R.id.passwordEditText)
        confirmPasswordEditText = view.findViewById(R.id.confirmEditText)

        activity?.setActionBar(toolbar)
        activity?.title = ""
        activity?.actionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }
        signupBtn.setOnClickListener {
//            Log.e("Checking Register form", name.text.toString())
//            Log.e("Checking Register form", email.text.toString())
//            Log.e("Checking Register form", password.text.toString())
//            Log.e("Checking Register form", confirmPassword.text.toString())
            val name = nameEditText.text.toString().trim()
            val email = emailEditText.text.toString().trim()
            val password = passwordEditText.text.toString().trim()
            val confirmPassword = confirmPasswordEditText.text.toString().trim()

            validate(name, email, password, confirmPassword)
        }
        return view
    }//onCreateView ends

    private fun validate(name: String, email: String, password: String, confirmPassword: String) {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(
                confirmPassword
            )
        ) {
            Toast.makeText(activity, "Some Field is Empty!", Toast.LENGTH_SHORT).show()
            return
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.error = "Invalid email address!"
            return
        }
        if (password.length < 6) {
            passwordEditText.error = "Password too short!"
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.error = "Password doesn't match!"
            return
        }
        signup(email, password)
    }

    private fun signup(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Log.d("Registration", "createUserWithEmail:success")
                Toast.makeText(activity, "Welcome!", Toast.LENGTH_SHORT).show()
                val intent = Intent(activity, MainHostActivity::class.java)
                val parentActivity = activity as? MainActivity
                Log.e("Result of safe cast", parentActivity.toString())
                parentActivity?.startActivity(intent)
                parentActivity?.finish()

            } else {
                // If sign in fails, display a message to the user.
                Log.w("Registration", "createUserWithEmail:failure", it.exception)
                Toast.makeText(activity, "Authentication failed :(", Toast.LENGTH_SHORT).show()
            }
        }
    }


}//class ends