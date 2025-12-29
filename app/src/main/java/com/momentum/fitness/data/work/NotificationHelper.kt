package com.momentum.fitness.data.work

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat

object NotificationHelper {
    private const val CHANNEL_ID = "sync_channel"
    private const val CHANNEL_NAME = "Background Sync"

    fun createNotificationChannel(context: Context) {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                CHANNEL_NAME,
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notifications for background Strava sync"
            }
            
            val notificationManager = ContextCompat.getSystemService(
                context,
                NotificationManager::class.java
            )
            notificationManager?.createNotificationChannel(channel)
        }
    }

    fun createSyncNotification(context: Context, service: String = "Strava"): NotificationCompat.Builder {
        createNotificationChannel(context)
        return NotificationCompat.Builder(context, CHANNEL_ID)
            .setContentTitle("Syncing activities")
            .setContentText("Fetching activities from $service...")
            .setSmallIcon(android.R.drawable.ic_menu_upload)
            .setOngoing(true)
            .setPriority(NotificationCompat.PRIORITY_LOW)
    }
}

