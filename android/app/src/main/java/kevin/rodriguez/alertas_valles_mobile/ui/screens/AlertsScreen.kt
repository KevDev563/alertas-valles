package kevin.rodriguez.alertas_valles_mobile.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kevin.rodriguez.alertas_valles_mobile.ui.viewmodel.AlertsViewModel
import kevin.rodriguez.alertas_valles_mobile.ui.viewmodel.UiState

@Composable
fun AlertsScreen(viewModel: AlertsViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) { viewModel.load() }

    Column(modifier = modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Alertas", style = MaterialTheme.typography.titleLarge)
            Button(onClick = { viewModel.load() }) { Text("Actualizar") }
        }

        when (val s = state) {
            is UiState.Loading -> CircularProgressIndicator()
            is UiState.Error -> {
                Text("Error: ${s.message}", color = MaterialTheme.colorScheme.error)
                Button(onClick = { viewModel.load() }) { Text("Reintentar") }
            }
            is UiState.Success -> {
                val alerts = s.data
                if (alerts.isEmpty()) {
                    Text("No hay alertas activas.")
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(alerts) { alert ->
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(4.dp)) {
                                    Text(alert.title, style = MaterialTheme.typography.titleMedium)
                                    Text("Nivel: ${alert.level ?: "-"}  •  Fuente: ${alert.source ?: "-"}")
                                    alert.description?.let { Text(it, style = MaterialTheme.typography.bodyMedium) }
                                    alert.zone?.name?.let { Text("Zona: $it", style = MaterialTheme.typography.bodySmall) }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
