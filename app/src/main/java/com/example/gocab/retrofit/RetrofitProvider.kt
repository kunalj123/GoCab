package com.example.gocab.retrofit

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitProvider{

    fun createDirectionService(
        baseUrl : String
    ): DirectionServices {

        return Retrofit.Builder()
            .baseUrl(baseUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(DirectionServices::class.java)

    }


}