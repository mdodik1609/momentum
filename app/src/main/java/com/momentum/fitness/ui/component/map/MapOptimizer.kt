package com.momentum.fitness.ui.component.map

import com.momentum.fitness.data.database.entity.ActivityStreamEntity
import org.maplibre.geojson.Point
import kotlin.math.abs
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Optimizes GPS points for map rendering using Douglas-Peucker algorithm
 * Reduces point count while preserving route shape
 */
object MapOptimizer {
    /**
     * Reduce GPS points using Douglas-Peucker algorithm
     * @param points Original GPS points
     * @param epsilon Distance threshold (in degrees, ~0.0001 = ~11 meters)
     * @return Reduced point list
     */
    fun reducePoints(points: List<Point>, epsilon: Double = 0.0001): List<Point> {
        if (points.size <= 2) return points
        
        // Find the point with maximum distance from line between first and last
        var maxDistance = 0.0
        var maxIndex = 0
        
        for (i in 1 until points.size - 1) {
            val distance = perpendicularDistance(
                points[i],
                points[0],
                points[points.size - 1]
            )
            if (distance > maxDistance) {
                maxDistance = distance
                maxIndex = i
            }
        }
        
        // If max distance is greater than epsilon, recursively simplify
        return if (maxDistance > epsilon) {
            val left = reducePoints(points.subList(0, maxIndex + 1), epsilon)
            val right = reducePoints(points.subList(maxIndex, points.size), epsilon)
            left.dropLast(1) + right
        } else {
            listOf(points[0], points[points.size - 1])
        }
    }
    
    private fun perpendicularDistance(point: Point, lineStart: Point, lineEnd: Point): Double {
        val dx = lineEnd.longitude() - lineStart.longitude()
        val dy = lineEnd.latitude() - lineStart.latitude()
        val lengthSquared = dx * dx + dy * dy
        
        if (lengthSquared == 0.0) {
            // Line is a point
            val dx2 = point.longitude() - lineStart.longitude()
            val dy2 = point.latitude() - lineStart.latitude()
            return sqrt(dx2 * dx2 + dy2 * dy2)
        }
        
        val t = ((point.longitude() - lineStart.longitude()) * dx +
                (point.latitude() - lineStart.latitude()) * dy) / lengthSquared
        
        val closestX = lineStart.longitude() + t * dx
        val closestY = lineStart.latitude() + t * dy
        
        val dx2 = point.longitude() - closestX
        val dy2 = point.latitude() - closestY
        
        return sqrt(dx2 * dx2 + dy2 * dy2)
    }
    
    /**
     * Sample points evenly (every Nth point)
     * Faster than Douglas-Peucker but less accurate
     */
    fun samplePoints(points: List<Point>, sampleRate: Int = 5): List<Point> {
        if (points.size <= sampleRate) return points
        return points.filterIndexed { index, _ -> index % sampleRate == 0 || index == points.size - 1 }
    }
    
    /**
     * Smart point reduction based on activity length
     * Uses Douglas-Peucker for short activities, sampling for long ones
     */
    fun smartReduce(streams: List<ActivityStreamEntity>, maxPoints: Int = 1000): List<Point> {
        val gpsPoints = streams
            .filter { it.latitude != null && it.longitude != null }
            .map { Point.fromLngLat(it.longitude!!, it.latitude!!) }
        
        if (gpsPoints.size <= maxPoints) return gpsPoints
        
        // For very long activities, use sampling first, then Douglas-Peucker
        val sampled = if (gpsPoints.size > maxPoints * 2) {
            samplePoints(gpsPoints, (gpsPoints.size / maxPoints).coerceAtLeast(2))
        } else {
            gpsPoints
        }
        
        return if (sampled.size > maxPoints) {
            reducePoints(sampled, 0.0001)
        } else {
            sampled
        }
    }
}







