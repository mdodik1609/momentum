package com.momentum.fitness.ui.screen.analytics

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momentum.fitness.data.repository.ActivityRepository
import com.momentum.fitness.data.util.FitnessFreshnessCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import javax.inject.Inject

@HiltViewModel
class AnalyticsViewModel @Inject constructor(
    private val activityRepository: ActivityRepository
) : ViewModel() {

    private val allActivities = activityRepository.getAllActivities()
        .catch { e ->
            emit(emptyList())
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val uiState: StateFlow<AnalyticsUiState> = allActivities.map { activities ->
        try {
            val fitnessData = FitnessFreshnessCalculator.calculateOverTime(activities)
            val currentFitness = FitnessFreshnessCalculator.calculateFitness(activities)
            val currentFatigue = FitnessFreshnessCalculator.calculateFatigue(activities)
            val currentForm = FitnessFreshnessCalculator.calculateForm(activities)

            AnalyticsUiState(
                fitnessData = fitnessData,
                currentFitness = currentFitness,
                currentFatigue = currentFatigue,
                currentForm = currentForm
            )
        } catch (e: Exception) {
            AnalyticsUiState()
        }
    }.catch { e ->
        emit(AnalyticsUiState())
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = AnalyticsUiState()
    )
}

data class AnalyticsUiState(
    val fitnessData: List<com.momentum.fitness.data.util.FitnessDataPoint> = emptyList(),
    val currentFitness: Double = 0.0,
    val currentFatigue: Double = 0.0,
    val currentForm: Double = 0.0
)



