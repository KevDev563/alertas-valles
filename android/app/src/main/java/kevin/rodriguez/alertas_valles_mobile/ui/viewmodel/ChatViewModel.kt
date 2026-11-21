package kevin.rodriguez.alertas_valles_mobile.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kevin.rodriguez.alertas_valles_mobile.data.model.ChatHistoryItem
import kevin.rodriguez.alertas_valles_mobile.data.repository.AppRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class ChatMessage(val role: String, val content: String)

data class ChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val sending: Boolean = false,
    val error: String? = null
)

class ChatViewModel(private val repository: AppRepository) : ViewModel() {

    private val _state = MutableStateFlow(ChatUiState())
    val state: StateFlow<ChatUiState> = _state

    fun send(text: String) {
        val message = text.trim()
        if (message.isEmpty() || _state.value.sending) return

        val userMsg = ChatMessage(role = "user", content = message)
        val history = _state.value.messages.map { ChatHistoryItem(it.role, it.content) }

        _state.update { it.copy(messages = it.messages + userMsg, sending = true, error = null) }

        viewModelScope.launch {
            try {
                val reply = repository.chat(message, history)
                _state.update {
                    it.copy(
                        messages = it.messages + ChatMessage(role = "assistant", content = reply),
                        sending = false
                    )
                }
            } catch (e: Exception) {
                _state.update {
                    it.copy(sending = false, error = e.message ?: "Error al enviar mensaje")
                }
            }
        }
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }
}
