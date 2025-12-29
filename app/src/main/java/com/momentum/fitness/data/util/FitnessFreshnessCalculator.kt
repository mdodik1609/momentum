package com.momentum.fitness.data.util

import com.momentum.fitness.data.database.entity.ActivityEntity
import java.time.Instant
import java.time.temporal.ChronoUnit

/**
 * Fitness & Freshness Calculator
 * Implements a basic model based on Training Stress Score (TSS) / TRIMP
 * 
 * Fitness (Chronic Load): Long-term training load (42-day exponential average)
 * Fatigue (Acute Load): Short-term training load (7-day exponential average)
 * Form (Balance): Fitness - Fatigue
 */
object FitnessFreshnessCalculator {
    private const val FITNESS_DECAY_DAYS = 42
    private const val FATIGUE_DECAY_DAYS = 7

    /**
     * Calculate fitness (chronic load) using exponential moving average
     */
    fun calculateFitness(
        activities: List<ActivityEntity>,
        currentDate: Instant = Instant.now()
    ): Double {
        var fitness = 0.0
        var totalWeight = 0.0

        activities.forEach { activity ->
            val daysAgo = ChronoUnit.DAYS.between(activity.startDate, currentDate).toDouble()
            if (daysAgo >= 0 && daysAgo <= FITNESS_DECAY_DAYS) {
                val stress = calculateActivityStress(activity)
                val weight = kotlin.math.exp(-daysAgo / FITNESS_DECAY_DAYS)
                fitness += stress * weight
                totalWeight += weight
            }
        }

        return if (totalWeight > 0) fitness / totalWeight else 0.0
    }

    /**
     * Calculate fatigue (acute load) using exponential moving average
     */
    fun calculateFatigue(
        activities: List<ActivityEntity>,
        currentDate: Instant = Instant.now()
    ): Double {
        var fatigue = 0.0
        var totalWeight = 0.0

        activities.forEach { activity ->
            val daysAgo = ChronoUnit.DAYS.between(activity.startDate, currentDate).toDouble()
            if (daysAgo >= 0 && daysAgo <= FATIGUE_DECAY_DAYS) {
                val stress = calculateActivityStress(activity)
                val weight = kotlin.math.exp(-daysAgo / FATIGUE_DECAY_DAYS)
                fatigue += stress * weight
                totalWeight += weight
            }
        }

        return if (totalWeight > 0) fatigue / totalWeight else 0.0
    }

    /**
     * Calculate form (fitness - fatigue)
     */
    fun calculateForm(
        activities: List<ActivityEntity>,
        currentDate: Instant = Instant.now()
    ): Double {
        val fitness = calculateFitness(activities, currentDate)
        val fatigue = calculateFatigue(activities, currentDate)
        return fitness - fatigue
    }

    /**
     * Calculate training stress for a single activity
     * Simplified version - uses duration and average HR as proxy
     * In a full implementation, this would use TRIMP or TSS
     */
    private fun calculateActivityStress(activity: ActivityEntity): Double {
        val durationHours = activity.movingTimeSeconds / 3600.0
        val hrFactor = activity.averageHeartRateBpm?.let { 
            // Normalize HR (0-1 scale, assuming max HR of 200)
            (it / 200.0).coerceIn(0.0, 1.0)
        } ?: 0.5
        
        // Stress = duration * intensity factor
        return durationHours * (1.0 + hrFactor)
    }

    /**
     * Get fitness, fatigue, and form over time (for graphing)
     */
    fun calculateOverTime(
        activities: List<ActivityEntity>,
        daysBack: Int = 90
    ): List<FitnessDataPoint> {
        val currentDate = Instant.now()
        val dataPoints = mutableListOf<FitnessDataPoint>()

        for (day in 0..daysBack) {
            val date = currentDate.minus(day.toLong(), ChronoUnit.DAYS)
            val activitiesUpToDate = activities.filter { it.startDate <= date }
            
            val fitness = calculateFitness(activitiesUpToDate, date)
            val fatigue = calculateFatigue(activitiesUpToDate, date)
            val form = fitness - fatigue

            dataPoints.add(
                FitnessDataPoint(
                    date = date,
                    fitness = fitness,
                    fatigue = fatigue,
                    form = form
                )
            )
        }

        return dataPoints.reversed() // Oldest first
    }
}

data class FitnessDataPoint(
    val date: Instant,
    val fitness: Double,
    val fatigue: Double,
    val form: Double
)







