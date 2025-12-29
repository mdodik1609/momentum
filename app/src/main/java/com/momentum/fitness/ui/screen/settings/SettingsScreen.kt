package com.momentum.fitness.ui.screen.settings

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.Alignment
import kotlinx.coroutines.launch
import androidx.compose.runtime.rememberCoroutineScope

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    onThemeChanged: ((Boolean) -> Unit)? = null,
    onSystemThemeChanged: ((Boolean) -> Unit)? = null,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val settings by viewModel.settings.collectAsState(initial = null)

    val context = LocalContext.current
    
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Settings") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (settings == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                item {
                    Text(
                        text = "Heart Rate Zones",
                        style = MaterialTheme.typography.titleLarge
                    )
                    HeartRateZonesSection(
                        settings = settings!!,
                        onUpdate = { z1, z2, z3, z4, z5 ->
                            viewModel.updateHeartRateZones(z1, z2, z3, z4, z5)
                        }
                    )
                }

                item {
                    Text(
                        text = "Functional Thresholds",
                        style = MaterialTheme.typography.titleLarge
                    )
                    FunctionalThresholdsSection(
                        settings = settings!!,
                        onFTPUpdate = { viewModel.updateFTP(it) },
                        onFTPaUpdate = { viewModel.updateFTPa(it) }
                    )
                }

                item {
                    Text(
                        text = "Appearance",
                        style = MaterialTheme.typography.titleLarge
                    )
                    DarkModeSection(
                        onThemeChanged = onThemeChanged,
                        onSystemThemeChanged = onSystemThemeChanged
                    )
                }

                item {
                    Text(
                        text = "Data Management",
                        style = MaterialTheme.typography.titleLarge
                    )
                    DatabaseManagementSection(viewModel = viewModel)
                }

                item {
                    Text(
                        text = "Data Import",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Text(
                        text = "Use the Import button on Dashboard to import activity files",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                item {
                    Text(
                        text = "Development",
                        style = MaterialTheme.typography.titleLarge
                    )
                    TestDataSection(viewModel = viewModel)
                }
                
                item {
                    Text(
                        text = "Connections",
                        style = MaterialTheme.typography.titleLarge
                    )
                    StravaConnectionSection(
                        settings = settings!!,
                        onConnect = {
                            try {
                                val url = viewModel.getStravaAuthUrl()
                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                // Show error - credentials not configured
                                // TODO: Show snackbar or dialog
                            }
                        },
                        onDisconnect = { viewModel.disconnectStrava() },
                        onSync = { viewModel.syncStravaActivities() },
                        viewModel = viewModel
                    )
                }

                item {
                    GarminConnectionSection(
                        settings = settings!!,
                        onConnect = {
                            try {
                                // Generate PKCE pair
                                val pkce = com.momentum.fitness.data.service.GarminAuthService.generatePKCE()
                                // Store code verifier temporarily (in production, use secure storage)
                                val url = viewModel.getGarminAuthUrl(pkce.codeChallenge)
                                val intent = android.content.Intent(android.content.Intent.ACTION_VIEW, android.net.Uri.parse(url))
                                context.startActivity(intent)
                            } catch (e: Exception) {
                                // Show error - credentials not configured
                            }
                        },
                        onDisconnect = { viewModel.disconnectGarmin() },
                        onSync = { viewModel.syncGarminActivities() },
                        viewModel = viewModel
                    )
                }
            }
        }
    }
}

@Composable
fun HeartRateZonesSection(
    settings: com.momentum.fitness.data.database.entity.UserSettingsEntity,
    onUpdate: (Int, Int, Int, Int, Int) -> Unit
) {
    var zone1Max by remember { mutableStateOf(settings.heartRateZone1Max.toString()) }
    var zone2Max by remember { mutableStateOf(settings.heartRateZone2Max.toString()) }
    var zone3Max by remember { mutableStateOf(settings.heartRateZone3Max.toString()) }
    var zone4Max by remember { mutableStateOf(settings.heartRateZone4Max.toString()) }
    var zone5Max by remember { mutableStateOf(settings.heartRateZone5Max.toString()) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            ZoneInput("Zone 1 Max", zone1Max) { zone1Max = it }
            ZoneInput("Zone 2 Max", zone2Max) { zone2Max = it }
            ZoneInput("Zone 3 Max", zone3Max) { zone3Max = it }
            ZoneInput("Zone 4 Max", zone4Max) { zone4Max = it }
            ZoneInput("Zone 5 Max", zone5Max) { zone5Max = it }
            
            Spacer(modifier = Modifier.height(8.dp))
            Button(
                onClick = {
                    val z1 = zone1Max.toIntOrNull() ?: 60
                    val z2 = zone2Max.toIntOrNull() ?: 70
                    val z3 = zone3Max.toIntOrNull() ?: 80
                    val z4 = zone4Max.toIntOrNull() ?: 90
                    val z5 = zone5Max.toIntOrNull() ?: 220
                    onUpdate(z1, z2, z3, z4, z5)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Zones")
            }
        }
    }
}

@Composable
fun ZoneInput(label: String, value: String, onValueChange: (String) -> Unit) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
        modifier = Modifier.fillMaxWidth(),
        singleLine = true
    )
    Spacer(modifier = Modifier.height(8.dp))
}

@Composable
fun FunctionalThresholdsSection(
    settings: com.momentum.fitness.data.database.entity.UserSettingsEntity,
    onFTPUpdate: (Int?) -> Unit,
    onFTPaUpdate: (Int?) -> Unit
) {
    var ftp by remember { mutableStateOf(settings.functionalThresholdPower?.toString() ?: "") }
    var ftpaMinutes by remember { 
        mutableStateOf(
            settings.functionalThresholdPaceSecondsPerKm?.let { "${it / 60}" } ?: ""
        )
    }
    var ftpaSeconds by remember { 
        mutableStateOf(
            settings.functionalThresholdPaceSecondsPerKm?.let { "${it % 60}" } ?: ""
        )
    }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Functional Threshold Power (FTP)",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(
                value = ftp,
                onValueChange = { ftp = it },
                label = { Text("FTP (Watts)") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.fillMaxWidth(),
                suffix = { Text("W") }
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            
            Text(
                text = "Functional Threshold Pace (FTPa)",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedTextField(
                    value = ftpaMinutes,
                    onValueChange = { ftpaMinutes = it },
                    label = { Text("Minutes") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
                OutlinedTextField(
                    value = ftpaSeconds,
                    onValueChange = { ftpaSeconds = it },
                    label = { Text("Seconds") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    modifier = Modifier.weight(1f)
                )
            }
            Text(
                text = "per km",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
            
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = {
                    onFTPUpdate(ftp.toIntOrNull())
                    val minutes = ftpaMinutes.toIntOrNull() ?: 0
                    val seconds = ftpaSeconds.toIntOrNull() ?: 0
                    val totalSeconds = minutes * 60 + seconds
                    onFTPaUpdate(if (totalSeconds > 0) totalSeconds else null)
                },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Save Thresholds")
            }
        }
    }
}

@Composable
fun StravaConnectionSection(
    settings: com.momentum.fitness.data.database.entity.UserSettingsEntity,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    onSync: suspend () -> kotlin.Result<com.momentum.fitness.data.service.SyncResult>,
    viewModel: com.momentum.fitness.ui.screen.settings.SettingsViewModel = hiltViewModel()
) {
    val isConnected = settings.stravaAccessToken != null
    var isSyncing by remember { mutableStateOf(false) }
    var syncResult by remember { mutableStateOf<kotlin.Result<com.momentum.fitness.data.service.SyncResult>?>(null) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Strava",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = if (isConnected) "Connected" else "Not connected",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (isConnected) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                isSyncing = true
                                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                                    syncResult = onSync()
                                    isSyncing = false
                                }
                            },
                            enabled = !isSyncing
                        ) {
                            if (isSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Sync Now")
                            }
                        }
                        OutlinedButton(
                            onClick = { viewModel.enableBackgroundSync() }
                        ) {
                            Text("Auto-Sync")
                        }
                        OutlinedButton(onClick = onDisconnect) {
                            Text("Disconnect")
                        }
                    }
                } else {
                    Button(onClick = onConnect) {
                        Text("Connect")
                    }
                }
            }
            
            syncResult?.onSuccess { result ->
                Spacer(modifier = Modifier.height(8.dp))
                if (result.skipped) {
                    Text(
                        text = result.reason ?: "Sync skipped",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = "Synced ${result.syncedCount} activities${if (result.skippedCount > 0) ", ${result.skippedCount} already existed" else ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }?.onFailure { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Sync failed: ${error.message}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun GarminConnectionSection(
    settings: com.momentum.fitness.data.database.entity.UserSettingsEntity,
    onConnect: () -> Unit,
    onDisconnect: () -> Unit,
    onSync: suspend () -> kotlin.Result<com.momentum.fitness.data.service.SyncResult>,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val isConnected = settings.garminAccessToken != null
    var isSyncing by remember { mutableStateOf(false) }
    var syncResult by remember { mutableStateOf<kotlin.Result<com.momentum.fitness.data.service.SyncResult>?>(null) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "Garmin",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Text(
                        text = if (isConnected) "Connected" else "Not connected",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                if (isConnected) {
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        Button(
                            onClick = {
                                isSyncing = true
                                kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                                    syncResult = onSync()
                                    isSyncing = false
                                }
                            },
                            enabled = !isSyncing
                        ) {
                            if (isSyncing) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text("Sync Now")
                            }
                        }
                        OutlinedButton(
                            onClick = { viewModel.enableBackgroundSync() }
                        ) {
                            Text("Auto-Sync")
                        }
                        OutlinedButton(onClick = onDisconnect) {
                            Text("Disconnect")
                        }
                    }
                } else {
                    Button(onClick = onConnect) {
                        Text("Connect")
                    }
                }
            }

            syncResult?.onSuccess { result ->
                Spacer(modifier = Modifier.height(8.dp))
                if (result.skipped) {
                    Text(
                        text = result.reason ?: "Sync skipped",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                } else {
                    Text(
                        text = "Synced ${result.syncedCount} activities${if (result.skippedCount > 0) ", ${result.skippedCount} already existed" else ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }?.onFailure { error ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Sync failed: ${error.message}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun DarkModeSection(
    onThemeChanged: ((Boolean) -> Unit)?,
    onSystemThemeChanged: ((Boolean) -> Unit)?
) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Theme Settings",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Theme settings are managed in the main app theme",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            if (onThemeChanged != null || onSystemThemeChanged != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Use the theme toggle in the main navigation to change appearance",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}

@Composable
fun DatabaseManagementSection(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    var isCleaning by remember { mutableStateOf(false) }
    var cleanupResult by remember { mutableStateOf<String?>(null) }
    val scope = rememberCoroutineScope()

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Database Management",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Clean up old activities to free up storage space",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            if (cleanupResult != null) {
                Text(
                    text = cleanupResult!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (cleanupResult!!.startsWith("Success")) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            Button(
                onClick = {
                    isCleaning = true
                    cleanupResult = null
                    scope.launch {
                        try {
                            val result = viewModel.cleanupOldActivities()
                            cleanupResult = "Successfully cleaned up $result old activities"
                        } catch (e: Exception) {
                            cleanupResult = "Error: ${e.message}"
                        } finally {
                            isCleaning = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isCleaning
            ) {
                if (isCleaning) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Clean Up Old Activities")
                }
            }
        }
    }
}

@Composable
fun TestDataSection(
    viewModel: SettingsViewModel = hiltViewModel()
) {
    var isLoading by remember { mutableStateOf(false) }
    var resultMessage by remember { mutableStateOf<String?>(null) }

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Load Test Data",
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Generate 20+ realistic test activities for development and testing",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            if (resultMessage != null) {
                Text(
                    text = resultMessage!!,
                    style = MaterialTheme.typography.bodySmall,
                    color = if (resultMessage!!.startsWith("Success")) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.error
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
            
            Button(
                onClick = {
                    isLoading = true
                    resultMessage = null
                    kotlinx.coroutines.CoroutineScope(kotlinx.coroutines.Dispatchers.Main).launch {
                        try {
                            val count = viewModel.loadTestData()
                            resultMessage = "Successfully loaded $count test activities!"
                        } catch (e: Exception) {
                            resultMessage = "Error: ${e.message}"
                        } finally {
                            isLoading = false
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        strokeWidth = 2.dp
                    )
                } else {
                    Text("Load Test Data")
                }
            }
            
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "Includes: Easy runs, tempo runs, intervals, long runs, sprints, easy rides, tempo rides, interval rides, long rides, climbs, and trail runs",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}
