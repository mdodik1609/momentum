package com.momentum.fitness.data.service

import com.momentum.fitness.data.api.garmin.GarminApi
import com.momentum.fitness.data.database.entity.ActivityEntity
import com.momentum.fitness.data.database.entity.ActivityStreamEntity
import com.momentum.fitness.data.model.SportType
import com.momentum.fitness.data.repository.ActivityRepository
import com.momentum.fitness.data.repository.UserSettingsRepository
import kotlinx.coroutines.delay
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Garmin Sync Service
 * Syncs activities from Garmin Connect API
 * Features:
 * - Incremental sync (only fetch new activities)
 * - Rate limiting
 * - Smart sync checks
 */
@Singleton
class GarminSyncService @Inject constructor(
    private val garminApi: GarminApi,
    private val garminAuthService: GarminAuthService,
    private val activityRepository: ActivityRepository,
    private val userSettingsRepository: UserSettingsRepository,
    private val rateLimiter: RateLimiter
) {
    companion object {
        // Minimum time between syncs (6 hours)
        const val MIN_SYNC_INTERVAL_SECONDS = 6 * 60 * 60L
    }

    /**
     * Check if sync is needed
     */
    suspend fun shouldSync(force: Boolean = false): Boolean {
        if (force) return true
        
        val settings = userSettingsRepository.getSettingsSync()
        val lastSync = settings.garminLastSyncTimestamp
        
        if (lastSync == null) return true // Never synced
        
        val now = System.currentTimeMillis() / 1000
        val timeSinceLastSync = now - lastSync
        
        return timeSinceLastSync >= MIN_SYNC_INTERVAL_SECONDS
    }

    /**
     * Sync activities from Garmin
     */
    suspend fun syncActivities(force: Boolean = false): kotlin.Result<SyncResult> {
        return try {
            // Check if sync is needed
            if (!shouldSync(force)) {
                return kotlin.Result.success(SyncResult(syncedCount = 0, skipped = true, reason = "Sync not needed yet"))
            }

            // Check rate limit
            if (!rateLimiter.checkGarminRateLimit()) {
                return kotlin.Result.failure(Exception("Rate limit exceeded. Please try again later."))
            }

            // 1. Get valid access token
            val tokenResult = garminAuthService.getValidAccessToken()
            val accessToken = tokenResult.getOrElse {
                return kotlin.Result.failure(Exception("Failed to get access token: ${it.message}"))
            }

            rateLimiter.recordGarminRequest()

            // 2. Get last sync timestamp
            val settings = userSettingsRepository.getSettingsSync()
            val lastSyncTimestamp = settings.garminLastSyncTimestamp
            
            // Fetch activities since last sync (or last 30 days if never synced)
            val afterTimestamp = if (lastSyncTimestamp != null && !force) {
                lastSyncTimestamp * 1000 // Garmin uses milliseconds
            } else {
                System.currentTimeMillis() - (30 * 24 * 60 * 60 * 1000L)
            }

            // 3. Fetch activities from Garmin
            val activities = try {
                rateLimiter.recordGarminRequest()
                garminApi.getActivities(
                    authorization = "Bearer $accessToken",
                    start = afterTimestamp / 1000, // Convert to seconds
                    limit = 100
                )
            } catch (e: Exception) {
                if (e.message?.contains("429") == true || e.message?.contains("rate") == true) {
                    delay(60_000) // Wait 1 minute
                    return kotlin.Result.failure(Exception("Rate limit exceeded. Please try again later."))
                }
                throw e
            }

            if (activities.isEmpty()) {
                updateLastSyncTimestamp()
                return kotlin.Result.success(SyncResult(syncedCount = 0, skipped = false, reason = "No new activities"))
            }

            // 4. Process activities
            var syncedCount = 0
            var skippedCount = 0
            var errorCount = 0

            for (garminActivity in activities) {
                try {
                    val activityEntity = mapGarminActivityToEntity(garminActivity)
                    
                    // Check if activity already exists
                    val existing = activityRepository.getActivityById(activityEntity.id)
                    if (existing == null) {
                        // Check rate limit before fetching streams
                        if (!rateLimiter.checkGarminRateLimit()) {
                            // Save activity without streams if rate limited
                            activityRepository.insertActivity(activityEntity)
                            syncedCount++
                            continue
                        }

                        // Fetch streams for the activity
                        try {
                            rateLimiter.recordGarminRequest()
                            val streams = garminApi.getActivityStreams(
                                authorization = "Bearer $accessToken",
                                activityId = garminActivity.activityId
                            )

                            // Convert streams
                            val streamEntities = convertGarminStreams(streams, activityEntity.id)
                            
                            // Save to database
                            activityRepository.insertActivity(activityEntity)
                            if (streamEntities.isNotEmpty()) {
                                activityRepository.insertStreams(streamEntities)
                            }
                            syncedCount++
                            
                            // Small delay to respect rate limits
                            delay(200)
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
     * Upload activity to Garmin
     * Note: Requires converting activity to FIT/GPX/TCX format
     */
    suspend fun uploadActivity(
        activity: ActivityEntity,
        streams: List<ActivityStreamEntity>
    ): kotlin.Result<Long> {
        return try {
            // Check rate limit
            if (!rateLimiter.checkGarminRateLimit()) {
                return kotlin.Result.failure(Exception("Rate limit exceeded"))
            }

            val tokenResult = garminAuthService.getValidAccessToken()
            val accessToken = tokenResult.getOrElse {
                return kotlin.Result.failure(Exception("Failed to get access token: ${it.message}"))
            }

            // Check if activity already exists on Garmin
            val existingGarminId = activity.sourceId?.takeIf { activity.source == "garmin" }
            if (existingGarminId != null) {
                return kotlin.Result.failure(Exception("Activity already exists on Garmin"))
            }

            // Note: Garmin upload requires converting to FIT/GPX/TCX format
            // This is a placeholder - full implementation would:
            // 1. Convert activity + streams to FIT/GPX/TCX file
            // 2. Upload file using Garmin upload API
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
                garminLastSyncTimestamp = System.currentTimeMillis() / 1000
            )
        )
    }

    private fun convertGarminStreams(
        streams: Map<String, com.momentum.fitness.data.api.garmin.GarminStream>,
        activityId: String
    ): List<ActivityStreamEntity> {
        val streamEntities = mutableListOf<ActivityStreamEntity>()
        
        val gpsStream = streams["GPS"]
        val hrStream = streams["HEARTRATE"]
        val cadenceStream = streams["CADENCE"]
        val powerStream = streams["POWER"]
        val speedStream = streams["SPEED"]
        val altitudeStream = streams["ALTITUDE"]

        val maxLength = maxOf(
            gpsStream?.values?.size ?: 0,
            hrStream?.values?.size ?: 0,
            cadenceStream?.values?.size ?: 0,
            powerStream?.values?.size ?: 0,
            speedStream?.values?.size ?: 0,
            altitudeStream?.values?.size ?: 0
        )

        for (i in 0 until maxLength) {
            val gps = gpsStream?.values?.getOrNull(i)
            val hr = hrStream?.values?.getOrNull(i)?.toInt()
            val cadence = cadenceStream?.values?.getOrNull(i)
            val power = powerStream?.values?.getOrNull(i)
            val speed = speedStream?.values?.getOrNull(i)
            val altitude = altitudeStream?.values?.getOrNull(i)

            // Garmin GPS format: [latitude, longitude] or separate streams
            val latitude = if (gps != null && gps > 0) {
                // Garmin may provide GPS as single value or array
                // This is simplified - actual implementation depends on Garmin API format
                null // Will need to parse based on actual API response
            } else null

            streamEntities.add(
                ActivityStreamEntity(
                    activityId = activityId,
                    timeOffsetSeconds = i,
                    latitude = latitude,
                    longitude = null, // Will need to parse from GPS stream
                    altitudeMeters = altitude,
                    heartRateBpm = hr,
                    cadenceRpm = cadence,
                    powerWatts = power,
                    speedMps = speed,
                    grade = null
                )
            )
        }

        return streamEntities
    }

    private fun mapGarminActivityToEntity(
        garminActivity: com.momentum.fitness.data.api.garmin.GarminActivity
    ): ActivityEntity {
        val startDate = Instant.parse(garminActivity.startTimeGMT)
        
        return ActivityEntity(
            id = "garmin_${garminActivity.activityId}",
            name = garminActivity.activityName,
            sportType = SportType.fromString(garminActivity.activityType.typeKey),
            startDate = startDate,
            timezone = null, // Garmin provides GMT time
            movingTimeSeconds = (garminActivity.movingDuration ?: garminActivity.duration ?: 0.0).toInt(),
            elapsedTimeSeconds = (garminActivity.elapsedDuration ?: garminActivity.duration ?: 0.0).toInt(),
            distanceMeters = garminActivity.distance,
            elevationGainMeters = garminActivity.elevationGain,
            elevationLossMeters = garminActivity.elevationLoss,
            averageSpeedMps = garminActivity.averageSpeed,
            maxSpeedMps = garminActivity.maxSpeed,
            averageHeartRateBpm = garminActivity.averageHR,
            maxHeartRateBpm = garminActivity.maxHR,
            averageCadenceRpm = garminActivity.averageCadence,
            averagePowerWatts = garminActivity.averagePower,
            maxPowerWatts = garminActivity.maxPower,
            calories = garminActivity.calories,
            description = null,
            source = "garmin",
            sourceId = garminActivity.activityId.toString(),
            createdAt = Instant.now(),
            updatedAt = Instant.now()
        )
    }
}







