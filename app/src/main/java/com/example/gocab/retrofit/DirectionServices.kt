package com.example.gocab.retrofit

import android.graphics.Path
import retrofit2.http.GET
import retrofit2.http.Query


interface DirectionServices {

//    https://maps.googleapis.com/maps/api/directions/json
//    ?destination=Montreal
//    &origin=Toronto
//    &key=AIzaSyD8-EAnPV7SOnkf1Rd2TRCWnsw58HJzYdE

    @GET("directions/json")
    suspend fun getDirection(
        @Query("destination") destination : String,
        @Query("origin") origin : String,
        @Query("waypoints") waypoints : String? = null,
        @Query("key") apiKey : String
    ) : DirectionResponse
}
