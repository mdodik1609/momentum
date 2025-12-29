package com.momentum.fitness.data.service

import kotlinx.coroutines.delay
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Rate Limiter for API calls
 * Implements rate limiting with sliding window approach
 * 
 * Strava Limits:
 * - 200 requests per 15 minutes (short-term)
 * - 2,000 requests per day (long-term)
 * 
 * Garmin Limits:
 * - Varies by endpoint, typically 100 requests per hour
 */
@Singleton
class RateLimiter @Inject constructor() {
    
    private data class RateLimitWindow(
        val requests: MutableList<Long> = mutableListOf(),
        val dailyRequests: AtomicInteger = AtomicInteger(0),
        var lastReset: Long = System.currentTimeMillis()
    )
    
    private val windows = ConcurrentHashMap<String, RateLimitWindow>()
    
    companion object {
        // Strava limits
        const val STRAVA_SHORT_TERM_LIMIT = 200
        const val STRAVA_SHORT_TERM_WINDOW_MS = 15 * 60 * 1000L // 15 minutes
        const val STRAVA_DAILY_LIMIT = 2000
        const val STRAVA_DAILY_WINDOW_MS = 24 * 60 * 60 * 1000L // 24 hours
        
        // Garmin limits (conservative estimates)
        const val GARMIN_HOURLY_LIMIT = 100
        const val GARMIN_HOURLY_WINDOW_MS = 60 * 60 * 1000L // 1 hour
        const val GARMIN_DAILY_LIMIT = 1000
        const val GARMIN_DAILY_WINDOW_MS = 24 * 60 * 60 * 1000L // 24 hours
    }
    
    /**
     * Check if request is allowed for Strava API
     * @return true if allowed, false if rate limited
     */
    suspend fun checkStravaRateLimit(): Boolean {
        val window = windows.getOrPut("strava") { RateLimitWindow() }
        val now = System.currentTimeMillis()
        
        // Clean old requests (outside 15-minute window)
        window.requests.removeAll { now - it > STRAVA_SHORT_TERM_WINDOW_MS }
        
        // Reset daily counter if needed
        if (now - window.lastReset > STRAVA_DAILY_WINDOW_MS) {
            window.dailyRequests.set(0)
            window.lastReset = now
        }
        
        // Check short-term limit
        if (window.requests.size >= STRAVA_SHORT_TERM_LIMIT) {
            val oldestRequest = window.requests.minOrNull() ?: return false
            val waitTime = STRAVA_SHORT_TERM_WINDOW_MS - (now - oldestRequest)
            if (waitTime > 0) {
                delay(waitTime)
                return checkStravaRateLimit() // Retry after waiting
            }
        }
        
        // Check daily limit
        if (window.dailyRequests.get() >= STRAVA_DAILY_LIMIT) {
            return false
        }
        
        return true
    }
    
    /**
     * Record a Strava API request
     */
    fun recordStravaRequest() {
        val window = windows.getOrPut("strava") { RateLimitWindow() }
        val now = System.currentTimeMillis()
        window.requests.add(now)
        window.dailyRequests.incrementAndGet()
    }
    
    /**
     * Check if request is allowed for Garmin API
     */
    suspend fun checkGarminRateLimit(): Boolean {
        val window = windows.getOrPut("garmin") { RateLimitWindow() }
        val now = System.currentTimeMillis()
        
        // Clean old requests (outside 1-hour window)
        window.requests.removeAll { now - it > GARMIN_HOURLY_WINDOW_MS }
        
        // Reset daily counter if needed
        if (now - window.lastReset > GARMIN_DAILY_WINDOW_MS) {
            window.dailyRequests.set(0)
            window.lastReset = now
        }
        
        // Check hourly limit
        if (window.requests.size >= GARMIN_HOURLY_LIMIT) {
            val oldestRequest = window.requests.minOrNull() ?: return false
            val waitTime = GARMIN_HOURLY_WINDOW_MS - (now - oldestRequest)
            if (waitTime > 0) {
                delay(waitTime)
                return checkGarminRateLimit() // Retry after waiting
            }
        }
        
        // Check daily limit
        if (window.dailyRequests.get() >= GARMIN_DAILY_LIMIT) {
            return false
        }
        
        return true
    }
    
    /**
     * Record a Garmin API request
     */
    fun recordGarminRequest() {
        val window = windows.getOrPut("garmin") { RateLimitWindow() }
        val now = System.currentTimeMillis()
        window.requests.add(now)
        window.dailyRequests.incrementAndGet()
    }
    
    /**
     * Get remaining requests for Strava
     */
    fun getStravaRemainingRequests(): Pair<Int, Int> {
        val window = windows.getOrPut("strava") { RateLimitWindow() }
        val now = System.currentTimeMillis()
        window.requests.removeAll { now - it > STRAVA_SHORT_TERM_WINDOW_MS }
        
        val shortTermRemaining = (STRAVA_SHORT_TERM_LIMIT - window.requests.size).coerceAtLeast(0)
        val dailyRemaining = (STRAVA_DAILY_LIMIT - window.dailyRequests.get()).coerceAtLeast(0)
        
        return shortTermRemaining to dailyRemaining
    }
    
    /**
     * Get remaining requests for Garmin
     */
    fun getGarminRemainingRequests(): Pair<Int, Int> {
        val window = windows.getOrPut("garmin") { RateLimitWindow() }
        val now = System.currentTimeMillis()
        window.requests.removeAll { now - it > GARMIN_HOURLY_WINDOW_MS }
        
        val hourlyRemaining = (GARMIN_HOURLY_LIMIT - window.requests.size).coerceAtLeast(0)
        val dailyRemaining = (GARMIN_DAILY_LIMIT - window.dailyRequests.get()).coerceAtLeast(0)
        
        return hourlyRemaining to dailyRemaining
    }
}


