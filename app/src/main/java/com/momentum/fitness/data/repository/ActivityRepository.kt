package com.momentum.fitness.data.repository

import com.momentum.fitness.data.database.dao.ActivityDao
import com.momentum.fitness.data.database.dao.ActivityStreamDao
import com.momentum.fitness.data.database.entity.ActivityEntity
import com.momentum.fitness.data.database.entity.ActivityStreamEntity
import com.momentum.fitness.data.model.SportType
import kotlinx.coroutines.flow.Flow
import java.time.Instant
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ActivityRepository @Inject constructor(
    private val activityDao: ActivityDao,
    private val streamDao: ActivityStreamDao
) {
    fun getAllActivities(): Flow<List<ActivityEntity>> = activityDao.getAllActivities()

    suspend fun getActivityById(id: String): ActivityEntity? = activityDao.getActivityById(id)

    fun getActivitiesByDateRange(startDate: Instant, endDate: Instant): Flow<List<ActivityEntity>> =
        activityDao.getActivitiesByDateRange(startDate, endDate)

    fun getActivitiesBySportType(sportType: SportType): Flow<List<ActivityEntity>> =
        activityDao.getActivitiesBySportType(sportType)

    fun searchActivities(
        query: String? = null,
        sportType: SportType? = null,
        startDate: Instant? = null,
        endDate: Instant? = null
    ): Flow<List<ActivityEntity>> = activityDao.searchActivities(query, sportType, startDate, endDate)

    suspend fun getActivityCountForDateRange(startDate: Instant, endDate: Instant): Int =
        activityDao.getActivityCountForDateRange(startDate, endDate)

    suspend fun getActiveDaysCount(startDate: Instant, endDate: Instant): Int =
        activityDao.getActiveDaysCount(startDate, endDate)

    suspend fun insertActivity(activity: ActivityEntity) = activityDao.insertActivity(activity)

    suspend fun insertActivities(activities: List<ActivityEntity>) = activityDao.insertActivities(activities)

    suspend fun getStreamsForActivity(activityId: String): Flow<List<ActivityStreamEntity>> =
        streamDao.getStreamsForActivity(activityId)

    suspend fun getStreamsForActivitySync(activityId: String): List<ActivityStreamEntity> =
        streamDao.getStreamsForActivitySync(activityId)

    suspend fun insertStreams(streams: List<ActivityStreamEntity>) = streamDao.insertStreams(streams)

    suspend fun getStatsForDateRange(startDate: Instant, endDate: Instant): ActivityDao.ActivityStats =
        activityDao.getStatsForDateRange(startDate, endDate) ?: ActivityDao.ActivityStats(0.0, 0, 0.0)

    suspend fun deleteActivity(activity: ActivityEntity) = activityDao.deleteActivity(activity)

    suspend fun getActivityCount(): Int = activityDao.getActivityCount()

    suspend fun getStreamCount(): Long = activityDao.getStreamCount()
}

