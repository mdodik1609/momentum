package com.momentum.fitness.data.util

import com.momentum.fitness.data.database.entity.ActivityEntity
import com.momentum.fitness.data.database.entity.ActivityStreamEntity
import com.momentum.fitness.data.model.HeartRateZones
import com.momentum.fitness.data.model.SportType
import com.momentum.fitness.data.repository.ActivityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.Instant
import java.time.temporal.ChronoUnit
import kotlin.math.*

/**
 * Projected Times Calculator
 * Predicts finish times for various distances based on recent performance
 * Similar to Strava/Garmin pace predictions
 * 
 * Uses:
 * - Recent run performance (last 30-90 days)
 * - HR zone analysis (effort level)
 * - Distance-specific pace curves
 * - Fitness trend analysis
 */
object ProjectedTimesCalculator {
    
    // Standard race distances in meters
    const val DISTANCE_5K = 5000.0
    const val DISTANCE_10K = 10000.0
    const val DISTANCE_HALF_MARATHON = 21097.5
    const val DISTANCE_MARATHON = 42195.0
    
    /**
     * Calculate projected times for standard distances
     * @param activities Recent running activities (last 30-90 days)
     * @param hrZones User's heart rate zones
     * @param ftp Functional Threshold Pace (seconds per km)
     * @return Map of distance to projected time in seconds
     */
    suspend fun calculateProjectedTimes(
        activities: List<ActivityEntity>,
        hrZones: HeartRateZones,
        ftp: Int?,
        activityRepository: ActivityRepository
    ): Map<Double, ProjectedTime> = withContext(Dispatchers.Default) {
        val runningActivities = activities
            .filter { it.sportType == SportType.RUN || it.sportType == SportType.TRAIL_RUN }
            .filter { it.distanceMeters != null && it.distanceMeters!! > 0 }
            .sortedByDescending { it.startDate }
            .take(30) // Last 30 activities
        
        if (runningActivities.isEmpty()) {
            return@withContext emptyMap()
        }
        
        // Get streams for activities to analyze HR zones
        val activitiesWithStreams = runningActivities.mapNotNull { activity ->
            val streams = activityRepository.getStreamsForActivitySync(activity.id)
            if (streams.isNotEmpty()) {
                activity to streams
            } else null
        }
        
        // Calculate pace curves for different distances
        val paceCurve = calculatePaceCurve(activitiesWithStreams, hrZones, ftp)
        
        // Project times for standard distances
        val projections = mutableMapOf<Double, ProjectedTime>()
        
        listOf(DISTANCE_5K, DISTANCE_10K, DISTANCE_HALF_MARATHON, DISTANCE_MARATHON).forEach { distance ->
            val projectedTime = projectTimeForDistance(distance, paceCurve, activitiesWithStreams, hrZones)
            if (projectedTime != null) {
                projections[distance] = projectedTime
            }
        }
        
        return@withContext projections
    }
    
    /**
     * Calculate pace curve based on recent activities
     * Uses best efforts at different distances and HR zones
     */
    private fun calculatePaceCurve(
        activitiesWithStreams: List<Pair<ActivityEntity, List<ActivityStreamEntity>>>,
        hrZones: HeartRateZones,
        ftp: Int?
    ): PaceCurve {
        // Group activities by distance ranges
        val shortDistance = activitiesWithStreams.filter { 
            it.first.distanceMeters != null && it.first.distanceMeters!! < 8000 
        }
        val mediumDistance = activitiesWithStreams.filter { 
            it.first.distanceMeters != null && 
            it.first.distanceMeters!! >= 8000 && 
            it.first.distanceMeters!! < 25000 
        }
        val longDistance = activitiesWithStreams.filter { 
            it.first.distanceMeters != null && 
            it.first.distanceMeters!! >= 25000 
        }
        
        // Calculate best paces at different effort levels
        val bestPace5K = calculateBestPaceForDistance(shortDistance, 5000.0, hrZones)
        val bestPace10K = calculateBestPaceForDistance(mediumDistance, 10000.0, hrZones)
        val bestPaceHalf = calculateBestPaceForDistance(mediumDistance, 21097.5, hrZones)
        val bestPaceMarathon = calculateBestPaceForDistance(longDistance, 42195.0, hrZones)
        
        // Use FTP as baseline if available
        val ftpPace = ftp?.toDouble() ?: bestPace10K?.averagePace
        
        return PaceCurve(
            best5K = bestPace5K,
            best10K = bestPace10K,
            bestHalfMarathon = bestPaceHalf,
            bestMarathon = bestPaceMarathon,
            ftpPace = ftpPace
        )
    }
    
    /**
     * Calculate best pace for a specific distance from activities
     * Considers HR zone distribution and effort level
     */
    private fun calculateBestPaceForDistance(
        activities: List<Pair<ActivityEntity, List<ActivityStreamEntity>>>,
        targetDistance: Double,
        hrZones: HeartRateZones
    ): BestPace? {
        // Find activities close to target distance (±20%)
        val relevantActivities = activities.filter { activity ->
            val distance = activity.first.distanceMeters ?: return@filter false
            distance >= targetDistance * 0.8 && distance <= targetDistance * 1.2
        }
        
        if (relevantActivities.isEmpty()) return null
        
        // Calculate normalized paces (adjusted for HR zones and distance)
        val normalizedPaces = relevantActivities.mapNotNull { (activity, streams) ->
            val distance = activity.distanceMeters ?: return@mapNotNull null
            val timeSeconds = activity.movingTimeSeconds
            val basePace = (timeSeconds / distance) * 1000.0 // seconds per km
            
            // Analyze HR zone distribution
            val hrZoneDistribution = analyzeHRZoneDistribution(streams, hrZones)
            
            // Adjust pace based on effort level
            // Higher HR zones = higher effort = better performance potential
            val effortMultiplier = calculateEffortMultiplier(hrZoneDistribution)
            
            // Normalize to target distance using power law (similar to Riegel formula)
            val distanceAdjustment = (distance / targetDistance).pow(0.07) // Typical running exponent
            
            val normalizedPace = basePace / (effortMultiplier * distanceAdjustment)
            
            NormalizedPace(
                pace = normalizedPace,
                originalDistance = distance,
                originalTime = timeSeconds,
                effortLevel = effortMultiplier,
                hrZoneDistribution = hrZoneDistribution
            )
        }
        
        if (normalizedPaces.isEmpty()) return null
        
        // Use median pace (more robust than average)
        val sortedPaces = normalizedPaces.sortedBy { it.pace }
        val medianPace = sortedPaces[sortedPaces.size / 2].pace
        
        // Calculate average effort level
        val avgEffort = normalizedPaces.map { it.effortLevel }.average()
        
        return BestPace(
            averagePace = medianPace,
            bestPace = sortedPaces.first().pace,
            effortLevel = avgEffort,
            sampleSize = normalizedPaces.size
        )
    }
    
    /**
     * Analyze HR zone distribution in activity streams
     */
    private fun analyzeHRZoneDistribution(
        streams: List<ActivityStreamEntity>,
        hrZones: HeartRateZones
    ): HRZoneDistribution {
        var zone1 = 0
        var zone2 = 0
        var zone3 = 0
        var zone4 = 0
        var zone5 = 0
        var total = 0
        
        streams.forEach { stream ->
            stream.heartRateBpm?.let { hr ->
                when {
                    hr < hrZones.zone1Max -> zone1++
                    hr < hrZones.zone2Max -> zone2++
                    hr < hrZones.zone3Max -> zone3++
                    hr < hrZones.zone4Max -> zone4++
                    else -> zone5++
                }
                total++
            }
        }
        
        return HRZoneDistribution(
            zone1Percent = if (total > 0) zone1.toDouble() / total * 100 else 0.0,
            zone2Percent = if (total > 0) zone2.toDouble() / total * 100 else 0.0,
            zone3Percent = if (total > 0) zone3.toDouble() / total * 100 else 0.0,
            zone4Percent = if (total > 0) zone4.toDouble() / total * 100 else 0.0,
            zone5Percent = if (total > 0) zone5.toDouble() / total * 100 else 0.0
        )
    }
    
    /**
     * Calculate effort multiplier based on HR zone distribution
     * Higher zones = higher effort = better performance potential
     */
    private fun calculateEffortMultiplier(distribution: HRZoneDistribution): Double {
        // Weighted average of zone multipliers
        val zone1Weight = distribution.zone1Percent * 0.2
        val zone2Weight = distribution.zone2Percent * 0.5
        val zone3Weight = distribution.zone3Percent * 1.0
        val zone4Weight = distribution.zone4Percent * 1.5
        val zone5Weight = distribution.zone5Percent * 2.0
        
        val totalWeight = zone1Weight + zone2Weight + zone3Weight + zone4Weight + zone5Weight
        val avgMultiplier = totalWeight / 100.0
        
        // Normalize to 0.5-1.5 range (conservative to aggressive effort)
        return (avgMultiplier / 1.0).coerceIn(0.5, 1.5)
    }
    
    /**
     * Project time for a specific distance using pace curve
     */
    private fun projectTimeForDistance(
        distance: Double,
        paceCurve: PaceCurve,
        activitiesWithStreams: List<Pair<ActivityEntity, List<ActivityStreamEntity>>>,
        hrZones: HeartRateZones
    ): ProjectedTime? {
        val ftpPace = paceCurve.ftpPace ?: return null
        
        // Use Riegel formula as base: T2 = T1 * (D2/D1)^1.06
        // But adjust based on available data points
        
        val projections = mutableListOf<Double>()
        
        // Project from 5K if available
        paceCurve.best5K?.let { best5K ->
            val projectedPace = best5K.averagePace * (distance / 5000.0).pow(0.07)
            projections.add(projectedPace * distance / 1000.0)
        }
        
        // Project from 10K if available
        paceCurve.best10K?.let { best10K ->
            val projectedPace = best10K.averagePace * (distance / 10000.0).pow(0.07)
            projections.add(projectedPace * distance / 1000.0)
        }
        
        // Project from half marathon if available
        paceCurve.bestHalfMarathon?.let { bestHalf ->
            val projectedPace = bestHalf.averagePace * (distance / 21097.5).pow(0.07)
            projections.add(projectedPace * distance / 1000.0)
        }
        
        // Project from marathon if available
        paceCurve.bestMarathon?.let { bestMarathon ->
            val projectedPace = bestMarathon.averagePace * (distance / 42195.0).pow(0.07)
            projections.add(projectedPace * distance / 1000.0)
        }
        
        // Use FTP-based projection as fallback
        val ftpProjectedPace = ftpPace * (distance / 10000.0).pow(0.07)
        val ftpProjectedTime = ftpProjectedPace * distance / 1000.0
        projections.add(ftpProjectedTime)
        
        if (projections.isEmpty()) return null
        
        // Use median projection
        val sortedProjections = projections.sorted()
        val medianTime = sortedProjections[sortedProjections.size / 2]
        
        // Calculate confidence based on sample size and distance match
        val confidence = calculateConfidence(paceCurve, distance)
        
        return ProjectedTime(
            timeSeconds = medianTime.toInt(),
            confidence = confidence,
            basedOn = paceCurve.getBestMatch(distance)
        )
    }
    
    /**
     * Calculate confidence level for projection
     */
    private fun calculateConfidence(paceCurve: PaceCurve, distance: Double): Confidence {
        val hasCloseMatch = when {
            distance < 8000 -> paceCurve.best5K != null
            distance < 15000 -> paceCurve.best10K != null || paceCurve.best5K != null
            distance < 30000 -> paceCurve.bestHalfMarathon != null || paceCurve.best10K != null
            else -> paceCurve.bestMarathon != null || paceCurve.bestHalfMarathon != null
        }
        
        val sampleSize = listOfNotNull(
            paceCurve.best5K?.sampleSize,
            paceCurve.best10K?.sampleSize,
            paceCurve.bestHalfMarathon?.sampleSize,
            paceCurve.bestMarathon?.sampleSize
        ).sum()
        
        return when {
            hasCloseMatch && sampleSize >= 5 -> Confidence.HIGH
            hasCloseMatch && sampleSize >= 2 -> Confidence.MEDIUM
            sampleSize >= 1 -> Confidence.LOW
            else -> Confidence.VERY_LOW
        }
    }
    
    /**
     * Get recent activities for projection calculation
     */
    suspend fun getRecentActivities(
        activityRepository: ActivityRepository,
        daysBack: Int = 90
    ): List<ActivityEntity> = withContext(Dispatchers.IO) {
        val cutoffDate = Instant.now().minus(daysBack.toLong(), ChronoUnit.DAYS)
        val allActivities = activityRepository.getAllActivities()
        
        // Collect activities from Flow
        val activities = mutableListOf<ActivityEntity>()
        allActivities.collect { list ->
            activities.addAll(list.filter { it.startDate >= cutoffDate })
        }
        
        return@withContext activities
    }
}

// Data classes
data class ProjectedTime(
    val timeSeconds: Int,
    val confidence: Confidence,
    val basedOn: String
)

enum class Confidence {
    VERY_LOW,
    LOW,
    MEDIUM,
    HIGH
}

data class PaceCurve(
    val best5K: BestPace?,
    val best10K: BestPace?,
    val bestHalfMarathon: BestPace?,
    val bestMarathon: BestPace?,
    val ftpPace: Double?
) {
    fun getBestMatch(distance: Double): String {
        return when {
            distance < 8000 -> "5K efforts"
            distance < 15000 -> "10K efforts"
            distance < 30000 -> "Half Marathon efforts"
            else -> "Marathon efforts"
        }
    }
}

data class BestPace(
    val averagePace: Double, // seconds per km
    val bestPace: Double,
    val effortLevel: Double,
    val sampleSize: Int
)

data class NormalizedPace(
    val pace: Double,
    val originalDistance: Double,
    val originalTime: Int,
    val effortLevel: Double,
    val hrZoneDistribution: HRZoneDistribution
)

data class HRZoneDistribution(
    val zone1Percent: Double,
    val zone2Percent: Double,
    val zone3Percent: Double,
    val zone4Percent: Double,
    val zone5Percent: Double
)


