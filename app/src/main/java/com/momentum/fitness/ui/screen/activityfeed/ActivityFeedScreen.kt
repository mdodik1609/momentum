package com.momentum.fitness.ui.screen.activityfeed

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.*
import androidx.compose.material3.ExperimentalMaterial3Api
// PullToRefresh not available in this Material3 version
// import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
// import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.momentum.fitness.data.database.entity.ActivityEntity
import com.momentum.fitness.data.model.SportType
import com.momentum.fitness.ui.component.EmptyState
import com.momentum.fitness.ui.util.formatDistance
import com.momentum.fitness.ui.util.formatTime
import java.time.format.DateTimeFormatter
import java.time.Instant

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ActivityFeedScreen(
    onNavigateBack: () -> Unit,
    onNavigateToActivityDetail: (String) -> Unit,
    viewModel: ActivityFeedViewModel = hiltViewModel()
) {
    val activities by viewModel.activities.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()
    val sportTypeFilter by viewModel.sportTypeFilter.collectAsState()
    val dateRangeFilter by viewModel.dateRangeFilter.collectAsState()
    
    var showFilters by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Activities") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    IconButton(onClick = { showFilters = !showFilters }) {
                        Icon(
                            Icons.Default.FilterList,
                            contentDescription = "Filters",
                            tint = if (sportTypeFilter != null || dateRangeFilter != null) {
                                MaterialTheme.colorScheme.primary
                            } else {
                                MaterialTheme.colorScheme.onSurface
                            }
                        )
                    }
                }
            )
        }
    ) { padding ->
        // PullToRefresh functionality temporarily disabled
        // val pullToRefreshState = rememberPullToRefreshState()
        
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Search bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { viewModel.updateSearchQuery(it) },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                placeholder = { Text("Search activities...") },
                leadingIcon = {
                    Icon(Icons.Default.Search, contentDescription = "Search")
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { viewModel.updateSearchQuery("") }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear")
                        }
                    }
                },
                singleLine = true
            )

            // Filters section
            if (showFilters) {
                FilterSection(
                    sportTypeFilter = sportTypeFilter,
                    dateRangeFilter = dateRangeFilter,
                    onSportTypeSelected = { viewModel.setSportTypeFilter(it) },
                    onDateRangeSelected = { start, end ->
                        viewModel.setDateRangeFilter(start, end)
                    },
                    onClearFilters = { viewModel.clearFilters() }
                )
            }

            // Activity list
            Box(modifier = Modifier.fillMaxSize()) {
                if (activities.isEmpty()) {
                    EmptyState(
                        message = if (searchQuery.isNotEmpty() || sportTypeFilter != null || dateRangeFilter != null) {
                            "No activities match your filters"
                        } else {
                            "No activities yet"
                        },
                        hint = if (searchQuery.isEmpty() && sportTypeFilter == null && dateRangeFilter == null) {
                            "Import files or connect Strava to get started"
                        } else {
                            "Try adjusting your search or filters"
                        },
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        contentPadding = PaddingValues(16.dp)
                    ) {
                        items(
                            items = activities,
                            key = { it.id }
                        ) { activity ->
                            ActivityItem(
                                activity = activity,
                                onClick = { 
                                    try {
                                        onNavigateToActivityDetail(activity.id)
                                    } catch (e: Exception) {
                                        // Handle navigation error silently
                                    }
                                }
                            )
                        }
                    }
                }
                
                // PullToRefreshContainer temporarily disabled
                // PullToRefreshContainer(
                //     state = pullToRefreshState,
                //     modifier = Modifier.align(Alignment.TopCenter)
                // )
            }
        }
    }
}

@Composable
fun FilterSection(
    sportTypeFilter: SportType?,
    dateRangeFilter: Pair<Instant, Instant>?,
    onSportTypeSelected: (SportType?) -> Unit,
    onDateRangeSelected: (Instant?, Instant?) -> Unit,
    onClearFilters: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = "Filters",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            // Sport type filter
            Text(
                text = "Sport Type",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FilterChip(
                    selected = sportTypeFilter == null,
                    onClick = { onSportTypeSelected(null) },
                    label = { Text("All") }
                )
                SportType.values().take(5).forEach { sportType ->
                    FilterChip(
                        selected = sportTypeFilter == sportType,
                        onClick = { 
                            onSportTypeSelected(
                                if (sportTypeFilter == sportType) null else sportType
                            )
                        },
                        label = { Text(sportType.displayName) }
                    )
                }
            }

            // Clear filters button
            if (sportTypeFilter != null || dateRangeFilter != null) {
                TextButton(onClick = onClearFilters) {
                    Text("Clear All Filters")
                }
            }
        }
    }
}

@Composable
fun ActivityItem(
    activity: ActivityEntity,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = activity.name,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = activity.sportType.displayName,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = try {
                        activity.startDate.atZone(java.time.ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("MMM d"))
                    } catch (e: Exception) {
                        ""
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                if (activity.distanceMeters != null && activity.distanceMeters > 0) {
                    StatItem("Distance", try { formatDistance(activity.distanceMeters) } catch (e: Exception) { "0 km" })
                }
                StatItem("Time", try { formatTime(activity.movingTimeSeconds) } catch (e: Exception) { "0:00" })
                if (activity.elevationGainMeters != null && activity.elevationGainMeters > 0) {
                    StatItem("Elevation", try { "${activity.elevationGainMeters.toInt()} m" } catch (e: Exception) { "0 m" })
                }
            }
        }
    }
}

@Composable
fun StatItem(label: String, value: String) {
    Column {
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.SemiBold
        )
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
