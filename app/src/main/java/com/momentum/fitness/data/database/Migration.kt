package com.momentum.fitness.data.database

import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

/**
 * Database migrations
 */
object DatabaseMigrations {
    val MIGRATION_1_2 = object : Migration(1, 2) {
        override fun migrate(database: SupportSQLiteDatabase) {
            // Add Garmin and sync timestamp fields
            database.execSQL("ALTER TABLE user_settings ADD COLUMN stravaLastSyncTimestamp INTEGER")
            database.execSQL("ALTER TABLE user_settings ADD COLUMN garminAccessToken TEXT")
            database.execSQL("ALTER TABLE user_settings ADD COLUMN garminRefreshToken TEXT")
            database.execSQL("ALTER TABLE user_settings ADD COLUMN garminTokenExpiresAt INTEGER")
            database.execSQL("ALTER TABLE user_settings ADD COLUMN garminUserId INTEGER")
            database.execSQL("ALTER TABLE user_settings ADD COLUMN garminLastSyncTimestamp INTEGER")
        }
    }
}







