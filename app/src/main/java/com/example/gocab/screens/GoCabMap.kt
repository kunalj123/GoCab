package com.example.gocab.screens

import android.Manifest
import android.app.Activity
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.example.gocab.components.DestinationSearchBar
import com.example.gocab.utilities.ExpandableFAB
import com.example.gocab.viewmodel.MapViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.widget.Autocomplete
import com.google.android.libraries.places.widget.model.AutocompleteActivityMode
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.Polyline
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.widgets.DisappearingScaleBar
import kotlinx.coroutines.launch

private enum class SearchTarget { PICKUP, DESTINATION, WAYPOINT_1, WAYPOINT_2, WAYPOINT_3 }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoCabMap(
    navController: NavController,
    viewModel: MapViewModel,
    onLogout: () -> Unit
) {
    val cameraPositionState = rememberCameraPositionState()
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()

    val userLocation by viewModel.userLocation.collectAsState()
    val routePoints by viewModel.routePoints.collectAsStateWithLifecycle()
    val markers by viewModel.markersInFirebase.collectAsStateWithLifecycle()

    val selectedCar by viewModel.selectedCar.collectAsState()
    val hasLocationPermission by viewModel.hasLocationPermission.collectAsState()
    val price by viewModel.price.collectAsState()
    val distanceKm by viewModel.distanceKm.collectAsState()
    val durationMin by viewModel.durationMin.collectAsState()

    var pickup by remember { mutableStateOf<LatLng?>(null) }
    var pickupLabel by remember { mutableStateOf("Current location") }
    var destination by remember { mutableStateOf<LatLng?>(null) }
    var destinationLabel by remember { mutableStateOf("Set destination") }
    var waypointLocations by remember { mutableStateOf(listOf<LatLng>()) }
    var waypointLabels by remember { mutableStateOf(listOf<String>()) }
    var activeSearchTarget by remember { mutableStateOf(SearchTarget.DESTINATION) }
    var showRideSheet by remember { mutableStateOf(false) }
    var fabExpanded by remember { mutableStateOf(false) }

    val autocompleteLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.let { intent ->
                val place = Autocomplete.getPlaceFromIntent(intent)
                val latLng = place.latLng ?: return@let
                when (activeSearchTarget) {
                    SearchTarget.PICKUP -> {
                        pickup = latLng
                        pickupLabel = place.name ?: "Pickup set"
                    }


                    SearchTarget.DESTINATION -> {
                        destination = latLng
                        destinationLabel = place.name ?: "Destination set"
                    }

                    SearchTarget.WAYPOINT_1, SearchTarget.WAYPOINT_2, SearchTarget.WAYPOINT_3 -> {
                        val index = activeSearchTarget.ordinal - 2
                        val mutableLocations = waypointLocations.toMutableList()
                        val mutableLabels = waypointLabels.toMutableList()
                        while (mutableLocations.size <= index) mutableLocations.add(latLng)
                        while (mutableLabels.size <= index) mutableLabels.add("Waypoint")
                        mutableLocations[index] = latLng
                        mutableLabels[index] = place.name ?: "Waypoint ${index + 1}"
                        waypointLocations = mutableLocations
                        waypointLabels = mutableLabels
                    }
                }

                coroutineScope.launch {
                    cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(latLng, 15f))
                }
            }
        }
    }

    fun launchAutocomplete(target: SearchTarget) {
        activeSearchTarget = target
        val fields = listOf(Place.Field.ID, Place.Field.NAME, Place.Field.LAT_LNG)
        val intent = Autocomplete.IntentBuilder(AutocompleteActivityMode.OVERLAY, fields).build(context)
        autocompleteLauncher.launch(intent)
    }

    fun calculateRouteIfPossible() {
        val dest = destination ?: return
        viewModel.fetchRoute(
            pickup = pickup,
            destination = dest,
            waypoints = waypointLocations
        )
    }


    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted -> viewModel.updatePermission(isGranted) }



    LaunchedEffect(Unit) {
        val granted = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.ACCESS_FINE_LOCATION
        ) == PackageManager.PERMISSION_GRANTED


        if (granted) viewModel.updatePermission(true)
        else permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    LaunchedEffect(userLocation) {
        userLocation?.let {
            if (pickup == null) pickup = it
            cameraPositionState.animate(CameraUpdateFactory.newLatLngZoom(it, 15f))
        }
    }

    val uiSettings = MapUiSettings(
        zoomGesturesEnabled = !showRideSheet,
        scrollGesturesEnabled = !showRideSheet,
        tiltGesturesEnabled = !showRideSheet,
        rotationGesturesEnabled = !showRideSheet
    )








    Box(modifier = Modifier.fillMaxSize()) {
        GoogleMap(
            modifier = Modifier.fillMaxSize(),
            cameraPositionState = cameraPositionState,
            uiSettings = uiSettings,
            onMapClick = {
                // Destination selection is intentionally disabled on map taps.
                // Users must choose pickup/destination/waypoints from Places search.

            }


        ) {
            userLocation?.let { Marker(state = MarkerState(it), title = "You are here") }
            destination?.let { Marker(state = MarkerState(it), title = "Destination") }
            waypointLocations.forEachIndexed { index, latLng ->
                Marker(state = MarkerState(latLng), title = "Waypoint ${index + 1}")
            }
            markers.forEach { marker ->

                Marker(state = MarkerState(marker.toLatLng()), title = marker.name)
            }
            if (routePoints.isNotEmpty()) Polyline(points = routePoints)
        }


        DestinationSearchBar(
            pickupLabel = pickupLabel,
            destinationLabel = destinationLabel,
            waypoints = waypointLabels,
            onPickupClick = { launchAutocomplete(SearchTarget.PICKUP) },
            onDestinationClick = { launchAutocomplete(SearchTarget.DESTINATION) },
            onWaypointClick = { launchAutocomplete(SearchTarget.values()[it + 2]) },
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        )

        if (!hasLocationPermission) {
            Card(
                modifier = Modifier
                    .align(Alignment.Center)
                    .padding(16.dp)
            ) { Text("Location permission is required for pickup detection", modifier = Modifier.padding(12.dp)) }
        }

        if (price != null) {
            Card(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 96.dp)
            ) {
                Text(
                    text = "₹${price!!.toInt()} • ${distanceKm?.let { String.format("%.1f", it) } ?: "0"} km • ${durationMin ?: 0} min",
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }




        ExpandableFAB(
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(16.dp),
            expanded = fabExpanded,
            onExpandedChange = { fabExpanded = it },
            isRouteReady = routePoints.isNotEmpty(),
            hasDestination = destination != null,
            onDrawRoute = {
                calculateRouteIfPossible()
                fabExpanded = false
            },
            onClearRoute = {
                viewModel.clearRoute()
                destination = null
                destinationLabel = "Set destination"
                waypointLocations = emptyList()
                waypointLabels = emptyList()
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

        val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
        if (showRideSheet) {
            ModalBottomSheet(
                onDismissRequest = { showRideSheet = false },
                sheetState = sheetState
            ) {

                RideSheetContent(
                    carTypes = viewModel.carTypes,
                    selectedCar = selectedCar,
                    price = price,
                    onCarSelected = viewModel::selectCar,
                    onConfirm = {
                        if ((viewModel.price.value ?: 0.0) > 0.0) {
                            showRideSheet = false
                            navController.navigate("ride")
                        }
                    }
                )
            }
        }
    }
}