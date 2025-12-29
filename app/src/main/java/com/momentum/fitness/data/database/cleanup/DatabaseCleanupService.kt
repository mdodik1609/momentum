package com.momentum.fitness.data.database.cleanup

import com.momentum.fitness.data.database.dao.ActivityDao
import com.momentum.fitness.data.database.dao.ActivityStreamDao
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.temporal.ChronoUnit
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Service for managing database size and cleanup
 * Prevents database from growing too large on device
 */
@Singleton
class DatabaseCleanupService @Inject constructor(
    private val activityDao: ActivityDao,
    private val streamDao: ActivityStreamDao
) {
    companion object {
        // Keep activities for 2 years by default
        const val DEFAULT_RETENTION_DAYS = 730
        
        // Maximum number of stream points per activity (for old activities)
        const val MAX_STREAM_POINTS_PER_ACTIVITY = 2000
        
        // Maximum total database size in MB (approximate)
        const val MAX_DB_SIZE_MB = 500
    }

    /**
     * Clean up old activities and their streams
     * @param retentionDays Number of days to keep activities (default: 2 years)
     * @return Number of activities deleted
     */
    suspend fun cleanupOldActivities(retentionDays: Int = DEFAULT_RETENTION_DAYS): Int {
        return withContext(Dispatchers.IO) {
            val cutoffDate = Instant.now().minus(retentionDays.toLong(), ChronoUnit.DAYS)
            
            var deletedCount = 0
            val batchSize = 50
            
            // Process in batches to avoid memory issues
            while (true) {
                val oldActivities = activityDao.getOldActivities(cutoffDate, batchSize)
                
                if (oldActivities.isEmpty()) break
                
                oldActivities.forEach { activity ->
                    // Delete streams first (CASCADE should handle this, but explicit is safer)
                    streamDao.deleteStreamsForActivity(activity.id)
                    // Delete activity
                    activityDao.deleteActivity(activity)
                    deletedCount++
                }
                
                // If we got fewer than batchSize, we're done
                if (oldActivities.size < batchSize) break
            }
            
            deletedCount
        }
    }

    /**
     * Reduce stream data for old activities (keep summary, reduce detail)
     * Samples streams to keep only every Nth point
     */
    suspend fun reduceStreamDataForOldActivities(
        olderThanDays: Int = 90,
        sampleRate: Int = 5
    ): Int {
        return withContext(Dispatchers.IO) {
            val cutoffDate = Instant.now().minus(olderThanDays.toLong(), ChronoUnit.DAYS)
            
            // Get old activities in batches
            var processedCount = 0
            val batchSize = 20
            
            while (true) {
                val oldActivities = activityDao.getOldActivities(cutoffDate, batchSize)
                if (oldActivities.isEmpty()) break
                
                oldActivities.forEach { activity ->
                    val streams = streamDao.getStreamsForActivitySync(activity.id)
                    
                    if (streams.size > MAX_STREAM_POINTS_PER_ACTIVITY) {
                        // Sample streams (keep every Nth point)
                        val sampledStreams = streams
                            .filterIndexed { index, _ -> 
                                index % sampleRate == 0 || 
                                index == 0 || 
                                index == streams.size - 1 
                            }
                        
                        // Delete old streams and insert sampled ones
                        streamDao.deleteStreamsForActivity(activity.id)
                        streamDao.insertStreams(sampledStreams)
                        processedCount++
                    }
                }
                
                if (oldActivities.size < batchSize) break
            }
            
            processedCount
        }
    }

    /**
     * Get approximate database size
     * @return Size in MB (approximate)
     */
    suspend fun getDatabaseSize(): Double {
        return withContext(Dispatchers.IO) {
            val activityCount = activityDao.getActivityCount()
            val streamCount = activityDao.getStreamCount()
            
            // Rough estimate:
            // - ~1KB per activity entity
            // - ~0.1KB per stream point (optimized estimate)
            val estimatedSize = (activityCount * 1.0) + (streamCount * 0.1)
            
            estimatedSize / 1024.0 // Convert to MB
        }
    }

    /**
     * Auto-cleanup based on database size
     * Removes oldest activities if database exceeds size limit
     */
    suspend fun autoCleanupIfNeeded(): CleanupResult {
        return withContext(Dispatchers.IO) {
            val currentSize = getDatabaseSize()
            
            if (currentSize > MAX_DB_SIZE_MB) {
                // Calculate how many days to keep to get under limit
                // Start with 1 year and work backwards
                var retentionDays = 365
                var deletedCount = 0
                
                while (currentSize > MAX_DB_SIZE_MB * 0.8 && retentionDays > 30) {
                    deletedCount = cleanupOldActivities(retentionDays)
                    retentionDays -= 30
                }
                
                CleanupResult(
                    deletedActivities = deletedCount,
                    newSizeMB = getDatabaseSize(),
                    wasCleanupNeeded = true
                )
            } else {
                CleanupResult(
                    deletedActivities = 0,
                    newSizeMB = currentSize,
                    wasCleanupNeeded = false
                )
            }
        }
    }
}

data class CleanupResult(
    val deletedActivities: Int,
    val newSizeMB: Double,
    val wasCleanupNeeded: Boolean
)

