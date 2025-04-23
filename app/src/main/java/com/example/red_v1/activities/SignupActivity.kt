package com.example.red_v1.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.textfield.TextInputLayout
import com.example.red_v1.databinding.ActivitySignupBinding
import com.example.red_v1.util.DATA_USERS
import com.example.red_v1.util.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class SignupActivity : AppCompatActivity() {

    private val firebaseDB = FirebaseFirestore.getInstance()

    private val firestoreAuth = FirebaseAuth.getInstance()
    // Auth listener to automatically navigate to Home if user is already logged in
    private val firebaseAuthListener = FirebaseAuth.AuthStateListener {
        val user = firestoreAuth.currentUser?.uid
        user?.let{
            startActivity(HomeActivity.newIntent(this))
            finish()
        }
    }
    private lateinit var binding: ActivitySignupBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //setContentView((R.layout.activity_signup))
        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        // Enable dynamic error handling
        setTextChangeListener(binding.usernameET,binding.usernameTIL)
        setTextChangeListener(binding.emailET,binding.emailTIL)
        setTextChangeListener(binding.passwordET,binding.passwordTIL)

        // Disable interaction with screen when progress layout is visible
        binding.signupProgressLayout.setOnTouchListener { v, event -> true }

    }

    //Clear error when the user starts typing again in a text field
    fun setTextChangeListener(et: EditText, til: TextInputLayout){
        et.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(s: Editable?) {
                til.isErrorEnabled = false
            }

        })
    }

    fun onSignup(v:View){

        var proceed =true
        if(binding.usernameET.text.isNullOrEmpty()){
            binding.usernameTIL.error = "Username is required."
            binding.usernameTIL.isErrorEnabled = true
            proceed = false
        }
        if(binding.emailET.text.isNullOrEmpty()){
            binding.emailTIL.error = "Email is required"
            binding.emailTIL.isErrorEnabled = true
            proceed=false
        }
        if(binding.passwordET.text.isNullOrEmpty()){
            binding.passwordTIL.error = "Password is required"
            binding.passwordTIL.isErrorEnabled = true
            proceed=false
        }
        // If all fields are valid, proceed to sign up
        if(proceed){
            binding.signupProgressLayout.visibility = View.VISIBLE
            firestoreAuth.createUserWithEmailAndPassword(binding.emailET.text.toString(), binding.passwordET.text.toString())
                .addOnCompleteListener{task ->
                    if(!task.isSuccessful){
                        // Show error if sign-up failed
                        Toast.makeText(this@SignupActivity,"Signup error: ${task.exception?.localizedMessage}",
                            Toast.LENGTH_SHORT).show()
                    }
                    else{ // Create a new user document in Firestore
                        val email = binding.emailET.text.toString()
                        val name = binding.usernameET.text.toString()
                        val user = User(email, name, "", "",arrayListOf(), arrayListOf())
                        firebaseDB.collection(DATA_USERS).document(firestoreAuth.uid!!).set(user)
                    }
                    binding.signupProgressLayout.visibility =View.GONE
                }
                .addOnFailureListener{ e -> // Handle unexpected failures
                    e.printStackTrace()
                    binding.signupProgressLayout.visibility=View.GONE

                }
        }

    }

    //Navigate to the Login screen if the user already has an account
    fun goToLogin(v:View){
        startActivity(LoginActivity.newIntent(this))
        finish()
    }

    // Add auth state listener when activity is visible
    override fun onStart() {
        super.onStart()
        firestoreAuth.addAuthStateListener(firebaseAuthListener)
    }

    // Remove auth state listener to avoid memory leaks
    override fun onStop() {
        super.onStop()
        firestoreAuth.removeAuthStateListener(firebaseAuthListener)
    }

    companion object{
        // Helper function to create intent to launch this activity
        fun newIntent(context: Context): Intent = Intent(context, SignupActivity::class.java)
    }
}
