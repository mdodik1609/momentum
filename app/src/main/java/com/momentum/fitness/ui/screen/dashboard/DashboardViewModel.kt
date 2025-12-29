package com.momentum.fitness.ui.screen.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momentum.fitness.data.repository.ActivityRepository
import com.momentum.fitness.data.test.TestDataLoader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val activityRepository: ActivityRepository,
    private val testDataLoader: TestDataLoader
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            try {
                val now = Instant.now()
                val zoneId = ZoneId.systemDefault()
                val today = LocalDate.now(zoneId)
                
                // Current period (this week)
                val weekStart = today.minusDays(today.dayOfWeek.value.toLong() - 1)
                    .atStartOfDay(zoneId).toInstant()
                val weekEnd = today.plusDays(7 - today.dayOfWeek.value.toLong())
                    .atTime(23, 59, 59).atZone(zoneId).toInstant()
                
                // Previous period (last week)
                val lastWeekStart = weekStart.minusSeconds(7 * 24 * 60 * 60)
                val lastWeekEnd = weekStart.minusSeconds(1)

                // This month
                val monthStart = today.withDayOfMonth(1).atStartOfDay(zoneId).toInstant()
                val monthEnd = today.atTime(23, 59, 59).atZone(zoneId).toInstant()

                val currentStats = activityRepository.getStatsForDateRange(weekStart, weekEnd)
                val previousStats = activityRepository.getStatsForDateRange(lastWeekStart, lastWeekEnd)
                
                val activityCountThisMonth = activityRepository.getActivityCountForDateRange(monthStart, monthEnd)
                val activeDaysThisMonth = activityRepository.getActiveDaysCount(monthStart, monthEnd)
                
                // Calculate streak with error handling
                val streak = try {
                    calculateStreak(activityRepository)
                } catch (e: Exception) {
                    0
                }

                _uiState.update { state ->
                    state.copy(
                        currentWeekDistance = currentStats.totalDistance,
                        currentWeekTime = currentStats.totalTime,
                        currentWeekElevation = currentStats.totalElevation,
                        previousWeekDistance = previousStats.totalDistance,
                        previousWeekTime = previousStats.totalTime,
                        previousWeekElevation = previousStats.totalElevation,
                        activityCountThisMonth = activityCountThisMonth,
                        activeDaysThisMonth = activeDaysThisMonth,
                        currentStreak = streak,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        isLoading = false,
                        error = e.message ?: "Unknown error"
                    )
                }
            }
        }
    }

    private suspend fun calculateStreak(repository: ActivityRepository): Int {
        val today = LocalDate.now()
        var currentDate = today
        var streak = 0
        
        // Check backwards from today
        while (true) {
            val dayStart = currentDate.atStartOfDay(ZoneId.systemDefault()).toInstant()
            val dayEnd = currentDate.atTime(23, 59, 59).atZone(ZoneId.systemDefault()).toInstant()
            
            val count = repository.getActivityCountForDateRange(dayStart, dayEnd)
            if (count > 0) {
                streak++
                currentDate = currentDate.minusDays(1)
            } else {
                break
            }
            
            // Limit to prevent infinite loop
            if (streak > 365) break
        }
        
        return streak
    }

    fun refresh() {
        _uiState.update { it.copy(isLoading = true) }
        loadDashboardData()
    }
    
    suspend fun loadTestData(): Int {
        return testDataLoader.loadTestData()
    }
}

data class DashboardUiState(
    val currentWeekDistance: Double = 0.0,
    val currentWeekTime: Int = 0,
    val currentWeekElevation: Double = 0.0,
    val previousWeekDistance: Double = 0.0,
    val previousWeekTime: Int = 0,
    val previousWeekElevation: Double = 0.0,
    val activityCountThisMonth: Int = 0,
    val activeDaysThisMonth: Int = 0,
    val currentStreak: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)
