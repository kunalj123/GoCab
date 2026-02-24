package com.example.gocab.module



data class User(
    val userId : String = "",
    val name : String = "",
    val email : String = "",
    val phoneNumber : String = "",
    val createdAt : Long = System.currentTimeMillis()
)
