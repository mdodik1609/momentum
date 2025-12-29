package com.momentum.fitness.ui.component.zone

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.momentum.fitness.data.model.ZoneAnalysisResult
import com.momentum.fitness.data.model.ZoneTime
import com.momentum.fitness.ui.util.formatTime

@Composable
fun ZoneAnalysisCard(
    title: String,
    zoneAnalysis: ZoneAnalysisResult,
    modifier: Modifier = Modifier
) {
    Card(modifier = modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = title,
                style = androidx.compose.material3.MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(16.dp))
            
            zoneAnalysis.zoneTimes.forEach { zoneTime ->
                ZoneBar(
                    zone = zoneTime.zone,
                    timeSeconds = zoneTime.timeSeconds,
                    percentage = zoneTime.percentage
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
fun ZoneBar(
    zone: Int,
    timeSeconds: Int,
    percentage: Float
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = "Zone $zone",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "${formatTime(timeSeconds)} (${String.format("%.1f", percentage)}%)",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                fontWeight = FontWeight.SemiBold
            )
        }
        Spacer(modifier = Modifier.height(4.dp))
        androidx.compose.material3.LinearProgressIndicator(
            progress = { percentage / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
            color = androidx.compose.ui.graphics.Color(0xFF4DB33D),
            trackColor = androidx.compose.ui.graphics.Color(0xFFC1BEBC)
        )
    }
}







