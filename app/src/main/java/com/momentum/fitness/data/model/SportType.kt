package com.momentum.fitness.data.model

enum class SportType(val displayName: String, val iconName: String) {
    RUN("Run", "run"),
    TRAIL_RUN("Trail Run", "trail_run"),
    INDOOR_RUN("Indoor Run", "indoor_run"),
    HIKE("Hike", "hike"),
    WALK("Walk", "walk"),
    RIDE("Ride", "ride"),
    INDOOR_RIDE("Indoor Ride", "indoor_ride"),
    SWIM("Swim", "swim"),
    OPEN_WATER_SWIM("Open Water Swim", "open_water_swim"),
    STAIRMASTER("Stairmaster", "stairmaster"),
    ROPE_JUMPING("Rope Jumping", "rope_jumping"),
    UNKNOWN("Unknown", "unknown");

    companion object {
        fun fromString(value: String): SportType {
            return values().find { it.name.equals(value, ignoreCase = true) } ?: UNKNOWN
        }
    }
}


