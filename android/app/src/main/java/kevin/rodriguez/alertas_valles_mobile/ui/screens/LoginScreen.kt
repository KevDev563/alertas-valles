package kevin.rodriguez.alertas_valles_mobile.ui.screens

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
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import kevin.rodriguez.alertas_valles_mobile.data.local.ServerConfigStore
import kevin.rodriguez.alertas_valles_mobile.data.repository.AppRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun LoginScreen(
    modifier: Modifier = Modifier,
    initialBaseUrl: String,
    onSaveBaseUrl: suspend (String) -> Unit,
    buildRepository: (String) -> AppRepository,
    repository: AppRepository,
    onLoginSuccess: () -> Unit
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
            onValueChange = { baseUrl = it },
            label = { Text("URL del servidor") },
            placeholder = { Text("http://IP_DE_TU_PC:8000/") },
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
                            val normalized = ServerConfigStore.normalize(baseUrl)
                            val repo = buildRepository(normalized)
                            val count = withContext(Dispatchers.IO) { repo.alerts().size }
                            onSaveBaseUrl(baseUrl)
                            status = "Conexión exitosa. Alertas recibidas: $count"
                        } catch (e: Exception) {
                            status = "Error de conexión: ${e.message ?: e.toString()}"
                        } finally {
                            loading = false
                        }
                    }
                },
                enabled = !loading
            ) { Text("Probar conexión") }
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text("Cuenta", style = MaterialTheme.typography.titleMedium)
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
            label = { Text("Nombre (solo para registrarse)") },
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
                            val normalized = ServerConfigStore.normalize(baseUrl)
                            onSaveBaseUrl(baseUrl)
                            val repo = buildRepository(normalized)
                            val user = withContext(Dispatchers.IO) { repo.login(email, password) }
                            status = "Bienvenido, ${user.name}"
                            onLoginSuccess()
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
                        status = "Registrando..."
                        try {
                            val normalized = ServerConfigStore.normalize(baseUrl)
                            onSaveBaseUrl(baseUrl)
                            val repo = buildRepository(normalized)
                            val user = withContext(Dispatchers.IO) {
                                repo.register(name.ifBlank { "Usuario" }, email, password)
                            }
                            status = "Cuenta creada: ${user.name}"
                            onLoginSuccess()
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
                Text(it, modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodyMedium)
            }
        }

        Text(
            text = "Emulador: http://10.0.2.2:8000/  •  Celular (misma red): http://IP_DE_TU_PC:8000/ (servidor con --host=0.0.0.0).",
            style = MaterialTheme.typography.bodySmall
        )
    }
}
