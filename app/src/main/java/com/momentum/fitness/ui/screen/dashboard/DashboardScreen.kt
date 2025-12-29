package com.momentum.fitness.ui.screen.dashboard

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import com.momentum.fitness.ui.util.ScreenSize
import com.momentum.fitness.ui.util.formatDistance
import com.momentum.fitness.ui.util.formatElevation
import com.momentum.fitness.ui.util.formatTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DashboardScreen(
    onNavigateToActivityFeed: () -> Unit,
    onNavigateToActivityDetail: (String) -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToTrainingLog: () -> Unit = {},
    onNavigateToFileImport: () -> Unit = {},
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Momentum") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        } else if (uiState.error != null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = "Error loading data",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = uiState.error ?: "Unknown error",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.refresh() }) {
                        Text("Retry")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                item {
                    Text(
                        text = "This Week",
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold
                    )
                }

            item {
                StatsCard(
                    title = "Distance",
                    currentValue = uiState.currentWeekDistance,
                    previousValue = uiState.previousWeekDistance,
                    formatter = { formatDistance(it) }
                )
            }

            item {
                StatsCard(
                    title = "Time",
                    currentValue = uiState.currentWeekTime.toDouble(),
                    previousValue = uiState.previousWeekTime.toDouble(),
                    formatter = { formatTime(it.toInt()) }
                )
            }

            item {
                StatsCard(
                    title = "Elevation",
                    currentValue = uiState.currentWeekElevation,
                    previousValue = uiState.previousWeekElevation,
                    formatter = { formatElevation(it) }
                )
            }

            // Statistics Summary Cards
            item {
                Text(
                    text = "This Month",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    SummaryCard(
                        title = "Activities",
                        value = uiState.activityCountThisMonth.toString(),
                        modifier = Modifier.weight(1f)
                    )
                    SummaryCard(
                        title = "Active Days",
                        value = uiState.activeDaysThisMonth.toString(),
                        modifier = Modifier.weight(1f)
                    )
                }
            }

            item {
                SummaryCard(
                    title = "Current Streak",
                    value = "${uiState.currentStreak} days",
                    subtitle = "Keep it up!",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            item {
                val isTablet = com.momentum.fitness.ui.util.ScreenSize.isTablet()
                if (isTablet) {
                    // Tablet: Show buttons in a row
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onNavigateToActivityFeed,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Activities")
                        }
                        Button(
                            onClick = onNavigateToTrainingLog,
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Training Log")
                        }
                    }
                } else {
                    // Phone: Stack buttons vertically
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Button(
                            onClick = onNavigateToActivityFeed,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Activities")
                        }
                        Button(
                            onClick = onNavigateToTrainingLog,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Text("Training Log")
                        }
                    }
                }
            }
            
            item {
                Button(
                    onClick = onNavigateToFileImport,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Import Activity File")
                }
            }
            
            item {
                OutlinedButton(
                    onClick = {
                        scope.launch {
                            try {
                                val count = viewModel.loadTestData()
                                // Show success message (you could add a snackbar here)
                            } catch (e: Exception) {
                                // Handle error
                            }
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Load Test Data (20+ Activities)")
                }
            }
            }
        }
    }
}

@Composable
fun StatsCard(
    title: String,
    currentValue: Double,
    previousValue: Double,
    formatter: (Double) -> String
) {
    val change = if (previousValue > 0) {
        ((currentValue - previousValue) / previousValue * 100).toInt()
    } else if (currentValue > 0) 100 else 0

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = formatter(currentValue),
                style = MaterialTheme.typography.headlineLarge,
                fontWeight = FontWeight.Bold
            )
            if (previousValue > 0 || currentValue > 0) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "${if (change >= 0) "+" else ""}$change% vs last week",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (change >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
fun SummaryCard(
    title: String,
    value: String,
    subtitle: String? = null,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
            if (subtitle != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}
