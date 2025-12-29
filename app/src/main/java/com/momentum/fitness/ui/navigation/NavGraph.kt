package com.momentum.fitness.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.momentum.fitness.ui.screen.dashboard.DashboardScreen
import com.momentum.fitness.ui.screen.activityfeed.ActivityFeedScreen
import com.momentum.fitness.ui.screen.activitydetail.ActivityDetailScreen
import com.momentum.fitness.ui.screen.settings.SettingsScreen
import com.momentum.fitness.ui.screen.personalrecords.PersonalRecordsScreen
import com.momentum.fitness.ui.screen.traininglog.TrainingLogScreen
import com.momentum.fitness.ui.screen.analytics.AnalyticsScreen
import com.momentum.fitness.ui.screen.fileimport.FileImportScreen

sealed class Screen(val route: String) {
    object Dashboard : Screen("dashboard")
    object ActivityFeed : Screen("activity_feed")
    object ActivityDetail : Screen("activity_detail/{activityId}") {
        fun createRoute(activityId: String) = "activity_detail/$activityId"
    }
    object Settings : Screen("settings")
    object PersonalRecords : Screen("personal_records")
    object TrainingLog : Screen("training_log")
    object Analytics : Screen("analytics")
    object FileImport : Screen("file_import")
}

@Composable
fun MomentumNavigation(
    navController: NavHostController = androidx.navigation.compose.rememberNavController(),
    onThemeChanged: ((Boolean) -> Unit)? = null,
    onSystemThemeChanged: ((Boolean) -> Unit)? = null
) {
    NavHost(
        navController = navController,
        startDestination = Screen.Dashboard.route
    ) {
        composable(Screen.Dashboard.route) {
            DashboardScreen(
                onNavigateToActivityFeed = { navController.navigate(Screen.ActivityFeed.route) },
                onNavigateToActivityDetail = { activityId ->
                    if (activityId.isNotBlank()) {
                        try {
                            navController.navigate(Screen.ActivityDetail.createRoute(activityId))
                        } catch (e: Exception) {
                            // Handle navigation error
                        }
                    }
                },
                onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                onNavigateToTrainingLog = { navController.navigate(Screen.TrainingLog.route) },
                onNavigateToFileImport = { navController.navigate(Screen.FileImport.route) }
            )
        }
        composable(Screen.ActivityFeed.route) {
            ActivityFeedScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToActivityDetail = { activityId ->
                    navController.navigate(Screen.ActivityDetail.createRoute(activityId))
                }
            )
        }
        composable(Screen.ActivityDetail.route) { backStackEntry ->
            val activityId = backStackEntry.arguments?.getString("activityId") ?: ""
            if (activityId.isNotBlank()) {
                ActivityDetailScreen(
                    activityId = activityId,
                    onNavigateBack = { navController.popBackStack() }
                )
            } else {
                // Show error or navigate back
                androidx.compose.runtime.LaunchedEffect(Unit) {
                    navController.popBackStack()
                }
            }
        }
        composable(Screen.Settings.route) {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() },
                onThemeChanged = onThemeChanged,
                onSystemThemeChanged = onSystemThemeChanged
            )
        }
        composable(Screen.PersonalRecords.route) {
            PersonalRecordsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.TrainingLog.route) {
            TrainingLogScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToActivityDetail = { activityId ->
                    navController.navigate(Screen.ActivityDetail.createRoute(activityId))
                }
            )
        }
        composable(Screen.Analytics.route) {
            AnalyticsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
        composable(Screen.FileImport.route) {
            FileImportScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}

