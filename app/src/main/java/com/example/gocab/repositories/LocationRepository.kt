package com.example.gocab.repositories

import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.StateFlow

interface LocationRepository {

    val userLocation : StateFlow<LatLng?>



    fun startLocationUpdates()

    fun stopLocationUpdates()
}