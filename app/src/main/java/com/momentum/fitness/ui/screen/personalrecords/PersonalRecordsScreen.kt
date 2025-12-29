package com.momentum.fitness.ui.screen.personalrecords

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
import com.momentum.fitness.data.database.entity.PersonalRecordEntity
import com.momentum.fitness.data.model.SportType
import com.momentum.fitness.ui.util.formatDistance
import com.momentum.fitness.ui.util.formatPace
import com.momentum.fitness.ui.util.formatTime
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PersonalRecordsScreen(
    onNavigateBack: () -> Unit,
    viewModel: PersonalRecordsViewModel = hiltViewModel()
) {
    val records by viewModel.allRecords.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Personal Records") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        if (records.isEmpty()) {
            com.momentum.fitness.ui.component.EmptyState(
                message = "No personal records yet",
                hint = "Complete activities to automatically detect PRs",
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
            )
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Group by sport type
                val recordsBySport = records.groupBy { it.sportType }

                recordsBySport.forEach { (sportType, sportRecords) ->
                    item {
                        Text(
                            text = sportType.displayName,
                            style = MaterialTheme.typography.headlineSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    items(sportRecords) { record ->
                        PersonalRecordItem(record = record)
                    }
                }
            }
        }
    }
}

@Composable
fun PersonalRecordItem(record: PersonalRecordEntity) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = formatRecordType(record.recordType),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = formatRecordValue(record.recordType, record.value),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = record.achievedAt.atZone(java.time.ZoneId.systemDefault())
                    .format(DateTimeFormatter.ofPattern("MMM d, yyyy")),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

fun formatRecordType(recordType: String): String {
    return when (recordType) {
        "1k" -> "1 Kilometer"
        "1_mile" -> "1 Mile"
        "5k" -> "5 Kilometer"
        "10k" -> "10 Kilometer"
        "half_marathon" -> "Half Marathon"
        "marathon" -> "Marathon"
        "20min_power" -> "20 Minute Power"
        else -> recordType.replace("_", " ").replaceFirstChar { it.uppercaseChar() }
    }
}

fun formatRecordValue(recordType: String, value: Double): String {
    return when (recordType) {
        "1k", "5k", "10k", "half_marathon", "marathon", "1_mile" -> {
            formatTime(value.toInt())
        }
        "20min_power" -> {
            "${value.toInt()} W"
        }
        else -> value.toString()
    }
}
