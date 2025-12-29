package com.momentum.fitness.data.database.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import com.momentum.fitness.data.model.SportType
import java.time.Instant

@Entity(
    tableName = "personal_records",
    foreignKeys = [
        ForeignKey(
            entity = ActivityEntity::class,
            parentColumns = ["id"],
            childColumns = ["activityId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["sportType", "recordType"])]
)
data class PersonalRecordEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val activityId: String,
    val sportType: SportType,
    val recordType: String, // "1k", "5k", "10k", "marathon", "20min_power", etc.
    val value: Double, // Distance in meters or power in watts, etc.
    val achievedAt: Instant,
    val createdAt: Instant
)


