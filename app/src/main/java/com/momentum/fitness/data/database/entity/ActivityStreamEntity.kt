package com.momentum.fitness.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "activity_streams",
    foreignKeys = [
        ForeignKey(
            entity = ActivityEntity::class,
            parentColumns = ["id"],
            childColumns = ["activityId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["activityId", "timeOffsetSeconds"])]
)
data class ActivityStreamEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val activityId: String,
    val timeOffsetSeconds: Int, // Seconds from activity start
    val latitude: Double?,
    val longitude: Double?,
    val altitudeMeters: Double?,
    val heartRateBpm: Int?,
    val cadenceRpm: Double?,
    val powerWatts: Double?,
    val speedMps: Double?,
    val grade: Double? // Calculated grade percentage
)


