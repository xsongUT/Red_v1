package com.example.red_v1.listeners

import androidx.recyclerview.widget.RecyclerView
import com.example.red_v1.util.DATA_REDS
import com.example.red_v1.util.DATA_REDS_LIKES
import com.example.red_v1.util.DATA_RED_IMAGES
import com.example.red_v1.util.DATA_RED_USER_IDS
import com.example.red_v1.util.Red
import com.example.red_v1.util.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RedListenerImpl(val redList: RecyclerView, var user: User?, val callback:HomeCallback?): RedListener {
    private val firebaseDB = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid


    override fun onLayoutClick(red: Red?) {
    }

    override fun onLike(red: Red?) {
        red?.let {
            redList.isClickable =false
            val likes = red.likes
            if(red.likes?.contains(userId) == true){
                likes?.remove(userId)
            }else{
                likes?.add(userId!!)
            }
            firebaseDB.collection(DATA_REDS).document(red.redId!!).update(DATA_REDS_LIKES,likes)
                .addOnSuccessListener {
                    redList.isClickable = true
                    callback?.onRefresh()
                }
                .addOnFailureListener {
                    redList.isClickable = true }
        }
    }

    override fun onRepost(red: Red?) {
        red?.let{
            redList.isClickable = false
            val reposts = red.userIds
            if(reposts?.contains(userId) == true){
                reposts?.remove(userId)
            }else{
                reposts?.add(userId!!)
            }
            firebaseDB.collection(DATA_REDS).document(red.redId!!).update(DATA_RED_USER_IDS,reposts)
                .addOnSuccessListener {
                    redList.isClickable = true
                    callback?.onRefresh()
                }
                .addOnFailureListener {
                    redList.isClickable = true
                }
        }
    }
}