package com.momentum.fitness

import android.app.Application
import com.momentum.fitness.data.config.ThemeManager
import com.momentum.fitness.data.database.cleanup.DatabaseCleanupService
import com.momentum.fitness.data.repository.ActivityRepository
import com.momentum.fitness.data.repository.UserSettingsRepository
import com.momentum.fitness.data.service.GarminAuthService
import com.momentum.fitness.data.test.TestDataLoader
import com.momentum.fitness.data.work.SyncWorkManager
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class MomentumApplication : Application() {
    @Inject
    lateinit var themeManager: ThemeManager
    
    @Inject
    lateinit var syncWorkManager: SyncWorkManager
    
    @Inject
    lateinit var databaseCleanupService: DatabaseCleanupService
    
    @Inject
    lateinit var garminAuthService: GarminAuthService
    
    @Inject
    lateinit var userSettingsRepository: UserSettingsRepository
    
    @Inject
    lateinit var testDataLoader: TestDataLoader
    
    @Inject
    lateinit var activityRepository: ActivityRepository

    private val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    override fun onCreate() {
        super.onCreate()
        // Create notification channel for background sync
        com.momentum.fitness.data.work.NotificationHelper.createNotificationChannel(this)
        
        // Initialize settings on app start (ensures settings exist)
        applicationScope.launch {
            try {
                userSettingsRepository.getSettingsSync()
            } catch (e: Exception) {
                android.util.Log.e("Momentum", "Settings initialization failed: ${e.message}")
            }
        }
        
        // Auto-load test data if no activities exist (for testing)
        // This runs in background and will populate data within a few seconds
        applicationScope.launch {
            try {
                // Small delay to ensure database is ready
                kotlinx.coroutines.delay(500)
                val activityCount = activityRepository.getActivityCount()
                android.util.Log.d("Momentum", "Current activity count: $activityCount")
                if (activityCount == 0) {
                    // Auto-load test data on first launch
                    android.util.Log.d("Momentum", "No activities found, loading test data...")
                    val loaded = testDataLoader.loadTestData()
                    android.util.Log.d("Momentum", "Auto-loaded $loaded test activities")
                } else {
                    android.util.Log.d("Momentum", "Activities already exist, skipping test data load")
                }
            } catch (e: Exception) {
                android.util.Log.e("Momentum", "Test data check failed: ${e.message}", e)
            }
        }
        
        // Auto-cleanup database if needed (runs in background)
        applicationScope.launch {
            try {
                databaseCleanupService.autoCleanupIfNeeded()
            } catch (e: Exception) {
                // Log error but don't crash app
                android.util.Log.e("Momentum", "Database cleanup failed: ${e.message}")
            }
        }
    }
}

