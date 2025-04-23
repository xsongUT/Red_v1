package com.example.red_v1.activities

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import com.example.red_v1.MainViewModel
import com.example.red_v1.R
import com.example.red_v1.databinding.ActivityHomeBinding
import com.example.red_v1.databinding.ActivityRedBinding
import com.example.red_v1.util.DATA_IMAGES
import com.example.red_v1.util.DATA_REDS
import com.example.red_v1.util.DATA_USERS
import com.example.red_v1.util.DATA_USER_IMAGE_URL
import com.example.red_v1.util.REQUEST_CODE_PHOTO
import com.example.red_v1.util.Red
import com.example.red_v1.util.loadUrl
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class RedActivity : AppCompatActivity() {

    private val firebaseDB = FirebaseFirestore.getInstance()
    private val firebaseStorage = FirebaseStorage.getInstance().reference
    private var imageUrl: String? = null
    private var userId:String? = null
    private var userName: String? =null
    private lateinit var ViewModel :MainViewModel

    private lateinit var binding: ActivityRedBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRedBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        // Retrieve user info from intent extras
        if(intent.hasExtra(PARAM_USER_ID) && intent.hasExtra(PARAM_USER_NAME)){
            userId = intent.getStringExtra((PARAM_USER_ID))
            userName = intent.getStringExtra(PARAM_USER_NAME)
        }else{
            Toast.makeText(this,"Error creating RedPost", Toast.LENGTH_SHORT).show()
            finish()
        }
        // Disable user interaction when the progress overlay is shown
        binding.redProgressLayout.setOnTouchListener { v, event -> true }
    }

    //Launch image picker to choose an image from gallery
    fun addImage(v: View){
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, REQUEST_CODE_PHOTO)
    }
    //Handle result from image picker
    override fun onActivityResult(
        requestCode: Int,
        resultCode: Int,
        data: Intent?
    ) {
        super.onActivityResult(requestCode, resultCode, data)
        if(resultCode == Activity.RESULT_OK && requestCode == REQUEST_CODE_PHOTO){
            storeImage(data?.data)
        }
    }


    //Uploads the selected image to Firebase Storage and updates UI
    fun storeImage(imageUri:Uri?){
        //load image to firebase
        imageUri?.let{
            Toast.makeText(this,"Uploading...",Toast.LENGTH_SHORT).show()
            binding.redProgressLayout.visibility = View.VISIBLE

            val filePath = firebaseStorage.child(DATA_IMAGES).child(userId!!)
            filePath.putFile(imageUri).addOnSuccessListener {
                //// Retrieve the download URL
                filePath.downloadUrl.addOnSuccessListener {uri ->
                            imageUrl = uri.toString()
                            binding.redImage.loadUrl(imageUrl, R.drawable.logo)
                            binding.redProgressLayout.visibility = View.GONE
                        }
                }.addOnFailureListener {
                    onUploadFailure()

            }.addOnFailureListener {
                onUploadFailure()
            }
        }
    }

    // Handle image upload failure
    fun onUploadFailure(){
        Toast.makeText(this, "Image upload failed. Please try again later.", Toast.LENGTH_SHORT).show()
        binding.redProgressLayout.visibility = View.GONE
    }

    /**
     * Called when user clicks post button
     * Creates a new Red object and stores it in Firestore
     */
    fun postRed(v:View){
        binding.redProgressLayout.visibility = View.VISIBLE
        val text = binding.RedText.text.toString()
        val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val address = sharedPreferences.getString("USER_ADDRESS", "Default Address")
        Log.d("ProfileActivity4", "User address: $address")
        val postText = if (address != null) {
            "$text —— Post from ${address}"
        } else {
            "$text"
        }
        val redId = firebaseDB.collection(DATA_REDS).document()
        // Extract hashtags from the post
        val hashtags = getHashtags(text)
<<<<<<< HEAD
        val red = Red(redId.id, arrayListOf(userId!!),userName,postText,imageUrl, System.currentTimeMillis(),hashtags,
            arrayListOf()
=======
        // Create Red post object
        val red = Red(redId.id, arrayListOf(userId!!),userName,text,imageUrl, System.currentTimeMillis(),hashtags,
            arrayListOf() // No likes initially
>>>>>>> 7b08c2e (add more comments)
        )
        // Save to Firestore
        redId.set(red).addOnCompleteListener {
            finish() }// Close activity on success
            .addOnFailureListener { e ->
            e.printStackTrace()
            binding.redProgressLayout.visibility = View.GONE
            Toast.makeText(this, "Failed to post",Toast.LENGTH_SHORT).show()
        }

    }

    //Parses hashtags from the text content of a Red
    fun getHashtags(source:String): ArrayList<String>{
        val hashtages:ArrayList<String> = arrayListOf<String>()
        var text = source

        while(text.contains("#")){
            var hashtag = ""
            val hash = text.indexOf("#")
            text = text.substring(hash+1)

            val firstSpace = text.indexOf(" ")
            val firstHash = text.indexOf("#")

            if (firstSpace == -1  && firstHash == -1){
                hashtag = text.substring(0)
            }else if(firstSpace != -1 && firstSpace < firstHash){
                hashtag = text.substring(0,firstSpace)
                text = text.substring(firstSpace+1)
            }else{
                hashtag = text.substring(0,firstHash)
                text = text.substring(firstHash)
            }
            // Add to list if not empty
            if(!hashtag.isNullOrEmpty()){
                hashtages.add(hashtag)
            }
        }
        return hashtages

    }


    companion object {
        // Keys for intent extras
        val PARAM_USER_ID = "UserId"
        val PARAM_USER_NAME = "UserName"
        //Helper to launch RedActivity with user data
        fun newIntent(context: Context, userId:String?, userName:String?): Intent {
            val intent = Intent(context, RedActivity::class.java)
            intent.putExtra(PARAM_USER_ID, userId)
            intent.putExtra(PARAM_USER_NAME,userName)
            return  intent
        }
    }
}