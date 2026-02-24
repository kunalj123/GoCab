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

    suspend override fun getRouteForMultipleMarkers(
        origin: LatLng,
        waypoints: List<LatLng>,
        destination: LatLng
    ): List<LatLng>? {


        val originStr = "${origin.latitude}, ${origin.longitude}"

        val destinationStr = "${destination.latitude},${destination.longitude}"


        val myWaypoints = if (waypoints.size > 2) {

            "optimize:true|" + waypoints.subList(1, waypoints.size - 1).joinToString("|") {
                "${it.latitude},${it.longitude}"
            }


        }else{
            waypoints.joinToString("|") {
                "${it.latitude},${it.longitude}"
            }
        }


        val response = directionServices.getDirection(
            origin = originStr,
            waypoints = myWaypoints,
            destination = destinationStr,
            apiKey = apiKey

        )


        val points = response.routes[0].overview_polyline.points

        return PolyUtil.decode(points).map { latLng ->
            LatLng(
                latLng.latitude,latLng.longitude
            )
        }


    }
}


