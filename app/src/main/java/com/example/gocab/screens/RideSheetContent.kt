package com.example.gocab.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
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

        Text(text = "Choose Your Ride", style = MaterialTheme.typography.titleLarge)

        Spacer(modifier = Modifier.height(16.dp))

        carTypes.forEach { car ->

            val isSelected = car == selectedCar


            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 6.dp)
                    .clickable { onCarSelected(car) },
                border = if (isSelected) BorderStroke(
                    1.dp,
                    MaterialTheme.colorScheme.primary
                ) else null,
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surface
                )
            ) {

                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.DirectionsCar, contentDescription = null)

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(text = car.name, style = MaterialTheme.typography.titleMedium)
                        Text("₹${car.ratePerKm.toInt()}/km • min ₹${car.minimumFare.toInt()}")

                        if (isSelected && price != null) {
                            Text(text = "Estimated fare ₹${price.toInt()}", color = MaterialTheme.colorScheme.primary)
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        Button(modifier = Modifier.fillMaxWidth(), onClick = onConfirm, enabled = selectedCar != null) {
            Text("Confirm Ride")
        }
    }
}
