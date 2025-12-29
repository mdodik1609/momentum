package com.momentum.fitness.data.api.garmin

import retrofit2.http.*

/**
 * Garmin Connect API interface
 * Documentation: https://developer.garmin.com/gc-developer-program/
 * 
 * Note: Garmin uses OAuth 2.0 PKCE flow for authentication
 * This is a simplified implementation - full PKCE flow requires additional steps
 */
interface GarminApi {
    /**
     * Exchange authorization code for access token (OAuth 2.0 PKCE)
     */
    @POST("oauth/token")
    suspend fun exchangeToken(
        @Query("client_id") clientId: String,
        @Query("code") code: String,
        @Query("code_verifier") codeVerifier: String,
        @Query("grant_type") grantType: String = "authorization_code",
        @Query("redirect_uri") redirectUri: String
    ): GarminTokenResponse

    /**
     * Refresh access token
     */
    @POST("oauth/token")
    suspend fun refreshToken(
        @Query("client_id") clientId: String,
        @Query("refresh_token") refreshToken: String,
        @Query("grant_type") grantType: String = "refresh_token"
    ): GarminTokenResponse

    /**
     * Get user profile
     */
    @GET("userProfile")
    suspend fun getUserProfile(@Header("Authorization") authorization: String): GarminUserProfile

    /**
     * Get activities list
     * @param start Start date (Unix timestamp in seconds)
     * @param limit Maximum number of activities to return
     */
    @GET("activities")
    suspend fun getActivities(
        @Header("Authorization") authorization: String,
        @Query("start") start: Long? = null,
        @Query("limit") limit: Int = 100
    ): List<GarminActivity>

    /**
     * Get activity details
     */
    @GET("activities/{activityId}")
    suspend fun getActivity(
        @Header("Authorization") authorization: String,
        @Path("activityId") activityId: Long
    ): GarminActivityDetail

    /**
     * Get activity streams (GPS, HR, power, etc.)
     */
    @GET("activities/{activityId}/streams")
    suspend fun getActivityStreams(
        @Header("Authorization") authorization: String,
        @Path("activityId") activityId: Long,
        @Query("types") types: String = "GPS,HEARTRATE,CADENCE,POWER,SPEED,ALTITUDE"
    ): Map<String, GarminStream>

    /**
     * Upload activity (FIT, GPX, or TCX file)
     */
    @POST("upload")
    @Multipart
    suspend fun uploadActivity(
        @Header("Authorization") authorization: String,
        @Part("file") file: okhttp3.RequestBody,
        @Part("dataType") dataType: String = "FIT"
    ): GarminUploadResponse
}

data class GarminTokenResponse(
    val access_token: String,
    val token_type: String,
    val refresh_token: String,
    val expires_in: Int
)

data class GarminUserProfile(
    val userId: Long,
    val displayName: String,
    val email: String?
)

data class GarminActivity(
    val activityId: Long,
    val activityName: String,
    val activityType: GarminActivityType,
    val startTimeLocal: String, // ISO 8601 format
    val startTimeGMT: String,
    val distance: Double?, // meters
    val duration: Double?, // seconds
    val elapsedDuration: Double?, // seconds
    val movingDuration: Double?, // seconds
    val elevationGain: Double?, // meters
    val elevationLoss: Double?, // meters
    val averageSpeed: Double?, // m/s
    val maxSpeed: Double?, // m/s
    val averageHR: Int?,
    val maxHR: Int?,
    val averageCadence: Double?,
    val maxCadence: Double?,
    val averagePower: Double?,
    val maxPower: Double?,
    val calories: Int?,
    val trainingEffect: Double?,
    val anaerobicTrainingEffect: Double?
)

data class GarminActivityType(
    val typeId: Int,
    val typeKey: String,
    val parentTypeId: Int?,
    val isHidden: Boolean
)

data class GarminActivityDetail(
    val activityId: Long,
    val activityName: String,
    val activityType: GarminActivityType,
    val startTimeLocal: String,
    val startTimeGMT: String,
    val distance: Double?,
    val duration: Double?,
    val elapsedDuration: Double?,
    val movingDuration: Double?,
    val elevationGain: Double?,
    val elevationLoss: Double?,
    val averageSpeed: Double?,
    val maxSpeed: Double?,
    val averageHR: Int?,
    val maxHR: Int?,
    val averageCadence: Double?,
    val maxCadence: Double?,
    val averagePower: Double?,
    val maxPower: Double?,
    val calories: Int?,
    val description: String?
)

data class GarminStream(
    val metricType: String,
    val values: List<Double>
)

data class GarminUploadResponse(
    val uploadId: Long,
    val status: String,
    val activityId: Long?
)







