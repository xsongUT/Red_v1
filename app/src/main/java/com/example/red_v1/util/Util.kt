package com.example.red_v1.util

import android.graphics.drawable.Drawable
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import androidx.swiperefreshlayout.widget.CircularProgressDrawable

import com.example.red_v1.R
import android.content.Context
import java.text.DateFormat
import java.util.Date


fun ImageView.loadUrl(url: String?, errorDrawable: Int = R.drawable.empty){
    context?.let {
        val options = RequestOptions().placeholder(progressDrawable(context))
            .error(errorDrawable)
        Glide.with(context.applicationContext).load(url).apply(options).into(this)
    }
}

fun progressDrawable(context: Context): CircularProgressDrawable{
        return CircularProgressDrawable(context).apply {
            strokeWidth = 5f
            centerRadius = 30f
            start()
        }
}

fun getDate(s:Long?):String{
    s?.let{
        val df = DateFormat.getDateInstance()
        return  df.format(Date(it))
    }
    return "unknown"
}