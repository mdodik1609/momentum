package com.momentum.fitness.data.model

data class ZoneTime(
    val zone: Int,
    val timeSeconds: Int,
    val percentage: Float
)

data class ZoneAnalysisResult(
    val zoneTimes: List<ZoneTime>,
    val totalTime: Int
)

/**
 * Analyzes time spent in zones for heart rate, pace, or power
 */
object ZoneAnalyzer {
    fun analyzeHeartRateZones(
        streams: List<com.momentum.fitness.data.database.entity.ActivityStreamEntity>,
        zones: HeartRateZones
    ): ZoneAnalysisResult {
        val zoneCounts = IntArray(5)
        var totalSamples = 0

        streams.forEach { stream ->
            stream.heartRateBpm?.let { hr ->
                when {
                    hr < zones.zone1Max -> zoneCounts[0]++
                    hr < zones.zone2Max -> zoneCounts[1]++
                    hr < zones.zone3Max -> zoneCounts[2]++
                    hr < zones.zone4Max -> zoneCounts[3]++
                    else -> zoneCounts[4]++
                }
                totalSamples++
            }
        }

        // Assuming 1 sample per second (adjust based on actual stream frequency)
        val totalTime = totalSamples
        val zoneTimes = zoneCounts.mapIndexed { index, count ->
            ZoneTime(
                zone = index + 1,
                timeSeconds = count,
                percentage = if (totalSamples > 0) (count.toFloat() / totalSamples) * 100f else 0f
            )
        }

        return ZoneAnalysisResult(zoneTimes, totalTime)
    }

    fun analyzePaceZones(
        streams: List<com.momentum.fitness.data.database.entity.ActivityStreamEntity>,
        ftpPaceSecondsPerKm: Int,
        distanceMeters: Double
    ): ZoneAnalysisResult {
        // Define pace zones based on FTP (Functional Threshold Pace)
        // Zone 1: > 120% of FTP
        // Zone 2: 100-120% of FTP
        // Zone 3: 90-100% of FTP
        // Zone 4: 80-90% of FTP
        // Zone 5: < 80% of FTP
        val zone1Threshold = ftpPaceSecondsPerKm * 1.2
        val zone2Threshold = ftpPaceSecondsPerKm * 1.0
        val zone3Threshold = ftpPaceSecondsPerKm * 0.9
        val zone4Threshold = ftpPaceSecondsPerKm * 0.8

        val zoneCounts = IntArray(5)
        var totalSamples = 0

        streams.forEach { stream ->
            stream.speedMps?.let { speed ->
                if (speed > 0) {
                    val paceSecondsPerKm = 1000.0 / speed
                    when {
                        paceSecondsPerKm > zone1Threshold -> zoneCounts[0]++
                        paceSecondsPerKm > zone2Threshold -> zoneCounts[1]++
                        paceSecondsPerKm > zone3Threshold -> zoneCounts[2]++
                        paceSecondsPerKm > zone4Threshold -> zoneCounts[3]++
                        else -> zoneCounts[4]++
                    }
                    totalSamples++
                }
            }
        }

        val totalTime = totalSamples
        val zoneTimes = zoneCounts.mapIndexed { index, count ->
            ZoneTime(
                zone = index + 1,
                timeSeconds = count,
                percentage = if (totalSamples > 0) (count.toFloat() / totalSamples) * 100f else 0f
            )
        }

        return ZoneAnalysisResult(zoneTimes, totalTime)
    }

    fun analyzePowerZones(
        streams: List<com.momentum.fitness.data.database.entity.ActivityStreamEntity>,
        ftpWatts: Int
    ): ZoneAnalysisResult {
        // Define power zones based on FTP
        // Zone 1: < 55% of FTP
        // Zone 2: 55-75% of FTP
        // Zone 3: 75-90% of FTP
        // Zone 4: 90-105% of FTP
        // Zone 5: > 105% of FTP
        val zone1Threshold = ftpWatts * 0.55
        val zone2Threshold = ftpWatts * 0.75
        val zone3Threshold = ftpWatts * 0.90
        val zone4Threshold = ftpWatts * 1.05

        val zoneCounts = IntArray(5)
        var totalSamples = 0

        streams.forEach { stream ->
            stream.powerWatts?.let { power ->
                when {
                    power < zone1Threshold -> zoneCounts[0]++
                    power < zone2Threshold -> zoneCounts[1]++
                    power < zone3Threshold -> zoneCounts[2]++
                    power < zone4Threshold -> zoneCounts[3]++
                    else -> zoneCounts[4]++
                }
                totalSamples++
            }
        }

        val totalTime = totalSamples
        val zoneTimes = zoneCounts.mapIndexed { index, count ->
            ZoneTime(
                zone = index + 1,
                timeSeconds = count,
                percentage = if (totalSamples > 0) (count.toFloat() / totalSamples) * 100f else 0f
            )
        }

        return ZoneAnalysisResult(zoneTimes, totalTime)
    }
}

data class HeartRateZones(
    val zone1Max: Int,
    val zone2Max: Int,
    val zone3Max: Int,
    val zone4Max: Int,
    val zone5Max: Int = 220
)







