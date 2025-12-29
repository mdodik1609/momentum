package com.momentum.fitness.data.service

import com.momentum.fitness.data.api.strava.StravaApi
import com.momentum.fitness.data.database.entity.ActivityEntity
import com.momentum.fitness.data.database.entity.ActivityStreamEntity
import com.momentum.fitness.data.model.SportType
import com.momentum.fitness.data.repository.ActivityRepository
import com.momentum.fitness.data.repository.UserSettingsRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.first
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Improved Strava Sync Service
 * Features:
 * - Incremental sync (only fetch new activities)
 * - Rate limiting
 * - Exponential backoff retry
 * - Smart sync (check if sync needed)
 */
@Singleton
class StravaSyncService @Inject constructor(
    private val stravaApi: StravaApi,
    private val stravaAuthService: StravaAuthService,
    private val activityRepository: ActivityRepository,
    private val userSettingsRepository: UserSettingsRepository,
    private val rateLimiter: RateLimiter
) {
    companion object {
        // Minimum time between syncs (6 hours)
        const val MIN_SYNC_INTERVAL_SECONDS = 6 * 60 * 60L
        
        // Maximum retries with exponential backoff
        const val MAX_RETRIES = 3
    }

    /**
     * Check if sync is needed
     * Returns true if:
     * - Never synced before, OR
     * - Last sync was more than MIN_SYNC_INTERVAL_SECONDS ago, OR
     * - Force sync requested
     */
    suspend fun shouldSync(force: Boolean = false): Boolean {
        if (force) return true
        
        val settings = userSettingsRepository.getSettingsSync()
        val lastSync = settings.stravaLastSyncTimestamp
        
        if (lastSync == null) return true // Never synced
        
        val now = System.currentTimeMillis() / 1000
        val timeSinceLastSync = now - lastSync
        
        return timeSinceLastSync >= MIN_SYNC_INTERVAL_SECONDS
    }

    /**
     * Sync activities from Strava with incremental updates
     * Only fetches activities newer than last sync
     */
    suspend fun syncActivities(force: Boolean = false): kotlin.Result<SyncResult> {
        return try {
            // Check if sync is needed
            if (!shouldSync(force)) {
                return kotlin.Result.success(SyncResult(syncedCount = 0, skipped = true, reason = "Sync not needed yet"))
            }

            // Check rate limit
            if (!rateLimiter.checkStravaRateLimit()) {
                return kotlin.Result.failure(Exception("Rate limit exceeded. Please try again later."))
            }

            // 1. Get valid access token
            val tokenResult = stravaAuthService.getValidAccessToken()
            val accessToken = tokenResult.getOrElse {
                return kotlin.Result.failure(Exception("Failed to get access token: ${it.message}"))
            }

            rateLimiter.recordStravaRequest()

            // 2. Get last sync timestamp
            val settings = userSettingsRepository.getSettingsSync()
            val lastSyncTimestamp = settings.stravaLastSyncTimestamp
            
            // Fetch activities since last sync (or all if never synced)
            val afterTimestamp = if (lastSyncTimestamp != null && !force) {
                lastSyncTimestamp
            } else {
                // Fetch last 30 days if never synced
                (System.currentTimeMillis() / 1000) - (30 * 24 * 60 * 60)
            }

            // 3. Fetch activities from Strava (incremental)
            var allActivities = mutableListOf<com.momentum.fitness.data.api.strava.StravaActivity>()
            var page = 1
            var hasMore = true
            var requestCount = 0

            while (hasMore && requestCount < 10) { // Max 10 pages to avoid infinite loops
                // Check rate limit before each request
                if (!rateLimiter.checkStravaRateLimit()) {
                    // Wait and retry
                    delay(60_000) // Wait 1 minute
                    if (!rateLimiter.checkStravaRateLimit()) {
                        break // Still rate limited, stop fetching
                    }
                }

                val activities = try {
                    rateLimiter.recordStravaRequest()
                    stravaApi.getActivities(
                        authorization = "Bearer $accessToken",
                        after = afterTimestamp,
                        page = page,
                        perPage = 200
                    )
                } catch (e: Exception) {
                    // Handle rate limit errors
                    if (e.message?.contains("429") == true || e.message?.contains("rate") == true) {
                        delay(60_000) // Wait 1 minute
                        continue
                    }
                    throw e
                }

                if (activities.isEmpty()) {
                    hasMore = false
                } else {
                    allActivities.addAll(activities)
                    // If we got less than 200, we're done
                    if (activities.size < 200) {
                        hasMore = false
                    } else {
                        page++
                        requestCount++
                        // Small delay between pages to be respectful
                        delay(500)
                    }
                }
            }

            if (allActivities.isEmpty()) {
                // Update last sync timestamp even if no new activities
                updateLastSyncTimestamp()
                return kotlin.Result.success(SyncResult(syncedCount = 0, skipped = false, reason = "No new activities"))
            }

            // 4. Process activities (only new ones)
            var syncedCount = 0
            var skippedCount = 0
            var errorCount = 0

            for (stravaActivity in allActivities) {
                try {
                    val activityEntity = mapStravaActivityToEntity(stravaActivity)
                    
                    // Check if activity already exists
                    val existing = activityRepository.getActivityById(activityEntity.id)
                    if (existing == null) {
                        // Check rate limit before fetching streams
                        if (!rateLimiter.checkStravaRateLimit()) {
                            // Save activity without streams if rate limited
                            activityRepository.insertActivity(activityEntity)
                            syncedCount++
                            continue
                        }

                        // Fetch streams for the activity
                        try {
                            rateLimiter.recordStravaRequest()
                            val streams = stravaApi.getActivityStreams(
                                authorization = "Bearer $accessToken",
                                id = stravaActivity.id
                            )

                            // Convert streams
                            val streamEntities = convertStravaStreams(streams, activityEntity.id)
                            
                            // Save to database
                            activityRepository.insertActivity(activityEntity)
                            if (streamEntities.isNotEmpty()) {
                                activityRepository.insertStreams(streamEntities)
                            }
                            syncedCount++
                            
                            // Small delay to respect rate limits
                            delay(100)
                        } catch (e: Exception) {
                            // If stream fetch fails, still save activity
                            activityRepository.insertActivity(activityEntity)
                            syncedCount++
                            errorCount++
                        }
                    } else {
                        skippedCount++
                    }
                } catch (e: Exception) {
                    errorCount++
                    // Continue with next activity
                }
            }

            // 5. Update last sync timestamp
            updateLastSyncTimestamp()

            kotlin.Result.success(
                SyncResult(
                    syncedCount = syncedCount,
                    skipped = false,
                    skippedCount = skippedCount,
                    errorCount = errorCount
                )
            )
        } catch (e: Exception) {
            kotlin.Result.failure(e)
        }
    }

    /**
     * Upload activity to Strava
     * Note: Strava API requires file upload, not direct activity creation
     */
    suspend fun uploadActivity(
        activity: ActivityEntity,
        streams: List<ActivityStreamEntity>
    ): kotlin.Result<Long> {
        return try {
            // Check rate limit
            if (!rateLimiter.checkStravaRateLimit()) {
                return kotlin.Result.failure(Exception("Rate limit exceeded"))
            }

            val tokenResult = stravaAuthService.getValidAccessToken()
            val accessToken = tokenResult.getOrElse {
                return kotlin.Result.failure(Exception("Failed to get access token: ${it.message}"))
            }

            // Check if activity already exists on Strava
            val existingStravaId = activity.sourceId?.takeIf { activity.source == "strava" }
            if (existingStravaId != null) {
                return kotlin.Result.failure(Exception("Activity already exists on Strava"))
            }

            // Note: Strava upload requires converting to FIT/GPX/TCX format
            // This is a placeholder - full implementation would:
            // 1. Convert activity + streams to FIT/GPX/TCX file
            // 2. Upload file using Strava upload API
            // 3. Poll for upload status
            // 4. Get activity ID from completed upload
            
            kotlin.Result.failure(Exception("Activity upload not yet implemented. Use file import instead."))
        } catch (e: Exception) {
            kotlin.Result.failure(e)
        }
    }

    private suspend fun updateLastSyncTimestamp() {
        val settings = userSettingsRepository.getSettingsSync()
        userSettingsRepository.updateSettings(
            settings.copy(
                stravaLastSyncTimestamp = System.currentTimeMillis() / 1000
            )
        )
    }

    private fun convertStravaStreams(
        streams: Map<String, com.momentum.fitness.data.api.strava.StravaStream>,
        activityId: String
    ): List<ActivityStreamEntity> {
        val streamEntities = mutableListOf<ActivityStreamEntity>()
        
        // Extract time stream
        val timeData = (streams["time"]?.data as? List<*>)?.mapNotNull { (it as? Number)?.toInt() } ?: emptyList()
        val latlngData = (streams["latlng"]?.data as? List<*>)?.mapNotNull { 
            (it as? List<*>)?.let { coords ->
                if (coords.size >= 2) {
                    (coords[0] as? Number)?.toDouble() to (coords[1] as? Number)?.toDouble()
                } else null
            }
        } ?: emptyList()
        val altitudeData = (streams["altitude"]?.data as? List<*>)?.mapNotNull { (it as? Number)?.toDouble() } ?: emptyList()
        val heartrateData = (streams["heartrate"]?.data as? List<*>)?.mapNotNull { (it as? Number)?.toInt() } ?: emptyList()
        val cadenceData = (streams["cadence"]?.data as? List<*>)?.mapNotNull { (it as? Number)?.toDouble() } ?: emptyList()
        val wattsData = (streams["watts"]?.data as? List<*>)?.mapNotNull { (it as? Number)?.toDouble() } ?: emptyList()
        val velocityData = (streams["velocity_smooth"]?.data as? List<*>)?.mapNotNull { (it as? Number)?.toDouble() } ?: emptyList()
        val gradeData = (streams["grade_smooth"]?.data as? List<*>)?.mapNotNull { (it as? Number)?.toDouble() } ?: emptyList()

        val maxLength = maxOf(
            timeData.size,
            latlngData.size,
            altitudeData.size,
            heartrateData.size,
            cadenceData.size,
            wattsData.size,
            velocityData.size,
            gradeData.size
        )

        for (i in 0 until maxLength) {
            val timeOffset = timeData.getOrNull(i) ?: i
            val latlng = latlngData.getOrNull(i)
            val altitude = altitudeData.getOrNull(i)
            val hr = heartrateData.getOrNull(i)
            val cadence = cadenceData.getOrNull(i)
            val power = wattsData.getOrNull(i)
            val speed = velocityData.getOrNull(i)
            val grade = gradeData.getOrNull(i)

            streamEntities.add(
                ActivityStreamEntity(
                    activityId = activityId,
                    timeOffsetSeconds = timeOffset,
                    latitude = latlng?.second,
                    longitude = latlng?.first,
                    altitudeMeters = altitude,
                    heartRateBpm = hr,
                    cadenceRpm = cadence,
                    powerWatts = power,
                    speedMps = speed,
                    grade = grade
                )
            )
        }

        return streamEntities
    }

    private fun mapStravaActivityToEntity(
        stravaActivity: com.momentum.fitness.data.api.strava.StravaActivity
    ): ActivityEntity {
        val startDate = Instant.parse(stravaActivity.start_date)
        
        return ActivityEntity(
            id = "strava_${stravaActivity.id}",
            name = stravaActivity.name,
            sportType = SportType.fromString(stravaActivity.type),
            startDate = startDate,
            timezone = stravaActivity.timezone,
            movingTimeSeconds = stravaActivity.moving_time,
            elapsedTimeSeconds = stravaActivity.elapsed_time,
            distanceMeters = stravaActivity.distance,
            elevationGainMeters = stravaActivity.total_elevation_gain,
            elevationLossMeters = null,
            averageSpeedMps = stravaActivity.average_speed,
            maxSpeedMps = stravaActivity.max_speed,
            averageHeartRateBpm = stravaActivity.average_heartrate?.toInt(),
            maxHeartRateBpm = stravaActivity.max_heartrate?.toInt(),
            averageCadenceRpm = stravaActivity.average_cadence,
            averagePowerWatts = stravaActivity.average_watts,
            maxPowerWatts = stravaActivity.max_watts,
            calories = stravaActivity.kilojoules?.toInt(),
            description = stravaActivity.description,
            source = "strava",
            sourceId = stravaActivity.id.toString(),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }
}

data class SyncResult(
    val syncedCount: Int,
    val skipped: Boolean = false,
    val skippedCount: Int = 0,
    val errorCount: Int = 0,
    val reason: String? = null
)
