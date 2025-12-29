package com.momentum.fitness.ui.component.graph

import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.dp

/**
 * Scrubbable graph component that allows users to drag to see values at specific points
 * TODO: Fix Vico chart integration - currently stubbed out
 */
@Composable
fun ScrubbableGraph(
    title: String,
    data: List<Float>,
    xAxisLabels: List<String>? = null,
    yAxisFormatter: Any? = null,
    modifier: Modifier = Modifier,
    onScrub: (Int, Float) -> Unit = { _, _ -> }
) {
    var scrubbedIndex by remember { mutableStateOf<Int?>(null) }

    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = title,
                style = androidx.compose.material3.MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(com.momentum.fitness.ui.util.ScreenSize.getGraphHeight())
                    .background(Color.LightGray.copy(alpha = 0.1f))
                    .pointerInput(Unit) {
                        detectDragGestures { change, _ ->
                            val x = change.position.x
                            val chartWidth = size.width.toFloat()
                            val index = ((x / chartWidth) * data.size).toInt().coerceIn(0, data.size - 1)
                            scrubbedIndex = index
                            onScrub(index, data[index])
                        }
                    },
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Chart placeholder",
                    style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                    color = Color.Gray
                )
            }
            
            scrubbedIndex?.let { index ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "Value: ${data[index]}",
                    style = androidx.compose.material3.MaterialTheme.typography.bodySmall,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
            }
        }
    }
}
