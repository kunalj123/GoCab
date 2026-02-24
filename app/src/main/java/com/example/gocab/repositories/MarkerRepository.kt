package com.example.gocab.repositories

import com.example.gocab.utilities.DestinationMarker
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.Flow


interface MarkerRepository {

    suspend fun addMarker(marker: LatLng, name: String)

     fun getAllMarkersFromFirestore() : Flow<List<DestinationMarker>>

}








