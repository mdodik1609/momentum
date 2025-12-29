package com.momentum.fitness.ui.component.map

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.momentum.fitness.data.database.entity.ActivityStreamEntity
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.OnMapReadyCallback
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.Feature
import org.maplibre.geojson.LineString
import org.maplibre.geojson.Point

/**
 * MapLibre-based activity map component
 * Displays GPS track with custom green/grey styling
 * Optimized for performance with point reduction
 */
@Composable
fun ActivityMap(
    streams: List<ActivityStreamEntity>,
    modifier: Modifier = Modifier,
    showStartMarker: Boolean = true,
    showEndMarker: Boolean = true
) {
    val context = LocalContext.current
    val hasGpsData = streams.any { it.latitude != null && it.longitude != null }
    
    // Optimize points for rendering (reduce if > 1000 points)
    val optimizedPoints = remember(streams) {
        if (hasGpsData) {
            MapOptimizer.smartReduce(streams, maxPoints = 1000)
        } else {
            emptyList()
        }
    }
    
    if (hasGpsData && optimizedPoints.isNotEmpty()) {
        AndroidView(
            factory = { ctx ->
                createMapView(ctx, optimizedPoints, showStartMarker, showEndMarker)
            },
            modifier = modifier.fillMaxSize(),
            update = { mapView ->
                // Update map if streams change
                mapView.getMapAsync { map ->
                    map.getStyle { style ->
                        val source = style.getSourceAs<GeoJsonSource>("activity-path")
                        if (source != null && optimizedPoints.isNotEmpty()) {
                            val lineString = LineString.fromLngLats(optimizedPoints.toMutableList())
                            val feature = Feature.fromGeometry(lineString)
                            source.setGeoJson(feature)
                            
                            // Update bounds
                            val bounds = org.maplibre.android.geometry.LatLngBounds.Builder()
                            optimizedPoints.forEach { point ->
                                bounds.include(org.maplibre.android.geometry.LatLng(point.latitude(), point.longitude()))
                            }
                            map.animateCamera(
                                org.maplibre.android.camera.CameraUpdateFactory.newLatLngBounds(
                                    bounds.build(),
                                    50
                                )
                            )
                        }
                    }
                }
            }
        )
    } else {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(androidx.compose.material3.MaterialTheme.colorScheme.surfaceVariant),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            androidx.compose.material3.Text(
                text = "No GPS data available",
                style = androidx.compose.material3.MaterialTheme.typography.bodyMedium,
                color = androidx.compose.material3.MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

private fun createMapView(
    context: Context,
    points: List<Point>,
    showStartMarker: Boolean,
    showEndMarker: Boolean
): MapView {
    return MapView(context).apply {
        onCreate(null)
        getMapAsync(object : OnMapReadyCallback {
            override fun onMapReady(map: MapLibreMap) {
                // Use a custom style URL or default
                map.setStyle(Style.Builder().fromUri("https://demotiles.maplibre.org/style.json")) { style ->
                    // Create line string from GPS points
                    val lineString = LineString.fromLngLats(points.toMutableList())
                    val feature = Feature.fromGeometry(lineString)
                    
                    // Add source
                    val source = GeoJsonSource("activity-path", feature)
                    style.addSource(source)
                    
                    // Add layer with green color and gradient based on elevation/speed
                    val lineLayer = LineLayer("activity-path-layer", "activity-path")
                        .withProperties(
                            PropertyFactory.lineColor("#4DB33D"), // Momentum green
                            PropertyFactory.lineWidth(4f),
                            PropertyFactory.lineCap("round"),
                            PropertyFactory.lineJoin("round"),
                            PropertyFactory.lineOpacity(0.9f)
                        )
                    style.addLayer(lineLayer)
                    
                    // Add start marker if requested
                    if (showStartMarker && points.isNotEmpty()) {
                        val startPoint = points.first()
                        val startSource = GeoJsonSource("start-marker", Feature.fromGeometry(startPoint))
                        style.addSource(startSource)
                        // Add circle layer for start marker
                        val startLayer = org.maplibre.android.style.layers.CircleLayer("start-marker-layer", "start-marker")
                            .withProperties(
                                PropertyFactory.circleRadius(8f),
                                PropertyFactory.circleColor("#4DB33D"),
                                PropertyFactory.circleStrokeWidth(2f),
                                PropertyFactory.circleStrokeColor("#FFFFFF")
                            )
                        style.addLayer(startLayer)
                    }
                    
                    // Add end marker if requested
                    if (showEndMarker && points.size > 1) {
                        val endPoint = points.last()
                        val endSource = GeoJsonSource("end-marker", Feature.fromGeometry(endPoint))
                        style.addSource(endSource)
                        // Add circle layer for end marker
                        val endLayer = org.maplibre.android.style.layers.CircleLayer("end-marker-layer", "end-marker")
                            .withProperties(
                                PropertyFactory.circleRadius(8f),
                                PropertyFactory.circleColor("#B33D3D"), // Red for end
                                PropertyFactory.circleStrokeWidth(2f),
                                PropertyFactory.circleStrokeColor("#FFFFFF")
                            )
                        style.addLayer(endLayer)
                    }
                    
                    // Fit bounds to show entire route
                    if (points.isNotEmpty()) {
                        val bounds = org.maplibre.android.geometry.LatLngBounds.Builder()
                        points.forEach { point ->
                            bounds.include(org.maplibre.android.geometry.LatLng(point.latitude(), point.longitude()))
                        }
                        map.animateCamera(
                            org.maplibre.android.camera.CameraUpdateFactory.newLatLngBounds(
                                bounds.build(),
                                50
                            )
                        )
                    }
                }
            }
        })
    }
}

fun createActivityPath(streams: List<ActivityStreamEntity>): List<Point> {
    return streams
        .filter { it.latitude != null && it.longitude != null }
        .map { Point.fromLngLat(it.longitude!!, it.latitude!!) }
}

fun createLineStringFeature(points: List<Point>): Feature {
    val lineString = LineString.fromLngLats(points.toMutableList())
    return Feature.fromGeometry(lineString)
}
