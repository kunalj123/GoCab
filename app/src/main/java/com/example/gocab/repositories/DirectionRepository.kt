package com.example.gocab.repositories

import com.example.gocab.domainlayer.RouteResult
import com.google.android.gms.maps.model.LatLng


interface DirectionRepository {

    suspend fun getRoute(
        origin : LatLng,
        destination : LatLng
    ) : RouteResult?

    suspend fun getRouteForMultipleMarkers(
        origin : LatLng,
        waypoints : List<LatLng>,
        destination : LatLng,
    ) : List<LatLng>?






















}