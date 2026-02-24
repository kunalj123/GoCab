package com.example.gocab.repositories

import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import android.util.Log
import androidx.annotation.RequiresPermission

import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.maps.model.LatLng

import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.security.Permission
import java.util.jar.Manifest
import javax.inject.Inject

class LocationRepositoryImpl @Inject  constructor(
    @ApplicationContext private val  context : Context,

) : LocationRepository {

    private val _userLocation  = MutableStateFlow<LatLng?>(null)

    override val userLocation: StateFlow<LatLng?> = _userLocation.asStateFlow()





    private val fusedLocationClient: FusedLocationProviderClient
            by lazy {
                LocationServices.getFusedLocationProviderClient(context)
            }



    private val locationCallback = object : LocationCallback(){

        override fun onLocationResult(result : LocationResult) {

            val location = result.lastLocation?.let { currentLocation ->

                _userLocation.value = LatLng(
                    currentLocation.latitude,
                    currentLocation.longitude
                )

            }
        }
    }






    @RequiresPermission(allOf = [android.Manifest.permission.ACCESS_FINE_LOCATION, android.Manifest.permission.ACCESS_COARSE_LOCATION])
    override fun startLocationUpdates() {
        val locationRequest = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY,
            5000L
        ).apply {
          setMinUpdateIntervalMillis(5000)
            setWaitForAccurateLocation(true)
        }.build()


        try{

            fusedLocationClient.requestLocationUpdates(
                locationRequest,
                locationCallback,
                Looper.getMainLooper()
            )
        }catch (e : SecurityException){
            Log.v("LocationRepo",e.message ?: "Location permission not granted")
        }




    }


    override fun stopLocationUpdates() {
        fusedLocationClient.removeLocationUpdates(locationCallback)

    }







}