package com.momentum.fitness.data.test

import com.momentum.fitness.data.repository.ActivityRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Test Data Loader
 * Loads generated test data into the database
 * Use this in development/testing to populate the app with sample activities
 */
@Singleton
class TestDataLoader @Inject constructor(
    private val activityRepository: ActivityRepository
) {
    /**
     * Load all test activities into the database
     * @return Number of activities loaded
     */
    suspend fun loadTestData(): Int = withContext(Dispatchers.IO) {
        try {
            val activities = TestDataGenerator.generateTestActivities()
            android.util.Log.d("TestDataLoader", "Generated ${activities.size} test activities")
            
            var loadedCount = 0
            activities.forEachIndexed { index, (activity, streams) ->
                try {
                    activityRepository.insertActivity(activity)
                    if (streams.isNotEmpty()) {
                        activityRepository.insertStreams(streams)
                    }
                    loadedCount++
                    if ((index + 1) % 5 == 0) {
                        android.util.Log.d("TestDataLoader", "Loaded ${index + 1}/${activities.size} activities")
                    }
                } catch (e: Exception) {
                    android.util.Log.e("TestDataLoader", "Failed to load activity ${activity.name}: ${e.message}")
                }
            }
            
            android.util.Log.d("TestDataLoader", "Successfully loaded $loadedCount/${activities.size} activities")
            return@withContext loadedCount
        } catch (e: Exception) {
            android.util.Log.e("TestDataLoader", "Failed to load test data: ${e.message}", e)
            return@withContext 0
        }
    }
    
    /**
     * Clear all test data from database
     * WARNING: This will delete ALL activities, not just test data
     */
    suspend fun clearTestData() = withContext(Dispatchers.IO) {
        // Note: In a real implementation, you might want to mark test data
        // and only delete those, or have a separate method
        // For now, this is a placeholder
    }
}



