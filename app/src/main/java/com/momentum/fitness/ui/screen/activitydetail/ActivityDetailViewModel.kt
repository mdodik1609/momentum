package com.momentum.fitness.ui.screen.activitydetail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momentum.fitness.data.database.entity.ActivityEntity
import com.momentum.fitness.data.database.entity.ActivityStreamEntity
import com.momentum.fitness.data.model.HeartRateZones
import com.momentum.fitness.data.model.ZoneAnalysisResult
import com.momentum.fitness.data.model.ZoneAnalyzer
import com.momentum.fitness.data.repository.ActivityRepository
import com.momentum.fitness.data.repository.UserSettingsRepository
import com.momentum.fitness.data.util.GapCalculator
import com.momentum.fitness.data.util.RelativeEffortCalculator
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ActivityDetailViewModel @Inject constructor(
    private val activityRepository: ActivityRepository,
    private val userSettingsRepository: UserSettingsRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ActivityDetailUiState())
    val uiState: StateFlow<ActivityDetailUiState> = _uiState.asStateFlow()

    fun loadActivity(activityId: String) {
        if (activityId.isBlank()) {
            _uiState.update { it.copy(isLoading = false, error = "Invalid activity ID") }
            return
        }
        
        viewModelScope.launch {
            try {
                _uiState.update { it.copy(isLoading = true, error = null) }
                
                val activity = activityRepository.getActivityById(activityId)
                if (activity == null) {
                    _uiState.update { it.copy(isLoading = false, error = "Activity not found") }
                    return@launch
                }
                
                val streams = try {
                    activityRepository.getStreamsForActivitySync(activity.id)
                } catch (e: Exception) {
                    emptyList()
                }
                
                val settings = try {
                    userSettingsRepository.getSettingsSync()
                } catch (e: Exception) {
                    _uiState.update { it.copy(isLoading = false, error = "Failed to load settings") }
                    return@launch
                }

            // Calculate derived metrics
            val hrZones = HeartRateZones(
                zone1Max = settings.heartRateZone1Max,
                zone2Max = settings.heartRateZone2Max,
                zone3Max = settings.heartRateZone3Max,
                zone4Max = settings.heartRateZone4Max,
                zone5Max = settings.heartRateZone5Max
            )

            val hrZoneAnalysis = if (streams.any { it.heartRateBpm != null }) {
                ZoneAnalyzer.analyzeHeartRateZones(streams, hrZones)
            } else null

            val paceZoneAnalysis = if (activity?.sportType?.let { 
                it == com.momentum.fitness.data.model.SportType.RUN || 
                it == com.momentum.fitness.data.model.SportType.TRAIL_RUN 
            } == true && settings.functionalThresholdPaceSecondsPerKm != null && activity.distanceMeters != null) {
                ZoneAnalyzer.analyzePaceZones(streams, settings.functionalThresholdPaceSecondsPerKm, activity.distanceMeters)
            } else null

            val powerZoneAnalysis = if (activity?.sportType?.let {
                it == com.momentum.fitness.data.model.SportType.RIDE || 
                it == com.momentum.fitness.data.model.SportType.INDOOR_RIDE
            } == true && settings.functionalThresholdPower != null) {
                ZoneAnalyzer.analyzePowerZones(streams, settings.functionalThresholdPower)
            } else null

            val gap = if (activity?.sportType?.let {
                it == com.momentum.fitness.data.model.SportType.RUN || 
                it == com.momentum.fitness.data.model.SportType.TRAIL_RUN
            } == true && activity.distanceMeters != null) {
                GapCalculator.calculateGap(streams, activity.distanceMeters)
            } else null

            val relativeEffort = if (hrZoneAnalysis != null) {
                RelativeEffortCalculator.calculateRelativeEffort(streams, hrZones)
            } else null

                _uiState.update {
                    it.copy(
                        activity = activity,
                        streams = streams,
                        hrZoneAnalysis = hrZoneAnalysis,
                        paceZoneAnalysis = paceZoneAnalysis,
                        powerZoneAnalysis = powerZoneAnalysis,
                        gapSecondsPerKm = gap,
                        relativeEffort = relativeEffort,
                        isLoading = false,
                        error = null
                    )
                }
            } catch (e: Exception) {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        error = e.message ?: "Failed to load activity"
                    )
                }
            }
        }
    }
}

data class ActivityDetailUiState(
    val activity: ActivityEntity? = null,
    val streams: List<ActivityStreamEntity> = emptyList(),
    val hrZoneAnalysis: ZoneAnalysisResult? = null,
    val paceZoneAnalysis: ZoneAnalysisResult? = null,
    val powerZoneAnalysis: ZoneAnalysisResult? = null,
    val gapSecondsPerKm: Double? = null,
    val relativeEffort: Int? = null,
    val isLoading: Boolean = true,
    val error: String? = null
)

