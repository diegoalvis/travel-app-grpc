package com.diegoalvis.travelapp

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import com.diegoalvis.example.grpc.Destination
import com.diegoalvis.travelapp.utils.getDestinationsMockData
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MainViewModel : BaseMainViewModel() {

    private val service by lazy { TravelService() }

    private val _uiState = MutableStateFlow<UiState>(UiState.Initial)
    override val uiState = _uiState

    override fun loadData() {
        viewModelScope.launch {
            _uiState.emit(UiState.Loading)
            try {
                delay(2_000L)
                val destinations = service.getDestinations()
                _uiState.emit(UiState.Success(data = destinations))
            } catch (e: Exception) {
                _uiState.emit(UiState.Error())
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        service.close()
    }
}




/**
 * For testing purposes
 */
class MockMainViewModel(private val application: Application) : BaseMainViewModel() {

    override val uiState = flow<UiState> {
        delay(2_000L)
        val data = getDestinationsMockData(application.applicationContext)
        emit(UiState.Success(data = data))
    }
        .catch { emit(UiState.Error()) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(),
            initialValue = UiState.Loading
        )

    override fun loadData() {
        // TODO implement force refresh
    }

}


/**
 * This helps to resolve a diff ViewModel depending on IS_MOCK_ENABLED value
 * For testing purposes
 */
abstract class BaseMainViewModel : ViewModel() {

    abstract val uiState: StateFlow<UiState>

    abstract fun loadData()

    sealed interface UiState {
        data object Initial : UiState
        data object Loading : UiState
        data class Success(
            val data: List<Destination>
        ) : UiState

        data class Error(
            val errorMessage: String = "Something went wrong. Try again",
        ) : UiState
    }


    @Suppress("UNCHECKED_CAST")
    companion object {
        private const val IS_MOCK_ENABLED: Boolean = true

        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            override fun <T : ViewModel> create(
                modelClass: Class<T>,
                extras: CreationExtras
            ): T {
                val application = checkNotNull(extras[APPLICATION_KEY])
                val vm = if (IS_MOCK_ENABLED) MockMainViewModel(application) else MainViewModel()
                return vm as T
            }
        }
    }
}