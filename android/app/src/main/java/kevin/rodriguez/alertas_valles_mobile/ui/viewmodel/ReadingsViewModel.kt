package kevin.rodriguez.alertas_valles_mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kevin.rodriguez.alertas_valles_mobile.data.model.LatestReadingsResponse
import kevin.rodriguez.alertas_valles_mobile.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class ReadingsViewModel(private val repository: AppRepository) : ViewModel() {

    private val _state = MutableStateFlow<UiState<LatestReadingsResponse>>(UiState.Loading)
    val state: StateFlow<UiState<LatestReadingsResponse>> = _state

    fun load(estacion: String = "Estacion_02") {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                val data = repository.latestReadings(estacion)
                _state.value = UiState.Success(data)
            } catch (e: Exception) {
                _state.value = UiState.Error(e.message ?: "Error al cargar lecturas")
            }
        }
    }
}
