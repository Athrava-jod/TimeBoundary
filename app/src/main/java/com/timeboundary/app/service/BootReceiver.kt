package com.timeboundary.app.service

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import com.timeboundary.app.data.preferences.PreferenceManager
import com.timeboundary.app.utils.PermissionUtils

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action
        if (action == Intent.ACTION_BOOT_COMPLETED || action == Intent.ACTION_MY_PACKAGE_REPLACED) {
            val preferenceManager = PreferenceManager(context.applicationContext)
            val prefs = preferenceManager.preferencesFlow.value

            if (prefs.globalMonitoringEnabled && PermissionUtils.hasUsageAccessPermission(context)) {
                val serviceIntent = Intent(context, AppMonitoringService::class.java)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }
            }

            // Schedule WorkManager watchdog
            MonitoringHealthWorker.enqueueWatchdog(context)
        }
    }
}
