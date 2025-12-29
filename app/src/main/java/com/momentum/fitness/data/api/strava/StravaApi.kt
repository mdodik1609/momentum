package com.momentum.fitness.data.api.strava

import retrofit2.http.*

/**
 * Strava API v3 interface
 * Documentation: https://developers.strava.com/docs/reference/
 */
interface StravaApi {
    @POST("oauth/token")
    suspend fun exchangeToken(
        @Query("client_id") clientId: String,
        @Query("client_secret") clientSecret: String,
        @Query("code") code: String,
        @Query("grant_type") grantType: String = "authorization_code"
    ): StravaTokenResponse

    @POST("oauth/token")
    suspend fun refreshToken(
        @Query("client_id") clientId: String,
        @Query("client_secret") clientSecret: String,
        @Query("refresh_token") refreshToken: String,
        @Query("grant_type") grantType: String = "refresh_token"
    ): StravaTokenResponse

    @GET("athlete")
    suspend fun getAthlete(@Header("Authorization") authorization: String): StravaAthlete

    @GET("athlete/activities")
    suspend fun getActivities(
        @Header("Authorization") authorization: String,
        @Query("before") before: Long? = null,
        @Query("after") after: Long? = null,
        @Query("page") page: Int = 1,
        @Query("per_page") perPage: Int = 200
    ): List<StravaActivity>

    @GET("activities/{id}")
    suspend fun getActivity(
        @Header("Authorization") authorization: String,
        @Path("id") id: Long
    ): StravaActivity

    @GET("activities/{id}/streams")
    suspend fun getActivityStreams(
        @Header("Authorization") authorization: String,
        @Path("id") id: Long,
        @Query("keys") keys: String = "time,latlng,altitude,heartrate,cadence,watts,velocity_smooth,grade_smooth",
        @Query("key_by_type") keyByType: Boolean = true
    ): Map<String, StravaStream>
}

data class StravaTokenResponse(
    val token_type: String,
    val expires_at: Long,
    val expires_in: Int,
    val refresh_token: String,
    val access_token: String,
    val athlete: StravaAthlete
)

data class StravaAthlete(
    val id: Long,
    val username: String?,
    val firstname: String,
    val lastname: String
)

data class StravaActivity(
    val id: Long,
    val name: String,
    val type: String,
    val start_date: String,
    val timezone: String?,
    val moving_time: Int,
    val elapsed_time: Int,
    val distance: Double?,
    val total_elevation_gain: Double?,
    val average_speed: Double?,
    val max_speed: Double?,
    val average_heartrate: Double?,
    val max_heartrate: Double?,
    val average_cadence: Double?,
    val average_watts: Double?,
    val max_watts: Double?,
    val kilojoules: Double?,
    val description: String?
)

data class StravaStream(
    val type: String,
    val data: List<Any>,
    val series_type: String,
    val original_size: Int,
    val resolution: String?
)

