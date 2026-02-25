package com.example.gocab.repositories

import android.util.Log
import com.example.gocab.domainlayer.RouteResult
import com.example.gocab.retrofit.DirectionServices
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.PolyUtil
import javax.inject.Inject
import javax.inject.Named

class DirectionRepositoryImpl @Inject constructor(
    @Named("mapApiKey") private val apiKey: String,
    private val directionServices: DirectionServices
) : DirectionRepository {


    override suspend fun getRoute(
        origin: LatLng,
        destination: LatLng
    ): RouteResult? {


        Log.d("ROUTE_DEBUG", "🔥 getRoute CALLED")

        try {







            val originStr = "${origin.latitude},${origin.longitude}"
            val destinationStr = "${destination.latitude},${destination.longitude}"

            val response = directionServices.getDirection(
                origin = originStr,
                destination = destinationStr,
                apiKey = apiKey
            )




            if (response.routes.isEmpty()) {
                Log.e("ROUTE_DEBUG", "No routes found!")
                return null
            }

            val route  = response.routes.firstOrNull() ?: return null
            val leg = route.legs.firstOrNull() ?: return null

            val distanceMeters = leg.distance.value
            val durationSeconds = leg.duration.value

            val polyline = route.overview_polyline.points
            val decodePoints = PolyUtil.decode(polyline)

            if (polyline.isEmpty()) {
                Log.e("ROUTE_DEBUG", "Polyline empty!")
                return null
            }

            return RouteResult(
                points =  decodePoints,
                distanceMeters = distanceMeters,
                durationSeconds = durationSeconds
            )







        } catch (e: Exception) {
            Log.e("ROUTE_DEBUG", "Route error", e)
            return null

        }


    }

    override suspend fun getRouteForMultipleMarkers(
        origin: LatLng,
        waypoints: List<LatLng>,
        destination: LatLng
    ): RouteResult? {


        val originStr = "${origin.latitude},${origin.longitude}"

        val destinationStr = "${destination.latitude},${destination.longitude}"


        val myWaypoints = if (waypoints.isNotEmpty()) {
            "optimize:true|" + waypoints.joinToString("|") {
                "${it.latitude},${it.longitude}"
            }
        } else {
            null
        }


        val response = directionServices.getDirection(
            origin = originStr,
            waypoints = myWaypoints,
            destination = destinationStr,
            apiKey = apiKey

        )


        if (response.routes.isEmpty()) return null
        val route = response.routes.firstOrNull() ?: return null
        val points = route.overview_polyline.points
        val distanceMeters = route.legs.sumOf { it.distance.value }
        val durationSeconds = route.legs.sumOf { it.duration.value }

        return RouteResult(
            points = PolyUtil.decode(points).map { latLng -> LatLng(latLng.latitude, latLng.longitude) },
            distanceMeters = distanceMeters,
            durationSeconds = durationSeconds
        )



    }
}


