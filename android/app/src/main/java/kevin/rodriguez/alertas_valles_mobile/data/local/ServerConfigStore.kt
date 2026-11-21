package kevin.rodriguez.alertas_valles_mobile.data.local

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

private val Context.serverConfigDataStore by preferencesDataStore(name = "server_config")

class ServerConfigStore(private val context: Context) {

    private val baseUrlKey = stringPreferencesKey("base_url")

    val baseUrlFlow: Flow<String> = context.serverConfigDataStore.data.map { prefs ->
        prefs[baseUrlKey] ?: DEFAULT_BASE_URL
    }

    suspend fun getBaseUrl(): String = baseUrlFlow.first()

    suspend fun saveBaseUrl(url: String) {
        context.serverConfigDataStore.edit { prefs ->
            prefs[baseUrlKey] = normalize(url)
        }
    }

    companion object {
        const val DEFAULT_BASE_URL = "http://10.0.2.2:8000/"

        fun normalize(url: String): String {
            val trimmed = url.trim()
            if (trimmed.isEmpty()) return DEFAULT_BASE_URL
            return if (trimmed.endsWith("/")) trimmed else "$trimmed/"
        }
    }
}
