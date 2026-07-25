package com.timeboundary.app

import android.app.Application
import com.timeboundary.app.service.MonitoringHealthWorker
import com.timeboundary.app.utils.NotificationHelper

class TimeBoundaryApp : Application() {

    override fun onCreate() {
        super.onCreate()
        NotificationHelper.createNotificationChannels(this)
        MonitoringHealthWorker.enqueueWatchdog(this)
    }
}
