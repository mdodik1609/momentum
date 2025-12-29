package com.momentum.fitness.ui.screen.analytics

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.momentum.fitness.ui.component.graph.ScrubbableGraph

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AnalyticsScreen(
    onNavigateBack: () -> Unit,
    viewModel: AnalyticsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Analytics") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Fitness & Freshness",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            if (uiState.fitnessData.isNotEmpty()) {
                item {
                    Card(modifier = Modifier.fillMaxWidth()) {
                        Column(modifier = Modifier.padding(16.dp)) {
                            Text(
                                text = "Fitness Trend",
                                style = MaterialTheme.typography.titleMedium
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            // Multi-line chart showing fitness, fatigue, and form
                            val fitnessData = uiState.fitnessData.map { it.fitness.toFloat() }
                            val fatigueData = uiState.fitnessData.map { it.fatigue.toFloat() }
                            val formData = uiState.fitnessData.map { it.form.toFloat() }
                            
                            if (fitnessData.isNotEmpty()) {
                                com.momentum.fitness.ui.component.graph.MultiLineGraph(
                                    title = "Fitness & Freshness",
                                    dataSets = listOf(
                                        "Fitness" to fitnessData,
                                        "Fatigue" to fatigueData,
                                        "Form" to formData
                                    ),
                                    colors = listOf(
                                        androidx.compose.ui.graphics.Color(0xFF4DB33D), // Green for Fitness
                                        androidx.compose.ui.graphics.Color(0xFFB33D3D), // Red for Fatigue
                                        androidx.compose.ui.graphics.Color(0xFF3F3E42)  // Dark for Form
                                    )
                                )
                            } else {
                                Text("No data available")
                            }
                        }
                    }
                }
            }

            item {
                Text(
                    text = "Current Status",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    MetricCard(
                        title = "Fitness",
                        value = String.format("%.1f", uiState.currentFitness),
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Fatigue",
                        value = String.format("%.1f", uiState.currentFatigue),
                        modifier = Modifier.weight(1f)
                    )
                    MetricCard(
                        title = "Form",
                        value = String.format("%.1f", uiState.currentForm),
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }
}

@Composable
fun MetricCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

