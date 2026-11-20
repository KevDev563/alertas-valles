package kevin.rodriguez.alertas_valles_mobile.data.api

import kevin.rodriguez.alertas_valles_mobile.data.model.AlertsResponse
import kevin.rodriguez.alertas_valles_mobile.data.model.AuthResponse
import kevin.rodriguez.alertas_valles_mobile.data.model.ChatRequest
import kevin.rodriguez.alertas_valles_mobile.data.model.ChatResponse
import kevin.rodriguez.alertas_valles_mobile.data.model.LatestReadingsResponse
import kevin.rodriguez.alertas_valles_mobile.data.model.LoginRequest
import kevin.rodriguez.alertas_valles_mobile.data.model.MessagesResponse
import kevin.rodriguez.alertas_valles_mobile.data.model.RegisterRequest
import kevin.rodriguez.alertas_valles_mobile.data.model.User
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {

    @POST("api/register")
    suspend fun register(@Body request: RegisterRequest): AuthResponse

    @POST("api/login")
    suspend fun login(@Body request: LoginRequest): AuthResponse

    @GET("api/me")
    suspend fun me(): User

    @POST("api/logout")
    suspend fun logout()

    @GET("api/readings/latest")
    suspend fun latestReadings(
        @Query("estacion") estacion: String = "Estacion_02"
    ): LatestReadingsResponse

    @GET("api/alerts")
    suspend fun alerts(): AlertsResponse

    @GET("api/messages")
    suspend fun messages(): MessagesResponse

    @POST("api/chat")
    suspend fun chat(@Body request: ChatRequest): ChatResponse
}
