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
import kevin.rodriguez.alertas_valles_mobile.ui.viewmodel.ReadingsViewModel
import kevin.rodriguez.alertas_valles_mobile.ui.viewmodel.UiState

@Composable
fun ReadingsScreen(viewModel: ReadingsViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.state.collectAsState()

    LaunchedEffect(Unit) { viewModel.load() }

    Column(modifier = modifier.fillMaxSize().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
            Text("Condiciones actuales", style = MaterialTheme.typography.titleLarge)
            Button(onClick = { viewModel.load() }) { Text("Actualizar") }
        }

        when (val s = state) {
            is UiState.Loading -> CircularProgressIndicator()
            is UiState.Error -> {
                Text("Error: ${s.message}", color = MaterialTheme.colorScheme.error)
                Button(onClick = { viewModel.load() }) { Text("Reintentar") }
            }
            is UiState.Success -> {
                val data = s.data
                Text("Estación: ${data.estacion}  •  Fecha: ${data.fecha ?: "sin datos"}", style = MaterialTheme.typography.bodyMedium)
                if (data.readings.isEmpty()) {
                    Text("No hay lecturas disponibles.")
                } else {
                    LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                        items(data.readings) { item ->
                            val value = if (item.sensorDato == "-1.0" || item.sensorDato == "-1") "No disponible" else item.sensorDato
                            Card(modifier = Modifier.fillMaxWidth()) {
                                Column(modifier = Modifier.padding(12.dp)) {
                                    Text(item.sensorName, style = MaterialTheme.typography.titleMedium)
                                    Text(value, style = MaterialTheme.typography.bodyLarge)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
