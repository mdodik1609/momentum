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
import java.time.format.DateTimeFormatter

/**
 * GPX File Parser
 * Parses .gpx files and extracts activity data
 */
object GpxParser {
    @Root(name = "gpx", strict = false)
    data class Gpx(
        @Element(name = "trk", required = false)
        var track: Track? = null
    )

    @Root(name = "trk", strict = false)
    data class Track(
        @Element(name = "name", required = false)
        var name: String? = null,
        @ElementList(name = "trkseg", required = false, inline = true)
        var segments: List<TrackSegment>? = null
    )

    @Root(name = "trkseg", strict = false)
    data class TrackSegment(
        @ElementList(name = "trkpt", required = false, inline = true)
        var points: List<TrackPoint>? = null
    )

    @Root(name = "trkpt", strict = false)
    data class TrackPoint(
        @Element(name = "ele", required = false)
        var elevation: Double? = null,
        @Element(name = "time", required = false)
        var time: String? = null,
        @Element(name = "extensions", required = false)
        var extensions: Extensions? = null
    ) {
        @get:org.simpleframework.xml.Attribute(name = "lat", required = false)
        var latitude: Double? = null

        @get:org.simpleframework.xml.Attribute(name = "lon", required = false)
        var longitude: Double? = null
    }

    @Root(name = "extensions", strict = false)
    data class Extensions(
        @Element(name = "hr", required = false)
        var heartRate: Int? = null,
        @Element(name = "cad", required = false)
        var cadence: Double? = null,
        @Element(name = "power", required = false)
        var power: Double? = null
    )

    fun parse(inputStream: InputStream): ParseResult {
        return try {
            val serializer = Persister()
            val gpx = serializer.read(Gpx::class.java, inputStream)

            val track = gpx.track ?: return ParseResult.Error("No track found in GPX file")
            val segments = track.segments ?: emptyList()
            val allPoints = segments.flatMap { it.points ?: emptyList() }

            if (allPoints.isEmpty()) {
                return ParseResult.Error("No track points found in GPX file")
            }

            // Extract data
            val points = allPoints.filter { it.latitude != null && it.longitude != null }
            val startTime = points.firstOrNull()?.time?.let { parseTime(it) } ?: Instant.now()
            
            // Calculate distance and other metrics
            var totalDistance = 0.0
            var totalElevationGain = 0.0
            var previousLat: Double? = null
            var previousLng: Double? = null
            var previousElevation: Double? = null

            val streams = mutableListOf<ActivityStreamEntity>()
            var timeOffset = 0

            points.forEach { point ->
                point.latitude?.let { lat ->
                    point.longitude?.let { lng ->
                        previousLat?.let { prevLat ->
                            previousLng?.let { prevLng ->
                                val segmentDistance = calculateDistance(prevLat, prevLng, lat, lng)
                                totalDistance += segmentDistance
                            }
                        }

                        point.elevation?.let { elev ->
                            previousElevation?.let { prevElev ->
                                if (elev > prevElev) {
                                    totalElevationGain += (elev - prevElev)
                                }
                            }
                            previousElevation = elev
                        }

                        streams.add(
                            ActivityStreamEntity(
                                activityId = "", // Will be set after activity creation
                                timeOffsetSeconds = timeOffset++,
                                latitude = lat,
                                longitude = lng,
                                altitudeMeters = point.elevation,
                                heartRateBpm = point.extensions?.heartRate,
                                cadenceRpm = point.extensions?.cadence,
                                powerWatts = point.extensions?.power,
                                speedMps = null,
                                grade = null
                            )
                        )

                        previousLat = lat
                        previousLng = lng
                    }
                }
            }

            val activity = ActivityEntity(
                id = "gpx_${startTime.toEpochMilli()}",
                name = track.name ?: "GPX Activity",
                sportType = SportType.RUN, // Default, could be improved with metadata
                startDate = startTime,
                timezone = ZoneId.systemDefault().id,
                movingTimeSeconds = streams.size, // Approximate
                elapsedTimeSeconds = streams.size,
                distanceMeters = totalDistance,
                elevationGainMeters = totalElevationGain,
                elevationLossMeters = null,
                averageSpeedMps = if (streams.size > 0) totalDistance / streams.size else null,
                maxSpeedMps = null,
                averageHeartRateBpm = streams.mapNotNull { it.heartRateBpm }.average().takeIf { it.isFinite() }?.toInt(),
                maxHeartRateBpm = streams.mapNotNull { it.heartRateBpm }.maxOrNull(),
                averageCadenceRpm = streams.mapNotNull { it.cadenceRpm }.average().takeIf { it.isFinite() },
                averagePowerWatts = streams.mapNotNull { it.powerWatts }.average().takeIf { it.isFinite() },
                maxPowerWatts = streams.mapNotNull { it.powerWatts }.maxOrNull(),
                calories = null,
                description = null,
                source = "file_import",
                sourceId = null,
                createdAt = Instant.now(),
                updatedAt = Instant.now()
            )

            ParseResult.Success(activity, streams)
        } catch (e: Exception) {
            ParseResult.Error("Failed to parse GPX file: ${e.message}")
        }
    }

    private fun parseTime(timeString: String): Instant {
        return try {
            Instant.parse(timeString)
        } catch (e: Exception) {
            try {
                // Try ISO format
                java.time.ZonedDateTime.parse(timeString, DateTimeFormatter.ISO_DATE_TIME).toInstant()
            } catch (e2: Exception) {
                Instant.now()
            }
        }
    }

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

sealed class ParseResult {
    data class Success(
        val activity: ActivityEntity,
        val streams: List<ActivityStreamEntity>
    ) : ParseResult()

    data class Error(val message: String) : ParseResult()
}


