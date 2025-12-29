package com.momentum.fitness.ui.screen.activitydetail

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.platform.LocalContext
import com.momentum.fitness.ui.screen.activitydetail.shareActivity
import com.momentum.fitness.ui.util.formatDistance
import com.momentum.fitness.ui.util.formatTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityDetailScreen(
    activityId: String,
    onNavigateBack: () -> Unit,
    viewModel: ActivityDetailViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(activityId) {
        viewModel.loadActivity(activityId)
    }

    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(uiState.activity?.name ?: "Activity") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (uiState.activity != null) {
                        IconButton(
                            onClick = {
                                shareActivity(context, uiState.activity!!)
                            }
                        ) {
                            Icon(Icons.Default.Share, contentDescription = "Share")
                        }
                    }
                }
            )
        }
    ) { padding ->
        if (uiState.isLoading) {
            com.momentum.fitness.ui.component.LoadingState(
                message = "Loading activity...",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        } else if (uiState.error != null || uiState.activity == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .padding(16.dp),
                contentAlignment = androidx.compose.ui.Alignment.Center
            ) {
                Column(horizontalAlignment = androidx.compose.ui.Alignment.CenterHorizontally) {
                    Text(
                        text = uiState.error ?: "Activity not found",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { viewModel.loadActivity(activityId) }) {
                        Text("Retry")
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(16.dp)
            ) {
                item {
                    ActivityStatsSection(activity = uiState.activity!!)
                }
                
                // GAP for running activities
                uiState.gapSecondsPerKm?.let { gap ->
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Grade Adjusted Pace",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = com.momentum.fitness.ui.util.formatPace(gap),
                                    style = MaterialTheme.typography.headlineSmall
                                )
                            }
                        }
                    }
                }
                
                // Relative Effort
                uiState.relativeEffort?.let { effort ->
                    item {
                        Card(modifier = Modifier.fillMaxWidth()) {
                            Column(modifier = Modifier.padding(16.dp)) {
                                Text(
                                    text = "Relative Effort",
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.height(8.dp))
                                Text(
                                    text = "$effort",
                                    style = MaterialTheme.typography.headlineSmall
                                )
                            }
                        }
                    }
                }
                
                // Heart Rate Zone Analysis
                uiState.hrZoneAnalysis?.let { analysis ->
                    item {
                        com.momentum.fitness.ui.component.zone.ZoneAnalysisCard(
                            title = "Heart Rate Zones",
                            zoneAnalysis = analysis
                        )
                    }
                }
                
                // Pace Zone Analysis
                uiState.paceZoneAnalysis?.let { analysis ->
                    item {
                        com.momentum.fitness.ui.component.zone.ZoneAnalysisCard(
                            title = "Pace Zones",
                            zoneAnalysis = analysis
                        )
                    }
                }
                
                // Power Zone Analysis
                uiState.powerZoneAnalysis?.let { analysis ->
                    item {
                        com.momentum.fitness.ui.component.zone.ZoneAnalysisCard(
                            title = "Power Zones",
                            zoneAnalysis = analysis
                        )
                    }
                }
                
                // Map section
                if (uiState.streams.any { it.latitude != null && it.longitude != null }) {
                    item {
                        Text(
                            text = "Map",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    item {
                        com.momentum.fitness.ui.component.map.ActivityMap(
                            streams = uiState.streams,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(com.momentum.fitness.ui.util.ScreenSize.getMapHeight())
                        )
                    }
                }
                
                // Graphs section
                if (uiState.streams.isNotEmpty()) {
                    item {
                        Text(
                            text = "Graphs",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    
                    // Elevation graph
                    if (uiState.streams.any { it.altitudeMeters != null }) {
                        item {
                            com.momentum.fitness.ui.component.graph.EnhancedGraph(
                                title = "Elevation Profile",
                                streams = uiState.streams,
                                dataType = com.momentum.fitness.ui.component.graph.GraphDataType.ELEVATION,
                                showXAxisAsDistance = true
                            )
                        }
                    }
                    
                    // Heart Rate graph
                    if (uiState.streams.any { it.heartRateBpm != null }) {
                        item {
                            com.momentum.fitness.ui.component.graph.EnhancedGraph(
                                title = "Heart Rate",
                                streams = uiState.streams,
                                dataType = com.momentum.fitness.ui.component.graph.GraphDataType.HEART_RATE,
                                showXAxisAsDistance = true
                            )
                        }
                    }
                    
                    // Power graph
                    if (uiState.streams.any { it.powerWatts != null }) {
                        item {
                            com.momentum.fitness.ui.component.graph.EnhancedGraph(
                                title = "Power",
                                streams = uiState.streams,
                                dataType = com.momentum.fitness.ui.component.graph.GraphDataType.POWER,
                                showXAxisAsDistance = true
                            )
                        }
                    }
                    
                    // Cadence graph
                    if (uiState.streams.any { it.cadenceRpm != null }) {
                        item {
                            com.momentum.fitness.ui.component.graph.EnhancedGraph(
                                title = "Cadence",
                                streams = uiState.streams,
                                dataType = com.momentum.fitness.ui.component.graph.GraphDataType.CADENCE,
                                showXAxisAsDistance = true
                            )
                        }
                    }
                    
                    // Pace graph (for running activities)
                    if (uiState.activity?.sportType?.let { 
                        it == com.momentum.fitness.data.model.SportType.RUN || 
                        it == com.momentum.fitness.data.model.SportType.TRAIL_RUN 
                    } == true && uiState.streams.any { it.speedMps != null && it.speedMps!! > 0 }) {
                        item {
                            com.momentum.fitness.ui.component.graph.EnhancedGraph(
                                title = "Pace",
                                streams = uiState.streams,
                                dataType = com.momentum.fitness.ui.component.graph.GraphDataType.PACE,
                                showXAxisAsDistance = true
                            )
                        }
                    }
                    
                    // Speed graph (for cycling activities)
                    if (uiState.activity?.sportType?.let { 
                        it == com.momentum.fitness.data.model.SportType.RIDE || 
                        it == com.momentum.fitness.data.model.SportType.INDOOR_RIDE 
                    } == true && uiState.streams.any { it.speedMps != null }) {
                        item {
                            com.momentum.fitness.ui.component.graph.EnhancedGraph(
                                title = "Speed",
                                streams = uiState.streams,
                                dataType = com.momentum.fitness.ui.component.graph.GraphDataType.SPEED,
                                showXAxisAsDistance = true
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ActivityStatsSection(activity: com.momentum.fitness.data.database.entity.ActivityEntity) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "Stats",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            if (activity.distanceMeters != null) {
                StatRow("Distance", formatDistance(activity.distanceMeters))
            }
            StatRow("Moving Time", formatTime(activity.movingTimeSeconds))
            if (activity.elevationGainMeters != null && activity.elevationGainMeters > 0) {
                StatRow("Elevation Gain", "${activity.elevationGainMeters.toInt()} m")
            }
            if (activity.averageHeartRateBpm != null) {
                StatRow("Avg Heart Rate", "${activity.averageHeartRateBpm} bpm")
            }
            if (activity.averagePowerWatts != null) {
                StatRow("Avg Power", "${activity.averagePowerWatts.toInt()} W")
            }
            if (activity.calories != null) {
                StatRow("Calories", "${activity.calories} kcal")
            }
        }
    }
}

@Composable
fun StatRow(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
    }
}

