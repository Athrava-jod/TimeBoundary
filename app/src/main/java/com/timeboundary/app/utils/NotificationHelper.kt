package com.timeboundary.app.utils

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.AudioAttributes
import android.media.RingtoneManager
import android.os.Build
import androidx.core.app.NotificationCompat
import com.timeboundary.app.R
import com.timeboundary.app.ui.MainActivity
import com.timeboundary.app.ui.screens.alert.FullScreenAlertActivity

object NotificationHelper {

    const val CHANNEL_MONITORING_ID = "timeboundary_monitoring_channel"
    const val CHANNEL_REMINDER_ID = "timeboundary_reminder_channel"
    const val NOTIFICATION_ID_SERVICE = 1001
    const val NOTIFICATION_ID_REMINDER = 2001

    fun createNotificationChannels(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            // 1. Service Monitoring Channel (Low Priority, no sound)
            val serviceChannel = NotificationChannel(
                CHANNEL_MONITORING_ID,
                "Background App Usage Monitoring",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Shows persistent status while TimeBoundary is actively monitoring app limits"
                setShowBadge(false)
            }

            // 2. High Priority Session Reminder Channel
            val soundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            val audioAttributes = AudioAttributes.Builder()
                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                .build()

            val reminderChannel = NotificationChannel(
                CHANNEL_REMINDER_ID,
                "Time Limits & Session Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Fires high priority notifications when a monitored app session timer expires"
                setSound(soundUri, audioAttributes)
                enableVibration(true)
                vibrationPattern = longArrayOf(0, 300, 200, 300)
            }

            notificationManager.createNotificationChannel(serviceChannel)
            notificationManager.createNotificationChannel(reminderChannel)
        }
    }

    fun buildForegroundServiceNotification(context: Context): Notification {
        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        return NotificationCompat.Builder(context, CHANNEL_MONITORING_ID)
            .setContentTitle("TimeBoundary Monitoring Active")
            .setContentText("Actively monitoring your app usage sessions")
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .setCategory(NotificationCompat.CATEGORY_SERVICE)
            .build()
    }

    fun triggerSessionExpiredNotification(
        context: Context,
        packageName: String,
        appLabel: String,
        durationMinutes: Int,
        sessionId: Long,
        isStrict: Boolean
    ) {
        val notificationManager =
            context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        // Intent for full screen alert
        val fullScreenIntent = Intent(context, FullScreenAlertActivity::class.java).apply {
            putExtra(FullScreenAlertActivity.EXTRA_PACKAGE_NAME, packageName)
            putExtra(FullScreenAlertActivity.EXTRA_APP_LABEL, appLabel)
            putExtra(FullScreenAlertActivity.EXTRA_DURATION_MINUTES, durationMinutes)
            putExtra(FullScreenAlertActivity.EXTRA_SESSION_ID, sessionId)
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }

        val fullScreenPendingIntent = PendingIntent.getActivity(
            context,
            sessionId.toInt(),
            fullScreenIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val title = "Time's up! $appLabel"
        val message = "You've been on $appLabel for $durationMinutes minutes."

        val builder = NotificationCompat.Builder(context, CHANNEL_REMINDER_ID)
            .setContentTitle(title)
            .setContentText(message)
            .setSmallIcon(R.drawable.ic_launcher_foreground)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .setContentIntent(fullScreenPendingIntent)
            .addAction(
                R.drawable.ic_launcher_foreground,
                "Open Alert",
                fullScreenPendingIntent
            )

        if (isStrict) {
            builder.setFullScreenIntent(fullScreenPendingIntent, true)
        }

        notificationManager.notify(sessionId.toInt(), builder.build())
    }
}
