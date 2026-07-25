package com.timeboundary.app.service

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.Worker
import androidx.work.WorkerParameters
import com.timeboundary.app.data.preferences.PreferenceManager
import com.timeboundary.app.utils.PermissionUtils
import java.util.concurrent.TimeUnit

class MonitoringHealthWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    override fun doWork(): Result {
        val preferenceManager = PreferenceManager(context.applicationContext)
        val prefs = preferenceManager.preferencesFlow.value

        if (prefs.globalMonitoringEnabled && PermissionUtils.hasUsageAccessPermission(context)) {
            val serviceIntent = Intent(context, AppMonitoringService::class.java)
            try {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    context.startForegroundService(serviceIntent)
                } else {
                    context.startService(serviceIntent)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "TimeBoundaryHealthWatchdog"

        fun enqueueWatchdog(context: Context) {
            val request = PeriodicWorkRequestBuilder<MonitoringHealthWorker>(
                15, TimeUnit.MINUTES
            ).build()

            WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                WORK_NAME,
                ExistingPeriodicWorkPolicy.KEEP,
                request
            )
        }
    }
}
