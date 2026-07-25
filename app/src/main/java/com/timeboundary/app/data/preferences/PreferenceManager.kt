package com.timeboundary.app.data.preferences

import android.content.Context
import android.content.SharedPreferences
import com.timeboundary.app.data.model.NotificationStyle
import com.timeboundary.app.data.model.UserPreferences
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class PreferenceManager(context: Context) {

    private val prefs: SharedPreferences =
        context.getSharedPreferences("timeboundary_prefs", Context.MODE_PRIVATE)

    private val _preferencesFlow = MutableStateFlow(loadPreferences())
    val preferencesFlow: StateFlow<UserPreferences> = _preferencesFlow.asStateFlow()

    private fun loadPreferences(): UserPreferences {
        val globalMonitoring = prefs.getBoolean(KEY_GLOBAL_MONITORING, true)
        val styleString = prefs.getString(KEY_NOTIFICATION_STYLE, NotificationStyle.GENTLE.name)
        val style = try {
            NotificationStyle.valueOf(styleString ?: NotificationStyle.GENTLE.name)
        } catch (e: Exception) {
            NotificationStyle.GENTLE
        }
        val maxSnooze = prefs.getInt(KEY_MAX_SNOOZE, 0)
        val sound = prefs.getBoolean(KEY_SOUND_ENABLED, true)
        val vibrate = prefs.getBoolean(KEY_VIBRATE_ENABLED, true)

        return UserPreferences(
            globalMonitoringEnabled = globalMonitoring,
            notificationStyle = style,
            maxSnoozeCount = maxSnooze,
            soundEnabled = sound,
            vibrateEnabled = vibrate
        )
    }

    fun setGlobalMonitoringEnabled(enabled: Boolean) {
        prefs.edit().putBoolean(KEY_GLOBAL_MONITORING, enabled).apply()
        _preferencesFlow.value = loadPreferences()
    }

    fun setNotificationStyle(style: NotificationStyle) {
        prefs.edit().putString(KEY_NOTIFICATION_STYLE, style.name).apply()
        _preferencesFlow.value = loadPreferences()
    }

    fun setMaxSnoozeCount(count: Int) {
        prefs.edit().putInt(KEY_MAX_SNOOZE, count).apply()
        _preferencesFlow.value = loadPreferences()
    }

    fun getSnoozeCountForSession(sessionId: String): Int {
        return prefs.getInt("snooze_$sessionId", 0)
    }

    fun incrementSnoozeCountForSession(sessionId: String): Int {
        val current = getSnoozeCountForSession(sessionId)
        val updated = current + 1
        prefs.edit().putInt("snooze_$sessionId", updated).apply()
        return updated
    }

    fun resetSnoozeCountForSession(sessionId: String) {
        prefs.edit().remove("snooze_$sessionId").apply()
    }

    companion object {
        private const val KEY_GLOBAL_MONITORING = "global_monitoring_enabled"
        private const val KEY_NOTIFICATION_STYLE = "notification_style"
        private const val KEY_MAX_SNOOZE = "max_snooze_count"
        private const val KEY_SOUND_ENABLED = "sound_enabled"
        private const val KEY_VIBRATE_ENABLED = "vibrate_enabled"
    }
}
