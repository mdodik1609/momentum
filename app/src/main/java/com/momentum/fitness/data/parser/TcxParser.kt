package com.momentum.fitness.data.parser

import com.momentum.fitness.data.database.entity.ActivityEntity
import com.momentum.fitness.data.database.entity.ActivityStreamEntity
import com.momentum.fitness.data.model.SportType
import org.simpleframework.xml.Element
import org.simpleframework.xml.ElementList
import org.simpleframework.xml.Root
import org.simpleframework.xml.core.Persister
import java.io.InputStream
import java.time.Instant
import java.time.ZoneId

/**
 * TCX File Parser
 * Parses .tcx files and extracts activity data
 */
object TcxParser {
    @Root(name = "TrainingCenterDatabase", strict = false)
    data class TrainingCenterDatabase(
        @Element(name = "Activities", required = false)
        var activities: Activities? = null
    )

    @Root(name = "Activities", strict = false)
    data class Activities(
        @ElementList(name = "Activity", required = false, inline = true)
        var activityList: List<Activity>? = null
    )

    @Root(name = "Activity", strict = false)
    data class Activity(
        @Element(name = "Id", required = false)
        var id: String? = null,
        @Element(name = "Sport", required = false)
        var sport: String? = null,
        @ElementList(name = "Lap", required = false, inline = true)
        var laps: List<Lap>? = null
    )

    @Root(name = "Lap", strict = false)
    data class Lap(
        @Element(name = "TotalTimeSeconds", required = false)
        var totalTimeSeconds: Double? = null,
        @Element(name = "DistanceMeters", required = false)
        var distanceMeters: Double? = null,
        @Element(name = "MaximumSpeed", required = false)
        var maximumSpeed: Double? = null,
        @Element(name = "Calories", required = false)
        var calories: Int? = null,
        @Element(name = "AverageHeartRateBpm", required = false)
        var averageHeartRateBpm: HeartRate? = null,
        @Element(name = "MaximumHeartRateBpm", required = false)
        var maximumHeartRateBpm: HeartRate? = null,
        @Element(name = "Intensity", required = false)
        var intensity: String? = null,
        @Element(name = "TriggerMethod", required = false)
        var triggerMethod: String? = null,
        @Element(name = "Track", required = false)
        var track: Track? = null
    )

    @Root(name = "Track", strict = false)
    data class Track(
        @ElementList(name = "Trackpoint", required = false, inline = true)
        var trackpoints: List<Trackpoint>? = null
    )

    @Root(name = "Trackpoint", strict = false)
    data class Trackpoint(
        @Element(name = "Time", required = false)
        var time: String? = null,
        @Element(name = "Position", required = false)
        var position: Position? = null,
        @Element(name = "AltitudeMeters", required = false)
        var altitudeMeters: Double? = null,
        @Element(name = "DistanceMeters", required = false)
        var distanceMeters: Double? = null,
        @Element(name = "HeartRateBpm", required = false)
        var heartRateBpm: HeartRate? = null,
        @Element(name = "Cadence", required = false)
        var cadence: Double? = null,
        @Element(name = "Extensions", required = false)
        var extensions: Extensions? = null
    )

    @Root(name = "Position", strict = false)
    data class Position(
        @Element(name = "LatitudeDegrees", required = false)
        var latitudeDegrees: Double? = null,
        @Element(name = "LongitudeDegrees", required = false)
        var longitudeDegrees: Double? = null
    )

    @Root(name = "HeartRateBpm", strict = false)
    data class HeartRate(
        @Element(name = "Value", required = false)
        var value: Int? = null
    )

    @Root(name = "Extensions", strict = false)
    data class Extensions(
        @Element(name = "TPX", required = false)
        var tpx: Tpx? = null
    )

    @Root(name = "TPX", strict = false)
    data class Tpx(
        @Element(name = "Watts", required = false)
        var watts: Double? = null,
        @Element(name = "Speed", required = false)
        var speed: Double? = null
    )

    fun parse(inputStream: InputStream): ParseResult {
        return try {
            val serializer = Persister()
            val database = serializer.read(TrainingCenterDatabase::class.java, inputStream)

            val activity = database.activities?.activityList?.firstOrNull()
                ?: return ParseResult.Error("No activity found in TCX file")

            val laps = activity.laps ?: emptyList()
            val allTrackpoints = laps.flatMap { it.track?.trackpoints ?: emptyList() }

            if (allTrackpoints.isEmpty()) {
                return ParseResult.Error("No trackpoints found in TCX file")
            }

            // Calculate totals
            val totalTime = laps.sumOf { it.totalTimeSeconds ?: 0.0 }.toInt()
            val totalDistance = laps.sumOf { it.distanceMeters ?: 0.0 }
            val totalElevationGain = calculateElevationGain(allTrackpoints)
            val maxSpeed = laps.mapNotNull { it.maximumSpeed }.maxOrNull()
            val calories = laps.sumOf { it.calories ?: 0 }
            val avgHR = laps.mapNotNull { it.averageHeartRateBpm?.value }.average().takeIf { it.isFinite() }?.toInt()
            val maxHR = laps.mapNotNull { it.maximumHeartRateBpm?.value }.maxOrNull()

            val startTime = allTrackpoints.firstOrNull()?.time?.let { parseTime(it) } ?: Instant.now()

            // Create streams
            val streams = mutableListOf<ActivityStreamEntity>()
            var timeOffset = 0

            allTrackpoints.forEach { trackpoint ->
                trackpoint.position?.let { pos ->
                    pos.latitudeDegrees?.let { lat ->
                        pos.longitudeDegrees?.let { lng ->
                            streams.add(
                                ActivityStreamEntity(
                                    activityId = "",
                                    timeOffsetSeconds = timeOffset++,
                                    latitude = lat,
                                    longitude = lng,
                                    altitudeMeters = trackpoint.altitudeMeters,
                                    heartRateBpm = trackpoint.heartRateBpm?.value,
                                    cadenceRpm = trackpoint.cadence,
                                    powerWatts = trackpoint.extensions?.tpx?.watts,
                                    speedMps = trackpoint.extensions?.tpx?.speed,
                                    grade = null
                                )
                            )
                        }
                    }
                }
            }

            val sportType = when (activity.sport?.uppercase()) {
                "RUNNING" -> SportType.RUN
                "CYCLING", "BIKING" -> SportType.RIDE
                "SWIMMING" -> SportType.SWIM
                else -> SportType.RUN
            }

            val activityEntity = ActivityEntity(
                id = "tcx_${startTime.toEpochMilli()}",
                name = "TCX Activity",
                sportType = sportType,
                startDate = startTime,
                timezone = ZoneId.systemDefault().id,
                movingTimeSeconds = totalTime,
                elapsedTimeSeconds = totalTime,
                distanceMeters = totalDistance,
                elevationGainMeters = totalElevationGain,
                elevationLossMeters = null,
                averageSpeedMps = if (totalTime > 0) totalDistance / totalTime else null,
                maxSpeedMps = maxSpeed,
                averageHeartRateBpm = avgHR,
                maxHeartRateBpm = maxHR,
                averageCadenceRpm = streams.mapNotNull { it.cadenceRpm }.average().takeIf { it.isFinite() },
                averagePowerWatts = streams.mapNotNull { it.powerWatts }.average().takeIf { it.isFinite() },
                maxPowerWatts = streams.mapNotNull { it.powerWatts }.maxOrNull(),
                calories = if (calories > 0) calories else null,
                description = null,
                source = "file_import",
                sourceId = null,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )

            ParseResult.Success(activityEntity, streams)
        } catch (e: Exception) {
            ParseResult.Error("Failed to parse TCX file: ${e.message}")
        }
    }

    private fun parseTime(timeString: String): Instant {
        return try {
            Instant.parse(timeString)
        } catch (e: Exception) {
            Instant.now()
        }
    }

    private fun calculateElevationGain(trackpoints: List<Trackpoint>): Double {
        var elevationGain = 0.0
        var previousElevation: Double? = null

        trackpoints.forEach { point ->
            point.altitudeMeters?.let { elev ->
                previousElevation?.let { prev ->
                    if (elev > prev) {
                        elevationGain += (elev - prev)
                    }
                }
                previousElevation = elev
            }
        }

        return elevationGain
    }
}


