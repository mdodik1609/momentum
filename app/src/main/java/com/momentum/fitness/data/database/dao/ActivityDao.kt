package com.momentum.fitness.data.database.dao

import androidx.room.*
import com.momentum.fitness.data.database.entity.ActivityEntity
import com.momentum.fitness.data.model.SportType
import kotlinx.coroutines.flow.Flow
import java.time.Instant

@Dao
interface ActivityDao {
    @Query("SELECT * FROM activities ORDER BY startDate DESC")
    fun getAllActivities(): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activities WHERE id = :id")
    suspend fun getActivityById(id: String): ActivityEntity?

    @Query("SELECT * FROM activities WHERE startDate >= :startDate AND startDate <= :endDate ORDER BY startDate DESC")
    fun getActivitiesByDateRange(startDate: Instant, endDate: Instant): Flow<List<ActivityEntity>>

    @Query("SELECT * FROM activities WHERE sportType = :sportType ORDER BY startDate DESC")
    fun getActivitiesBySportType(sportType: SportType): Flow<List<ActivityEntity>>

    @Query("""
        SELECT * FROM activities 
        WHERE (:query IS NULL OR :query = '' OR name LIKE '%' || :query || '%')
        AND (:sportType IS NULL OR sportType = :sportType)
        AND (:startDate IS NULL OR startDate >= :startDate)
        AND (:endDate IS NULL OR startDate <= :endDate)
        ORDER BY startDate DESC
    """)
    fun searchActivities(
        query: String?,
        sportType: SportType?,
        startDate: Instant?,
        endDate: Instant?
    ): Flow<List<ActivityEntity>>

    @Query("""
        SELECT COUNT(*) FROM activities 
        WHERE startDate >= :startDate AND startDate <= :endDate
    """)
    suspend fun getActivityCountForDateRange(startDate: Instant, endDate: Instant): Int

    @Query("""
        SELECT COUNT(DISTINCT CAST(startDate / 86400000 AS INTEGER))
        FROM activities 
        WHERE startDate >= :startDate AND startDate <= :endDate
    """)
    suspend fun getActiveDaysCount(startDate: Instant, endDate: Instant): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivity(activity: ActivityEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivities(activities: List<ActivityEntity>)

    @Update
    suspend fun updateActivity(activity: ActivityEntity)

    @Delete
    suspend fun deleteActivity(activity: ActivityEntity)

    @Query("SELECT COUNT(*) FROM activities")
    suspend fun getActivityCount(): Int

    @Query("SELECT COUNT(*) FROM activity_streams")
    suspend fun getStreamCount(): Long

    @Query("""
        SELECT COUNT(*) FROM activities 
        WHERE startDate < :cutoffDate
    """)
    suspend fun getOldActivityCount(cutoffDate: Instant): Int

    @Query("""
        SELECT * FROM activities 
        WHERE startDate < :cutoffDate
        ORDER BY startDate ASC
        LIMIT :limit
    """)
    suspend fun getOldActivities(cutoffDate: Instant, limit: Int): List<ActivityEntity>

    @Query("""
        SELECT 
            COALESCE(SUM(distanceMeters), 0) as totalDistance,
            COALESCE(SUM(movingTimeSeconds), 0) as totalTime,
            COALESCE(SUM(elevationGainMeters), 0) as totalElevation
        FROM activities 
        WHERE startDate >= :startDate AND startDate <= :endDate
    """)
    suspend fun getStatsForDateRange(startDate: Instant, endDate: Instant): ActivityStats?

    data class ActivityStats(
        val totalDistance: Double,
        val totalTime: Int,
        val totalElevation: Double
    )
}

