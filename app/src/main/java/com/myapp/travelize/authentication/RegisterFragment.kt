package com.myapp.travelize.authentication

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextUtils
import android.text.TextWatcher
import android.util.Log
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import android.widget.Toolbar
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.auth.FirebaseAuth
import com.myapp.travelize.R
import com.myapp.travelize.main.MainHostActivity

class RegisterFragment : Fragment() {
    private lateinit var auth: FirebaseAuth
    lateinit var emailEditText: TextInputEditText
    lateinit var nameEditText: TextInputEditText
    lateinit var passwordEditText: TextInputEditText
    lateinit var confirmPasswordEditText: TextInputEditText
    private lateinit var  signupBtn: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        auth = FirebaseAuth.getInstance()
    }//onCreate ends

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_register, container, false)

    }//onCreateView ends

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val toolbar = view.findViewById<Toolbar>(R.id.backToolBar)
        signupBtn = view.findViewById<Button>(R.id.signupBtn)
        auth = FirebaseAuth.getInstance()
        nameEditText = view.findViewById(R.id.nameEditText)
        emailEditText = view.findViewById(R.id.emailEditText)
        passwordEditText = view.findViewById(R.id.passwordEditText)
        confirmPasswordEditText = view.findViewById(R.id.confirmEditText)

        //disable button
        signupBtn.isEnabled = false

        activity?.setActionBar(toolbar)
        activity?.title = ""
        activity?.actionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener {
            activity?.supportFragmentManager?.popBackStack()
        }

        //set text change listener
        nameEditText.addTextChangedListener( textWatcher)
        emailEditText.addTextChangedListener( textWatcher)
        passwordEditText.addTextChangedListener( textWatcher)
        confirmPasswordEditText.addTextChangedListener( textWatcher)

        signupBtn.setOnClickListener {
//            Log.e("Checking Register form", name.text.toString())
//            Log.e("Checking Register form", email.text.toString())
//            Log.e("Checking Register form", password.text.toString())
//            Log.e("Checking Register form", confirmPassword.text.toString())
            val name = getText( nameEditText )
            val email = getText(emailEditText)
            val password = getText( passwordEditText  )
            val confirmPassword = getText( confirmPasswordEditText)

            if( validate(name, email, password, confirmPassword) ){
                signup(email, password, name)
            }
        }
    }

    // get text from edit text
    fun getText( editText : TextInputEditText ) : String =
                editText.text.toString().trim()

    // make function independent
    private fun validate(name: String, email: String, password: String, confirmPassword: String) : Boolean {
        if (TextUtils.isEmpty(name) || TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(
                confirmPassword
            )
        ) {
            Toast.makeText(activity, "Some Field is Empty!", Toast.LENGTH_SHORT).show()
            return false
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailEditText.error = "Invalid email address!"
            return false
        }
        if (password.length < 6) {
            passwordEditText.error = "Password too short!"
            return false
        }
        if (!password.equals(confirmPassword)) {
            confirmPasswordEditText.error = "Password doesn't match!"
            return false
        }

        return true
    }

    //textwatcher
    val textWatcher : TextWatcher = object : TextWatcher{
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            //
            val name = getText(nameEditText)
            val email = getText(emailEditText)
            val password = getText(passwordEditText)
            val confirmPassword = getText(confirmPasswordEditText)

            // eabled when all texts are filled
            signupBtn.isEnabled =
                ( !TextUtils.isEmpty(name) && !TextUtils.isEmpty(email)  && !TextUtils.isEmpty( password) &&  !TextUtils.isEmpty(confirmPassword) )
        }

        override fun afterTextChanged(s: Editable?) {  }

    }

    private fun signup(email: String, password: String, name: String) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
            if (it.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                val parentActivity = activity as? MainActivity
                parentActivity?.saveSharedPrefs(name,email)
                Log.d("Registration", "createUserWithEmail:success")
                Toast.makeText(activity, "Welcome!", Toast.LENGTH_SHORT).show()
                val intent = Intent(activity, MainHostActivity::class.java)
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