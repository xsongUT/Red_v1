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

// Listener implementation that handles user interactions with Red items (e.g., follow/unfollow, like, repost)
class RedListenerImpl(val redList: RecyclerView, var user: User?, val callback: HomeCallback?) :
    RedListener {
    private val firebaseDB = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid


    // Called when a Red layout is clicked (used to follow/unfollow user)
    override fun onLayoutClick(red: Red?) {
        red?.let {
            val owner = red.userIds?.get(0)
            // Check if clicked red is not from current user
            if (owner != userId) {
                if (user?.followUsers?.contains(owner) == true) {
                    // Prompt user to unfollow
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
                    // Prompt user to follow
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
                                // Update Firestore with new follow list
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

    // Called when a Red is liked/unliked
    override fun onLike(red: Red?) {
        red?.let {
            redList.isClickable = false
            val likes = red.likes
            if (red.likes?.contains(userId) == true) {
                // Unlike
                likes?.remove(userId)
            } else {
                // Unlike
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

    // Called when a post is reposted/un-reposted
    override fun onRepost(red: Red?) {
        red?.let {
            redList.isClickable = false
            val reposts = red.userIds
            if (reposts?.contains(userId) == true) {
                // Remove repost
                reposts?.remove(userId)
            } else {
                // Add repost
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