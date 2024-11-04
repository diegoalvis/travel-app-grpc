package com.diegoalvis.travelapp.components

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.diegoalvis.example.grpc.FoodAndDrink


@Composable
fun FoodAndDrinkItem(foodAndDrink: FoodAndDrink) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Spacer(modifier = Modifier.width(8.dp))
        AsyncImage(
//            model = foodAndDrink.imageUrl,
            model = "https://images.unsplash.com/photo-1502602898657-3e91760cbb34",
            contentDescription = foodAndDrink.name,
            modifier = Modifier
                .size(80.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
    }
}
