package com.momentum.fitness.data.util

import com.momentum.fitness.data.database.entity.ActivityEntity
import com.momentum.fitness.data.database.entity.ActivityStreamEntity
import com.momentum.fitness.data.model.SportType
import com.momentum.fitness.data.repository.PersonalRecordRepository
import kotlinx.coroutines.flow.first

/**
 * Personal Record Detector
 * Automatically scans activities to find new PRs
 */
object PersonalRecordDetector {
    data class RecordType(
        val name: String,
        val distanceMeters: Double? = null,
        val durationSeconds: Int? = null,
        val sportTypes: List<SportType>
    )

    val RUNNING_RECORDS = listOf(
        RecordType("1k", distanceMeters = 1000.0, sportTypes = listOf(SportType.RUN, SportType.TRAIL_RUN)),
        RecordType("1_mile", distanceMeters = 1609.34, sportTypes = listOf(SportType.RUN, SportType.TRAIL_RUN)),
        RecordType("5k", distanceMeters = 5000.0, sportTypes = listOf(SportType.RUN, SportType.TRAIL_RUN)),
        RecordType("10k", distanceMeters = 10000.0, sportTypes = listOf(SportType.RUN, SportType.TRAIL_RUN)),
        RecordType("half_marathon", distanceMeters = 21097.5, sportTypes = listOf(SportType.RUN, SportType.TRAIL_RUN)),
        RecordType("marathon", distanceMeters = 42195.0, sportTypes = listOf(SportType.RUN, SportType.TRAIL_RUN))
    )

    val CYCLING_RECORDS = listOf(
        RecordType("20min_power", durationSeconds = 1200, sportTypes = listOf(SportType.RIDE, SportType.INDOOR_RIDE))
    )

    /**
     * Detect PRs in an activity
     */
    suspend fun detectPRs(
        activity: ActivityEntity,
        streams: List<ActivityStreamEntity>,
        repository: PersonalRecordRepository
    ): List<DetectedPR> {
        val detectedPRs = mutableListOf<DetectedPR>()
        val allRecords = RUNNING_RECORDS + CYCLING_RECORDS

        allRecords.forEach { recordType ->
            if (recordType.sportTypes.contains(activity.sportType)) {
                val pr = detectPRForRecordType(activity, streams, recordType, repository)
                pr?.let { detectedPRs.add(it) }
            }
        }

        return detectedPRs
    }

    private suspend fun detectPRForRecordType(
        activity: ActivityEntity,
        streams: List<ActivityStreamEntity>,
        recordType: RecordType,
        repository: PersonalRecordRepository
    ): DetectedPR? {
        val existingPR = repository.getBestRecord(recordType.name, activity.sportType)

        when {
            // Distance-based records (running)
            recordType.distanceMeters != null -> {
                val bestTime = findBestTimeForDistance(streams, recordType.distanceMeters, activity.distanceMeters)
                bestTime?.let { time ->
                    val isNewPR = existingPR == null || time < existingPR.value
                    if (isNewPR) {
                        return DetectedPR(
                            recordType = recordType.name,
                            value = time,
                            activityId = activity.id,
                            achievedAt = activity.startDate
                        )
                    }
                }
            }
            // Duration-based records (cycling power)
            recordType.durationSeconds != null -> {
                val bestPower = findBestPowerForDuration(streams, recordType.durationSeconds)
                bestPower?.let { power ->
                    val isNewPR = existingPR == null || power > existingPR.value
                    if (isNewPR) {
                        return DetectedPR(
                            recordType = recordType.name,
                            value = power,
                            activityId = activity.id,
                            achievedAt = activity.startDate
                        )
                    }
                }
            }
        }

        return null
    }

    /**
     * Find best time for a given distance using a sliding window
     */
    private fun findBestTimeForDistance(
        streams: List<ActivityStreamEntity>,
        targetDistance: Double,
        totalDistance: Double?
    ): Double? {
        if (streams.isEmpty() || totalDistance == null || totalDistance < targetDistance) {
            return null
        }

        // Calculate cumulative distance
        val cumulativeDistances = mutableListOf<Pair<Int, Double>>()
        var currentDistance = 0.0
        var previousLat: Double? = null
        var previousLng: Double? = null

        streams.forEachIndexed { index, stream ->
            stream.latitude?.let { lat ->
                stream.longitude?.let { lng ->
                    previousLat?.let { prevLat ->
                        previousLng?.let { prevLng ->
                            val segmentDistance = calculateDistance(prevLat, prevLng, lat, lng)
                            currentDistance += segmentDistance
                        }
                    }
                    cumulativeDistances.add(index to currentDistance)
                    previousLat = lat
                    previousLng = lng
                }
            }
        }

        // Find fastest segment of target distance
        var bestTime: Double? = null
        for (i in cumulativeDistances.indices) {
            val startDistance = cumulativeDistances[i].second
            val endDistance = startDistance + targetDistance

            val endIndex = cumulativeDistances.binarySearch { (_, dist) ->
                when {
                    dist < endDistance -> -1
                    dist > endDistance -> 1
                    else -> 0
                }
            }.let { if (it < 0) -it - 1 else it }

            if (endIndex < cumulativeDistances.size) {
                val timeSeconds = (endIndex - i).toDouble()
                if (bestTime == null || timeSeconds < bestTime!!) {
                    bestTime = timeSeconds
                }
            }
        }

        return bestTime
    }

    /**
     * Find best average power for a given duration
     */
    private fun findBestPowerForDuration(
        streams: List<ActivityStreamEntity>,
        durationSeconds: Int
    ): Double? {
        if (streams.size < durationSeconds) return null

        var bestPower: Double? = null

        for (i in 0..(streams.size - durationSeconds)) {
            val segment = streams.subList(i, i + durationSeconds)
            val avgPower = segment.mapNotNull { it.powerWatts }.average()
            
            if (avgPower.isFinite() && (bestPower == null || avgPower > bestPower!!)) {
                bestPower = avgPower
            }
        }

        return bestPower
    }

    /**
     * Calculate distance between two lat/lng points (Haversine formula)
     */
    private fun calculateDistance(lat1: Double, lng1: Double, lat2: Double, lng2: Double): Double {
        val earthRadius = 6371000.0 // meters
        val dLat = Math.toRadians(lat2 - lat1)
        val dLng = Math.toRadians(lng2 - lng1)
        val a = kotlin.math.sin(dLat / 2) * kotlin.math.sin(dLat / 2) +
                kotlin.math.cos(Math.toRadians(lat1)) * kotlin.math.cos(Math.toRadians(lat2)) *
                kotlin.math.sin(dLng / 2) * kotlin.math.sin(dLng / 2)
        val c = 2 * kotlin.math.atan2(kotlin.math.sqrt(a), kotlin.math.sqrt(1 - a))
        return earthRadius * c
    }
}

data class DetectedPR(
    val recordType: String,
    val value: Double,
    val activityId: String,
    val achievedAt: java.time.Instant
)







