package kevin.rodriguez.alertas_valles_mobile.data.model

import com.google.gson.annotations.SerializedName

data class LoginRequest(
    val email: String,
    val password: String
)

data class RegisterRequest(
    val name: String,
    val email: String,
    val password: String
)

data class AuthResponse(
    val token: String,
    val user: User
)

data class Zone(
    val id: Int,
    val name: String,
    val slug: String,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
)

data class User(
    val id: Int,
    val name: String,
    val email: String,
    @SerializedName("email_verified_at") val emailVerifiedAt: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    val zones: List<Zone> = emptyList()
)
