package com.momentum.fitness.data.work

import android.content.Context
import androidx.hilt.work.HiltWorker
import androidx.work.CoroutineWorker
import androidx.work.ForegroundInfo
import androidx.work.WorkerParameters
import androidx.work.workDataOf
import com.momentum.fitness.data.service.GarminSyncService
import dagger.assisted.Assisted
import dagger.assisted.AssistedInject
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

@HiltWorker
class GarminSyncWorker @AssistedInject constructor(
    @Assisted private val context: Context,
    @Assisted params: WorkerParameters,
    private val garminSyncService: GarminSyncService
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result = withContext(Dispatchers.IO) {
        setForeground(createForegroundInfo())

        return@withContext try {
            val result = garminSyncService.syncActivities(force = false)
            result.fold(
                onSuccess = { syncResult ->
                    Result.success(workDataOf(
                        "synced_count" to syncResult.syncedCount,
                        "skipped" to syncResult.skipped
                    ))
                },
                onFailure = { error ->
                    Result.retry()
                }
            )
        } catch (e: Exception) {
            Result.failure(workDataOf("error" to e.message))
        }
    }

    private fun createForegroundInfo(): ForegroundInfo {
        val notification = NotificationHelper.createSyncNotification(context, "Garmin")
            .build()

        return ForegroundInfo(NOTIFICATION_ID, notification)
    }

    companion object {
        const val NOTIFICATION_ID = 2
        const val WORK_NAME = "garmin_sync_work"
    }
}







