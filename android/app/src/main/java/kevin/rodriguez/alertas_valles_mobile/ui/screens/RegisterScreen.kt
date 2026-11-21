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
fun RegisterScreen(
    modifier: Modifier = Modifier,
    baseUrl: String,
    onSaveBaseUrl: suspend (String) -> Unit,
    buildRepository: (String) -> AppRepository,
    onRegistered: (String) -> Unit,
    onBackToLogin: () -> Unit
) {
    val scope = rememberCoroutineScope()
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var status by remember { mutableStateOf<String?>(null) }
    var loading by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text("Crear cuenta", style = MaterialTheme.typography.headlineSmall)
        Text("Completa tus datos para registrarte en Región Valles.", style = MaterialTheme.typography.bodyMedium)

        OutlinedTextField(
            value = name,
            onValueChange = { name = it },
            label = { Text("Nombre completo") },
            singleLine = true,
            modifier = Modifier.fillMaxWidth()
        )
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
            label = { Text("Contraseña (mínimo 8 caracteres)") },
            singleLine = true,
            visualTransformation = PasswordVisualTransformation(),
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(4.dp))

        Button(
            onClick = {
                scope.launch {
                    loading = true
                    status = "Registrando cuenta..."
                    try {
                        val normalized = ServerConfigStore.normalize(baseUrl)
                        onSaveBaseUrl(baseUrl)
                        val repo = buildRepository(normalized)
                        val user = withContext(Dispatchers.IO) {
                            repo.register(name.trim(), email.trim(), password)
                        }
                        onRegistered("Cuenta creada para ${user.email}. Ahora inicia sesión.")
                    } catch (e: Exception) {
                        status = "Error al registrar: ${e.message ?: e.toString()}"
                    } finally {
                        loading = false
                    }
                }
            },
            enabled = !loading && name.isNotBlank() && email.isNotBlank() && password.length >= 8,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Registrarme") }

        OutlinedButton(
            onClick = onBackToLogin,
            enabled = !loading,
            modifier = Modifier.fillMaxWidth()
        ) { Text("Volver al inicio de sesión") }

        if (loading) {
            CircularProgressIndicator()
        }
        status?.let {
            Card(modifier = Modifier.fillMaxWidth()) {
                Text(it, modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}
