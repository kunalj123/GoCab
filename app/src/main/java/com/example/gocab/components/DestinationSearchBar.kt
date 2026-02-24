package com.example.gocab.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun DestinationSearchBar(
    pickupLabel: String,
    destinationLabel: String,
    waypoints: List<String>,
    onPickupClick: () -> Unit,
    onDestinationClick: () -> Unit,
    onWaypointClick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        tonalElevation = 4.dp,
        shape = RoundedCornerShape(16.dp),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.padding(12.dp)
        ) {
            SearchFieldRow("Pickup", pickupLabel, onPickupClick)
            SearchFieldRow("Destination", destinationLabel, onDestinationClick)
            (0 until 3).forEach { index ->
                val value = waypoints.getOrNull(index) ?: "Add waypoint"
                SearchFieldRow("Waypoint ${index + 1}", value, { onWaypointClick(index) })
            }
        }
    }
}

@Composable
private fun SearchFieldRow(title: String, value: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 12.dp)
    ) {
        Text(
            text = "$title: $value",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}