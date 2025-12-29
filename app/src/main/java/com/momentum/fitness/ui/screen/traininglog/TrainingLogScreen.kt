package com.momentum.fitness.ui.screen.traininglog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TrainingLogScreen(
    onNavigateBack: () -> Unit,
    onNavigateToActivityDetail: (String) -> Unit,
    viewModel: TrainingLogViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Training Log") },
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
                    text = uiState.currentMonth.format(DateTimeFormatter.ofPattern("MMMM yyyy")),
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            item {
                CalendarView(
                    month = uiState.currentMonth,
                    activitiesByDate = uiState.activitiesByDate,
                    onDateClick = { date ->
                        uiState.activitiesByDate[date]?.firstOrNull()?.let { activity ->
                            onNavigateToActivityDetail(activity.id)
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun CalendarView(
    month: LocalDate,
    activitiesByDate: Map<LocalDate, List<com.momentum.fitness.data.database.entity.ActivityEntity>>,
    onDateClick: (LocalDate) -> Unit
) {
    val firstDayOfMonth = month.withDayOfMonth(1)
    val lastDayOfMonth = month.withDayOfMonth(month.lengthOfMonth())
    val firstDayOfWeek = firstDayOfMonth.dayOfWeek.value % 7 // 0 = Sunday
    val daysInMonth = month.lengthOfMonth()

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            // Day headers
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly
            ) {
                listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat").forEach { day ->
                    Text(
                        text = day,
                        style = MaterialTheme.typography.bodySmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.weight(1f),
                        textAlign = androidx.compose.ui.text.style.TextAlign.Center
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Calendar grid
            var dayCounter = 1
            repeat(6) { week ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    repeat(7) { dayOfWeek ->
                        if (week == 0 && dayOfWeek < firstDayOfWeek) {
                            // Empty cell before first day
                            Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                        } else if (dayCounter <= daysInMonth) {
                            val date = firstDayOfMonth.plusDays((dayCounter - 1).toLong())
                            val hasActivity = activitiesByDate.containsKey(date)
                            
                            CalendarDay(
                                day = dayCounter,
                                hasActivity = hasActivity,
                                onClick = { onDateClick(date) },
                                modifier = Modifier.weight(1f)
                            )
                            dayCounter++
                        } else {
                            // Empty cell after last day
                            Box(modifier = Modifier.weight(1f).aspectRatio(1f))
                        }
                    }
                }
                if (dayCounter > daysInMonth) return@repeat
            }
        }
    }
}

@Composable
fun CalendarDay(
    day: Int,
    hasActivity: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .then(
                if (hasActivity) {
                    Modifier
                        .clickable { onClick() }
                        .background(
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.3f),
                            shape = androidx.compose.foundation.shape.CircleShape
                        )
                } else {
                    Modifier
                }
            ),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$day",
            style = MaterialTheme.typography.bodyMedium,
            color = if (hasActivity) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}

