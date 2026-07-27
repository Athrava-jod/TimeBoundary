package com.timeboundary.app.ui

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Apps
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.timeboundary.app.service.AppMonitoringService
import com.timeboundary.app.ui.screens.dashboard.DashboardScreen
import com.timeboundary.app.ui.screens.onboarding.PermissionOnboardingScreen
import com.timeboundary.app.ui.screens.picker.AppPickerScreen
import com.timeboundary.app.ui.screens.settings.SettingsScreen
import com.timeboundary.app.ui.theme.MintPrimary
import com.timeboundary.app.ui.theme.SlateBorder
import com.timeboundary.app.ui.theme.SlateCard
import com.timeboundary.app.ui.theme.TimeBoundaryTheme
import androidx.compose.ui.unit.dp
import com.timeboundary.app.ui.theme.TextMuted
import com.timeboundary.app.ui.theme.TextPrimary
import com.timeboundary.app.utils.PermissionUtils

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Dashboard)
    object Picker : Screen("picker", "App Picker", Icons.Default.Apps)
    object Settings : Screen("settings", "Settings", Icons.Default.Settings)
    object Onboarding : Screen("onboarding", "Onboarding", Icons.Default.Dashboard)
}

class MainActivity : ComponentActivity() {

    private var hasUsageAccess by mutableStateOf(false)
    private var hasNotificationPermission by mutableStateOf(false)
    private var hasExactAlarmPermission by mutableStateOf(false)
    private var hasOverlayPermission by mutableStateOf(false)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        checkPermissionsAndUpdateState()

        setContent {
            TimeBoundaryTheme {
                TimeBoundaryMainApp(
                    hasUsageAccess = hasUsageAccess,
                    hasNotificationPermission = hasNotificationPermission,
                    hasExactAlarmPermission = hasExactAlarmPermission,
                    hasOverlayPermission = hasOverlayPermission,
                    onGrantUsageAccess = { PermissionUtils.openUsageAccessSettings(this) },
                    onGrantNotification = { PermissionUtils.openAppSettings(this) },
                    onGrantExactAlarm = { PermissionUtils.openExactAlarmSettings(this) },
                    onGrantOverlay = { PermissionUtils.openOverlaySettings(this) }
                )
            }
        }
    }

    override fun onResume() {
        super.onResume()
        checkPermissionsAndUpdateState()
        if (hasUsageAccess) {
            startMonitoringService()
        }
    }

    private fun checkPermissionsAndUpdateState() {
        hasUsageAccess = PermissionUtils.hasUsageAccessPermission(this)
        hasNotificationPermission = PermissionUtils.hasNotificationPermission(this)
        hasExactAlarmPermission = PermissionUtils.hasExactAlarmPermission(this)
        hasOverlayPermission = PermissionUtils.hasOverlayPermission(this)
    }

    private fun startMonitoringService() {
        val serviceIntent = Intent(this, AppMonitoringService::class.java)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            startForegroundService(serviceIntent)
        } else {
            startService(serviceIntent)
        }
    }
}

@Composable
fun TimeBoundaryMainApp(
    hasUsageAccess: Boolean,
    hasNotificationPermission: Boolean,
    hasExactAlarmPermission: Boolean,
    hasOverlayPermission: Boolean,
    onGrantUsageAccess: () -> Unit,
    onGrantNotification: () -> Unit,
    onGrantExactAlarm: () -> Unit,
    onGrantOverlay: () -> Unit
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        Screen.Dashboard,
        Screen.Picker,
        Screen.Settings
    )

    if (!hasUsageAccess || !hasOverlayPermission) {
        PermissionOnboardingScreen(
            hasUsageAccess = hasUsageAccess,
            hasNotificationPermission = hasNotificationPermission,
            hasExactAlarmPermission = hasExactAlarmPermission,
            hasOverlayPermission = hasOverlayPermission,
            onGrantUsageAccessClick = onGrantUsageAccess,
            onGrantNotificationClick = onGrantNotification,
            onGrantAlarmClick = onGrantExactAlarm,
            onGrantOverlayClick = onGrantOverlay,
            onContinueClick = { }
        )
    } else {
        Scaffold(
            bottomBar = {
                if (currentRoute in bottomNavItems.map { it.route }) {
                    NavigationBar(
                        containerColor = SlateCard,
                        tonalElevation = 8.dp
                    ) {
                        bottomNavItems.forEach { screen ->
                            val isSelected = currentRoute == screen.route
                            NavigationBarItem(
                                selected = isSelected,
                                onClick = {
                                    navController.navigate(screen.route) {
                                        popUpTo(navController.graph.findStartDestination().id) {
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Icon(
                                        imageVector = screen.icon,
                                        contentDescription = screen.title
                                    )
                                },
                                label = { Text(screen.title) },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = MintPrimary,
                                    selectedTextColor = MintPrimary,
                                    indicatorColor = SlateBorder,
                                    unselectedIconColor = TextMuted,
                                    unselectedTextColor = TextMuted
                                )
                            )
                        }
                    }
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = navController,
                startDestination = Screen.Dashboard.route,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                composable(Screen.Dashboard.route) {
                    DashboardScreen(
                        onAddAppsClick = {
                            navController.navigate(Screen.Picker.route)
                        }
                    )
                }

                composable(Screen.Picker.route) {
                    AppPickerScreen(
                        onBackClick = {
                            navController.popBackStack()
                        }
                    )
                }

                composable(Screen.Settings.route) {
                    SettingsScreen()
                }
            }
        }
    }
}
