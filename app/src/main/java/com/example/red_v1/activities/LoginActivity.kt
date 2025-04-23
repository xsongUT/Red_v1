package com.example.red_v1.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.firebase.auth.FirebaseAuth
import com.example.red_v1.databinding.ActivityLoginBinding
import android.widget.Toast
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.red_v1.R


class LoginActivity : AppCompatActivity() {
    // Firebase authentication instance
    private val firestoreAuth = FirebaseAuth.getInstance()
    // Listener to detect if user is already logged in
    private val firebaseAuthListener = FirebaseAuth.AuthStateListener {
       val user = firestoreAuth.currentUser?.uid
       user?.let{
           // If user is logged in, go to HomeActivity
           startActivity(HomeActivity.newIntent(this))
           finish()
       }
    }

    // UI components
    private lateinit var emailET: TextInputEditText
    private lateinit var emailTIL: TextInputLayout
    private lateinit var passwordET: TextInputEditText
    private lateinit var passwordTIL: TextInputLayout
    private lateinit var loginProgressLayout: LinearLayout
    private lateinit var binding: ActivityLoginBinding

    // Lifecycle: Add auth listener
    override fun onStart() {
        super.onStart()
        firestoreAuth.addAuthStateListener(firebaseAuthListener)
    }

    // Lifecycle: Remove auth listener
    override fun onStop() {
        super.onStop()
        firestoreAuth.removeAuthStateListener(firebaseAuthListener)
    }

    // Helper method to create an intent for LoginActivity
    companion object{
        fun newIntent(context: Context): Intent = Intent(context, LoginActivity::class.java)
    }


    // Initialize UI and setup event listeners
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        emailET = findViewById<TextInputEditText>(R.id.emailET)
        emailTIL = findViewById<TextInputLayout>(R.id.emailTIL)
        passwordET = findViewById<TextInputEditText>(R.id.passwordET)
        passwordTIL = findViewById<TextInputLayout>(R.id.passwordTIL)
        loginProgressLayout = findViewById<LinearLayout>(R.id.loginProgressLayout)
        // Setup validation listeners
        setTextChangeListener(emailET,emailTIL)
        setTextChangeListener(passwordET,passwordTIL)
        // Disable touches on progress layout (to prevent interaction during loading)
        loginProgressLayout.setOnTouchListener { v, event -> true }

    }

    // Automatically clear error message when user starts typing
    fun setTextChangeListener(et:EditText,til:TextInputLayout){
        et.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                til.isErrorEnabled = false
            }

        })
    }

    // Called when user presses the "Login" button
    fun onLogin(v:View){

        var proceed =true
        // Validate email
        if(emailET.text.isNullOrEmpty()){
                    emailTIL.error = "Email is required"
                    emailTIL.isErrorEnabled = true
                proceed=false
            }
        // Validate password
        if(passwordET.text.isNullOrEmpty()){
            passwordTIL.error = "Password is required"
            passwordTIL.isErrorEnabled = true
            proceed=false
        }
        // Proceed with Firebase login if inputs are valid
        if(proceed){
            loginProgressLayout.visibility = View.VISIBLE
            firestoreAuth.signInWithEmailAndPassword(emailET.text.toString(),passwordET.text.toString())
                .addOnCompleteListener{task ->
                    if(!task.isSuccessful){
                        // Login failed
                        loginProgressLayout.visibility =View.GONE
                        Toast.makeText(this@LoginActivity,"login error: ${task.exception?.localizedMessage}",Toast.LENGTH_SHORT).show()
                    }
                    else{
                        // Login successful, go to HomeActivity
                        startActivity(HomeActivity.newIntent(this))
                        finish()
                    }
                }
                .addOnFailureListener{e ->
                    // Handle unexpected error
                    e.printStackTrace()
                    loginProgressLayout.visibility=View.GONE

                }
        }
    }

    // Called when user taps "Sign Up" button
    fun goToSignup(v:View){
        startActivity(SignupActivity.newIntent(this))
        finish()
    }













}

