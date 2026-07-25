package com.timeboundary.app.ui.screens.picker

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.timeboundary.app.data.repository.AppRepository
import com.timeboundary.app.data.repository.InstalledAppItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class AppPickerUiState(
    val isLoading: Boolean = true,
    val installedApps: List<InstalledAppItem> = emptyList(),
    val searchQuery: String = "",
    val filteredApps: List<InstalledAppItem> = emptyList()
)

class AppPickerViewModel(application: Application) : AndroidViewModel(application) {

    private val appRepository = AppRepository(application)

    private val _uiState = MutableStateFlow(AppPickerUiState())
    val uiState: StateFlow<AppPickerUiState> = _uiState.asStateFlow()

    init {
        loadInstalledApps()
    }

    fun loadInstalledApps() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true)
            val apps = appRepository.getInstalledLaunchableApps()
            _uiState.value = _uiState.value.copy(
                isLoading = false,
                installedApps = apps,
                filteredApps = filterApps(apps, _uiState.value.searchQuery)
            )
        }
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(
            searchQuery = query,
            filteredApps = filterApps(_uiState.value.installedApps, query)
        )
    }

    private fun filterApps(apps: List<InstalledAppItem>, query: String): List<InstalledAppItem> {
        if (query.isBlank()) return apps
        return apps.filter {
            it.appLabel.contains(query, ignoreCase = true) ||
            it.packageName.contains(query, ignoreCase = true)
        }
    }

    fun setAppLimit(packageName: String, label: String, durationMinutes: Int) {
        viewModelScope.launch {
            appRepository.addOrUpdateMonitoredApp(packageName, label, durationMinutes)
            loadInstalledApps()
        }
    }

    fun removeAppLimit(packageName: String) {
        viewModelScope.launch {
            appRepository.removeMonitoredApp(packageName)
            loadInstalledApps()
        }
    }
}
