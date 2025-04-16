package com.example.red_v1.activities

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.red_v1.R
import com.example.red_v1.databinding.ActivityHomeBinding
import com.example.red_v1.databinding.ActivityRedBinding
import com.example.red_v1.util.DATA_REDS
import com.example.red_v1.util.Red
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class RedActivity : AppCompatActivity() {

    private val firebaseDB = FirebaseFirestore.getInstance()
    private val firebaseStorage = FirebaseStorage.getInstance().reference
    private val imageUrl: String? = null
    private var userId:String? = null
    private var userName: String? =null


    private lateinit var binding: ActivityRedBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRedBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(intent.hasExtra(PARAM_USER_ID) && intent.hasExtra(PARAM_USER_NAME)){
            userId = intent.getStringExtra((PARAM_USER_ID))
            userName = intent.getStringExtra(PARAM_USER_NAME)
        }else{
            Toast.makeText(this,"Error creating RedPost", Toast.LENGTH_SHORT).show()
            finish()
        }
        binding.redProgressLayout.setOnTouchListener { v, event -> true }
    }

    fun addImage(v: View){

    }

    fun postRed(v:View){
        binding.redProgressLayout.visibility = View.VISIBLE
        val text = binding.RedText.text.toString()
        val redId = firebaseDB.collection(DATA_REDS).document()
        val hashtags = getHashtags(text)
        val red = Red(redId.id, arrayListOf(userId!!),userName,text,imageUrl, System.currentTimeMillis(),hashtags,
            arrayListOf()
        )
        redId.set(red).addOnCompleteListener { finish() }.addOnFailureListener { e ->
            e.printStackTrace()
            binding.redProgressLayout.visibility = View.GONE
            Toast.makeText(this, "Failed to post",Toast.LENGTH_SHORT).show()
        }

    }

    fun getHashtags(source:String): ArrayList<String>{
        return arrayListOf()
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