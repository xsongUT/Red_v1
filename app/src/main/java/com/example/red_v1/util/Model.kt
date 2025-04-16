package com.example.red_v1.util

import com.google.firebase.Timestamp

data class User(
    val email:String? = "",
    val username:String? = "",
    val imageUrl:String?="",
    val followHashtags:ArrayList<String>? =  arrayListOf(),
    val followUsers:ArrayList<String>? = arrayListOf()

)

data class Red(
    val redId: String? = "",
    val userIds: ArrayList<String>? = arrayListOf(),
    val username: String? = "",
    val text: String? = "",
    val imageUrl: String? = "",
    val timestamp: Long? = 0,
    val hashtags: ArrayList<String>? = arrayListOf(),
    val likes:ArrayList<String>? = arrayListOf()

)