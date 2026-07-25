package com.timeboundary.app.ui.screens.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.timeboundary.app.data.model.NotificationStyle
import com.timeboundary.app.data.model.UserPreferences
import com.timeboundary.app.data.preferences.PreferenceManager
import com.timeboundary.app.data.repository.AppRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val preferenceManager = PreferenceManager(application)
    private val appRepository = AppRepository(application)

    val preferencesState: StateFlow<UserPreferences> = preferenceManager.preferencesFlow.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = UserPreferences()
    )

    fun setGlobalMonitoringEnabled(enabled: Boolean) {
        preferenceManager.setGlobalMonitoringEnabled(enabled)
    }

    fun setNotificationStyle(style: NotificationStyle) {
        preferenceManager.setNotificationStyle(style)
    }

    fun setMaxSnoozeCount(count: Int) {
        preferenceManager.setMaxSnoozeCount(count)
    }

    fun resetAllStats() {
        viewModelScope.launch {
            appRepository.clearAllStats()
        }
    }
}
