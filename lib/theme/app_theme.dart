import 'package:flutter/material.dart';

// Strava color palette - exact match
class AppColors {
  static const Color stravaOrange = Color(0xFFFC4C02); // Primary Strava orange
  static const Color stravaOrangeDark = Color(0xFFE03D00); // Darker for pressed states
  static const Color stravaOrangeLight = Color(0xFFFF6B35); // Lighter accent
  static const Color stravaBlack = Color(0xFF000000); // Primary text
  static const Color stravaGray = Color(0xFF666666); // Secondary text
  static const Color stravaLightGray = Color(0xFFE5E5E5); // Borders, dividers
  static const Color stravaBackground = Color(0xFFFAFAFA); // Main background
  static const Color stravaWhite = Color(0xFFFFFFFF); // Cards, surfaces
  static const Color stravaRed = Color(0xFFE63946); // Error states
  static const Color stravaGreen = Color(0xFF06A77D); // Success states
  static const Color stravaCardBackground = Color(0xFFFFFFFF); // Card background
}

class AppTheme {
  static ThemeData get lightTheme {
    return ThemeData(
      useMaterial3: true,
      colorScheme: ColorScheme.light(
        primary: AppColors.stravaOrange,
        onPrimary: AppColors.stravaWhite,
        secondary: AppColors.stravaGray,
        onSecondary: AppColors.stravaWhite,
        error: AppColors.stravaRed,
        onError: AppColors.stravaWhite,
        background: AppColors.stravaBackground,
        onBackground: AppColors.stravaBlack,
        surface: AppColors.stravaWhite,
        onSurface: AppColors.stravaBlack,
        surfaceVariant: AppColors.stravaLightGray,
        onSurfaceVariant: AppColors.stravaGray,
      ),
      scaffoldBackgroundColor: AppColors.stravaBackground,
      appBarTheme: const AppBarTheme(
        backgroundColor: AppColors.stravaWhite,
        foregroundColor: AppColors.stravaBlack,
        elevation: 0,
        centerTitle: false,
      ),
      bottomNavigationBarTheme: const BottomNavigationBarThemeData(
        backgroundColor: AppColors.stravaWhite,
        selectedItemColor: AppColors.stravaOrange,
        unselectedItemColor: AppColors.stravaGray,
        type: BottomNavigationBarType.fixed,
        elevation: 0, // Strava uses flat design
        showUnselectedLabels: true,
      ),
      cardTheme: CardThemeData(
        color: AppColors.stravaWhite,
        elevation: 2,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      ),
    );
  }

  static ThemeData get darkTheme {
    return ThemeData(
      useMaterial3: true,
      colorScheme: ColorScheme.dark(
        primary: AppColors.stravaOrange,
        onPrimary: AppColors.stravaBlack,
        secondary: AppColors.stravaGray,
        onSecondary: AppColors.stravaWhite,
        error: AppColors.stravaRed,
        onError: AppColors.stravaWhite,
        background: AppColors.stravaBlack,
        onBackground: AppColors.stravaWhite,
        surface: const Color(0xFF1A1A1A),
        onSurface: AppColors.stravaWhite,
        surfaceVariant: const Color(0xFF2A2A2A),
        onSurfaceVariant: AppColors.stravaGray,
      ),
      scaffoldBackgroundColor: AppColors.stravaBlack,
      appBarTheme: const AppBarTheme(
        backgroundColor: Color(0xFF1A1A1A),
        foregroundColor: AppColors.stravaWhite,
        elevation: 0,
        centerTitle: false,
      ),
      bottomNavigationBarTheme: const BottomNavigationBarThemeData(
        backgroundColor: Color(0xFF1A1A1A),
        selectedItemColor: AppColors.stravaOrange,
        unselectedItemColor: AppColors.stravaGray,
        type: BottomNavigationBarType.fixed,
        elevation: 8,
      ),
      cardTheme: CardThemeData(
        color: const Color(0xFF1A1A1A),
        elevation: 2,
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(12)),
      ),
    );
  }
}
