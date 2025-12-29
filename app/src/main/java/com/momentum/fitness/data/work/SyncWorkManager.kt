package com.momentum.fitness.data.work

import android.content.Context
import androidx.work.Constraints
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.NetworkType
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SyncWorkManager @Inject constructor(
    private val context: Context
) {
    private val workManager = WorkManager.getInstance(context)

    /**
     * Schedule periodic sync for Strava
     * Syncs once per day (with 12-hour flex interval)
     */
    fun scheduleStravaSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(false)
            .build()

        val syncWork = PeriodicWorkRequestBuilder<StravaSyncWorker>(
            1, TimeUnit.DAYS,
            12, TimeUnit.HOURS // Flex interval
        )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            StravaSyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            syncWork
        )
    }

    /**
     * Schedule periodic sync for Garmin
     * Syncs once per day (with 12-hour flex interval)
     */
    fun scheduleGarminSync() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .setRequiresBatteryNotLow(false)
            .build()

        val syncWork = PeriodicWorkRequestBuilder<com.momentum.fitness.data.work.GarminSyncWorker>(
            1, TimeUnit.DAYS,
            12, TimeUnit.HOURS // Flex interval
        )
            .setConstraints(constraints)
            .build()

        workManager.enqueueUniquePeriodicWork(
            com.momentum.fitness.data.work.GarminSyncWorker.WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            syncWork
        )
    }

    /**
     * Schedule sync for both services
     */
    fun schedulePeriodicSync() {
        scheduleStravaSync()
        scheduleGarminSync()
    }

    fun cancelStravaSync() {
        workManager.cancelUniqueWork(StravaSyncWorker.WORK_NAME)
    }

    fun cancelGarminSync() {
        workManager.cancelUniqueWork(com.momentum.fitness.data.work.GarminSyncWorker.WORK_NAME)
    }

    fun cancelSync() {
        cancelStravaSync()
        cancelGarminSync()
    }

    fun triggerStravaSyncNow() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncWork = androidx.work.OneTimeWorkRequestBuilder<StravaSyncWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueue(syncWork)
    }

    fun triggerGarminSyncNow() {
        val constraints = Constraints.Builder()
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val syncWork = androidx.work.OneTimeWorkRequestBuilder<com.momentum.fitness.data.work.GarminSyncWorker>()
            .setConstraints(constraints)
            .build()

        workManager.enqueue(syncWork)
    }

    fun triggerSyncNow() {
        triggerStravaSyncNow()
        triggerGarminSyncNow()
    }
}

