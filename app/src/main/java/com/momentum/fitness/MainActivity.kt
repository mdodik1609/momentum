package com.momentum.fitness

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.momentum.fitness.data.service.StravaAuthService
import com.momentum.fitness.ui.navigation.MomentumNavigation
import com.momentum.fitness.ui.theme.MomentumTheme
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var stravaAuthService: StravaAuthService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        
        // Handle Strava OAuth callback
        handleStravaCallback(intent)
        
        setContent {
            val themeManager = try {
                (application as? MomentumApplication)?.themeManager
            } catch (e: Exception) {
                null
            }
            val isDarkMode = remember { mutableStateOf(themeManager?.isDarkMode ?: false) }
            val useSystemTheme = remember { mutableStateOf(themeManager?.useSystemTheme ?: true) }
            
            MomentumTheme(
                darkTheme = if (useSystemTheme.value) {
                    androidx.compose.foundation.isSystemInDarkTheme()
                } else {
                    isDarkMode.value
                }
            ) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    com.momentum.fitness.ui.navigation.MainScaffold(
                        onThemeChanged = { dark ->
                            isDarkMode.value = dark
                            themeManager?.isDarkMode = dark
                        },
                        onSystemThemeChanged = { useSystem ->
                            useSystemTheme.value = useSystem
                            themeManager?.useSystemTheme = useSystem
                        }
                    )
                }
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        handleStravaCallback(intent)
        handleGarminCallback(intent)
    }

    private fun handleStravaCallback(intent: Intent?) {
        intent?.data?.let { uri ->
            stravaAuthService.extractCodeFromUri(uri)?.let { code ->
                CoroutineScope(Dispatchers.Main).launch {
                    val result = stravaAuthService.exchangeCodeForToken(code)
                    result.onSuccess {
                        // Success - token saved, schedule sync
                        try {
                            (application as? MomentumApplication)?.syncWorkManager?.scheduleStravaSync()
                        } catch (e: Exception) {
                            android.util.Log.e("Momentum", "Failed to schedule sync: ${e.message}")
                        }
                    }.onFailure { error ->
                        // Error - could show toast or snackbar
                        android.util.Log.e("Momentum", "Strava auth failed: ${error.message}")
                    }
                }
            }
        }
    }

    private fun handleGarminCallback(intent: Intent?) {
        intent?.data?.let { uri ->
            try {
                // Note: Garmin PKCE requires storing code_verifier
                // This is simplified - in production, store code_verifier securely
                val garminAuthService = (application as? MomentumApplication)?.garminAuthService
                garminAuthService?.extractCodeFromUri(uri)?.let { code ->
                    CoroutineScope(Dispatchers.Main).launch {
                        // TODO: Retrieve stored code_verifier
                        // For now, this is a placeholder
                        android.util.Log.d("Momentum", "Garmin code received: $code")
                        // val result = garminAuthService.exchangeCodeForToken(code, codeVerifier)
                    }
                }
            } catch (e: Exception) {
                android.util.Log.e("Momentum", "Garmin callback error: ${e.message}")
            }
        }
    }
}

