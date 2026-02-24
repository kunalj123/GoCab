package com.example.gocab.screens

import android.Manifest
import android.app.Activity

import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp

import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.gocab.utilities.DestinationMarker
import com.example.gocab.utilities.ExpandableFAB
import com.example.gocab.viewmodel.MapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.widgets.DisappearingScaleBar

import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Polyline
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoCabMap(
    navController: NavController,
    viewModel: MapViewModel
) {

    val cameraPositionState = rememberCameraPositionState()


        val viewModel: MapViewModel = hiltViewModel()

    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()


    val userLocation by viewModel.userLocation.collectAsState()
    val routePoints by viewModel.routePoints.collectAsStateWithLifecycle()
    val selectedPosition by viewModel.selectedLocation.collectAsStateWithLifecycle()
    var markerPosition by remember { mutableStateOf<LatLng?>(null) }
    val markers by viewModel.markersInFirebase.collectAsStateWithLifecycle()



    val autocompleteLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.StartActivityForResult()
        ) { result ->

            if (result.resultCode == Activity.RESULT_OK) {

                result.data?.let { intent ->

                    val place = Autocomplete.getPlaceFromIntent(intent)
                    val latLng = place.latLng

                    latLng?.let {

                        // Place marker
                        markerPosition = it
                        viewModel.setSelectedLocation(it)

                        // Move camera

                        coroutineScope.launch {

                            cameraPositionState.animate(
                                CameraUpdateFactory.newLatLngZoom(it, 15f)
                            )

                        }


                        //  Auto draw route
                        viewModel.fetchRouteFromCurrentPositionToMarker(it)
                    }

                }


            }
        }




    val permissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->

            viewModel.updatePermission(isGranted)

        }


    LaunchedEffect(Unit) {

        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION

        ) == PackageManager.PERMISSION_GRANTED


        if (granted) {
            viewModel.updatePermission(true)
        } else {
            permissionLauncher.launch(
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        }
    }

    LaunchedEffect(userLocation) {
        userLocation?.let {
            cameraPositionState.animate(
                CameraUpdateFactory.newLatLngZoom(it, 15f)
            )
        }
    }

    LaunchedEffect(routePoints) {
        Log.d("Route", "Points: $routePoints")
    }


//    LaunchedEffect(Unit) {
//    viewModel.addMarker(
//        userLocation!!,
//        "Current Location",
//        onSuccess = {
//            Log.v("Tagy", "Marker added Succesfully")
//        },
//        onFailure = { e ->
//            Log.v(
//                "Tagy", e.message.toString()
//
//
//            )
//        }
//    )
//
//}






    LaunchedEffect(routePoints) {
        Log.d("route", "Route points = $routePoints")
    }


    fun launchAutocomplete() {

        val fields = listOf(
            Place.Field.ID,
            Place.Field.NAME,
            Place.Field.LAT_LNG
        )

        val intent = Autocomplete.IntentBuilder(
            AutocompleteActivityMode.OVERLAY,
            fields
        ).build(context)

        autocompleteLauncher.launch(intent)
    }

    var showRideSheet by remember { mutableStateOf(false) }

    val uiSettings = MapUiSettings(
        zoomGesturesEnabled = !showRideSheet,
        scrollGesturesEnabled = !showRideSheet,
        tiltGesturesEnabled = !showRideSheet,
        rotationGesturesEnabled = !showRideSheet
    )








    Box(
        modifier = Modifier.fillMaxSize()
    ) {



        Card(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(16.dp)
        ) {
            Text(
                text = "Search Destination",
                modifier = Modifier
                    .padding(16.dp)
                    .clickable {
                        launchAutocomplete()
                    },
                style = MaterialTheme.typography.bodyLarge
            )
        }

        // 1- Google Map
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = uiSettings,


            onMapClick = { myMarker ->

                markerPosition = myMarker
                viewModel.setSelectedLocation(myMarker)


            }


        ) {




            userLocation?.let {
                Marker(
                    state = MarkerState(it),
                    title = "You are here"
                )
            }



            markers.forEach { marker ->

                Marker(
                    state = MarkerState(marker.toLatLng()),
                    title = marker.name,
                    snippet = "Marker in ${marker.name}"
                )

            }




            markerPosition?.let {

                Marker(
                    state = MarkerState(it),
                    snippet = "${it.latitude},${it.longitude}",
                    title = "This is destination location",
                    onClick = {

                        viewModel.setSelectedLocation(markerPosition!!)
                        false
                    }

                )
            }


            //Draw a polyline by taking route points
            if(routePoints.isNotEmpty()){
                Polyline(points = routePoints)
            }


        }


        val selectedCar by viewModel.selectedCar.collectAsState()
        val price by viewModel.price.collectAsState()

        if (price != null) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp)
            ) {
                Text(
                    text = "₹${price!!.toInt()}",
                    modifier = Modifier.padding(16.dp),
                    style = MaterialTheme.typography.titleLarge
                )
            }
        }


        val hasDestination = markerPosition != null
        val isRouteReady = routePoints.isNotEmpty()



        val sheetState  = rememberModalBottomSheetState(
            skipPartiallyExpanded = true
        )

        var fabExpanded by remember { mutableStateOf(false)}

        //2 - ExpandableFAB

        ExpandableFAB(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),

            expanded = fabExpanded,
            onExpandedChange = { fabExpanded = it},

            isRouteReady = isRouteReady,
            hasDestination = hasDestination,

            onDrawRoute = {
                markerPosition?.let {
                    viewModel.fetchRouteFromCurrentPositionToMarker(it)
                }
            },

            onClearRoute = {
                viewModel.clearRoute()
                fabExpanded = false
            },

            onRequestRide = {
                fabExpanded = false
                showRideSheet = true
                Log.d("FAB", "Request Ride Clicked")
            }
        )


        //3 - Scalebar

        DisappearingScaleBar(
            modifier = Modifier
                .padding(top = 5.dp)
                .align(Alignment.TopStart),
            cameraPositionState = cameraPositionState
        )



        if (showRideSheet) {

            ModalBottomSheet(
                onDismissRequest = {
                    showRideSheet = false
                },
                sheetState = sheetState
            ) {

                RideSheetContent(
                    carTypes = viewModel.carTypes,
                    selectedCar = selectedCar,
                    price = price,
                    onCarSelected = { car ->
                        viewModel.selectCar(car)
                    },
                    onConfirm = {
                        val currentPrice = viewModel.price.value ?: 0.0

                        if (currentPrice > 0) {
                            showRideSheet = false
                            navController.navigate("ride/${currentPrice.toFloat()}")
                        }
                    }
                )
            }
        }


    }



}


