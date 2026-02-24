package com.example.gocab.retrofit




data class WayPoint(
    val geocoder_status  : String,
    val place_id : String,
    val types : List<String>
)


data class DirectionResponse(
    val geocoded_waypoints : List<WayPoint>,
    val routes : List<Route>
)

data class Route(

    val overview_polyline : PolyLine,

    val legs : List<Leg>

)

data class PolyLine(
    val points : String
)

data class Leg(
    val distance : Distance,
    val duration : Duration
)

data class Distance(
    val text : String,
    val value : Int
)

data class Duration(
    val text : String,
    val value : Int
)




