package com.example.gocab.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.gocab.module.CarType


@Composable
fun RideSheetContent(
    carTypes: List<CarType>,
    selectedCar: CarType?,
    price: Double?,
    onCarSelected: (CarType) -> Unit,
    onConfirm: () -> Unit
) {

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {

        Text(
            text = "Choose Your Ride",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(modifier = Modifier.height(16.dp))

        carTypes.forEach { car ->

            val isSelected = remember(selectedCar) {
                car == selectedCar
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable {
                        onCarSelected(car)
                    },
                colors = CardDefaults.cardColors(
                    containerColor =
                        if (isSelected)
                            MaterialTheme.colorScheme.primaryContainer
                        else
                            MaterialTheme.colorScheme.surface
                )
            ) {

                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    Icon(
                        imageVector = Icons.Default.DirectionsCar,
                        contentDescription = null
                    )

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(text = car.name)
                        Text("₹${car.ratePerKm}/km")

                        if (isSelected && price != null) {
                            Text(
                                text = "₹${price.toInt()}",
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(
            modifier = Modifier.fillMaxWidth(),
            onClick = onConfirm,
            enabled = selectedCar != null
        )
        {
            Text("Confirm Ride")
        }
    }
}


