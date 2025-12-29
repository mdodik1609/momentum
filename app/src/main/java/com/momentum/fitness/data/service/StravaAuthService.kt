package com.momentum.fitness.data.service

import android.content.Context
import android.content.Intent
import android.net.Uri
import com.momentum.fitness.data.api.strava.StravaApi
import com.momentum.fitness.data.api.strava.StravaTokenResponse
import com.momentum.fitness.data.repository.UserSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Strava OAuth2 Authentication Service
 * Handles the OAuth2 flow for Strava API
 */
@Singleton
class StravaAuthService @Inject constructor(
    private val stravaApi: StravaApi,
    private val userSettingsRepository: UserSettingsRepository,
    private val appConfig: com.momentum.fitness.data.config.AppConfig
) {
    companion object {
        const val STRAVA_REDIRECT_URI = "momentum://strava"
        const val STRAVA_AUTH_URL = "https://www.strava.com/oauth/authorize"
    }
    
    private val clientId: String?
        get() = appConfig.stravaClientId
    
    private val clientSecret: String?
        get() = appConfig.stravaClientSecret

    /**
     * Get the authorization URL for Strava OAuth
     */
    fun getAuthorizationUrl(): String {
        val clientId = clientId ?: throw IllegalStateException("Strava client ID not configured")
        return "$STRAVA_AUTH_URL?" +
                "client_id=$clientId&" +
                "redirect_uri=$STRAVA_REDIRECT_URI&" +
                "response_type=code&" +
                "scope=activity:read_all,read"
    }

    /**
     * Launch Strava OAuth in browser
     */
    fun launchOAuthFlow(context: Context) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(getAuthorizationUrl()))
        context.startActivity(intent)
    }

    /**
     * Exchange authorization code for access token
     */
    suspend fun exchangeCodeForToken(code: String): kotlin.Result<StravaTokenResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val clientIdValue = clientId ?: return@withContext kotlin.Result.failure(
                    Exception("Strava client ID not configured")
                )
                val clientSecretValue = clientSecret ?: return@withContext kotlin.Result.failure(
                    Exception("Strava client secret not configured")
                )
                
                val response = stravaApi.exchangeToken(
                    clientId = clientIdValue,
                    clientSecret = clientSecretValue,
                    code = code
                )
                
                // Save tokens to settings
                val settings = userSettingsRepository.getSettingsSync()
                userSettingsRepository.updateSettings(
                    settings.copy(
                        stravaAccessToken = response.access_token,
                        stravaRefreshToken = response.refresh_token,
                        stravaTokenExpiresAt = response.expires_at,
                        stravaAthleteId = response.athlete.id.toString()
                    )
                )
                
                kotlin.Result.success(response)
            } catch (e: Exception) {
                kotlin.Result.failure(e)
            }
        }
    }

    /**
     * Refresh access token using refresh token
     */
    suspend fun refreshAccessToken(): kotlin.Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val clientIdValue = clientId ?: return@withContext kotlin.Result.failure(
                    Exception("Strava client ID not configured")
                )
                val clientSecretValue = clientSecret ?: return@withContext kotlin.Result.failure(
                    Exception("Strava client secret not configured")
                )
                
                val settings = userSettingsRepository.getSettingsSync()
                val refreshToken = settings.stravaRefreshToken
                    ?: return@withContext kotlin.Result.failure(Exception("No refresh token available"))

                val response = stravaApi.refreshToken(
                    clientId = clientIdValue,
                    clientSecret = clientSecretValue,
                    refreshToken = refreshToken
                )

                // Update tokens
                userSettingsRepository.updateSettings(
                    settings.copy(
                        stravaAccessToken = response.access_token,
                        stravaRefreshToken = response.refresh_token,
                        stravaTokenExpiresAt = response.expires_at
                    )
                )

                kotlin.Result.success(response.access_token)
            } catch (e: Exception) {
                kotlin.Result.failure(e)
            }
        }
    }

    /**
     * Get valid access token (refresh if needed)
     */
    suspend fun getValidAccessToken(): kotlin.Result<String> {
        val settings = userSettingsRepository.getSettingsSync()
        val accessToken = settings.stravaAccessToken
            ?: return kotlin.Result.failure(Exception("Not authenticated with Strava"))

        val expiresAt = settings.stravaTokenExpiresAt
            ?: return kotlin.Result.failure(Exception("Token expiration unknown"))

        // Refresh if token expires in less than 1 hour
        val now = System.currentTimeMillis() / 1000
        if (expiresAt - now < 3600) {
            return refreshAccessToken()
        }

        return kotlin.Result.success(accessToken)
    }

    /**
     * Extract authorization code from callback URI
     */
    fun extractCodeFromUri(uri: Uri): String? {
        return if (uri.scheme == "momentum" && uri.host == "strava") {
            uri.getQueryParameter("code")
        } else {
            null
        }
    }
}

