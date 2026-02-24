package com.example.gocab.utilities

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun ExpandableFAB(
    modifier: Modifier = Modifier,
    isRouteReady: Boolean,
    hasDestination: Boolean,
    expanded: Boolean,
    onExpandedChange: (Boolean) -> Unit,
    onDrawRoute: () -> Unit,
    onClearRoute: () -> Unit,
    onRequestRide: () -> Unit
) {



    Box(modifier = modifier.wrapContentSize(), contentAlignment = Alignment.BottomEnd) {

        AnimatedVisibility(
            visible = expanded,
            enter = fadeIn() + expandVertically(expandFrom = Alignment.Bottom),
            exit = fadeOut() + shrinkVertically(shrinkTowards = Alignment.Bottom)
        ) {

            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(bottom = 70.dp) // space above main FAB
            ) {


                if (hasDestination && !isRouteReady) {

                    ExtendedFloatingActionButton(
                        text = { Text("Draw Route") },
                        onClick = {
                            onExpandedChange(false)
                            onDrawRoute()
                        },
                        icon = { Icon(Icons.Default.Edit, contentDescription = null) }
                    )

                }


                if (isRouteReady) {

                    ExtendedFloatingActionButton(
                        text = { Text("Request Ride") },
                        onClick = {
                            onExpandedChange(false)
                            onRequestRide()
                        },
                        icon = { Icon(Icons.Default.DirectionsCar, null) }
                    )


                    ExtendedFloatingActionButton(
                        text = { Text("Clear Route") },
                        onClick = {
                            onExpandedChange(false)
                            onClearRoute()
                        },
                        icon = { Icon(Icons.Default.Clear, contentDescription = null) }
                    )

                }


            }
        }

        // Main FAB (always visible)
        FloatingActionButton(
            onClick = { onExpandedChange(!expanded) },
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                if (expanded) Icons.Default.Close else Icons.Default.Menu,
                contentDescription = null
            )
        }
    }
}
