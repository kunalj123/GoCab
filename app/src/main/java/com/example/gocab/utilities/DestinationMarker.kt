package com.example.gocab.utilities

import com.google.android.gms.maps.model.LatLng


data class DestinationMarker(
    val id : String,
    val latitude : Double,
    val longitude : Double,
    val name : String
){
    fun toLatLng() = LatLng(latitude, longitude)
}
