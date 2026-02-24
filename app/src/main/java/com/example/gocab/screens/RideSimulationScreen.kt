package com.example.gocab.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.LinearProgressIndicator
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

    LaunchedEffect(rideState) {
        if (rideState == RideState.IDLE) viewModel.startRideSimulation()
    }

    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {

        when (rideState) {

            RideState.REQUESTED -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Spacer(modifier = Modifier.height(16.dp))
                    Text("Searching for driver...")
                }
            }

            RideState.IN_PROGRESS -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Ride in Progress 🚗")
                    Spacer(modifier = Modifier.height(8.dp))
                    LinearProgressIndicator()
                }
            }

            RideState.COMPLETED -> {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Ride Completed 🎉")
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = onRideCompleted) {
                        Text("Proceed to Payment")
                    }
                }
            }

            else -> Unit
        }
    }
}
