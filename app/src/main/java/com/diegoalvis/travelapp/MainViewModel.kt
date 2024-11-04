package com.diegoalvis.travelapp

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.diegoalvis.example.grpc.Destination
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class MainViewModel : ViewModel() {

    private val service by lazy { TravelService() }

    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    val uiState = _uiState.asStateFlow()

    fun loadDestination() {
        viewModelScope.launch {
        _uiState.emit(UiState.Loading)
            try {
                val destinations = service.getDestinations()
                _uiState.emit(UiState.Success(data = destinations))
            } catch (e: Exception) {
                _uiState.emit(UiState.Error(errorMessage = "Something went wrong. Try again"))
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        service.close()
    }

    sealed interface UiState {
        data object Initial : UiState
        data object Loading : UiState
        data class Success(
            val data: List<Destination>
        ) : UiState

        data class Error(
            val errorMessage: String,
        ) : UiState
    }

}