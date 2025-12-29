package com.momentum.fitness.data.database.dao

import androidx.room.*
import com.momentum.fitness.data.database.entity.ActivityStreamEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ActivityStreamDao {
    @Query("SELECT * FROM activity_streams WHERE activityId = :activityId ORDER BY timeOffsetSeconds ASC")
    fun getStreamsForActivity(activityId: String): Flow<List<ActivityStreamEntity>>

    @Query("SELECT * FROM activity_streams WHERE activityId = :activityId ORDER BY timeOffsetSeconds ASC")
    suspend fun getStreamsForActivitySync(activityId: String): List<ActivityStreamEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStream(stream: ActivityStreamEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStreams(streams: List<ActivityStreamEntity>)

    @Query("DELETE FROM activity_streams WHERE activityId = :activityId")
    suspend fun deleteStreamsForActivity(activityId: String)

    @Query("SELECT COUNT(*) FROM activity_streams WHERE activityId = :activityId")
    suspend fun getStreamCountForActivity(activityId: String): Int
}

