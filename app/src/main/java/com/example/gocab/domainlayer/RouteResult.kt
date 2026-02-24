package com.example.gocab.domainlayer

import com.google.android.gms.maps.model.LatLng

data class RouteResult(
    val points : List<LatLng>,
    val distanceMeters : Int,
    val durationSeconds : Int
)