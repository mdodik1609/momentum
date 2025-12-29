package com.momentum.fitness.data.database.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey
    val id: Int = 1, // Singleton
    val heartRateZone1Min: Int = 50,
    val heartRateZone1Max: Int = 60,
    val heartRateZone2Min: Int = 60,
    val heartRateZone2Max: Int = 70,
    val heartRateZone3Min: Int = 70,
    val heartRateZone3Max: Int = 80,
    val heartRateZone4Min: Int = 80,
    val heartRateZone4Max: Int = 90,
    val heartRateZone5Min: Int = 90,
    val heartRateZone5Max: Int = 220,
    val functionalThresholdPower: Int? = null, // Watts
    val functionalThresholdPaceSecondsPerKm: Int? = null, // Seconds per km
    val stravaAccessToken: String? = null,
    val stravaRefreshToken: String? = null,
    val stravaTokenExpiresAt: Long? = null, // Unix timestamp
    val stravaAthleteId: String? = null,
    val stravaLastSyncTimestamp: Long? = null, // Unix timestamp of last successful sync
    val garminAccessToken: String? = null,
    val garminRefreshToken: String? = null,
    val garminTokenExpiresAt: Long? = null, // Unix timestamp
    val garminUserId: Long? = null,
    val garminLastSyncTimestamp: Long? = null // Unix timestamp of last successful sync
)

