package kevin.rodriguez.alertas_valles_mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import kevin.rodriguez.alertas_valles_mobile.data.api.ApiClient
import kevin.rodriguez.alertas_valles_mobile.data.local.ServerConfigStore
import kevin.rodriguez.alertas_valles_mobile.data.local.TokenStore
import kevin.rodriguez.alertas_valles_mobile.data.repository.AppRepository
import kevin.rodriguez.alertas_valles_mobile.ui.screens.HomeScreen
import kevin.rodriguez.alertas_valles_mobile.ui.screens.LoginScreen
import kotlinx.coroutines.runBlocking

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                AlertasApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AlertasApp() {
    val context = LocalContext.current
    val serverConfigStore = remember { ServerConfigStore(context) }
    val tokenStore = remember { TokenStore(context) }

    val savedBaseUrl by serverConfigStore.baseUrlFlow.collectAsState(initial = ServerConfigStore.DEFAULT_BASE_URL)
    val token by tokenStore.tokenFlow.collectAsState(initial = null)

    fun buildRepository(baseUrl: String): AppRepository {
        val api = ApiClient.create(baseUrl) { runBlocking { tokenStore.getToken() } }
        return AppRepository(api, tokenStore)
    }

    var currentBaseUrl by remember { mutableStateOf(ServerConfigStore.DEFAULT_BASE_URL) }
    var repository by remember { mutableStateOf(buildRepository(ServerConfigStore.DEFAULT_BASE_URL)) }
    var authScreen by remember { mutableStateOf("login") } // "login" | "register"
    var loginInfoMessage by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(savedBaseUrl) {
        currentBaseUrl = savedBaseUrl
        repository = buildRepository(savedBaseUrl)
    }

    if (token.isNullOrBlank()) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(if (authScreen == "register") "Registro" else "Inicio de sesión") },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                )
            }
        ) { innerPadding ->
            if (authScreen == "register") {
                kevin.rodriguez.alertas_valles_mobile.ui.screens.RegisterScreen(
                    modifier = Modifier.padding(innerPadding),
                    baseUrl = currentBaseUrl.ifBlank { savedBaseUrl },
                    onSaveBaseUrl = { url ->
                        val normalized = ServerConfigStore.normalize(url)
                        serverConfigStore.saveBaseUrl(normalized)
                        currentBaseUrl = normalized
                        repository = buildRepository(normalized)
                    },
                    buildRepository = { buildRepository(it) },
                    onRegistered = { message ->
                        loginInfoMessage = message
                        authScreen = "login"
                    },
                    onBackToLogin = { authScreen = "login" }
                )
            } else {
                LoginScreen(
                    modifier = Modifier.padding(innerPadding),
                    initialBaseUrl = savedBaseUrl,
                    infoMessage = loginInfoMessage,
                    onSaveBaseUrl = { url ->
                        val normalized = ServerConfigStore.normalize(url)
                        serverConfigStore.saveBaseUrl(normalized)
                        currentBaseUrl = normalized
                        repository = buildRepository(normalized)
                    },
                    buildRepository = { buildRepository(it) },
                    onLoginSuccess = {
                        loginInfoMessage = null
                        /* tokenFlow actualizará y mostrará Home */
                    },
                    onGoToRegister = { authScreen = "register" }
                )
            }
        }
    } else {
        authScreen = "login"
        HomeScreen(
            repository = repository,
            baseUrl = currentBaseUrl,
            onLogout = { /* tokenFlow actualizará y mostrará Login */ }
        )
    }
}
