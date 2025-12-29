package com.momentum.fitness.data.util

import com.momentum.fitness.data.database.entity.ActivityStreamEntity
import com.momentum.fitness.data.model.HeartRateZones

/**
 * Relative Effort Calculator
 * Implements a Strava-like algorithm based on time spent in HR zones
 * Higher zones contribute more to the effort score
 * 
 * Strava's algorithm:
 * - Uses exponential scaling for HR zones
 * - Considers duration and intensity
 * - Normalizes based on user's max HR
 */
object RelativeEffortCalculator {
    /**
     * Calculate relative effort based on heart rate zones (Strava-like)
     * Uses exponential scaling for more accurate effort representation
     * 
     * Zone multipliers (based on Strava's algorithm):
     * Zone 1 (50-60% max HR): 0.1x
     * Zone 2 (60-70% max HR): 0.3x
     * Zone 3 (70-80% max HR): 0.6x
     * Zone 4 (80-90% max HR): 1.0x
     * Zone 5 (90-100% max HR): 1.5x
     * 
     * Formula: effort = Σ(time_in_zone * zone_multiplier * exponential_factor)
     */
    fun calculateRelativeEffort(
        streams: List<ActivityStreamEntity>,
        zones: HeartRateZones
    ): Int {
        if (streams.isEmpty()) return 0
        
        // Estimate max HR from zone 5 max (typically ~95% of max HR)
        val estimatedMaxHR = (zones.zone5Max / 0.95).toInt().coerceAtMost(220)
        val restingHR = 60 // Default resting HR, could be made configurable
        
        var totalEffort = 0.0
        var totalTime = 0

        streams.forEach { stream ->
            stream.heartRateBpm?.let { hr ->
                totalTime++
                
                // Calculate HR reserve percentage
                val hrReserve = ((hr - restingHR).toDouble() / (estimatedMaxHR - restingHR)).coerceIn(0.0, 1.0)
                
                // Determine zone and base multiplier
                val (baseMultiplier, zoneIntensity) = when {
                    hr < zones.zone1Max -> 0.1 to 0.55 // ~50-60% max HR
                    hr < zones.zone2Max -> 0.3 to 0.65 // ~60-70% max HR
                    hr < zones.zone3Max -> 0.6 to 0.75 // ~70-80% max HR
                    hr < zones.zone4Max -> 1.0 to 0.85 // ~80-90% max HR
                    else -> 1.5 to 0.95 // ~90-100% max HR
                }
                
                // Apply exponential scaling (similar to TRIMP but simpler)
                // Higher HR = exponentially more effort
                val exponentialFactor = kotlin.math.exp(2.0 * hrReserve) / kotlin.math.exp(2.0)
                
                // Calculate effort for this second
                val secondEffort = baseMultiplier * exponentialFactor
                totalEffort += secondEffort
            }
        }
        
        // Normalize by duration (effort per minute)
        val effortPerMinute = if (totalTime > 0) {
            (totalEffort / totalTime) * 60.0
        } else {
            0.0
        }
        
        // Scale to Strava-like range (typically 0-500+ for most activities)
        // A 1-hour zone 4 effort ≈ 60 effort points
        val scaledEffort = effortPerMinute * (totalTime / 60.0)
        
        return scaledEffort.toInt()
    }
    
    /**
     * Calculate relative effort with power data (for cycling)
     * More accurate when power data is available
     */
    fun calculateRelativeEffortWithPower(
        streams: List<ActivityStreamEntity>,
        zones: HeartRateZones,
        ftp: Int? // Functional Threshold Power
    ): Int {
        if (streams.isEmpty()) return 0
        if (ftp == null) return calculateRelativeEffort(streams, zones)
        
        var totalEffort = 0.0
        var totalTime = 0
        
        streams.forEach { stream ->
            stream.powerWatts?.let { power ->
                totalTime++
                
                // Calculate intensity as % of FTP
                val intensity = (power / ftp).coerceIn(0.0, 2.0) // Cap at 200% FTP
                
                // Power-based effort (similar to TSS - Training Stress Score)
                // TSS = (seconds * normalized_power^2) / (FTP^2 * 3600) * 100
                // Simplified: effort = intensity^2 * time_factor
                val powerEffort = intensity * intensity
                
                // Also consider HR if available (weighted average)
                stream.heartRateBpm?.let { hr ->
                    val hrReserve = ((hr - 60).toDouble() / (zones.zone5Max - 60).toDouble()).coerceIn(0.0, 1.0)
                    val hrEffort = hrReserve * 1.5
                    
                    // Weighted: 70% power, 30% HR
                    totalEffort += (powerEffort * 0.7 + hrEffort * 0.3)
                } ?: run {
                    totalEffort += powerEffort
                }
            } ?: run {
                // Fallback to HR-only if no power
                stream.heartRateBpm?.let { hr ->
                    totalTime++
                    val hrReserve = ((hr - 60).toDouble() / (zones.zone5Max - 60).toDouble()).coerceIn(0.0, 1.0)
                    totalEffort += hrReserve * 1.0
                }
            }
        }
        
        // Scale similar to HR-based calculation
        val scaledEffort = (totalEffort / totalTime.coerceAtLeast(1)) * totalTime * 0.6
        
        return scaledEffort.toInt()
    }

    /**
     * Calculate TRIMP (Training Impulse) - an alternative effort metric
     * Based on Banister's TRIMP formula
     */
    fun calculateTRIMP(
        streams: List<ActivityStreamEntity>,
        maxHeartRate: Int,
        restingHeartRate: Int
    ): Double {
        var trimp = 0.0

        streams.forEach { stream ->
            stream.heartRateBpm?.let { hr ->
                val hrReserve = (hr - restingHeartRate).toDouble() / (maxHeartRate - restingHeartRate)
                val trimpValue = hrReserve * 0.64 * kotlin.math.exp(1.92 * hrReserve)
                trimp += trimpValue
            }
        }

        return trimp
    }
}

