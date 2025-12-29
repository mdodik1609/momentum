package com.momentum.fitness.di

import android.content.Context
import com.momentum.fitness.data.config.AppConfig
import com.momentum.fitness.data.config.SecureStorage
import com.momentum.fitness.data.config.ThemeManager
import com.momentum.fitness.data.service.RateLimiter
import com.momentum.fitness.data.util.PerformanceOptimizer
import com.momentum.fitness.data.work.SyncWorkManager
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object ConfigModule {
    @Provides
    @Singleton
    fun provideAppConfig(
        @ApplicationContext context: Context,
        secureStorage: SecureStorage
    ): AppConfig {
        return AppConfig(context, secureStorage)
    }

    @Provides
    @Singleton
    fun provideThemeManager(@ApplicationContext context: Context): ThemeManager {
        return ThemeManager(context)
    }

    @Provides
    @Singleton
    fun provideSecureStorage(@ApplicationContext context: Context): SecureStorage {
        return SecureStorage(context)
    }

    @Provides
    @Singleton
    fun provideSyncWorkManager(@ApplicationContext context: Context): SyncWorkManager {
        return SyncWorkManager(context)
    }

    @Provides
    @Singleton
    fun providePerformanceOptimizer(@ApplicationContext context: Context): PerformanceOptimizer {
        return PerformanceOptimizer(context)
    }

    @Provides
    @Singleton
    fun provideRateLimiter(): RateLimiter {
        return RateLimiter()
    }
}

