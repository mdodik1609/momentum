package com.momentum.fitness.ui.screen.traininglog

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momentum.fitness.data.repository.ActivityRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId
import javax.inject.Inject

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class TrainingLogViewModel @Inject constructor(
    private val activityRepository: ActivityRepository
) : ViewModel() {

    private val _currentMonth = MutableStateFlow(LocalDate.now())
    val currentMonth: StateFlow<LocalDate> = _currentMonth.asStateFlow()

    val activitiesByDate: StateFlow<Map<LocalDate, List<com.momentum.fitness.data.database.entity.ActivityEntity>>> = 
        _currentMonth.flatMapLatest { month ->
            try {
                val startDate = month.withDayOfMonth(1).atStartOfDay(ZoneId.systemDefault()).toInstant()
                val endDate = month.withDayOfMonth(month.lengthOfMonth())
                    .plusDays(1)
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()
                
                activityRepository.getActivitiesByDateRange(startDate, endDate)
                    .map { activities ->
                        try {
                            activities.groupBy { activity ->
                                activity.startDate.atZone(ZoneId.systemDefault()).toLocalDate()
                            }
                        } catch (e: Exception) {
                            emptyMap()
                        }
                    }
                    .catch { e ->
                        emit(emptyMap())
                    }
            } catch (e: Exception) {
                flowOf(emptyMap())
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )

    val uiState: StateFlow<TrainingLogUiState> = combine(
        _currentMonth,
        activitiesByDate
    ) { month, activities ->
        TrainingLogUiState(
            currentMonth = month,
            activitiesByDate = activities
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = TrainingLogUiState(
            currentMonth = LocalDate.now(),
            activitiesByDate = emptyMap()
        )
    )

    fun previousMonth() {
        _currentMonth.update { it.minusMonths(1) }
    }

    fun nextMonth() {
        _currentMonth.update { it.plusMonths(1) }
    }
}

data class TrainingLogUiState(
    val currentMonth: LocalDate,
    val activitiesByDate: Map<LocalDate, List<com.momentum.fitness.data.database.entity.ActivityEntity>>
)

