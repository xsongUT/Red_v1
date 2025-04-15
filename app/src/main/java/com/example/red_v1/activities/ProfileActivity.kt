package com.example.red_v1.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import com.example.red_v1.R

class ProfileActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

    }

    fun onApply(v: View){

    }

    fun onSignout(v:View){

    }

    companion object{
        fun newIntent(context: Context): Intent = Intent(context, ProfileActivity::class.java)
    }
}
