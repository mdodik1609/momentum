package com.momentum.fitness.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import androidx.room.migration.Migration
import com.momentum.fitness.data.database.dao.*
import com.momentum.fitness.data.database.entity.*
import com.momentum.fitness.data.database.converter.InstantConverter
import com.momentum.fitness.data.database.converter.SportTypeConverter
import com.momentum.fitness.data.database.DatabaseMigrations

@Database(
    entities = [
        ActivityEntity::class,
        ActivityStreamEntity::class,
        PersonalRecordEntity::class,
        UserSettingsEntity::class
    ],
    version = 2,
    exportSchema = false
)
// Note: Indices are defined in entity classes using @Entity(indices = [...])
@TypeConverters(InstantConverter::class, SportTypeConverter::class)
abstract class MomentumDatabase : RoomDatabase() {
    companion object {
        val MIGRATIONS = arrayOf<Migration>(
            DatabaseMigrations.MIGRATION_1_2
        )
    }
    abstract fun activityDao(): ActivityDao
    abstract fun activityStreamDao(): ActivityStreamDao
    abstract fun personalRecordDao(): PersonalRecordDao
    abstract fun userSettingsDao(): UserSettingsDao
}

