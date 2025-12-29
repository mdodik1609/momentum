package com.momentum.fitness.ui.util

import java.text.DecimalFormat

fun formatDistance(meters: Double): String {
    return when {
        meters >= 1000 -> {
            val km = meters / 1000.0
            "${DecimalFormat("#.##").format(km)} km"
        }
        else -> "${meters.toInt()} m"
    }
}

fun formatElevation(meters: Double): String {
    return "${meters.toInt()} m"
}

fun formatTime(seconds: Int): String {
    val hours = seconds / 3600
    val minutes = (seconds % 3600) / 60
    return when {
        hours > 0 -> "${hours}h ${minutes}m"
        else -> "${minutes}m"
    }
}

fun formatPace(secondsPerKm: Double): String {
    val minutes = (secondsPerKm / 60).toInt()
    val seconds = (secondsPerKm % 60).toInt()
    return "${minutes}:${seconds.toString().padStart(2, '0')} /km"
}

fun formatSpeed(mps: Double): String {
    val kmh = mps * 3.6
    return "${DecimalFormat("#.#").format(kmh)} km/h"
}







