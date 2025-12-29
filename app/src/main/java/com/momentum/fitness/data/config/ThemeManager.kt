package com.momentum.fitness.data.config

import android.content.Context
import android.content.SharedPreferences
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ThemeManager @Inject constructor(
    private val context: Context
) {
    private val prefs: SharedPreferences by lazy {
        context.getSharedPreferences("momentum_prefs", Context.MODE_PRIVATE)
    }

    var isDarkMode: Boolean
        get() = prefs.getBoolean("dark_mode", false)
        set(value) = prefs.edit().putBoolean("dark_mode", value).apply()

    var useSystemTheme: Boolean
        get() = prefs.getBoolean("use_system_theme", true)
        set(value) = prefs.edit().putBoolean("use_system_theme", value).apply()
}







