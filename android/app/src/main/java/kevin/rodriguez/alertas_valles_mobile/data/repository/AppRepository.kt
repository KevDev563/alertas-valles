package kevin.rodriguez.alertas_valles_mobile.data.repository

import kevin.rodriguez.alertas_valles_mobile.data.api.ApiService
import kevin.rodriguez.alertas_valles_mobile.data.local.TokenStore
import kevin.rodriguez.alertas_valles_mobile.data.model.Alert
import kevin.rodriguez.alertas_valles_mobile.data.model.AppMessage
import kevin.rodriguez.alertas_valles_mobile.data.model.ChatRequest
import kevin.rodriguez.alertas_valles_mobile.data.model.LatestReadingsResponse
import kevin.rodriguez.alertas_valles_mobile.data.model.LoginRequest
import kevin.rodriguez.alertas_valles_mobile.data.model.RegisterRequest
import kevin.rodriguez.alertas_valles_mobile.data.model.User

class AppRepository(
    private val api: ApiService,
    private val tokenStore: TokenStore
) {
    val tokenFlow = tokenStore.tokenFlow

    suspend fun login(email: String, password: String): User {
        val response = api.login(LoginRequest(email, password))
        tokenStore.saveToken(response.token)
        return response.user
    }

    suspend fun register(name: String, email: String, password: String): User {
        val response = api.register(RegisterRequest(name, email, password))
        // No guardamos el token aquí: tras registrarse, el usuario vuelve al login.
        return response.user
    }

    suspend fun me(): User = api.me()

    suspend fun logout() {
        try {
            api.logout()
        } finally {
            tokenStore.clearToken()
        }
    }

    suspend fun latestReadings(estacion: String = "Estacion_02"): LatestReadingsResponse =
        api.latestReadings(estacion)

    suspend fun alerts(): List<Alert> = api.alerts().data

    suspend fun messages(): List<AppMessage> = api.messages().data

    suspend fun chat(message: String, history: List<kevin.rodriguez.alertas_valles_mobile.data.model.ChatHistoryItem>? = null): String =
        api.chat(ChatRequest(message, history)).reply
}
