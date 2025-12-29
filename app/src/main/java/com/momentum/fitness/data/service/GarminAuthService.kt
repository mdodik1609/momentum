package com.momentum.fitness.data.service

import android.content.Context
import android.net.Uri
import com.momentum.fitness.data.api.garmin.GarminApi
import com.momentum.fitness.data.config.AppConfig
import com.momentum.fitness.data.repository.UserSettingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.security.MessageDigest
import java.util.Base64
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Garmin OAuth 2.0 PKCE Authentication Service
 * Garmin uses PKCE (Proof Key for Code Exchange) for enhanced security
 */
@Singleton
class GarminAuthService @Inject constructor(
    private val garminApi: GarminApi,
    private val userSettingsRepository: UserSettingsRepository,
    private val appConfig: AppConfig
) {
    companion object {
        const val GARMIN_REDIRECT_URI = "momentum://garmin"
        const val GARMIN_AUTH_URL = "https://connect.garmin.com/oauthConfirm"
        const val GARMIN_BASE_URL = "https://connectapi.garmin.com"

        /**
         * Generate PKCE code verifier and challenge
         * PKCE is required for Garmin OAuth
         */
        fun generatePKCE(): PKCEPair {
            // Generate random code verifier (43-128 characters)
            val codeVerifier = generateRandomString(64)
            
            // Generate code challenge (SHA256 hash, base64url encoded)
            val digest = MessageDigest.getInstance("SHA-256")
            val hash = digest.digest(codeVerifier.toByteArray())
            val codeChallenge = Base64.getUrlEncoder().withoutPadding().encodeToString(hash)
            
            return PKCEPair(codeVerifier, codeChallenge)
        }

        private fun generateRandomString(length: Int): String {
            val charset = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789-._~"
            return (1..length)
                .map { charset.random() }
                .joinToString("")
        }
    }

    private val clientId: String?
        get() = appConfig.garminClientId

    private val clientSecret: String?
        get() = appConfig.garminClientSecret

    /**
     * Get authorization URL for Garmin OAuth with PKCE
     */
    fun getAuthorizationUrl(codeChallenge: String): String {
        val clientId = clientId ?: throw IllegalStateException("Garmin client ID not configured")
        return "$GARMIN_AUTH_URL?" +
                "client_id=$clientId&" +
                "response_type=code&" +
                "redirect_uri=$GARMIN_REDIRECT_URI&" +
                "code_challenge=$codeChallenge&" +
                "code_challenge_method=S256&" +
                "scope=activity"
    }

    /**
     * Exchange authorization code for access token (PKCE flow)
     */
    suspend fun exchangeCodeForToken(
        code: String,
        codeVerifier: String
    ): kotlin.Result<com.momentum.fitness.data.api.garmin.GarminTokenResponse> {
        return withContext(Dispatchers.IO) {
            try {
                val clientId = clientId ?: return@withContext kotlin.Result.failure(
                    Exception("Garmin client ID not configured")
                )

                val response = garminApi.exchangeToken(
                    clientId = clientId,
                    code = code,
                    codeVerifier = codeVerifier,
                    redirectUri = GARMIN_REDIRECT_URI
                )

                // Save tokens to settings
                val settings = userSettingsRepository.getSettingsSync()
                userSettingsRepository.updateSettings(
                    settings.copy(
                        garminAccessToken = response.access_token,
                        garminRefreshToken = response.refresh_token,
                        garminTokenExpiresAt = System.currentTimeMillis() / 1000 + response.expires_in,
                        garminUserId = null // Will be fetched separately
                    )
                )

                kotlin.Result.success(response)
            } catch (e: Exception) {
                kotlin.Result.failure(e)
            }
        }
    }

    /**
     * Refresh access token
     */
    suspend fun refreshAccessToken(): kotlin.Result<String> {
        return withContext(Dispatchers.IO) {
            try {
                val clientId = clientId ?: return@withContext kotlin.Result.failure(
                    Exception("Garmin client ID not configured")
                )

                val settings = userSettingsRepository.getSettingsSync()
                val refreshToken = settings.garminRefreshToken
                    ?: return@withContext kotlin.Result.failure(Exception("No refresh token available"))

                val response = garminApi.refreshToken(
                    clientId = clientId,
                    refreshToken = refreshToken
                )

                // Update tokens
                userSettingsRepository.updateSettings(
                    settings.copy(
                        garminAccessToken = response.access_token,
                        garminRefreshToken = response.refresh_token,
                        garminTokenExpiresAt = System.currentTimeMillis() / 1000 + response.expires_in
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
        val accessToken = settings.garminAccessToken
            ?: return kotlin.Result.failure(Exception("Not authenticated with Garmin"))

        val expiresAt = settings.garminTokenExpiresAt
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
        return if (uri.scheme == "momentum" && uri.host == "garmin") {
            uri.getQueryParameter("code")
        } else {
            null
        }
    }
}

data class PKCEPair(
    val codeVerifier: String,
    val codeChallenge: String
)

