package com.momentum.fitness.di

import android.content.Context
import androidx.room.Room
import com.momentum.fitness.data.database.MomentumDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun provideDatabase(@ApplicationContext context: Context): MomentumDatabase {
        return Room.databaseBuilder(
            context,
            MomentumDatabase::class.java,
            "momentum_database"
        )
        .addMigrations(*com.momentum.fitness.data.database.MomentumDatabase.Companion.MIGRATIONS)
        .build()
    }

    @Provides
    fun provideActivityDao(database: MomentumDatabase) = database.activityDao()

    @Provides
    fun provideActivityStreamDao(database: MomentumDatabase) = database.activityStreamDao()

    @Provides
    fun providePersonalRecordDao(database: MomentumDatabase) = database.personalRecordDao()

    @Provides
    fun provideUserSettingsDao(database: MomentumDatabase) = database.userSettingsDao()
}

