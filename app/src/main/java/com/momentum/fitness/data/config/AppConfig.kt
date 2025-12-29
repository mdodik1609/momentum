package com.momentum.fitness.data.config

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

/**
 * App configuration manager
 * Handles secure storage of API keys and configuration
 * Now uses SecureStorage for credentials (Android Keystore)
 */
@Singleton
class AppConfig @Inject constructor(
    private val context: Context,
    private val secureStorage: SecureStorage
) {
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences("momentum_config", Context.MODE_PRIVATE)
    }

    var stravaClientId: String?
        get() = secureStorage.getStravaClientId()
        set(value) {
            if (value != null) {
                secureStorage.saveStravaClientId(value)
            }
        }

    var stravaClientSecret: String?
        get() = secureStorage.getStravaClientSecret()
        set(value) {
            if (value != null) {
                secureStorage.saveStravaClientSecret(value)
            }
        }

    fun hasStravaCredentials(): Boolean {
        return secureStorage.hasStravaCredentials()
    }

    fun clearStravaCredentials() {
        secureStorage.clearStravaCredentials()
    }

    var garminClientId: String?
        get() = secureStorage.getGarminClientId()
        set(value) {
            if (value != null) {
                secureStorage.saveGarminClientId(value)
            }
        }

    var garminClientSecret: String?
        get() = secureStorage.getGarminClientSecret()
        set(value) {
            if (value != null) {
                secureStorage.saveGarminClientSecret(value)
            }
        }

    fun hasGarminCredentials(): Boolean {
        return secureStorage.hasGarminCredentials()
    }

    fun clearGarminCredentials() {
        secureStorage.clearGarminCredentials()
    }
}

