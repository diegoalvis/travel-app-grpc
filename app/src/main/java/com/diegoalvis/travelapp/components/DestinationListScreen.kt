package com.diegoalvis.travelapp.components

import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import com.diegoalvis.example.grpc.Destination


@Composable
fun DestinationListScreen(
    destinations: List<Destination>,
    onDestinationClick: (Destination) -> Unit
) {
    LazyColumn() {
        items(destinations) { element ->
            DestinationCard(
                destination = element,
                onClick = { onDestinationClick(it) }
            )
        }
    }
}