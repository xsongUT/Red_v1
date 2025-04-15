package com.example.red_v1.activities

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import android.content.Intent
import android.content.Context
import android.view.View
import com.example.red_v1.R
import com.google.firebase.auth.FirebaseAuth
import com.google.android.material.tabs.TabLayout




class HomeActivity : AppCompatActivity() {
    private val firebaseAuth = FirebaseAuth.getInstance()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)

        val tabLayout = findViewById<TabLayout>(R.id.tabs)

//        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.selector_home))
//        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.selector_search))
//        tabLayout.addTab(tabLayout.newTab().setIcon(R.drawable.selector_myactivity))
        val tabHome = tabLayout.newTab().setIcon(R.drawable.selector_home)
        val tabSearch = tabLayout.newTab().setIcon(R.drawable.selector_search)
        val tabMyActivity = tabLayout.newTab().setIcon(R.drawable.selector_myactivity)

        tabLayout.addTab(tabHome)
        tabLayout.addTab(tabSearch)
        tabLayout.addTab(tabMyActivity)


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
