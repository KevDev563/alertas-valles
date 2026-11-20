package kevin.rodriguez.alertas_valles_mobile

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kevin.rodriguez.alertas_valles_mobile.data.api.ApiClient
import kevin.rodriguez.alertas_valles_mobile.data.local.ServerConfigStore
import kevin.rodriguez.alertas_valles_mobile.data.local.TokenStore
import kevin.rodriguez.alertas_valles_mobile.data.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

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

    var currentBaseUrl by remember(savedBaseUrl) { mutableStateOf(savedBaseUrl) }

    fun buildRepository(baseUrl: String): AppRepository {
        val api = ApiClient.create(baseUrl) { runBlocking { tokenStore.getToken() } }
        return AppRepository(api, tokenStore)
    }

    var repository by remember(currentBaseUrl) { mutableStateOf(buildRepository(currentBaseUrl)) }

    // Recrear repositorio si cambia la URL guardada externamente
    LaunchedEffect(savedBaseUrl) {
        if (savedBaseUrl != currentBaseUrl) {
            currentBaseUrl = savedBaseUrl
        }
        repository = buildRepository(savedBaseUrl)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Alertas Valles") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        if (token.isNullOrBlank()) {
            LoginScreen(
                modifier = Modifier.padding(innerPadding),
                initialBaseUrl = savedBaseUrl,
                onBaseUrlChange = { currentBaseUrl = it },
                onSaveBaseUrl = { url ->
                    val normalized = ServerConfigStore.normalize(url)
                    serverConfigStore.saveBaseUrl(normalized)
                    currentBaseUrl = normalized
                    repository = buildRepository(normalized)
                },
                buildRepository = { buildRepository(it) },
                repository = repository
            )
        } else {
            HomeScreen(
                modifier = Modifier.padding(innerPadding),
                repository = repository,
                baseUrl = currentBaseUrl
            )
        }
    }
}

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    initialBaseUrl: String,
    onBaseUrlChange: (String) -> Unit,
    onSaveBaseUrl: suspend (String) -> Unit,
    buildRepository: (String) -> AppRepository,
    repository: AppRepository
) {
    val scope = rememberCoroutineScope()
    var baseUrl by remember(initialBaseUrl) { mutableStateOf(initialBaseUrl) }
    var email by remember { mutableStateOf("demo@alertas-valles.test") }
    var password by remember { mutableStateOf("password") }
    var name by remember { mutableStateOf("") }
    var status by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Conexión al servidor", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = baseUrl,
            onValueChange = {
                baseUrl = it
                onBaseUrlChange(it)
            },
            label = { Text("URL del servidor (ej. http://192.168.1.6:8000/)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    scope.launch {
                        loading = true
                        status = "Probando conexión (GET /api/alerts)..."
                        try {
                            val repo = buildRepository(ServerConfigStore.normalize(baseUrl))
                            val count = withContext(Dispatchers.IO) { repo.alerts().size }
                            onSaveBaseUrl(baseUrl)
                            status = "OK: conexión exitosa. Alertas recibidas: $count"
                        } catch (e: Exception) {
                            status = "Error de conexión: ${e.message ?: e.toString()}"
                        } finally {
                            loading = false
                        }
                    }
                },
                enabled = !loading
            ) {
                Text("Probar conexión (GET)")
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("Inicio de sesión", style = MaterialTheme.typography.titleMedium)
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Correo") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Contraseña") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre (solo para registro)") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            Button(
                onClick = {
                    scope.launch {
                        loading = true
                        status = "Iniciando sesión..."
                        try {
                            onSaveBaseUrl(baseUrl)
                            val user = withContext(Dispatchers.IO) { repository.login(email, password) }
                            status = "Sesión iniciada: ${user.name}"
                        } catch (e: Exception) {
                            status = "Error al iniciar sesión: ${e.message ?: e.toString()}"
                        } finally {
                            loading = false
                        }
                    }
                },
                enabled = !loading
            ) { Text("Iniciar sesión") }

            OutlinedButton(
                onClick = {
                    scope.launch {
                        loading = true
                        status = "Registrando usuario..."
                        try {
                            onSaveBaseUrl(baseUrl)
                            val user = withContext(Dispatchers.IO) {
                                repository.register(name.ifBlank { "Usuario" }, email, password)
                            }
                            status = "Registrado: ${user.name}"
                        } catch (e: Exception) {
                            status = "Error al registrar: ${e.message ?: e.toString()}"
                        } finally {
                            loading = false
                        }
                    }
                },
                enabled = !loading
            ) { Text("Registrarse") }
        }

        if (loading) {
            CircularProgressIndicator()
        }
        status?.let {
            Card(modifier = Modifier.fillMaxWidth()) {
                Text(
                    text = it,
                    modifier = Modifier.padding(12.dp),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }

        Text(
            text = "Tip: emulador = http://10.0.2.2:8000/ | celular físico = http://IP_DE_TU_PC:8000/ (misma red, servidor con --host=0.0.0.0).",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
fun HomeScreen(
    modifier: Modifier = Modifier,
    repository: AppRepository,
    baseUrl: String
) {
    val scope = rememberCoroutineScope()
    var status by remember { mutableStateOf("Sesión activa. Servidor: $baseUrl") }
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Pruebas de comunicación con la API", style = MaterialTheme.typography.titleMedium)

        Button(
            onClick = {
                scope.launch {
                    loading = true
                    status = "GET /api/readings/latest..."
                    try {
                        val res = withContext(Dispatchers.IO) { repository.latestReadings() }
                        status = "Lecturas OK (${res.estacion}, ${res.fecha})\n" +
                            res.readings.take(8).joinToString("\n") { "${it.sensorName}: ${it.sensorDato}" }
                    } catch (e: Exception) {
                        status = "Error GET lecturas: ${e.message ?: e.toString()}"
                    } finally {
                        loading = false
                    }
                }
            },
            enabled = !loading,
            modifier = Modifier.fillMaxWidth()
        ) { Text("GET Lecturas (latest)") }

        Button(
            onClick = {
                scope.launch {
                    loading = true
                    status = "GET /api/alerts..."
                    try {
                        val alerts = withContext(Dispatchers.IO) { repository.alerts() }
                        status = "Alertas OK (${alerts.size})\n" +
                            alerts.take(5).joinToString("\n") { "- [${it.level}] ${it.title}" }
                    } catch (e: Exception) {
                        status = "Error GET alertas: ${e.message ?: e.toString()}"
                    } finally {
                        loading = false
                    }
                }
            },
            enabled = !loading,
            modifier = Modifier.fillMaxWidth()
        ) { Text("GET Alertas") }

        Button(
            onClick = {
                scope.launch {
                    loading = true
                    status = "GET /api/messages..."
                    try {
                        val msgs = withContext(Dispatchers.IO) { repository.messages() }
                        status = "Mensajes OK (${msgs.size})\n" +
                            msgs.take(5).joinToString("\n") { "- ${it.title}" }
                    } catch (e: Exception) {
                        status = "Error GET mensajes: ${e.message ?: e.toString()}"
                    } finally {
                        loading = false
                    }
                }
            },
            enabled = !loading,
            modifier = Modifier.fillMaxWidth()
        ) { Text("GET Mensajes") }

        Button(
            onClick = {
                scope.launch {
                    loading = true
                    status = "POST /api/chat..."
                    try {
                        val reply = withContext(Dispatchers.IO) {
                            repository.chat("Dame una recomendación breve ante mala calidad del aire.")
                        }
                        status = "Chat OK:\n$reply"
                    } catch (e: Exception) {
                        status = "Error POST chat: ${e.message ?: e.toString()}"
                    } finally {
                        loading = false
                    }
                }
            },
            enabled = !loading,
            modifier = Modifier.fillMaxWidth()
        ) { Text("POST Chat (Gemini)") }

        OutlinedButton(
            onClick = {
                scope.launch {
                    loading = true
                    status = "Cerrando sesión..."
                    try {
                        withContext(Dispatchers.IO) { repository.logout() }
                        status = "Sesión cerrada"
                    } catch (e: Exception) {
                        status = "Error al cerrar sesión: ${e.message ?: e.toString()}"
                    } finally {
                        loading = false
                    }
                }
            },
            enabled = !loading,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Cerrar sesión") }

        if (loading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
        }

        Card(modifier = Modifier.fillMaxWidth()) {
            Text(
                text = status,
                modifier = Modifier.padding(12.dp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
