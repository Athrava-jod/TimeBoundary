package com.timeboundary.app.utils

import android.app.usage.UsageEvents
import android.app.usage.UsageStatsManager
import android.content.Context
import android.os.Build

object UsageStatsHelper {

    fun getForegroundAppPackageName(context: Context): String? {
        val usageStatsManager =
            context.getSystemService(Context.USAGE_STATS_SERVICE) as? UsageStatsManager
                ?: return null

        val endTime = System.currentTimeMillis()

        // 1. Try a short 15-second window first (common case during transitions)
        val shortPackage = getForegroundFromEvents(usageStatsManager, endTime - 15000L, endTime)
        if (shortPackage != null) return shortPackage

        // 2. Fallback to a wider 2-hour window if no recent events occurred (user is actively using the same app)
        return getForegroundFromEvents(usageStatsManager, endTime - 2 * 3600000L, endTime)
    }

    private fun getForegroundFromEvents(
        usageStatsManager: UsageStatsManager,
        startTime: Long,
        endTime: Long
    ): String? {
        val usageEvents = usageStatsManager.queryEvents(startTime, endTime) ?: return null
        val event = UsageEvents.Event()
        var currentForegroundPackage: String? = null
        var lastEventTime = 0L

        while (usageEvents.hasNextEvent()) {
            usageEvents.getNextEvent(event)

            val eventType = event.eventType
            val isForegroundEvent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                eventType == UsageEvents.Event.ACTIVITY_RESUMED ||
                        eventType == UsageEvents.Event.MOVE_TO_FOREGROUND
            } else {
                @Suppress("DEPRECATION")
                eventType == UsageEvents.Event.MOVE_TO_FOREGROUND
            }

            if (isForegroundEvent && event.timeStamp > lastEventTime) {
                currentForegroundPackage = event.packageName
                lastEventTime = event.timeStamp
            }
        }

        return currentForegroundPackage
    }
}
