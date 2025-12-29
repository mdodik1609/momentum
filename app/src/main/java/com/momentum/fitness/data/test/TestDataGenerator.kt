package com.momentum.fitness.data.test

import com.momentum.fitness.data.database.entity.ActivityEntity
import com.momentum.fitness.data.database.entity.ActivityStreamEntity
import com.momentum.fitness.data.model.SportType
import kotlin.math.*
import java.time.Instant
import java.time.ZoneId
import java.util.UUID

/**
 * Test Data Generator
 * Generates realistic test activities for development and testing
 * Creates activities with proper GPS tracks, HR zones, pace/power variations
 */
object TestDataGenerator {
    
    /**
     * Generate a set of test activities
     * @return List of ActivityEntity with streams
     */
    fun generateTestActivities(): List<Pair<ActivityEntity, List<ActivityStreamEntity>>> {
        val activities = mutableListOf<Pair<ActivityEntity, List<ActivityStreamEntity>>>()
        val baseDate = Instant.now().minusSeconds(90 * 24 * 3600) // 90 days ago
        
        // Running activities (10)
        activities.add(generateEasyRun(baseDate.plusSeconds(0 * 24 * 3600)))
        activities.add(generateTempoRun(baseDate.plusSeconds(2 * 24 * 3600)))
        activities.add(generateIntervalRun(baseDate.plusSeconds(4 * 24 * 3600)))
        activities.add(generateLongRun(baseDate.plusSeconds(7 * 24 * 3600)))
        activities.add(generateEasyRun(baseDate.plusSeconds(9 * 24 * 3600)))
        activities.add(generateSprintWorkout(baseDate.plusSeconds(11 * 24 * 3600)))
        activities.add(generateTrailRun(baseDate.plusSeconds(14 * 24 * 3600)))
        activities.add(generateEasyRun(baseDate.plusSeconds(16 * 24 * 3600)))
        activities.add(generateTempoRun(baseDate.plusSeconds(18 * 24 * 3600)))
        activities.add(generateLongRun(baseDate.plusSeconds(21 * 24 * 3600)))
        
        // Cycling activities (7)
        activities.add(generateEasyRide(baseDate.plusSeconds(1 * 24 * 3600)))
        activities.add(generateTempoRide(baseDate.plusSeconds(3 * 24 * 3600)))
        activities.add(generateIntervalRide(baseDate.plusSeconds(5 * 24 * 3600)))
        activities.add(generateLongRide(baseDate.plusSeconds(8 * 24 * 3600)))
        activities.add(generateEasyRide(baseDate.plusSeconds(10 * 24 * 3600)))
        activities.add(generateClimbRide(baseDate.plusSeconds(13 * 24 * 3600)))
        activities.add(generateTempoRide(baseDate.plusSeconds(15 * 24 * 3600)))
        
        // Trail Running activities (3)
        activities.add(generateTrailRun(baseDate.plusSeconds(6 * 24 * 3600)))
        activities.add(generateTrailRun(baseDate.plusSeconds(12 * 24 * 3600)))
        activities.add(generateTrailRun(baseDate.plusSeconds(20 * 24 * 3600)))
        
        return activities
    }
    
    // ========== RUNNING ACTIVITIES ==========
    
    private fun generateEasyRun(startDate: Instant): Pair<ActivityEntity, List<ActivityStreamEntity>> {
        val distance = 5000.0 + (Math.random() * 3000) // 5-8 km
        val duration = (distance / 5.5 * 3600).toInt() // ~5:30/km pace
        val activityId = UUID.randomUUID().toString()
        
        val activity = ActivityEntity(
            id = activityId,
            name = "Easy Run",
            sportType = SportType.RUN,
            startDate = startDate,
            timezone = "UTC",
            movingTimeSeconds = duration,
            elapsedTimeSeconds = duration + 60,
            distanceMeters = distance,
            elevationGainMeters = 50.0 + Math.random() * 100,
            elevationLossMeters = 30.0 + Math.random() * 80,
            averageSpeedMps = distance / duration,
            maxSpeedMps = (distance / duration) * 1.3,
            averageHeartRateBpm = 140,
            maxHeartRateBpm = 155,
            averageCadenceRpm = 170.0,
            averagePowerWatts = null,
            maxPowerWatts = null,
            calories = (distance / 1000 * 60).toInt(),
            description = "Easy recovery run",
            source = "test_data",
            sourceId = activityId,
            createdAt = startDate,
            updatedAt = startDate
        )
        
        val streams = generateRunningStreams(
            activityId = activityId,
            duration = duration,
            distance = distance,
            avgPace = 5.5,
            hrBase = 140,
            hrVariation = 10,
            elevationGain = activity.elevationGainMeters ?: 0.0
        )
        
        return activity to streams
    }
    
    private fun generateTempoRun(startDate: Instant): Pair<ActivityEntity, List<ActivityStreamEntity>> {
        val distance = 8000.0 + (Math.random() * 2000) // 8-10 km
        val duration = (distance / 4.2 * 3600).toInt() // ~4:12/km pace (tempo)
        val activityId = UUID.randomUUID().toString()
        
        val activity = ActivityEntity(
            id = activityId,
            name = "Tempo Run",
            sportType = SportType.RUN,
            startDate = startDate,
            timezone = "UTC",
            movingTimeSeconds = duration,
            elapsedTimeSeconds = duration + 120,
            distanceMeters = distance,
            elevationGainMeters = 80.0 + Math.random() * 120,
            elevationLossMeters = 60.0 + Math.random() * 100,
            averageSpeedMps = distance / duration,
            maxSpeedMps = (distance / duration) * 1.4,
            averageHeartRateBpm = 165,
            maxHeartRateBpm = 180,
            averageCadenceRpm = 180.0,
            averagePowerWatts = null,
            maxPowerWatts = null,
            calories = (distance / 1000 * 70).toInt(),
            description = "Tempo run at threshold pace",
            source = "test_data",
            sourceId = activityId,
            createdAt = startDate,
            updatedAt = startDate
        )
        
        val streams = generateRunningStreams(
            activityId = activityId,
            duration = duration,
            distance = distance,
            avgPace = 4.2,
            hrBase = 165,
            hrVariation = 8,
            elevationGain = activity.elevationGainMeters ?: 0.0
        )
        
        return activity to streams
    }
    
    private fun generateIntervalRun(startDate: Instant): Pair<ActivityEntity, List<ActivityStreamEntity>> {
        val distance = 6000.0 + (Math.random() * 2000) // 6-8 km
        val duration = (distance / 4.8 * 3600).toInt() // Mixed pace
        val activityId = UUID.randomUUID().toString()
        
        val activity = ActivityEntity(
            id = activityId,
            name = "Interval Run",
            sportType = SportType.RUN,
            startDate = startDate,
            timezone = "UTC",
            movingTimeSeconds = duration,
            elapsedTimeSeconds = duration + 300, // 5 min rest
            distanceMeters = distance,
            elevationGainMeters = 60.0 + Math.random() * 80,
            elevationLossMeters = 40.0 + Math.random() * 60,
            averageSpeedMps = distance / duration,
            maxSpeedMps = (distance / duration) * 1.8, // Fast intervals
            averageHeartRateBpm = 170,
            maxHeartRateBpm = 195,
            averageCadenceRpm = 185.0,
            averagePowerWatts = null,
            maxPowerWatts = null,
            calories = (distance / 1000 * 75).toInt(),
            description = "5x1km intervals with recovery",
            source = "test_data",
            sourceId = activityId,
            createdAt = startDate,
            updatedAt = startDate
        )
        
        val streams = generateIntervalStreams(
            activityId = activityId,
            duration = duration,
            distance = distance,
            intervalPace = 3.8,
            recoveryPace = 6.0,
            hrBase = 170,
            elevationGain = activity.elevationGainMeters ?: 0.0
        )
        
        return activity to streams
    }
    
    private fun generateLongRun(startDate: Instant): Pair<ActivityEntity, List<ActivityStreamEntity>> {
        val distance = 18000.0 + (Math.random() * 4000) // 18-22 km
        val duration = (distance / 5.8 * 3600).toInt() // ~5:48/km pace
        val activityId = UUID.randomUUID().toString()
        
        val activity = ActivityEntity(
            id = activityId,
            name = "Long Run",
            sportType = SportType.RUN,
            startDate = startDate,
            timezone = "UTC",
            movingTimeSeconds = duration,
            elapsedTimeSeconds = duration + 180,
            distanceMeters = distance,
            elevationGainMeters = 150.0 + Math.random() * 200,
            elevationLossMeters = 120.0 + Math.random() * 180,
            averageSpeedMps = distance / duration,
            maxSpeedMps = (distance / duration) * 1.2,
            averageHeartRateBpm = 145,
            maxHeartRateBpm = 165,
            averageCadenceRpm = 168.0,
            averagePowerWatts = null,
            maxPowerWatts = null,
            calories = (distance / 1000 * 65).toInt(),
            description = "Long easy run",
            source = "test_data",
            sourceId = activityId,
            createdAt = startDate,
            updatedAt = startDate
        )
        
        val streams = generateRunningStreams(
            activityId = activityId,
            duration = duration,
            distance = distance,
            avgPace = 5.8,
            hrBase = 145,
            hrVariation = 12,
            elevationGain = activity.elevationGainMeters ?: 0.0
        )
        
        return activity to streams
    }
    
    private fun generateSprintWorkout(startDate: Instant): Pair<ActivityEntity, List<ActivityStreamEntity>> {
        val distance = 3000.0 + (Math.random() * 2000) // 3-5 km
        val duration = (distance / 5.0 * 3600).toInt()
        val activityId = UUID.randomUUID().toString()
        
        val activity = ActivityEntity(
            id = activityId,
            name = "Sprint Workout",
            sportType = SportType.RUN,
            startDate = startDate,
            timezone = "UTC",
            movingTimeSeconds = duration,
            elapsedTimeSeconds = duration + 600, // 10 min rest
            distanceMeters = distance,
            elevationGainMeters = 20.0 + Math.random() * 40,
            elevationLossMeters = 15.0 + Math.random() * 30,
            averageSpeedMps = distance / duration,
            maxSpeedMps = (distance / duration) * 2.5, // Very fast sprints
            averageHeartRateBpm = 175,
            maxHeartRateBpm = 200,
            averageCadenceRpm = 195.0,
            averagePowerWatts = null,
            maxPowerWatts = null,
            calories = (distance / 1000 * 80).toInt(),
            description = "8x400m sprints",
            source = "test_data",
            sourceId = activityId,
            createdAt = startDate,
            updatedAt = startDate
        )
        
        val streams = generateSprintStreams(
            activityId = activityId,
            duration = duration,
            distance = distance,
            sprintPace = 3.2,
            recoveryPace = 7.0,
            hrBase = 175
        )
        
        return activity to streams
    }
    
    // ========== CYCLING ACTIVITIES ==========
    
    private fun generateEasyRide(startDate: Instant): Pair<ActivityEntity, List<ActivityStreamEntity>> {
        val distance = 30000.0 + (Math.random() * 10000) // 30-40 km
        val duration = (distance / 25.0 * 3600).toInt() // ~25 km/h
        val activityId = UUID.randomUUID().toString()
        
        val activity = ActivityEntity(
            id = activityId,
            name = "Easy Ride",
            sportType = SportType.RIDE,
            startDate = startDate,
            timezone = "UTC",
            movingTimeSeconds = duration,
            elapsedTimeSeconds = duration + 300,
            distanceMeters = distance,
            elevationGainMeters = 200.0 + Math.random() * 300,
            elevationLossMeters = 180.0 + Math.random() * 280,
            averageSpeedMps = distance / duration,
            maxSpeedMps = (distance / duration) * 1.5,
            averageHeartRateBpm = 125,
            maxHeartRateBpm = 145,
            averageCadenceRpm = 85.0,
            averagePowerWatts = 150.0,
            maxPowerWatts = 350.0,
            calories = (distance / 1000 * 25).toInt(),
            description = "Easy recovery ride",
            source = "test_data",
            sourceId = activityId,
            createdAt = startDate,
            updatedAt = startDate
        )
        
        val streams = generateCyclingStreams(
            activityId = activityId,
            duration = duration,
            distance = distance,
            avgSpeed = 25.0,
            avgPower = 150.0,
            hrBase = 125,
            hrVariation = 8,
            elevationGain = activity.elevationGainMeters ?: 0.0
        )
        
        return activity to streams
    }
    
    private fun generateTempoRide(startDate: Instant): Pair<ActivityEntity, List<ActivityStreamEntity>> {
        val distance = 40000.0 + (Math.random() * 10000) // 40-50 km
        val duration = (distance / 30.0 * 3600).toInt() // ~30 km/h
        val activityId = UUID.randomUUID().toString()
        
        val activity = ActivityEntity(
            id = activityId,
            name = "Tempo Ride",
            sportType = SportType.RIDE,
            startDate = startDate,
            timezone = "UTC",
            movingTimeSeconds = duration,
            elapsedTimeSeconds = duration + 180,
            distanceMeters = distance,
            elevationGainMeters = 300.0 + Math.random() * 400,
            elevationLossMeters = 280.0 + Math.random() * 380,
            averageSpeedMps = distance / duration,
            maxSpeedMps = (distance / duration) * 1.6,
            averageHeartRateBpm = 155,
            maxHeartRateBpm = 175,
            averageCadenceRpm = 90.0,
            averagePowerWatts = 220.0,
            maxPowerWatts = 450.0,
            calories = (distance / 1000 * 35).toInt(),
            description = "Tempo ride at threshold",
            source = "test_data",
            sourceId = activityId,
            createdAt = startDate,
            updatedAt = startDate
        )
        
        val streams = generateCyclingStreams(
            activityId = activityId,
            duration = duration,
            distance = distance,
            avgSpeed = 30.0,
            avgPower = 220.0,
            hrBase = 155,
            hrVariation = 10,
            elevationGain = activity.elevationGainMeters ?: 0.0
        )
        
        return activity to streams
    }
    
    private fun generateIntervalRide(startDate: Instant): Pair<ActivityEntity, List<ActivityStreamEntity>> {
        val distance = 25000.0 + (Math.random() * 5000) // 25-30 km
        val duration = (distance / 28.0 * 3600).toInt()
        val activityId = UUID.randomUUID().toString()
        
        val activity = ActivityEntity(
            id = activityId,
            name = "Interval Ride",
            sportType = SportType.RIDE,
            startDate = startDate,
            timezone = "UTC",
            movingTimeSeconds = duration,
            elapsedTimeSeconds = duration + 600,
            distanceMeters = distance,
            elevationGainMeters = 150.0 + Math.random() * 200,
            elevationLossMeters = 120.0 + Math.random() * 180,
            averageSpeedMps = distance / duration,
            maxSpeedMps = (distance / duration) * 2.0,
            averageHeartRateBpm = 165,
            maxHeartRateBpm = 190,
            averageCadenceRpm = 95.0,
            averagePowerWatts = 250.0,
            maxPowerWatts = 600.0,
            calories = (distance / 1000 * 40).toInt(),
            description = "5x5min intervals",
            source = "test_data",
            sourceId = activityId,
            createdAt = startDate,
            updatedAt = startDate
        )
        
        val streams = generateIntervalCyclingStreams(
            activityId = activityId,
            duration = duration,
            distance = distance,
            intervalPower = 350.0,
            recoveryPower = 150.0,
            hrBase = 165,
            elevationGain = activity.elevationGainMeters ?: 0.0
        )
        
        return activity to streams
    }
    
    private fun generateLongRide(startDate: Instant): Pair<ActivityEntity, List<ActivityStreamEntity>> {
        val distance = 80000.0 + (Math.random() * 20000) // 80-100 km
        val duration = (distance / 26.0 * 3600).toInt() // ~26 km/h
        val activityId = UUID.randomUUID().toString()
        
        val activity = ActivityEntity(
            id = activityId,
            name = "Long Ride",
            sportType = SportType.RIDE,
            startDate = startDate,
            timezone = "UTC",
            movingTimeSeconds = duration,
            elapsedTimeSeconds = duration + 1800, // 30 min break
            distanceMeters = distance,
            elevationGainMeters = 800.0 + Math.random() * 1200,
            elevationLossMeters = 750.0 + Math.random() * 1100,
            averageSpeedMps = distance / duration,
            maxSpeedMps = (distance / duration) * 1.8,
            averageHeartRateBpm = 135,
            maxHeartRateBpm = 165,
            averageCadenceRpm = 82.0,
            averagePowerWatts = 180.0,
            maxPowerWatts = 500.0,
            calories = (distance / 1000 * 30).toInt(),
            description = "Long endurance ride",
            source = "test_data",
            sourceId = activityId,
            createdAt = startDate,
            updatedAt = startDate
        )
        
        val streams = generateCyclingStreams(
            activityId = activityId,
            duration = duration,
            distance = distance,
            avgSpeed = 26.0,
            avgPower = 180.0,
            hrBase = 135,
            hrVariation = 15,
            elevationGain = activity.elevationGainMeters ?: 0.0
        )
        
        return activity to streams
    }
    
    private fun generateClimbRide(startDate: Instant): Pair<ActivityEntity, List<ActivityStreamEntity>> {
        val distance = 20000.0 + (Math.random() * 5000) // 20-25 km
        val duration = (distance / 22.0 * 3600).toInt() // Slower due to climbs
        val activityId = UUID.randomUUID().toString()
        
        val activity = ActivityEntity(
            id = activityId,
            name = "Climb Ride",
            sportType = SportType.RIDE,
            startDate = startDate,
            timezone = "UTC",
            movingTimeSeconds = duration,
            elapsedTimeSeconds = duration + 300,
            distanceMeters = distance,
            elevationGainMeters = 600.0 + Math.random() * 400, // Significant elevation
            elevationLossMeters = 50.0 + Math.random() * 100,
            averageSpeedMps = distance / duration,
            maxSpeedMps = (distance / duration) * 1.4,
            averageHeartRateBpm = 170,
            maxHeartRateBpm = 195,
            averageCadenceRpm = 70.0, // Lower cadence on climbs
            averagePowerWatts = 280.0,
            maxPowerWatts = 550.0,
            calories = (distance / 1000 * 50).toInt(),
            description = "Mountain climb",
            source = "test_data",
            sourceId = activityId,
            createdAt = startDate,
            updatedAt = startDate
        )
        
        val streams = generateClimbingStreams(
            activityId = activityId,
            duration = duration,
            distance = distance,
            avgPower = 280.0,
            hrBase = 170,
            elevationGain = activity.elevationGainMeters ?: 0.0
        )
        
        return activity to streams
    }
    
    // ========== TRAIL RUNNING ==========
    
    private fun generateTrailRun(startDate: Instant): Pair<ActivityEntity, List<ActivityStreamEntity>> {
        val distance = 10000.0 + (Math.random() * 5000) // 10-15 km
        val duration = (distance / 6.5 * 3600).toInt() // Slower pace on trails
        val activityId = UUID.randomUUID().toString()
        
        val activity = ActivityEntity(
            id = activityId,
            name = "Trail Run",
            sportType = SportType.TRAIL_RUN,
            startDate = startDate,
            timezone = "UTC",
            movingTimeSeconds = duration,
            elapsedTimeSeconds = duration + 300,
            distanceMeters = distance,
            elevationGainMeters = 300.0 + Math.random() * 400, // More elevation
            elevationLossMeters = 280.0 + Math.random() * 380,
            averageSpeedMps = distance / duration,
            maxSpeedMps = (distance / duration) * 1.3,
            averageHeartRateBpm = 150,
            maxHeartRateBpm = 175,
            averageCadenceRpm = 165.0, // Slightly lower on trails
            averagePowerWatts = null,
            maxPowerWatts = null,
            calories = (distance / 1000 * 70).toInt(),
            description = "Trail running",
            source = "test_data",
            sourceId = activityId,
            createdAt = startDate,
            updatedAt = startDate
        )
        
        val streams = generateTrailStreams(
            activityId = activityId,
            duration = duration,
            distance = distance,
            avgPace = 6.5,
            hrBase = 150,
            hrVariation = 15,
            elevationGain = activity.elevationGainMeters ?: 0.0
        )
        
        return activity to streams
    }
    
    // ========== STREAM GENERATORS ==========
    
    private fun generateRunningStreams(
        activityId: String,
        duration: Int,
        distance: Double,
        avgPace: Double,
        hrBase: Int,
        hrVariation: Int,
        elevationGain: Double
    ): List<ActivityStreamEntity> {
        val streams = mutableListOf<ActivityStreamEntity>()
        val samplesPerSecond = 1
        val totalSamples = duration * samplesPerSecond
        val startLat = 45.5 + (Math.random() * 0.1) // Realistic coordinates
        val startLon = -73.5 + (Math.random() * 0.1)
        
        var currentLat = startLat
        var currentLon = startLon
        var currentAlt = 100.0 + Math.random() * 50
        var cumulativeDistance = 0.0
        
        for (i in 0 until totalSamples) {
            val progress = i.toDouble() / totalSamples
            
            // GPS movement
            val distanceThisSecond = (distance / totalSamples) * 1000 // meters
            val bearing = Math.random() * 2 * PI
            currentLat += (distanceThisSecond * cos(bearing)) / 111000.0
            currentLon += (distanceThisSecond * sin(bearing)) / (111000.0 * cos(currentLat * PI / 180))
            cumulativeDistance += distanceThisSecond
            
            // Elevation variation
            if (progress < 0.3) {
                currentAlt += (elevationGain / totalSamples * 0.3) // Climb in first 30%
            } else if (progress < 0.7) {
                currentAlt += (elevationGain / totalSamples * 0.4) // More climb
            } else {
                currentAlt -= (elevationGain / totalSamples * 0.3) // Descent
            }
            
            // Pace variation
            val paceVariation = (Math.random() - 0.5) * 0.3
            val currentPace = avgPace + paceVariation
            val speedMps = 1000.0 / (currentPace * 60)
            
            // HR variation
            val hrVariation = (Math.random() - 0.5) * hrVariation * 2
            val hr = (hrBase + hrVariation).toInt().coerceIn(100, 200)
            
            streams.add(
                ActivityStreamEntity(
                    activityId = activityId,
                    timeOffsetSeconds = i,
                    latitude = currentLat,
                    longitude = currentLon,
                    altitudeMeters = currentAlt,
                    heartRateBpm = hr,
                    cadenceRpm = (170.0 + (Math.random() - 0.5) * 10),
                    powerWatts = null,
                    speedMps = speedMps,
                    grade = null
                )
            )
        }
        
        return streams
    }
    
    private fun generateIntervalStreams(
        activityId: String,
        duration: Int,
        distance: Double,
        intervalPace: Double,
        recoveryPace: Double,
        hrBase: Int,
        elevationGain: Double
    ): List<ActivityStreamEntity> {
        val streams = mutableListOf<ActivityStreamEntity>()
        val samplesPerSecond = 1
        val totalSamples = duration * samplesPerSecond
        val startLat = 45.5 + (Math.random() * 0.1)
        val startLon = -73.5 + (Math.random() * 0.1)
        
        var currentLat = startLat
        var currentLon = startLon
        var currentAlt = 100.0 + Math.random() * 50
        
        for (i in 0 until totalSamples) {
            val progress = i.toDouble() / totalSamples
            val isInterval = (i / 120) % 2 == 0 && (i % 120) < 60 // 1 min on, 1 min off
            
            val distanceThisSecond = (distance / totalSamples) * 1000
            val bearing = Math.random() * 2 * PI
            currentLat += (distanceThisSecond * cos(bearing)) / 111000.0
            currentLon += (distanceThisSecond * sin(bearing)) / (111000.0 * cos(currentLat * PI / 180))
            
            currentAlt += (elevationGain / totalSamples) * (if (isInterval) 1.2 else 0.8)
            
            val pace = if (isInterval) intervalPace else recoveryPace
            val speedMps = 1000.0 / (pace * 60)
            val hr = if (isInterval) {
                (hrBase + 20 + (Math.random() - 0.5) * 10).toInt().coerceIn(100, 200)
            } else {
                (hrBase - 30 + (Math.random() - 0.5) * 10).toInt().coerceIn(100, 200)
            }
            
            streams.add(
                ActivityStreamEntity(
                    activityId = activityId,
                    timeOffsetSeconds = i,
                    latitude = currentLat,
                    longitude = currentLon,
                    altitudeMeters = currentAlt,
                    heartRateBpm = hr,
                    cadenceRpm = if (isInterval) 190.0 else 160.0,
                    powerWatts = null,
                    speedMps = speedMps,
                    grade = null
                )
            )
        }
        
        return streams
    }
    
    private fun generateSprintStreams(
        activityId: String,
        duration: Int,
        distance: Double,
        sprintPace: Double,
        recoveryPace: Double,
        hrBase: Int
    ): List<ActivityStreamEntity> {
        val streams = mutableListOf<ActivityStreamEntity>()
        val startLat = 45.5 + (Math.random() * 0.1)
        val startLon = -73.5 + (Math.random() * 0.1)
        
        var currentLat = startLat
        var currentLon = startLon
        
        for (i in 0 until duration) {
            val isSprint = (i / 90) % 2 == 0 && (i % 90) < 15 // 15s sprint, 75s recovery
            
            val distanceThisSecond = (distance / duration) * 1000
            val bearing = Math.random() * 2 * PI
            currentLat += (distanceThisSecond * cos(bearing)) / 111000.0
            currentLon += (distanceThisSecond * sin(bearing)) / (111000.0 * cos(currentLat * PI / 180))
            
            val pace = if (isSprint) sprintPace else recoveryPace
            val speedMps = 1000.0 / (pace * 60)
            val hr = if (isSprint) {
                (hrBase + 25 + (Math.random() - 0.5) * 5).toInt().coerceIn(100, 200)
            } else {
                (hrBase - 20 + (Math.random() - 0.5) * 10).toInt().coerceIn(100, 200)
            }
            
            streams.add(
                ActivityStreamEntity(
                    activityId = activityId,
                    timeOffsetSeconds = i,
                    latitude = currentLat,
                    longitude = currentLon,
                    altitudeMeters = 100.0 + Math.random() * 20,
                    heartRateBpm = hr,
                    cadenceRpm = if (isSprint) 200.0 else 165.0,
                    powerWatts = null,
                    speedMps = speedMps,
                    grade = null
                )
            )
        }
        
        return streams
    }
    
    private fun generateCyclingStreams(
        activityId: String,
        duration: Int,
        distance: Double,
        avgSpeed: Double,
        avgPower: Double,
        hrBase: Int,
        hrVariation: Int,
        elevationGain: Double
    ): List<ActivityStreamEntity> {
        val streams = mutableListOf<ActivityStreamEntity>()
        val samplesPerSecond = 1
        val totalSamples = duration * samplesPerSecond
        val startLat = 45.5 + (Math.random() * 0.1)
        val startLon = -73.5 + (Math.random() * 0.1)
        
        var currentLat = startLat
        var currentLon = startLon
        var currentAlt = 100.0 + Math.random() * 50
        
        for (i in 0 until totalSamples) {
            val progress = i.toDouble() / totalSamples
            
            val distanceThisSecond = (distance / totalSamples) * 1000
            val bearing = Math.random() * 2 * PI
            currentLat += (distanceThisSecond * cos(bearing)) / 111000.0
            currentLon += (distanceThisSecond * sin(bearing)) / (111000.0 * cos(currentLat * PI / 180))
            
            if (progress < 0.4) {
                currentAlt += (elevationGain / totalSamples * 0.4)
            } else if (progress < 0.8) {
                currentAlt += (elevationGain / totalSamples * 0.4)
            } else {
                currentAlt -= (elevationGain / totalSamples * 0.2)
            }
            
            val speedVariation = (Math.random() - 0.5) * 3.0
            val speedMps = ((avgSpeed + speedVariation) / 3.6).coerceAtLeast(5.0)
            
            val powerVariation = (Math.random() - 0.5) * 50.0
            val power = (avgPower + powerVariation).coerceAtLeast(50.0)
            
            val hrVariation = (Math.random() - 0.5) * hrVariation * 2
            val hr = (hrBase + hrVariation).toInt().coerceIn(100, 200)
            
            streams.add(
                ActivityStreamEntity(
                    activityId = activityId,
                    timeOffsetSeconds = i,
                    latitude = currentLat,
                    longitude = currentLon,
                    altitudeMeters = currentAlt,
                    heartRateBpm = hr,
                    cadenceRpm = 85.0 + (Math.random() - 0.5) * 10,
                    powerWatts = power,
                    speedMps = speedMps,
                    grade = null
                )
            )
        }
        
        return streams
    }
    
    private fun generateIntervalCyclingStreams(
        activityId: String,
        duration: Int,
        distance: Double,
        intervalPower: Double,
        recoveryPower: Double,
        hrBase: Int,
        elevationGain: Double
    ): List<ActivityStreamEntity> {
        val streams = mutableListOf<ActivityStreamEntity>()
        val startLat = 45.5 + (Math.random() * 0.1)
        val startLon = -73.5 + (Math.random() * 0.1)
        
        var currentLat = startLat
        var currentLon = startLon
        var currentAlt = 100.0 + Math.random() * 50
        
        for (i in 0 until duration) {
            val isInterval = (i / 300) % 2 == 0 && (i % 300) < 300 // 5 min intervals
            
            val distanceThisSecond = (distance / duration) * 1000
            val bearing = Math.random() * 2 * PI
            currentLat += (distanceThisSecond * cos(bearing)) / 111000.0
            currentLon += (distanceThisSecond * sin(bearing)) / (111000.0 * cos(currentLat * PI / 180))
            
            currentAlt += (elevationGain / duration) * (if (isInterval) 1.1 else 0.9)
            
            val power = if (isInterval) {
                intervalPower + (Math.random() - 0.5) * 30
            } else {
                recoveryPower + (Math.random() - 0.5) * 20
            }
            
            val speedMps = (power / 20.0).coerceAtLeast(5.0) / 3.6 // Rough conversion
            
            val hr = if (isInterval) {
                (hrBase + 15 + (Math.random() - 0.5) * 8).toInt().coerceIn(100, 200)
            } else {
                (hrBase - 25 + (Math.random() - 0.5) * 10).toInt().coerceIn(100, 200)
            }
            
            streams.add(
                ActivityStreamEntity(
                    activityId = activityId,
                    timeOffsetSeconds = i,
                    latitude = currentLat,
                    longitude = currentLon,
                    altitudeMeters = currentAlt,
                    heartRateBpm = hr,
                    cadenceRpm = if (isInterval) 95.0 else 80.0,
                    powerWatts = power,
                    speedMps = speedMps,
                    grade = null
                )
            )
        }
        
        return streams
    }
    
    private fun generateClimbingStreams(
        activityId: String,
        duration: Int,
        distance: Double,
        avgPower: Double,
        hrBase: Int,
        elevationGain: Double
    ): List<ActivityStreamEntity> {
        val streams = mutableListOf<ActivityStreamEntity>()
        val startLat = 45.5 + (Math.random() * 0.1)
        val startLon = -73.5 + (Math.random() * 0.1)
        
        var currentLat = startLat
        var currentLon = startLon
        var currentAlt = 100.0 + Math.random() * 50
        
        for (i in 0 until duration) {
            val progress = i.toDouble() / duration
            
            val distanceThisSecond = (distance / duration) * 1000
            val bearing = Math.random() * 2 * PI
            currentLat += (distanceThisSecond * cos(bearing)) / 111000.0
            currentLon += (distanceThisSecond * sin(bearing)) / (111000.0 * cos(currentLat * PI / 180))
            
            // Steady climb
            currentAlt += (elevationGain / duration)
            
            val power = avgPower + (Math.random() - 0.5) * 60
            val speedMps = (power / 25.0).coerceAtLeast(3.0) / 3.6 // Slower on climbs
            val hr = (hrBase + (Math.random() - 0.5) * 12).toInt().coerceIn(100, 200)
            
            streams.add(
                ActivityStreamEntity(
                    activityId = activityId,
                    timeOffsetSeconds = i,
                    latitude = currentLat,
                    longitude = currentLon,
                    altitudeMeters = currentAlt,
                    heartRateBpm = hr,
                    cadenceRpm = 70.0 + (Math.random() - 0.5) * 8, // Lower on climbs
                    powerWatts = power,
                    speedMps = speedMps,
                    grade = null
                )
            )
        }
        
        return streams
    }
    
    private fun generateTrailStreams(
        activityId: String,
        duration: Int,
        distance: Double,
        avgPace: Double,
        hrBase: Int,
        hrVariation: Int,
        elevationGain: Double
    ): List<ActivityStreamEntity> {
        val streams = mutableListOf<ActivityStreamEntity>()
        val samplesPerSecond = 1
        val totalSamples = duration * samplesPerSecond
        val startLat = 45.5 + (Math.random() * 0.1)
        val startLon = -73.5 + (Math.random() * 0.1)
        
        var currentLat = startLat
        var currentLon = startLon
        var currentAlt = 200.0 + Math.random() * 100 // Higher starting altitude
        
        for (i in 0 until totalSamples) {
            val progress = i.toDouble() / totalSamples
            
            val distanceThisSecond = (distance / totalSamples) * 1000
            val bearing = Math.random() * 2 * PI
            currentLat += (distanceThisSecond * cos(bearing)) / 111000.0
            currentLon += (distanceThisSecond * sin(bearing)) / (111000.0 * cos(currentLat * PI / 180))
            
            // More elevation variation on trails
            if (progress < 0.2) {
                currentAlt += (elevationGain / totalSamples * 0.3)
            } else if (progress < 0.5) {
                currentAlt += (elevationGain / totalSamples * 0.4)
            } else if (progress < 0.8) {
                currentAlt -= (elevationGain / totalSamples * 0.2)
            } else {
                currentAlt -= (elevationGain / totalSamples * 0.1)
            }
            
            val paceVariation = (Math.random() - 0.5) * 0.5 // More variation on trails
            val currentPace = avgPace + paceVariation
            val speedMps = 1000.0 / (currentPace * 60)
            
            val hrVariation = (Math.random() - 0.5) * hrVariation * 2
            val hr = (hrBase + hrVariation).toInt().coerceIn(100, 200)
            
            streams.add(
                ActivityStreamEntity(
                    activityId = activityId,
                    timeOffsetSeconds = i,
                    latitude = currentLat,
                    longitude = currentLon,
                    altitudeMeters = currentAlt,
                    heartRateBpm = hr,
                    cadenceRpm = 165.0 + (Math.random() - 0.5) * 12,
                    powerWatts = null,
                    speedMps = speedMps,
                    grade = null
                )
            )
        }
        
        return streams
    }
}







