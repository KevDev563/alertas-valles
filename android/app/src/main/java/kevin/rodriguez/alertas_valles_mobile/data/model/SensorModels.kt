package kevin.rodriguez.alertas_valles_mobile.data.model

import com.google.gson.annotations.SerializedName

data class SensorReadingItem(
    @SerializedName("sensor_name") val sensorName: String,
    @SerializedName("sensor_dato") val sensorDato: String,
    val fecha: String
)

data class LatestReadingsResponse(
    val estacion: String,
    val fecha: String?,
    val readings: List<SensorReadingItem> = emptyList()
)

data class SensorReading(
    val id: Int? = null,
    val estacion: String? = null,
    @SerializedName("sensor_name") val sensorName: String,
    @SerializedName("sensor_dato") val sensorDato: String,
    val fecha: String,
    @SerializedName("created_at") val createdAt: String? = null,
    @SerializedName("updated_at") val updatedAt: String? = null
)

data class ReadingsHistoryResponse(
    val data: List<SensorReading> = emptyList()
)
