package com.example.red_v1.activities

import android.app.Activity
import android.app.ComponentCaller
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.example.red_v1.MainViewModel
import com.example.red_v1.R
import com.example.red_v1.databinding.ActivityHomeBinding
import com.example.red_v1.databinding.ActivityProfileBinding
import com.example.red_v1.fragments.MapFragment
import com.example.red_v1.util.DATA_IMAGES
import com.example.red_v1.util.DATA_USERS
import com.example.red_v1.util.DATA_USER_EMAIL
import com.example.red_v1.util.DATA_USER_IMAGE_URL
import com.example.red_v1.util.DATA_USER_USERNAME
import com.example.red_v1.util.REQUEST_CODE_PHOTO
import com.example.red_v1.util.User
import com.example.red_v1.util.loadUrl
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import androidx.lifecycle.Observer
import com.example.red_v1.util.DATA_USER_LOCALNAME

class ProfileActivity : AppCompatActivity() {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firebaseDB = FirebaseFirestore.getInstance()
    private val firebaseStorage = FirebaseStorage.getInstance().reference
    private val userId = FirebaseAuth.getInstance().currentUser?.uid
    private lateinit var binding: ActivityProfileBinding
    private var imageUrl: String? = null
//    private lateinit var pickImageLauncher: ActivityResultLauncher<Intent>

    private lateinit var ViewModel: MainViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val profileProgressLayout = binding.profileProgressLayout

        if(userId == null){
            finish()
        }
        profileProgressLayout.setOnTouchListener{v,event -> true}

        binding.photoIV.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_CODE_PHOTO)
        }
        populateInfo()
        binding.fabMap.setOnClickListener {
            binding.fabMap.visibility = View.GONE
            val mapFragment = MapFragment()

            supportFragmentManager.beginTransaction()
                .replace(R.id.fragment_container, mapFragment)
                .addToBackStack(null)
                .commit()
        }
        ViewModel = ViewModelProvider(this).get(MainViewModel::class.java)

        ViewModel.address.observe(this, Observer { newAddress ->

            binding.baseET.setText(newAddress)
            Log.d("ProfileActivity", "Address updated: $newAddress")

        })

    }

    fun populateInfo(){
        binding.profileProgressLayout.visibility = View.VISIBLE
        firebaseDB.collection(DATA_USERS).document(userId!!).get()
            .addOnSuccessListener { documentSnapshot ->
                val user = documentSnapshot.toObject(User::class.java)
                binding.usernameET.setText(user?.username, TextView.BufferType.EDITABLE)
                binding.emailET.setText(user?.email, TextView.BufferType.EDITABLE)
                binding.baseET.setText(user?.localname, TextView.BufferType.EDITABLE)
                val userAddress = binding.baseET.text.toString()
                val sharedPreferences = getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
                val editor = sharedPreferences.edit()
                editor.putString("USER_ADDRESS", userAddress)
                editor.apply()
                if (!user?.imageUrl.isNullOrEmpty()) {
                    imageUrl = user?.imageUrl
                    binding.photoIV.loadUrl(imageUrl, R.drawable.logo)
                }
                binding.profileProgressLayout.visibility = View.GONE
            }.addOnFailureListener{e ->
                e.printStackTrace()
                finish()
            }

    }

    fun onApply(v: View){
        binding.profileProgressLayout.visibility = View.VISIBLE
        val username = binding.usernameET.text.toString()
        val email = binding.emailET.text.toString()
        val localname = binding.baseET.text.toString()
        val map = HashMap<String,Any>()
        map[DATA_USER_USERNAME] = username
        map[DATA_USER_EMAIL] = email
        map[DATA_USER_LOCALNAME] = localname


        //update the database
        firebaseDB.collection(DATA_USERS).document(userId!!).update(map)
            .addOnSuccessListener {
                Toast.makeText(this,"Update successful for user: $userId", Toast.LENGTH_SHORT).show()

                finish()
            }.addOnFailureListener{e ->
                //let the user try again
                e.printStackTrace()
                Toast.makeText(this,"Update failed.Please try again for user: $userId", Toast.LENGTH_SHORT).show()
                binding.profileProgressLayout.visibility = View.GONE
            }

    }

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

    fun storeImage(imageUri:Uri?){
        //load image to firebase
        imageUri?.let{
            Toast.makeText(this,"Uploading...",Toast.LENGTH_SHORT).show()
            binding.profileProgressLayout.visibility = View.VISIBLE

            val filePath = firebaseStorage.child(DATA_IMAGES).child(userId!!)
            filePath.putFile(imageUri).addOnSuccessListener {
                filePath.downloadUrl.addOnSuccessListener {uri ->
                    val url = uri.toString()
                    firebaseDB.collection(DATA_USERS).document(userId!!).update(DATA_USER_IMAGE_URL,url)
                        .addOnSuccessListener {
                            imageUrl = url
                            binding.photoIV.loadUrl(imageUrl,R.drawable.logo)
                        }
                    binding.profileProgressLayout.visibility = View.GONE
                }.addOnFailureListener {
                    onUploadFailure()
                }
            }.addOnFailureListener {
                onUploadFailure()
            }
        }
    }

    fun onUploadFailure(){
        Toast.makeText(this, "Image upload failed. Please try again later.", Toast.LENGTH_SHORT).show()
        binding.profileProgressLayout.visibility = View.GONE
    }

    fun onSignout(v:View){
        firebaseAuth.signOut()
        val login_intent = Intent(this, LoginActivity::class.java)
        login_intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(login_intent)
        //finish()
    }

    companion object{
        fun newIntent(context: Context): Intent = Intent(context, ProfileActivity::class.java)
    }
}
