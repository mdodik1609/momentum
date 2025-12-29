package com.momentum.fitness.data.util

import com.momentum.fitness.data.database.entity.ActivityStreamEntity

/**
 * Grade Adjusted Pace (GAP) Calculator
 * GAP adjusts pace based on elevation gain/loss to provide a normalized pace
 * Useful for comparing efforts on different terrain
 */
object GapCalculator {
    /**
     * Calculate GAP for a running activity
     * Formula based on common running science: ~6-8 seconds per meter of elevation gain
     */
    fun calculateGap(
        streams: List<ActivityStreamEntity>,
        distanceMeters: Double
    ): Double {
        if (streams.isEmpty() || distanceMeters <= 0) return 0.0

        var totalElevationGain = 0.0
        var previousAltitude: Double? = null

        streams.forEach { stream ->
            stream.altitudeMeters?.let { altitude ->
                previousAltitude?.let { prev ->
                    val elevationChange = altitude - prev
                    if (elevationChange > 0) {
                        totalElevationGain += elevationChange
                    }
                }
                previousAltitude = altitude
            }
        }

        // Calculate average pace in seconds per km
        val totalTimeSeconds = streams.size.toDouble() // Assuming 1 sample per second
        val averagePaceSecondsPerKm = (totalTimeSeconds / distanceMeters) * 1000.0

        // Adjust for elevation: ~7 seconds per meter of elevation gain
        val elevationAdjustment = (totalElevationGain * 7.0) / distanceMeters * 1000.0
        val gapSecondsPerKm = averagePaceSecondsPerKm - elevationAdjustment

        return gapSecondsPerKm.coerceAtLeast(0.0)
    }

    /**
     * Calculate GAP for a specific segment of the activity
     */
    fun calculateGapForSegment(
        streams: List<ActivityStreamEntity>,
        startIndex: Int,
        endIndex: Int,
        distanceMeters: Double
    ): Double {
        val segmentStreams = streams.subList(
            startIndex.coerceAtLeast(0),
            endIndex.coerceAtMost(streams.size)
        )
        val segmentDistance = distanceMeters * (segmentStreams.size.toDouble() / streams.size)
        return calculateGap(segmentStreams, segmentDistance)
    }
}







