package com.diegoalvis.travelapp.components

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.diegoalvis.example.grpc.ThingToDo

@Composable
fun ThingToDoItem(thingToDo: ThingToDo) {
    Column {
        Text(text = thingToDo.title, style = MaterialTheme.typography.bodySmall)
        Text(text = "${thingToDo.score}", style = MaterialTheme.typography.labelMedium)
        AsyncImage(
            model = thingToDo.imageUrl,
            contentDescription = thingToDo.title,
            modifier = Modifier
                .width(120.dp)
                .height(100.dp)
                .clip(RoundedCornerShape(16.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}