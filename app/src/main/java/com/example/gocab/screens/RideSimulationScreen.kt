package com.example.gocab.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gocab.module.RideState
import com.example.gocab.viewmodel.MapViewModel
@Composable
fun RideSimulationScreen(
    viewModel: MapViewModel,
    onRideCompleted: () -> Unit
) {

    val rideState by viewModel.rideState.collectAsState()
    val driver by viewModel.driverProfile.collectAsState()
    val eta by viewModel.driverEtaMinutes.collectAsState()

    LaunchedEffect(rideState) {
        if (rideState == RideState.IDLE) {
            viewModel.startRideSimulation()
        }
    }

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {

        Column(horizontalAlignment = Alignment.CenterHorizontally) {

            driver?.let {
                Card(modifier = Modifier.padding(16.dp)) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text("Driver: ${it.name}", style = MaterialTheme.typography.titleMedium)
                        Text("Rating: ${it.rating} ★")
                        Text("Vehicle: ${it.vehicleType} (${it.vehicleNumber})")
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            when (rideState) {

                RideState.REQUESTED -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        CircularProgressIndicator()
                        Spacer(Modifier.height(12.dp))
                        Text("Finding nearby driver...")
                    }
                }

                RideState.ARRIVING -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Driver arriving in $eta min")
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator()
                    }
                }

                RideState.ARRIVED -> Text("Driver has arrived at pickup")


                RideState.TRIP_STARTED -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Trip started 🚗")
                        Spacer(Modifier.height(8.dp))
                        LinearProgressIndicator()
                    }
                }

                RideState.COMPLETED -> {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Trip completed 🎉")
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(onClick = onRideCompleted) {
                            Text("Proceed to Payment")
                        }
                    }
                }

                RideState.IDLE -> Unit
            }
        }
    }
}