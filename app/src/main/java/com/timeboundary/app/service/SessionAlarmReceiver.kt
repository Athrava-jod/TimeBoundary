package com.timeboundary.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.timeboundary.app.data.model.NotificationStyle
import com.timeboundary.app.data.preferences.PreferenceManager
import com.timeboundary.app.data.repository.AppRepository
import com.timeboundary.app.ui.screens.alert.FullScreenAlertActivity
import com.timeboundary.app.utils.NotificationHelper
import com.timeboundary.app.utils.UsageStatsHelper
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SessionAlarmReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == ACTION_SESSION_EXPIRED) {
            val packageName = intent.getStringExtra(EXTRA_PACKAGE_NAME) ?: return
            val appLabel = intent.getStringExtra(EXTRA_APP_LABEL) ?: packageName
            val durationMinutes = intent.getIntExtra(EXTRA_DURATION_MINUTES, 15)
            val sessionId = intent.getLongExtra(EXTRA_SESSION_ID, System.currentTimeMillis())

            val appRepository = AppRepository(context.applicationContext)
            val preferenceManager = PreferenceManager(context.applicationContext)

            val pendingResult = goAsync()
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Record that limit was exceeded for today
                    appRepository.recordLimitExceeded(packageName)

                    val prefs = preferenceManager.preferencesFlow.value
                    val currentForegroundPackage = UsageStatsHelper.getForegroundAppPackageName(context)
                    val isAppCurrentlyForeground = (currentForegroundPackage == packageName)

                    val isStrict = (prefs.notificationStyle == NotificationStyle.STRICT) || isAppCurrentlyForeground

                    // Always trigger high-priority notification channel
                    NotificationHelper.triggerSessionExpiredNotification(
                        context = context,
                        packageName = packageName,
                        appLabel = appLabel,
                        durationMinutes = durationMinutes,
                        sessionId = sessionId,
                        isStrict = isStrict
                    )

                    // If Strict mode or app is currently active in foreground, open FullScreenAlertActivity directly
                    if (isStrict) {
                        val alertIntent = Intent(context, FullScreenAlertActivity::class.java).apply {
                            putExtra(FullScreenAlertActivity.EXTRA_PACKAGE_NAME, packageName)
                            putExtra(FullScreenAlertActivity.EXTRA_APP_LABEL, appLabel)
                            putExtra(FullScreenAlertActivity.EXTRA_DURATION_MINUTES, durationMinutes)
                            putExtra(FullScreenAlertActivity.EXTRA_SESSION_ID, sessionId)
                            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
                        }
                        context.startActivity(alertIntent)
                    }

                } catch (e: Exception) {
                    e.printStackTrace()
                } finally {
                    pendingResult.finish()
                }
            }
        }
    }

    companion object {
        const val ACTION_SESSION_EXPIRED = "com.timeboundary.app.ACTION_SESSION_EXPIRED"
        const val EXTRA_PACKAGE_NAME = "extra_package_name"
        const val EXTRA_APP_LABEL = "extra_app_label"
        const val EXTRA_DURATION_MINUTES = "extra_duration_minutes"
        const val EXTRA_SESSION_ID = "extra_session_id"
    }
}
