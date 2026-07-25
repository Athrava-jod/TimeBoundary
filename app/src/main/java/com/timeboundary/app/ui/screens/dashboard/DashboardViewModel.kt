package com.timeboundary.app.ui.screens.dashboard

import android.app.Application
import android.graphics.drawable.Drawable
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.timeboundary.app.data.model.AppUsageStat
import com.timeboundary.app.data.model.MonitoredApp
import com.timeboundary.app.data.preferences.PreferenceManager
import com.timeboundary.app.data.repository.AppRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class MonitoredAppUiItem(
    val packageName: String,
    val appLabel: String,
    val icon: Drawable?,
    val durationMinutes: Int,
    val isEnabled: Boolean,
    val opensToday: Int,
    val exceededToday: Int
)

data class DashboardUiState(
    val isGlobalMonitoringEnabled: Boolean = true,
    val monitoredApps: List<MonitoredAppUiItem> = emptyList(),
    val totalOpensToday: Int = 0,
    val totalExceededToday: Int = 0
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val appRepository = AppRepository(application)
    private val preferenceManager = PreferenceManager(application)

    val uiState: StateFlow<DashboardUiState> = combine(
        appRepository.monitoredAppsFlow,
        appRepository.getTodayStatsFlow(),
        preferenceManager.preferencesFlow
    ) { apps, stats, prefs ->
        val statsMap = stats.associateBy { it.packageName }
        
        val uiItems = apps.map { app ->
            val stat = statsMap[app.packageName]
            MonitoredAppUiItem(
                packageName = app.packageName,
                appLabel = app.appLabel,
                icon = appRepository.getAppIcon(app.packageName),
                durationMinutes = app.durationMinutes,
                isEnabled = app.isEnabled,
                opensToday = stat?.openCount ?: 0,
                exceededToday = stat?.limitExceededCount ?: 0
            )
        }

        val totalOpens = stats.sumOf { it.openCount }
        val totalExceeded = stats.sumOf { it.limitExceededCount }

        DashboardUiState(
            isGlobalMonitoringEnabled = prefs.globalMonitoringEnabled,
            monitoredApps = uiItems,
            totalOpensToday = totalOpens,
            totalExceededToday = totalExceeded
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = DashboardUiState()
    )

    fun toggleGlobalMonitoring(enabled: Boolean) {
        preferenceManager.setGlobalMonitoringEnabled(enabled)
    }

    fun toggleAppMonitoring(packageName: String, enabled: Boolean) {
        viewModelScope.launch {
            appRepository.toggleMonitoredAppEnabled(packageName, enabled)
        }
    }

    fun removeMonitoredApp(packageName: String) {
        viewModelScope.launch {
            appRepository.removeMonitoredApp(packageName)
        }
    }
}
