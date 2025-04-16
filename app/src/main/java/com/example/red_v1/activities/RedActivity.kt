package com.example.red_v1.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.red_v1.R
import com.example.red_v1.databinding.ActivityHomeBinding
import com.example.red_v1.databinding.ActivityRedBinding

class RedActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRedBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRedBinding.inflate(layoutInflater)
        setContentView(binding.root)

    }

    fun addImage(v: View){

    }

    fun postRed(v:View){

    }


    companion object {
        val PARAM_USER_ID = "UserId"
        val PARAM_USER_NAME = "UserName"
        fun newIntent(context: Context, userId:String?, userName:String?): Intent {
            val intent = Intent(context, RedActivity::class.java)
            intent.putExtra(PARAM_USER_ID, userId)
            intent.putExtra(PARAM_USER_NAME,userName)
            return  intent
        }
    }
}