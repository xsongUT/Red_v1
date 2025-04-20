package com.example.red_v1.listeners

import android.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.red_v1.util.DATA_REDS
import com.example.red_v1.util.DATA_REDS_LIKES
import com.example.red_v1.util.DATA_RED_IMAGES
import com.example.red_v1.util.DATA_RED_USER_IDS
import com.example.red_v1.util.DATA_USERS
import com.example.red_v1.util.DATA_USER_FOLLOW
import com.example.red_v1.util.Red
import com.example.red_v1.util.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class RedListenerImpl(val redList: RecyclerView, var user: User?, val callback: HomeCallback?) :
    RedListener {
    private val firebaseDB = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid


    override fun onLayoutClick(red: Red?) {
        red?.let {
            val owner = red.userIds?.get(0)
            if (owner != userId) {
                if (user?.followUsers?.contains(owner) == true) {
                    AlertDialog.Builder(redList.context)
                        .setTitle("Unfollow ${red.username}?")
                        .setPositiveButton("yes") { dialog, which ->
                            redList.isClickable = false
                            var followedUsers = user?.followUsers
                            if(followedUsers == null){
                                followedUsers = arrayListOf()
                            }
                            followedUsers?.remove(owner)
                            firebaseDB.collection(DATA_USERS).document(userId!!).update(
                                DATA_USER_FOLLOW, followedUsers
                            )
                                .addOnSuccessListener {
                                    redList.isClickable = true
                                    callback?.onUserUpdated()
                                }
                                .addOnFailureListener {
                                    redList.isClickable = true
                                }
                        }
                        .setNegativeButton("cancel") { dialg, which -> }
                        .show()
                } else {
                    AlertDialog.Builder(redList.context)
                        .setTitle("Follow ${red.username}?")
                        .setPositiveButton("yes") { dialog, which ->
                            redList.isClickable = false
                            var followedUsers = user?.followUsers
                            if(followedUsers == null){
                                followedUsers = arrayListOf()
                            }
                            owner?.let {
                                followedUsers?.add(owner)
                                firebaseDB.collection(DATA_USERS).document(userId!!).update(
                                    DATA_USER_FOLLOW, followedUsers
                                )
                                    .addOnSuccessListener {
                                        redList.isClickable = true
                                        callback?.onUserUpdated()
                                    }
                                    .addOnFailureListener {
                                        redList.isClickable = true
                                    }
                            }
                        }
                        .setNegativeButton("cancel") { dialg, which -> }
                        .show()
                }
            }
        }
    }

    override fun onLike(red: Red?) {
        red?.let {
            redList.isClickable = false
            val likes = red.likes
            if (red.likes?.contains(userId) == true) {
                likes?.remove(userId)
            } else {
                likes?.add(userId!!)
            }
            firebaseDB.collection(DATA_REDS).document(red.redId!!).update(DATA_REDS_LIKES, likes)
                .addOnSuccessListener {
                    redList.isClickable = true
                    callback?.onRefresh()
                }
                .addOnFailureListener {
                    redList.isClickable = true
                }
        }
    }

    override fun onRepost(red: Red?) {
        red?.let {
            redList.isClickable = false
            val reposts = red.userIds
            if (reposts?.contains(userId) == true) {
                reposts?.remove(userId)
            } else {
                reposts?.add(userId!!)
            }
            firebaseDB.collection(DATA_REDS).document(red.redId!!)
                .update(DATA_RED_USER_IDS, reposts)
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