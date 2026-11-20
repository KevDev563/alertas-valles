package kevin.rodriguez.alertas_valles_mobile.data.model

import com.google.gson.annotations.SerializedName

data class Alert(
    val id: Int,
    @SerializedName("zone_id") val zoneId: Int? = null,
    val title: String,
    val description: String? = null,
    val level: String? = null,
    val source: String? = null,
    val active: Boolean? = null,
    @SerializedName("starts_at") val startsAt: String? = null,
    @SerializedName("ends_at") val endsAt: String? = null,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null,
    val zone: Zone? = null
)

data class AlertsResponse(
    val data: List<Alert> = emptyList()
)
