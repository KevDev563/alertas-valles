package kevin.rodriguez.alertas_valles_mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kevin.rodriguez.alertas_valles_mobile.data.model.AppMessage
import kevin.rodriguez.alertas_valles_mobile.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class MessagesViewModel(private val repository: AppRepository) : ViewModel() {

    private val _state = MutableStateFlow<UiState<List<AppMessage>>>(UiState.Loading)
    val state: StateFlow<UiState<List<AppMessage>>> = _state

    fun load() {
        viewModelScope.launch {
            _state.value = UiState.Loading
            try {
                _state.value = UiState.Success(repository.messages())
            } catch (e: Exception) {
                _state.value = UiState.Error(e.message ?: "Error al cargar mensajes")
            }
        }
    }
}
