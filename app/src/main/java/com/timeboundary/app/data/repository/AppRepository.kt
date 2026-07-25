package com.timeboundary.app.data.repository

import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.graphics.drawable.Drawable
import com.timeboundary.app.data.db.AppDatabase
import com.timeboundary.app.data.model.AppUsageStat
import com.timeboundary.app.data.model.MonitoredApp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

data class InstalledAppItem(
    val packageName: String,
    val appLabel: String,
    val icon: Drawable?,
    val isMonitored: Boolean,
    val durationMinutes: Int
)

class AppRepository(private val context: Context) {

    private val db = AppDatabase.getDatabase(context)
    private val monitoredAppDao = db.monitoredAppDao()
    private val appUsageStatDao = db.appUsageStatDao()
    private val packageManager: PackageManager = context.packageManager

    val monitoredAppsFlow: Flow<List<MonitoredApp>> = monitoredAppDao.getAllMonitoredApps()

    fun getTodayStatsFlow(): Flow<List<AppUsageStat>> {
        val todayStr = getTodayDateString()
        return appUsageStatDao.getStatsForDate(todayStr)
    }

    suspend fun getEnabledAppsSync(): List<MonitoredApp> {
        return monitoredAppDao.getEnabledMonitoredAppsSync()
    }

    suspend fun getMonitoredApp(packageName: String): MonitoredApp? {
        return monitoredAppDao.getMonitoredApp(packageName)
    }

    suspend fun addOrUpdateMonitoredApp(packageName: String, label: String, durationMinutes: Int) {
        val existing = monitoredAppDao.getMonitoredApp(packageName)
        val app = MonitoredApp(
            packageName = packageName,
            appLabel = label,
            durationMinutes = durationMinutes,
            isEnabled = existing?.isEnabled ?: true,
            lastSessionStartTime = existing?.lastSessionStartTime ?: 0L,
            activeSessionId = existing?.activeSessionId ?: 0L
        )
        monitoredAppDao.insertOrUpdate(app)
    }

    suspend fun removeMonitoredApp(packageName: String) {
        monitoredAppDao.deleteByPackage(packageName)
    }

    suspend fun toggleMonitoredAppEnabled(packageName: String, isEnabled: Boolean) {
        monitoredAppDao.setEnabledStatus(packageName, isEnabled)
    }

    suspend fun updateSessionStart(packageName: String, startTime: Long, sessionId: Long) {
        monitoredAppDao.updateSessionStart(packageName, startTime, sessionId)
    }

    suspend fun recordAppOpened(packageName: String) = withContext(Dispatchers.IO) {
        val today = getTodayDateString()
        val existing = appUsageStatDao.getStat(packageName, today)
        if (existing == null) {
            appUsageStatDao.insertOrUpdate(
                AppUsageStat(
                    packageName = packageName,
                    dateString = today,
                    openCount = 1,
                    limitExceededCount = 0
                )
            )
        } else {
            appUsageStatDao.insertOrUpdate(
                existing.copy(openCount = existing.openCount + 1)
            )
        }
    }

    suspend fun recordLimitExceeded(packageName: String) = withContext(Dispatchers.IO) {
        val today = getTodayDateString()
        val existing = appUsageStatDao.getStat(packageName, today)
        if (existing == null) {
            appUsageStatDao.insertOrUpdate(
                AppUsageStat(
                    packageName = packageName,
                    dateString = today,
                    openCount = 1,
                    limitExceededCount = 1
                )
            )
        } else {
            appUsageStatDao.insertOrUpdate(
                existing.copy(limitExceededCount = existing.limitExceededCount + 1)
            )
        }
    }

    suspend fun clearAllStats() = withContext(Dispatchers.IO) {
        appUsageStatDao.clearAllStats()
    }

    suspend fun getInstalledLaunchableApps(): List<InstalledAppItem> = withContext(Dispatchers.IO) {
        val monitoredApps = monitoredAppDao.getEnabledMonitoredAppsSync().associateBy { it.packageName }
        val mainIntent = Intent(Intent.ACTION_MAIN, null).apply {
            addCategory(Intent.CATEGORY_LAUNCHER)
        }

        val resolveInfos = packageManager.queryIntentActivities(mainIntent, 0)
        val selfPackageName = context.packageName

        val result = mutableListOf<InstalledAppItem>()

        for (resolveInfo in resolveInfos) {
            val pkgName = resolveInfo.activityInfo.packageName
            // Exclude TimeBoundary app itself
            if (pkgName == selfPackageName) continue

            val appLabel = resolveInfo.loadLabel(packageManager).toString()
            val icon = try {
                resolveInfo.loadIcon(packageManager)
            } catch (e: Exception) {
                null
            }

            val monitoredApp = monitoredApps[pkgName]
            val isMonitored = monitoredApp != null
            val duration = monitoredApp?.durationMinutes ?: 15

            result.add(
                InstalledAppItem(
                    packageName = pkgName,
                    appLabel = appLabel,
                    icon = icon,
                    isMonitored = isMonitored,
                    durationMinutes = duration
                )
            )
        }

        result.sortedBy { it.appLabel.lowercase(Locale.getDefault()) }
    }

    fun getAppIcon(packageName: String): Drawable? {
        return try {
            packageManager.getApplicationIcon(packageName)
        } catch (e: Exception) {
            null
        }
    }

    fun getAppLabel(packageName: String): String {
        return try {
            val appInfo = packageManager.getApplicationInfo(packageName, 0)
            packageManager.getApplicationLabel(appInfo).toString()
        } catch (e: Exception) {
            packageName
        }
    }

    private fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }
}
