package com.example.red_v1.listeners

import com.example.red_v1.util.Red

interface RedListener {

    fun onLayoutClick(red: Red?)
    fun onLike(red:Red?)
    fun onRepost(red:Red?)
}