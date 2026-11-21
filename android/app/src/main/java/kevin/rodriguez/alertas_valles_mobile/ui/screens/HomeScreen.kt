package kevin.rodriguez.alertas_valles_mobile.ui.screens

import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import kevin.rodriguez.alertas_valles_mobile.data.repository.AppRepository
import kevin.rodriguez.alertas_valles_mobile.ui.viewmodel.AlertsViewModel
import kevin.rodriguez.alertas_valles_mobile.ui.viewmodel.ChatViewModel
import kevin.rodriguez.alertas_valles_mobile.ui.viewmodel.MessagesViewModel
import kevin.rodriguez.alertas_valles_mobile.ui.viewmodel.ReadingsViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    repository: AppRepository,
    baseUrl: String,
    onLogout: () -> Unit
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val scope = rememberCoroutineScope()

    val readingsVm = remember(repository) { ReadingsViewModel(repository) }
    val alertsVm = remember(repository) { AlertsViewModel(repository) }
    val messagesVm = remember(repository) { MessagesViewModel(repository) }
    val chatVm = remember(repository) { ChatViewModel(repository) }

    val tabs = listOf("Lecturas", "Alertas", "Avisos", "Chat")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Alertas Valles") },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                ),
                actions = {
                    TextButton(onClick = {
                        scope.launch {
                            withContext(Dispatchers.IO) { repository.logout() }
                            onLogout()
                        }
                    }) { Text("Salir") }
                }
            )
        },
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 },
                    icon = { Icon(Icons.Default.Home, contentDescription = "Lecturas") },
                    label = { Text(tabs[0]) }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 },
                    icon = { Icon(Icons.Default.Warning, contentDescription = "Alertas") },
                    label = { Text(tabs[1]) }
                )
                NavigationBarItem(
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 },
                    icon = { Icon(Icons.Default.Email, contentDescription = "Avisos") },
                    label = { Text(tabs[2]) }
                )
                NavigationBarItem(
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 },
                    icon = { Icon(Icons.Default.Info, contentDescription = "Chat") },
                    label = { Text(tabs[3]) }
                )
            }
        }
    ) { innerPadding ->
        when (selectedTab) {
            0 -> ReadingsScreen(viewModel = readingsVm, modifier = Modifier.padding(innerPadding))
            1 -> AlertsScreen(viewModel = alertsVm, modifier = Modifier.padding(innerPadding))
            2 -> MessagesScreen(viewModel = messagesVm, modifier = Modifier.padding(innerPadding))
            3 -> ChatScreen(viewModel = chatVm, modifier = Modifier.padding(innerPadding))
        }
    }
}
