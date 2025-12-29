package com.momentum.fitness.ui.screen.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.momentum.fitness.data.database.entity.UserSettingsEntity
import com.momentum.fitness.data.database.cleanup.DatabaseCleanupService
import com.momentum.fitness.data.repository.ActivityRepository
import com.momentum.fitness.data.repository.UserSettingsRepository
import com.momentum.fitness.data.service.GarminAuthService
import com.momentum.fitness.data.service.GarminSyncService
import com.momentum.fitness.data.test.TestDataLoader
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val userSettingsRepository: UserSettingsRepository,
    private val stravaAuthService: com.momentum.fitness.data.service.StravaAuthService,
    private val stravaSyncService: com.momentum.fitness.data.service.StravaSyncService,
    private val appConfig: com.momentum.fitness.data.config.AppConfig,
    private val syncWorkManager: com.momentum.fitness.data.work.SyncWorkManager,
    private val activityRepository: ActivityRepository,
    private val databaseCleanupService: DatabaseCleanupService,
    private val testDataLoader: TestDataLoader,
    private val garminAuthService: GarminAuthService,
    private val garminSyncService: GarminSyncService
) : ViewModel() {

    val settings: StateFlow<UserSettingsEntity?> = userSettingsRepository.getSettings()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    fun updateHeartRateZones(
        zone1Max: Int,
        zone2Max: Int,
        zone3Max: Int,
        zone4Max: Int,
        zone5Max: Int
    ) {
        viewModelScope.launch {
            val current = userSettingsRepository.getSettingsSync()
            userSettingsRepository.updateSettings(
                current.copy(
                    heartRateZone1Max = zone1Max,
                    heartRateZone2Max = zone2Max,
                    heartRateZone3Max = zone3Max,
                    heartRateZone4Max = zone4Max,
                    heartRateZone5Max = zone5Max
                )
            )
        }
    }

    fun updateFTP(ftp: Int?) {
        viewModelScope.launch {
            val current = userSettingsRepository.getSettingsSync()
            userSettingsRepository.updateSettings(
                current.copy(functionalThresholdPower = ftp)
            )
        }
    }

    fun updateFTPa(ftpaSecondsPerKm: Int?) {
        viewModelScope.launch {
            val current = userSettingsRepository.getSettingsSync()
            userSettingsRepository.updateSettings(
                current.copy(functionalThresholdPaceSecondsPerKm = ftpaSecondsPerKm)
            )
        }
    }

    fun disconnectStrava() {
        viewModelScope.launch {
            val current = userSettingsRepository.getSettingsSync()
            userSettingsRepository.updateSettings(
                current.copy(
                    stravaAccessToken = null,
                    stravaRefreshToken = null,
                    stravaTokenExpiresAt = null,
                    stravaAthleteId = null
                )
            )
            // Cancel background sync when disconnected
            syncWorkManager.cancelSync()
        }
    }

    fun enableBackgroundSync() {
        syncWorkManager.schedulePeriodicSync()
    }

    fun disableBackgroundSync() {
        syncWorkManager.cancelSync()
    }

    fun triggerSyncNow() {
        syncWorkManager.triggerSyncNow()
    }

    fun getStravaAuthUrl(): String {
        return stravaAuthService.getAuthorizationUrl()
    }

    suspend fun syncStravaActivities(): kotlin.Result<com.momentum.fitness.data.service.SyncResult> {
        return stravaSyncService.syncActivities(force = false)
    }

    suspend fun syncGarminActivities(): kotlin.Result<com.momentum.fitness.data.service.SyncResult> {
        return garminSyncService.syncActivities(force = false)
    }

    fun getGarminAuthUrl(codeChallenge: String): String {
        return garminAuthService.getAuthorizationUrl(codeChallenge)
    }

    suspend fun exchangeGarminCode(code: String, codeVerifier: String): kotlin.Result<com.momentum.fitness.data.api.garmin.GarminTokenResponse> {
        return garminAuthService.exchangeCodeForToken(code, codeVerifier)
    }

    fun disconnectGarmin() {
        viewModelScope.launch {
            val current = userSettingsRepository.getSettingsSync()
            userSettingsRepository.updateSettings(
                current.copy(
                    garminAccessToken = null,
                    garminRefreshToken = null,
                    garminTokenExpiresAt = null,
                    garminUserId = null,
                    garminLastSyncTimestamp = null
                )
            )
            syncWorkManager.cancelGarminSync()
        }
    }

    suspend fun loadTestData(): Int {
        return testDataLoader.loadTestData()
    }

    suspend fun cleanupOldActivities(): Int {
        return databaseCleanupService.cleanupOldActivities()
    }
}
