package com.example.red_v1.fragments

import android.content.Context
import androidx.fragment.app.Fragment
import com.example.red_v1.adapters.RedListAdapter
import com.example.red_v1.listeners.HomeCallback
import com.example.red_v1.listeners.RedListener
import com.example.red_v1.listeners.RedListenerImpl
import com.example.red_v1.util.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

// Base fragment for screens that display a list of "reds" (posts)
// Provides shared functionality for subclasses like HomeFragment, SearchFragment, etc.
abstract class RedFragment : Fragment(){
    // Adapter for the RecyclerView that displays reds
    protected var redsAdapter: RedListAdapter? = null
    // The currently logged-in user's data
    protected var currentUser: User? = null
    protected val firebaseDB = FirebaseFirestore.getInstance()
    protected val userId = FirebaseAuth.getInstance().currentUser?.uid
    protected var listener: RedListenerImpl? = null
    protected var callback:HomeCallback? = null


    // Called when the fragment is attached to its context (usually the activity)
    override fun onAttach(context: Context) {
        super.onAttach(context)
        // Ensure the activity implements HomeCallback for communication
        if(context is HomeCallback){
            callback = context
        }else{
            throw RuntimeException(context.toString()+"must implement HomeCallback")
        }
    }
    // Sets the current user and updates the listener with the new user object
    fun setUser(user: User?){
        this.currentUser= user
        listener?.user = user
    }

    // Abstract method that must be implemented by subclasses to update their list of reds
    abstract  fun updateList()

    // Refresh the list whenever the fragment resumes
    override fun onResume(){
        super.onResume()
        updateList()
    }


}