package com.timeboundary.app.data.model

enum class NotificationStyle {
    GENTLE, // High priority notification only
    STRICT  // Notification + Full Screen Overlay / Alert
}

data class UserPreferences(
    val globalMonitoringEnabled: Boolean = true,
    val notificationStyle: NotificationStyle = NotificationStyle.GENTLE,
    val maxSnoozeCount: Int = 0, // Default 0 = no snoozing allowed
    val soundEnabled: Boolean = true,
    val vibrateEnabled: Boolean = true
)
