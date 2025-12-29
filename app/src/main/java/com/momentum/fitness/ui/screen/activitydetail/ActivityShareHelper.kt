package com.momentum.fitness.ui.screen.activitydetail

import android.content.Context
import android.content.Intent
import com.momentum.fitness.data.database.entity.ActivityEntity
import com.momentum.fitness.ui.util.formatDistance
import com.momentum.fitness.ui.util.formatTime
import java.time.format.DateTimeFormatter

fun shareActivity(context: Context, activity: ActivityEntity) {
    val shareText = buildActivityShareText(activity)
    
    val shareIntent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, shareText)
        type = "text/plain"
    }
    
    val chooserIntent = Intent.createChooser(shareIntent, "Share Activity")
    context.startActivity(chooserIntent)
}

private fun buildActivityShareText(activity: ActivityEntity): String {
    val dateFormatter = DateTimeFormatter.ofPattern("MMM d, yyyy")
    val dateStr = activity.startDate
        .atZone(java.time.ZoneId.systemDefault())
        .format(dateFormatter)
    
    val builder = StringBuilder()
    builder.append("${activity.name}\n")
    builder.append("${activity.sportType.displayName} on $dateStr\n\n")
    
    if (activity.distanceMeters != null) {
        builder.append("Distance: ${formatDistance(activity.distanceMeters)}\n")
    }
    
    builder.append("Time: ${formatTime(activity.movingTimeSeconds)}\n")
    
    if (activity.elevationGainMeters != null && activity.elevationGainMeters > 0) {
        builder.append("Elevation: ${activity.elevationGainMeters.toInt()} m\n")
    }
    
    if (activity.averageHeartRateBpm != null) {
        builder.append("Avg HR: ${activity.averageHeartRateBpm} bpm\n")
    }
    
    if (activity.averagePowerWatts != null) {
        builder.append("Avg Power: ${activity.averagePowerWatts.toInt()} W\n")
    }
    
    if (activity.calories != null) {
        builder.append("Calories: ${activity.calories}\n")
    }
    
    builder.append("\nRecorded with Momentum")
    
    return builder.toString()
}







