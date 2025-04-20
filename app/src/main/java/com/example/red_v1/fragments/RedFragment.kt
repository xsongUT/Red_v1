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

abstract class RedFragment : Fragment(){
    protected var redsAdapter: RedListAdapter? = null
    protected var currentUser: User? = null
    protected val firebaseDB = FirebaseFirestore.getInstance()
    protected val userId = FirebaseAuth.getInstance().currentUser?.uid
    protected var listener: RedListenerImpl? = null
    protected var callback:HomeCallback? = null
    //protected var redListener: RedListener? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if(context is HomeCallback){
            callback = context
        }else{
            throw RuntimeException(context.toString()+"must implement HomeCallback")
        }
    }
    fun setUser(user: User?){
        this.currentUser= user
        listener?.user = user
    }

    abstract  fun updateList()

    override fun onResume(){
        super.onResume()
        updateList()
    }


}