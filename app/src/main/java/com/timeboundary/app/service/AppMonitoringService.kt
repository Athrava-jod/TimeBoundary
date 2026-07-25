package com.timeboundary.app.service

import android.app.AlarmManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import com.timeboundary.app.data.preferences.PreferenceManager
import com.timeboundary.app.data.repository.AppRepository
import com.timeboundary.app.utils.NotificationHelper
import com.timeboundary.app.utils.PermissionUtils
import com.timeboundary.app.utils.UsageStatsHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch

class AppMonitoringService : Service() {

    private val serviceJob = Job()
    private val serviceScope = CoroutineScope(Dispatchers.IO + serviceJob)

    private lateinit var appRepository: AppRepository
    private lateinit var preferenceManager: PreferenceManager
    private lateinit var alarmManager: AlarmManager

    private var lastForegroundApp: String? = null
    private var isPolling = false

    override fun onCreate() {
        super.onCreate()
        appRepository = AppRepository(applicationContext)
        preferenceManager = PreferenceManager(applicationContext)
        alarmManager = getSystemService(Context.ALARM_SERVICE) as AlarmManager

        NotificationHelper.createNotificationChannels(applicationContext)
        val notification = NotificationHelper.buildForegroundServiceNotification(applicationContext)
        startForeground(NotificationHelper.NOTIFICATION_ID_SERVICE, notification)

        startPollingLoop()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        if (!isPolling) {
            startPollingLoop()
        }
        return START_STICKY
    }

    private fun startPollingLoop() {
        isPolling = true
        serviceScope.launch {
            while (isActive) {
                try {
                    val prefs = preferenceManager.preferencesFlow.value
                    if (prefs.globalMonitoringEnabled && PermissionUtils.hasUsageAccessPermission(applicationContext)) {
                        val currentForeground = UsageStatsHelper.getForegroundAppPackageName(applicationContext)

                        if (currentForeground != null && currentForeground != lastForegroundApp) {
                            // Foreground app transition detected!
                            handleForegroundAppChange(currentForeground)
                            lastForegroundApp = currentForeground
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }

                // Poll every 2 seconds
                delay(2000L)
            }
        }
    }

    private suspend fun handleForegroundAppChange(newPackageName: String) {
        // Ignore TimeBoundary app itself
        if (newPackageName == packageName) return

        val monitoredApp = appRepository.getMonitoredApp(newPackageName)
        if (monitoredApp != null && monitoredApp.isEnabled) {
            val now = System.currentTimeMillis()
            val durationMillis = monitoredApp.durationMinutes * 60 * 1000L
            val triggerTime = now + durationMillis
            val sessionId = now // Unique timestamp identifier for session

            // 1. Cancel previous pending alarm for this app if active
            cancelPreviousAlarm(newPackageName, monitoredApp.activeSessionId)

            // 2. Schedule new exact countdown alarm
            scheduleExactSessionAlarm(
                packageName = newPackageName,
                appLabel = monitoredApp.appLabel,
                durationMinutes = monitoredApp.durationMinutes,
                triggerTime = triggerTime,
                sessionId = sessionId
            )

            // 3. Update database session state and record open count
            appRepository.updateSessionStart(newPackageName, now, sessionId)
            appRepository.recordAppOpened(newPackageName)
        }
    }

    private fun scheduleExactSessionAlarm(
        packageName: String,
        appLabel: String,
        durationMinutes: Int,
        triggerTime: Long,
        sessionId: Long
    ) {
        val intent = Intent(applicationContext, SessionAlarmReceiver::class.java).apply {
            action = SessionAlarmReceiver.ACTION_SESSION_EXPIRED
            putExtra(SessionAlarmReceiver.EXTRA_PACKAGE_NAME, packageName)
            putExtra(SessionAlarmReceiver.EXTRA_APP_LABEL, appLabel)
            putExtra(SessionAlarmReceiver.EXTRA_DURATION_MINUTES, durationMinutes)
            putExtra(SessionAlarmReceiver.EXTRA_SESSION_ID, sessionId)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            sessionId.toInt(),
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        } else {
            alarmManager.setExact(
                AlarmManager.RTC_WAKEUP,
                triggerTime,
                pendingIntent
            )
        }
    }

    private fun cancelPreviousAlarm(packageName: String, previousSessionId: Long) {
        if (previousSessionId == 0L) return

        val intent = Intent(applicationContext, SessionAlarmReceiver::class.java).apply {
            action = SessionAlarmReceiver.ACTION_SESSION_EXPIRED
        }
        val pendingIntent = PendingIntent.getBroadcast(
            applicationContext,
            previousSessionId.toInt(),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )

        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        isPolling = false
        serviceJob.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
