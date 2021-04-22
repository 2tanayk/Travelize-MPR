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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.myapp.travelize.R
import com.myapp.travelize.main.MainHostActivity
import com.myapp.travelize.main.MainHostActivity2
import java.lang.Exception

class LoginFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    lateinit var passwordEditText: TextInputEditText
    lateinit var emailEditText: TextInputEditText
    lateinit var resetTextView: TextView
    lateinit var  signinBtn: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_login, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val signupInsteadBtn = view.findViewById<TextView>(R.id.signupTextView)
        signinBtn = view.findViewById<Button>(R.id.signinBtn)
        emailEditText = view.findViewById(R.id.emailEditText2)
        passwordEditText = view.findViewById(R.id.passwordEditText2)
        resetTextView = view.findViewById(R.id.forgotPasswordTextView)
        Log.e("Checking something:", activity.toString())

        signupInsteadBtn.setOnClickListener {
            (activity as? MainActivity)?.signupInstead()
        }

        signinBtn.setOnClickListener {
//            Log.e("Checking Register form", name.text.toString())
//            Log.e("Checking Register form", email.text.toString())
//            Log.e("Checking Register form", password.text.toString())
//            Log.e("Checking Register form", confirmPassword.text.toString())

            val email =getText( emailEditText)
            val password = getText(passwordEditText)

           if( validate(email, password) ){
               signin(email, password)
           }
        }
        resetTextView.setOnClickListener {
            val email = getText( emailEditText)
            invokeAlertDialog(email)
        }
    }

    // get text from edit text
    fun getText( editText : TextInputEditText ) : String =
        editText.text.toString().trim()

    private fun invokeAlertDialog(email: String) {
        val dialog = MaterialAlertDialogBuilder(requireActivity())
            .setTitle("Reset Password")
            .setMessage("Do you really want to reset your password?")
            .setNegativeButton("Cancel") { dialog, which ->
                // Respond to negative button press
            }
            .setPositiveButton("Yes") { dialog, which ->
                // Respond to positive button press
                try {
                    resetPassword(email)
                } catch (e: Exception) {
                    e.printStackTrace()
                    Toast.makeText(activity, "Wrong email :(", Toast.LENGTH_SHORT).show()
                }

            }.show()


        dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            .setTextColor(ContextCompat.getColor(requireActivity(), R.color.black))
        dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            .setTextColor(ContextCompat.getColor(requireActivity(), R.color.black))

    }

    private fun validate(email: String, password: String) : Boolean {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(activity, "Some Field is Empty!", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.error = "Invalid email address!"
            return false
        }
        return true
    }

    private fun signin(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                Toast.makeText(activity, "Welcome Back!", Toast.LENGTH_SHORT).show()
                Log.d("Login Fragment", "signInWithEmail:success")
                val user = auth.currentUser
                val intent = Intent(activity, MainHostActivity2::class.java)
                val parentActivity = activity as? MainActivity
                Log.e("Result of safe cast", parentActivity.toString())
                parentActivity?.startActivity(intent)
                parentActivity?.finish()
            } else {
                // If sign in fails, display a message to the user.
                Log.w("Login Fragment", "signInWithEmail:failure", it.exception)
                Toast.makeText(activity, "Authentication failed :(", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun resetPassword(email: String) {
        auth.sendPasswordResetEmail(email)
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d("Login Fragment", "Email sent!")
                    Toast.makeText(activity, "Email sent!", Toast.LENGTH_LONG).show()
                } else {
                    Toast.makeText(activity, "An error occured :(", Toast.LENGTH_SHORT).show()
                }
            }
    }


}