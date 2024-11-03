package com.diegoalvis.travelapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.diegoalvis.example.grpc.Travel


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val mainViewModel by viewModels<MainViewModel>()
        setContent {
            Surface(color = MaterialTheme.colorScheme.background) {
                DestinationScreen(mainViewModel)
            }
        }
    }
}

@Composable
fun DestinationScreen(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()


    LaunchedEffect(Unit) {
        viewModel.loadDestination()
    }

    when (val state = uiState) {
        is MainViewModel.UiState.Initial -> {
            InitialScreen()
        }

        is MainViewModel.UiState.Loading -> {
            LoadingScreen()
        }

        is MainViewModel.UiState.Success -> {
            DestinationListScreen(destinations = state.data)
        }

        is MainViewModel.UiState.Error -> {
            ErrorScreen(errorMessage = state.errorMessage) {
                viewModel.loadDestination() // Retry on error
            }
        }
    }
}

@Composable
fun InitialScreen() {
    Text(
        text = "Welcome to the Travel App", modifier = Modifier
            .fillMaxSize()
            .wrapContentSize()
    )
}

@Composable
fun LoadingScreen() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
fun ErrorScreen(errorMessage: String, onRetry: () -> Unit) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(text = errorMessage, color = Color.Red)
            Spacer(modifier = Modifier.height(16.dp))
            Button(onClick = onRetry) {
                Text(text = "Retry")
            }
        }
    }
}

@Composable
fun DestinationListScreen(destinations: List<Travel.Destination>) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        items(items = destinations) { element ->
            DestinationItem(element)
            Spacer(modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}

@Composable
fun DestinationItem(destination: Travel.Destination) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(text = destination.title, style = MaterialTheme.typography.headlineLarge)
        Text(text = destination.description, style = MaterialTheme.typography.bodyMedium)
        AsyncImage(
            model = destination.imageUrl,
            contentDescription = destination.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))

        // Display "Things to Do" in destination
        Text(text = "Things to Do", style = MaterialTheme.typography.headlineLarge)
        destination.thingsToDoList.forEach { thingToDo ->
            ThingToDoItem(thingToDo)
        }

        // Display "Food and Drinks" in destination
        Text(text = "Food and Drinks", style = MaterialTheme.typography.headlineLarge)
        destination.foodAndDrinksList.forEach { foodAndDrink ->
            FoodAndDrinkItem(foodAndDrink)
        }
    }
}

@Composable
fun ThingToDoItem(thingToDo: Travel.ThingToDo) {
    Column {
        Text(text = thingToDo.title, style = MaterialTheme.typography.bodySmall)
        Text(text = "Reviews: ${thingToDo.reviewsCount} - Score: ${thingToDo.score}", style = MaterialTheme.typography.labelMedium)
        AsyncImage(
            model = thingToDo.imageUrl,
            contentDescription = thingToDo.title,
            modifier = Modifier
                .fillMaxWidth()
                .height(120.dp)
                .clip(RoundedCornerShape(8.dp)),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.height(8.dp))
    }
}

@Composable
fun FoodAndDrinkItem(foodAndDrink: Travel.FoodAndDrink) {
    Row(verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(
            model = foodAndDrink.imageUrl,
            contentDescription = foodAndDrink.name,
            modifier = Modifier
                .size(50.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = foodAndDrink.name, style = MaterialTheme.typography.bodyMedium)
    }
}
