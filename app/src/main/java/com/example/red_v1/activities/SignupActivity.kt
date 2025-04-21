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
        setTextChangeListener(binding.usernameET,binding.usernameTIL)
        setTextChangeListener(binding.emailET,binding.emailTIL)
        setTextChangeListener(binding.passwordET,binding.passwordTIL)

        binding.signupProgressLayout.setOnTouchListener { v, event -> true }

    }

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
        if(proceed){
            binding.signupProgressLayout.visibility = View.VISIBLE
            firestoreAuth.createUserWithEmailAndPassword(binding.emailET.text.toString(), binding.passwordET.text.toString())
                .addOnCompleteListener{task ->
                    if(!task.isSuccessful){
                        Toast.makeText(this@SignupActivity,"Signup error: ${task.exception?.localizedMessage}",
                            Toast.LENGTH_SHORT).show()
                    }
                    else{
                        val email = binding.emailET.text.toString()
                        val name = binding.usernameET.text.toString()
                        val user = User(email, name, "", "",arrayListOf(), arrayListOf())
                        firebaseDB.collection(DATA_USERS).document(firestoreAuth.uid!!).set(user)
                    }
                    binding.signupProgressLayout.visibility =View.GONE
                }
                .addOnFailureListener{ e ->
                    e.printStackTrace()
                    binding.signupProgressLayout.visibility=View.GONE

                }
        }

    }

    fun goToLogin(v:View){
        startActivity(LoginActivity.newIntent(this))
        finish()
    }

    override fun onStart() {
        super.onStart()
        firestoreAuth.addAuthStateListener(firebaseAuthListener)
    }

    override fun onStop() {
        super.onStop()
        firestoreAuth.removeAuthStateListener(firebaseAuthListener)
    }

    companion object{
        fun newIntent(context: Context): Intent = Intent(context, SignupActivity::class.java)
    }
}
