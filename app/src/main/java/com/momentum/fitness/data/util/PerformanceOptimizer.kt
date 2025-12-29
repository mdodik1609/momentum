package com.momentum.fitness.data.util

import android.content.Context
import android.os.Build
import androidx.core.content.ContextCompat
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Performance optimization utilities
 * Handles caching, memory management, and battery optimization
 */
@Singleton
class PerformanceOptimizer @Inject constructor(
    private val context: Context
) {
    companion object {
        private const val CACHE_DIR = "momentum_cache"
        private const val MAX_CACHE_SIZE_MB = 50L
    }

    /**
     * Get cache directory for app data
     */
    fun getCacheDirectory(): File {
        val cacheDir = File(context.cacheDir, CACHE_DIR)
        if (!cacheDir.exists()) {
            cacheDir.mkdirs()
        }
        return cacheDir
    }

    /**
     * Clear old cache files if cache exceeds size limit
     */
    suspend fun cleanupCacheIfNeeded() {
        withContext(Dispatchers.IO) {
            val cacheDir = getCacheDirectory()
            val files = cacheDir.listFiles() ?: return@withContext
            
            var totalSize = 0L
            val fileSizes = files.map { file ->
                val size = file.length()
                totalSize += size
                file to size
            }.sortedByDescending { it.second }
            
            val maxSizeBytes = MAX_CACHE_SIZE_MB * 1024 * 1024
            
            if (totalSize > maxSizeBytes) {
                // Delete oldest files until under limit
                var currentSize = totalSize
                for ((file, size) in fileSizes) {
                    if (currentSize <= maxSizeBytes * 0.8) break
                    file.delete()
                    currentSize -= size
                }
            }
        }
    }

    /**
     * Check if device has low memory
     */
    fun isLowMemoryDevice(): Boolean {
        val activityManager = ContextCompat.getSystemService(context, android.app.ActivityManager::class.java)
        return activityManager?.isLowRamDevice == true
    }

    /**
     * Get recommended max items for lists based on device memory
     */
    fun getRecommendedListSize(): Int {
        return if (isLowMemoryDevice()) {
            50 // Smaller lists on low-memory devices
        } else {
            200 // Normal size
        }
    }

    /**
     * Check if battery optimization should be enabled
     */
    fun shouldEnableBatteryOptimization(): Boolean {
        // Enable on older devices or low battery
        return Build.VERSION.SDK_INT < Build.VERSION_CODES.P
    }
}







