package com.momentum.fitness.ui.component.graph

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.momentum.fitness.data.database.entity.ActivityStreamEntity
import com.momentum.fitness.ui.util.formatDistance
import com.momentum.fitness.ui.util.formatPace
import com.momentum.fitness.ui.util.formatTime
import kotlin.math.max
import kotlin.math.min

/**
 * Enhanced graph with proper time/distance axis, formatted values, and visual scrubber
 * Similar to Strava's graph implementation
 */
@Composable
fun EnhancedGraph(
    title: String,
    streams: List<ActivityStreamEntity>,
    dataType: GraphDataType,
    modifier: Modifier = Modifier,
    showXAxisAsDistance: Boolean = true, // true = distance, false = time
    onScrub: ((Int, String, String) -> Unit)? = null // (index, xValue, yValue)
) {
    var scrubbedIndex by remember { mutableStateOf<Int?>(null) }
    
    // Extract data based on type
    val data = remember(streams, dataType) {
        extractData(streams, dataType)
    }
    
    // Calculate cumulative distance for X-axis
    val distances = remember(streams) {
        calculateCumulativeDistances(streams)
    }
    
    // Calculate time offsets for X-axis
    val timeOffsets = remember(streams) {
        streams.mapIndexed { index, stream ->
            stream.timeOffsetSeconds ?: (index * 1) // Fallback to 1 second per point
        }
    }
    
    if (data.isEmpty()) {
        Card(modifier = modifier) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = title,
                    style = androidx.compose.material3.MaterialTheme.typography.titleMedium
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "No data available",
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
        return
    }
    
    val minValue = data.minOrNull() ?: 0f
    val maxValue = data.maxOrNull() ?: 1f
    val valueRange = maxValue - minValue
    val padding = valueRange * 0.1f // 10% padding
    
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            // Graph canvas
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(com.momentum.fitness.ui.util.ScreenSize.getGraphHeight())
                    .pointerInput(Unit) {
                        detectTapGestures { tapOffset ->
                            val index = ((tapOffset.x / size.width) * data.size).toInt()
                                .coerceIn(0, data.size - 1)
                            scrubbedIndex = index
                            updateScrubCallback(index, data, distances, timeOffsets, showXAxisAsDistance, dataType, onScrub)
                        }
                    }
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            val index = ((change.position.x / size.width) * data.size).toInt()
                                .coerceIn(0, data.size - 1)
                            scrubbedIndex = index
                            updateScrubCallback(index, data, distances, timeOffsets, showXAxisAsDistance, dataType, onScrub)
                        }
                    }
            ) {
                GraphCanvas(
                    data = data,
                    minValue = minValue - padding,
                    maxValue = maxValue + padding,
                    scrubbedIndex = scrubbedIndex,
                    color = getColorForDataType(dataType)
                )
            }
            
            // X-axis labels
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = if (showXAxisAsDistance && distances.isNotEmpty()) {
                        "0 ${getDistanceUnit()}"
                    } else {
                        formatTime(0)
                    },
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                )
                if (distances.isNotEmpty() || timeOffsets.isNotEmpty()) {
                    val lastIndex = if (showXAxisAsDistance) {
                        distances.size - 1
                    } else {
                        timeOffsets.size - 1
                    }
                    Text(
                        text = if (showXAxisAsDistance) {
                            formatDistance(distances.last())
                        } else {
                            formatTime(timeOffsets.last())
                        },
                        style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                        color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            
            // Scrubber info
            if (scrubbedIndex != null) {
                val index = scrubbedIndex!!
                Spacer(modifier = Modifier.height(8.dp))
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = androidx.compose.material3.CardDefaults.cardColors(
                        containerColor = androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant
                    )
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column {
                            Text(
                                text = if (showXAxisAsDistance && index < distances.size) {
                                    formatDistance(distances[index])
                                } else if (index < timeOffsets.size) {
                                    formatTime(timeOffsets[index])
                                } else {
                                    "Point $index"
                                },
                                style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = getFormattedValue(data[index], dataType),
                                style = androidx.compose.material3.MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Text(
                            text = "Tap or drag to explore",
                            style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                            color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Tap or drag on graph to see values",
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                    color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}

@Composable
private fun GraphCanvas(
    data: List<Float>,
    minValue: Float,
    maxValue: Float,
    scrubbedIndex: Int?,
    color: Color
) {
    val gridColor = androidx.compose.material3.MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)
    val surfaceColor = androidx.compose.material3.MaterialTheme.colorScheme.surface
    
    Canvas(modifier = Modifier.fillMaxSize()) {
        if (data.isEmpty()) return@Canvas
        
        val width = size.width
        val height = size.height
        val padding = 40.dp.toPx()
        val graphWidth = width - padding * 2
        val graphHeight = height - padding * 2
        
        val valueRange = maxValue - minValue
        if (valueRange == 0f) return@Canvas
        
        // Draw grid lines
        for (i in 0..4) {
            val y = padding + (graphHeight / 4) * i
            drawLine(
                color = gridColor,
                start = Offset(padding, y),
                end = Offset(width - padding, y),
                strokeWidth = 1.dp.toPx()
            )
        }
        
        // Draw graph line
        val path = Path()
        data.forEachIndexed { index, value ->
            val x = padding + (graphWidth / (data.size - 1).coerceAtLeast(1)) * index
            val normalizedValue = (value - minValue) / valueRange
            val y = padding + graphHeight - (normalizedValue * graphHeight)
            
            if (index == 0) {
                path.moveTo(x, y)
            } else {
                path.lineTo(x, y)
            }
        }
        
        drawPath(
            path = path,
            color = color,
            style = Stroke(width = 3.dp.toPx(), cap = androidx.compose.ui.graphics.StrokeCap.Round)
        )
        
        // Draw scrubber indicator
        scrubbedIndex?.let { index ->
            if (index in data.indices) {
                val x = padding + (graphWidth / (data.size - 1).coerceAtLeast(1)) * index
                val normalizedValue = (data[index] - minValue) / valueRange
                val y = padding + graphHeight - (normalizedValue * graphHeight)
                
                // Vertical line
                drawLine(
                    color = color.copy(alpha = 0.5f),
                    start = Offset(x, padding),
                    end = Offset(x, height - padding),
                    strokeWidth = 2.dp.toPx()
                )
                
                // Point indicator
                drawCircle(
                    color = color,
                    radius = 8.dp.toPx(),
                    center = Offset(x, y)
                )
                drawCircle(
                    color = surfaceColor,
                    radius = 4.dp.toPx(),
                    center = Offset(x, y)
                )
            }
        }
    }
}

private fun extractData(streams: List<ActivityStreamEntity>, dataType: GraphDataType): List<Float> {
    return when (dataType) {
        GraphDataType.HEART_RATE -> streams.mapNotNull { it.heartRateBpm?.toFloat() }
        GraphDataType.POWER -> streams.mapNotNull { it.powerWatts?.toFloat() }
        GraphDataType.CADENCE -> streams.mapNotNull { it.cadenceRpm?.toFloat() }
        GraphDataType.ELEVATION -> streams.mapNotNull { it.altitudeMeters?.toFloat() }
        GraphDataType.PACE -> {
            streams.mapNotNull { stream ->
                stream.speedMps?.let { speed ->
                    if (speed > 0) {
                        (1000f / speed.toFloat()) // seconds per km
                    } else null
                } ?: run {
                    // Calculate speed from GPS if not available
                    val index = streams.indexOf(stream)
                    if (index > 0) {
                        val prev = streams[index - 1]
                        if (prev.latitude != null && prev.longitude != null &&
                            stream.latitude != null && stream.longitude != null) {
                            val distance = calculateHaversineDistance(
                                prev.latitude!!, prev.longitude!!,
                                stream.latitude!!, stream.longitude!!
                            )
                            val timeDiff = (stream.timeOffsetSeconds ?: index) - (prev.timeOffsetSeconds ?: (index - 1))
                            if (timeDiff > 0) {
                                val speed = (distance / timeDiff).toFloat()
                                if (speed > 0) (1000f / speed) else null
                            } else null
                        } else null
                    } else null
                }
            }
        }
        GraphDataType.SPEED -> streams.mapNotNull { it.speedMps?.toFloat()?.times(3.6f) } // Convert to km/h
    }
}

private fun calculateCumulativeDistances(streams: List<ActivityStreamEntity>): List<Double> {
    val distances = mutableListOf<Double>()
    var cumulative = 0.0
    
    for (i in streams.indices) {
        if (i > 0) {
            val prev = streams[i - 1]
            val curr = streams[i]
            
            if (prev.latitude != null && prev.longitude != null &&
                curr.latitude != null && curr.longitude != null) {
                val distance = calculateHaversineDistance(
                    prev.latitude!!, prev.longitude!!,
                    curr.latitude!!, curr.longitude!!
                )
                cumulative += distance
            }
        }
        distances.add(cumulative)
    }
    
    return distances
}

private fun calculateHaversineDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double): Double {
    val R = 6371000.0 // Earth radius in meters
    val dLat = Math.toRadians(lat2 - lat1)
    val dLon = Math.toRadians(lon2 - lon1)
    val a = Math.sin(dLat / 2) * Math.sin(dLat / 2) +
            Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
            Math.sin(dLon / 2) * Math.sin(dLon / 2)
    val c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a))
    return R * c
}

private fun getColorForDataType(dataType: GraphDataType): Color {
    return when (dataType) {
        GraphDataType.HEART_RATE -> Color(0xFFE53935) // Red
        GraphDataType.POWER -> Color(0xFFFB8C00) // Orange
        GraphDataType.CADENCE -> Color(0xFF4DB33D) // Green
        GraphDataType.ELEVATION -> Color(0xFF3F3E42) // Dark gray
        GraphDataType.PACE -> Color(0xFF1976D2) // Blue
        GraphDataType.SPEED -> Color(0xFF7B1FA2) // Purple
    }
}

private fun getFormattedValue(value: Float, dataType: GraphDataType): String {
    return when (dataType) {
        GraphDataType.HEART_RATE -> "${value.toInt()} bpm"
        GraphDataType.POWER -> "${value.toInt()} W"
        GraphDataType.CADENCE -> "${value.toInt()} rpm"
        GraphDataType.ELEVATION -> "${value.toInt()} m"
        GraphDataType.PACE -> formatPace(value.toDouble())
        GraphDataType.SPEED -> String.format("%.1f km/h", value)
    }
}

private fun getDistanceUnit(): String {
    // Could be made configurable (km vs miles)
    return "km"
}

private fun updateScrubCallback(
    index: Int,
    data: List<Float>,
    distances: List<Double>,
    timeOffsets: List<Int>,
    showXAxisAsDistance: Boolean,
    dataType: GraphDataType,
    onScrub: ((Int, String, String) -> Unit)?
) {
    if (index in data.indices) {
        val xValue = if (showXAxisAsDistance && index < distances.size) {
            formatDistance(distances[index])
        } else if (index < timeOffsets.size) {
            formatTime(timeOffsets[index])
        } else {
            "Point $index"
        }
        val yValue = getFormattedValue(data[index], dataType)
        onScrub?.invoke(index, xValue, yValue)
    }
}

enum class GraphDataType {
    HEART_RATE,
    POWER,
    CADENCE,
    ELEVATION,
    PACE,
    SPEED
}

