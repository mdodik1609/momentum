package com.momentum.fitness.data.config

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Secure storage using Android Keystore and EncryptedSharedPreferences
 * Provides hardware-backed encryption for sensitive data
 */
@Singleton
class SecureStorage @Inject constructor(
    private val context: Context
) {
    private val masterKey = MasterKey.Builder(context)
        .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
        .build()

    private val encryptedPrefs: SharedPreferences by lazy {
        EncryptedSharedPreferences.create(
            context,
            "momentum_secure_prefs",
            masterKey,
            EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
            EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
        )
    }

    fun saveStravaClientId(clientId: String) {
        encryptedPrefs.edit().putString("strava_client_id", clientId).apply()
    }

    fun getStravaClientId(): String? {
        return encryptedPrefs.getString("strava_client_id", null)
    }

    fun saveStravaClientSecret(secret: String) {
        encryptedPrefs.edit().putString("strava_client_secret", secret).apply()
    }

    fun getStravaClientSecret(): String? {
        return encryptedPrefs.getString("strava_client_secret", null)
    }

    fun clearStravaCredentials() {
        encryptedPrefs.edit()
            .remove("strava_client_id")
            .remove("strava_client_secret")
            .apply()
    }

    fun hasStravaCredentials(): Boolean {
        return !getStravaClientId().isNullOrBlank() && 
               !getStravaClientSecret().isNullOrBlank()
    }

    fun saveGarminClientId(clientId: String) {
        encryptedPrefs.edit().putString("garmin_client_id", clientId).apply()
    }

    fun getGarminClientId(): String? {
        return encryptedPrefs.getString("garmin_client_id", null)
    }

    fun saveGarminClientSecret(secret: String) {
        encryptedPrefs.edit().putString("garmin_client_secret", secret).apply()
    }

    fun getGarminClientSecret(): String? {
        return encryptedPrefs.getString("garmin_client_secret", null)
    }

    fun clearGarminCredentials() {
        encryptedPrefs.edit()
            .remove("garmin_client_id")
            .remove("garmin_client_secret")
            .apply()
    }

    fun hasGarminCredentials(): Boolean {
        return !getGarminClientId().isNullOrBlank() && 
               !getGarminClientSecret().isNullOrBlank()
    }
}


