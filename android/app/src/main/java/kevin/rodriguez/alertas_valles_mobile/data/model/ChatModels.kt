package kevin.rodriguez.alertas_valles_mobile.data.model

data class ChatHistoryItem(
    val role: String,
    val content: String
)

data class ChatRequest(
    val message: String,
    val history: List<ChatHistoryItem>? = null
)

data class ChatResponse(
    val reply: String
)
