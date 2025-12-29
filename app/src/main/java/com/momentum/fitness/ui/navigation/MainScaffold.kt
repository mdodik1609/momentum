package com.momentum.fitness.ui.navigation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.momentum.fitness.ui.screen.dashboard.DashboardScreen
import com.momentum.fitness.ui.screen.activityfeed.ActivityFeedScreen
import com.momentum.fitness.ui.screen.activitydetail.ActivityDetailScreen
import com.momentum.fitness.ui.screen.settings.SettingsScreen
import com.momentum.fitness.ui.screen.personalrecords.PersonalRecordsScreen
import com.momentum.fitness.ui.screen.traininglog.TrainingLogScreen
import com.momentum.fitness.ui.screen.analytics.AnalyticsScreen
import com.momentum.fitness.ui.screen.fileimport.FileImportScreen

sealed class Screen(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
    object ActivityFeed : Screen("activity_feed", "Activities", Icons.Default.List)
    object TrainingLog : Screen("training_log", "Training", Icons.Default.CalendarToday)
    object Analytics : Screen("analytics", "Analytics", Icons.Default.Analytics)
    object Profile : Screen("profile", "Profile", Icons.Default.Person)
    
    // Secondary screens (no bottom nav)
    object ActivityDetail : Screen("activity_detail/{activityId}", "Activity", Icons.Default.Info) {
        fun createRoute(activityId: String) = "activity_detail/$activityId"
    }
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object PersonalRecords : Screen("personal_records", "Personal Records", Icons.Default.EmojiEvents)
    object FileImport : Screen("file_import", "Import", Icons.Default.Upload)
}

val bottomNavScreens = listOf(
    Screen.Dashboard,
    Screen.ActivityFeed,
    Screen.TrainingLog,
    Screen.Analytics,
    Screen.Profile
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScaffold(
    onThemeChanged: ((Boolean) -> Unit)? = null,
    onSystemThemeChanged: ((Boolean) -> Unit)? = null
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    
    val showBottomNav = bottomNavScreens.any { screen ->
        currentDestination?.hierarchy?.any { it.route == screen.route } == true
    }
    
    Scaffold(
        bottomBar = {
            if (showBottomNav) {
                NavigationBar {
                    bottomNavScreens.forEach { screen ->
                        NavigationBarItem(
                            icon = { Icon(screen.icon, contentDescription = screen.title) },
                            label = { Text(screen.title) },
                            selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true,
                            onClick = {
                                navController.navigate(screen.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { paddingValues ->
        NavHost(
            navController = navController,
            startDestination = Screen.Dashboard.route,
            modifier = Modifier.padding(paddingValues)
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
                        if (activityId.isNotBlank()) {
                            try {
                                navController.navigate(Screen.ActivityDetail.createRoute(activityId))
                            } catch (e: Exception) {
                                // Handle navigation error
                            }
                        }
                    }
                )
            }
            composable(Screen.TrainingLog.route) {
                TrainingLogScreen(
                    onNavigateBack = { navController.popBackStack() },
                    onNavigateToActivityDetail = { activityId ->
                        if (activityId.isNotBlank()) {
                            try {
                                navController.navigate(Screen.ActivityDetail.createRoute(activityId))
                            } catch (e: Exception) {
                                // Handle navigation error
                            }
                        }
                    }
                )
            }
            composable(Screen.Analytics.route) {
                AnalyticsScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onNavigateToSettings = { navController.navigate(Screen.Settings.route) },
                    onNavigateToPersonalRecords = { navController.navigate(Screen.PersonalRecords.route) },
                    onNavigateToAnalytics = { navController.navigate(Screen.Analytics.route) }
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
            composable(Screen.FileImport.route) {
                FileImportScreen(
                    onNavigateBack = { navController.popBackStack() }
                )
            }
        }
    }
}

// Profile screen placeholder
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    onNavigateToSettings: () -> Unit,
    onNavigateToPersonalRecords: () -> Unit,
    onNavigateToAnalytics: () -> Unit
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile") },
                actions = {
                    IconButton(onClick = onNavigateToSettings) {
                        Icon(Icons.Default.Settings, contentDescription = "Settings")
                    }
                }
            )
        }
    ) { padding ->
        androidx.compose.foundation.layout.Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            // Profile content will be added
            Text("Profile Screen")
        }
    }
}

