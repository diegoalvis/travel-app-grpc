package com.diegoalvis.travelapp

import android.os.Bundle
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.diegoalvis.example.grpc.Destination
import com.diegoalvis.travelapp.components.DestinationDetailsScreen
import com.diegoalvis.travelapp.components.DestinationListScreen


class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val viewModel by viewModels<MainViewModel>()
        setContent {
            LaunchedEffect(Unit) {
                viewModel.loadDestination()
            }
            Surface(color = MaterialTheme.colorScheme.background) {
                HomeScreen(viewModel)
            }
        }
    }
}

@Composable
fun HomeScreen(viewModel: MainViewModel) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Column(modifier = Modifier.padding(16.dp)) {
        Text(
            text = "Are you ready to explore the world?",
            style = MaterialTheme.typography.headlineSmall,
            modifier = Modifier.padding(8.dp)
        )
        TextField(
            value = "",
            onValueChange = {},
            placeholder = { Text(text = "Find location") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            TextButton(onClick = { /* All */ }) { Text("All") }
            TextButton(onClick = { /* New */ }) { Text("New") }
            TextButton(onClick = { /* Trend */ }) { Text("Trend") }
            TextButton(onClick = { /* Mountain */ }) { Text("Mountain") }
        }
        Spacer(modifier = Modifier.height(16.dp))

        when (val state = uiState) {
            is MainViewModel.UiState.Initial -> {
                InitialScreen()
            }

            is MainViewModel.UiState.Loading -> {
                LoadingScreen()
            }

            is MainViewModel.UiState.Success -> {
                MainContent(destinations = state.data)
            }

            is MainViewModel.UiState.Error -> {
                ErrorScreen(errorMessage = state.errorMessage) {
                    viewModel.loadDestination() // Retry on error
                }
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
fun MainContent(destinations: List<Destination>) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "destinationList") {
        composable("destinationList") {
            DestinationListScreen(
                destinations = destinations,
                onDestinationClick = { destination ->
                    navController.navigate("destinationDetails/${destination.id}")
                }
            )
        }
        composable("destinationDetails/{destinationId}") { backStackEntry ->
            val destinationId = backStackEntry.arguments?.getString("destinationId")
            val destination = destinations.find { it.id == destinationId }
            destination?.let {
                DestinationDetailsScreen(it)
            }
        }
    }
}