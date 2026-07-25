package com.timeboundary.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "monitored_apps")
data class MonitoredApp(
    @PrimaryKey
    val packageName: String,
    val appLabel: String,
    val durationMinutes: Int,
    val isEnabled: Boolean = true,
    val lastSessionStartTime: Long = 0L,
    val activeSessionId: Long = 0L
)
