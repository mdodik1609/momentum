package com.momentum.fitness.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.momentum.fitness.data.model.SportType
import java.time.Instant

@Entity(
    tableName = "activities",
    indices = [
        androidx.room.Index(value = ["startDate"]),
        androidx.room.Index(value = ["sportType"]),
        androidx.room.Index(value = ["source", "sourceId"])
    ]
)
data class ActivityEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val sportType: SportType,
    val startDate: Instant,
    val timezone: String?,
    val movingTimeSeconds: Int,
    val elapsedTimeSeconds: Int,
    val distanceMeters: Double?,
    val elevationGainMeters: Double?,
    val elevationLossMeters: Double?,
    val averageSpeedMps: Double?,
    val maxSpeedMps: Double?,
    val averageHeartRateBpm: Int?,
    val maxHeartRateBpm: Int?,
    val averageCadenceRpm: Double?,
    val averagePowerWatts: Double?,
    val maxPowerWatts: Double?,
    val calories: Int?,
    val description: String?,
    val source: String, // "strava", "garmin", "file_import", etc.
    val sourceId: String?, // External ID from source
    val createdAt: Instant,
    val updatedAt: Instant
)

