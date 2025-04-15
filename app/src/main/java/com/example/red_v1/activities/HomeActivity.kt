package com.example.red_v1.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import android.content.Intent
import android.content.Context
import android.view.View
import com.example.red_v1.R
import com.google.firebase.auth.FirebaseAuth



class HomeActivity : AppCompatActivity() {
    private val firebaseAuth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)


    }
    fun onLogout(v:View){
        firebaseAuth.signOut()
        startActivity(LoginActivity.newIntent(this))
        finish()
    }


    companion object{
        fun newIntent(context: Context): Intent = Intent(context, HomeActivity::class.java)
    }



}
