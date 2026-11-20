package kevin.rodriguez.alertas_valles_mobile.data.model

import com.google.gson.annotations.SerializedName

data class AppMessage(
    val id: Int,
    @SerializedName("zone_id") val zoneId: Int? = null,
    val title: String,
    val body: String,
    val source: String? = null,
    @SerializedName("published_at") val publishedAt: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    val zone: Zone? = null
)

data class MessagesResponse(
    val data: List<AppMessage> = emptyList()
)
