package com.timeboundary.app.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_usage_stats")
data class AppUsageStat(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val packageName: String,
    val dateString: String, // Format: YYYY-MM-DD
    val openCount: Int = 0,
    val limitExceededCount: Int = 0
)
