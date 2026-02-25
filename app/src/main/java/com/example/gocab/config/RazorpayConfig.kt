package com.example.gocab.config

import android.content.Context
import com.example.gocab.R
object RazorpayConfig{
    fun getKeyId(context: Context): String = context.getString(R.string.razorpay_key_id)
}