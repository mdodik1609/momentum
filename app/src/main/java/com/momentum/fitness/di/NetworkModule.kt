package com.momentum.fitness.di

import com.momentum.fitness.data.api.garmin.GarminApi
import com.momentum.fitness.data.api.strava.StravaApi
import com.momentum.fitness.data.network.RetryInterceptor
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Qualifier
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {
    private const val STRAVA_BASE_URL = "https://www.strava.com/api/v3/"
    private const val GARMIN_BASE_URL = "https://connectapi.garmin.com/"

    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = HttpLoggingInterceptor.Level.BODY
        }

        val retryInterceptor = RetryInterceptor(
            maxRetries = 3,
            baseDelayMs = 1000
        )

        return OkHttpClient.Builder()
            .addInterceptor(retryInterceptor)
            .addInterceptor(loggingInterceptor)
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build()
    }

    @Provides
    @Singleton
    @StravaRetrofit
    fun provideStravaRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(STRAVA_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    @GarminRetrofit
    fun provideGarminRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(GARMIN_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    @Provides
    @Singleton
    fun provideStravaApi(@StravaRetrofit retrofit: Retrofit): StravaApi {
        return retrofit.create(StravaApi::class.java)
    }

    @Provides
    @Singleton
    fun provideGarminApi(@GarminRetrofit retrofit: Retrofit): GarminApi {
        return retrofit.create(GarminApi::class.java)
    }
}

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class StravaRetrofit

@Qualifier
@Retention(AnnotationRetention.BINARY)
annotation class GarminRetrofit
