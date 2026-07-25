package com.timeboundary.app.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.timeboundary.app.data.model.MonitoredApp
import kotlinx.coroutines.flow.Flow

@Dao
interface MonitoredAppDao {

    @Query("SELECT * FROM monitored_apps ORDER BY appLabel ASC")
    fun getAllMonitoredApps(): Flow<List<MonitoredApp>>

    @Query("SELECT * FROM monitored_apps WHERE isEnabled = 1")
    suspend fun getEnabledMonitoredAppsSync(): List<MonitoredApp>

    @Query("SELECT * FROM monitored_apps WHERE packageName = :packageName LIMIT 1")
    suspend fun getMonitoredApp(packageName: String): MonitoredApp?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(app: MonitoredApp)

    @Update
    suspend fun update(app: MonitoredApp)

    @Delete
    suspend fun delete(app: MonitoredApp)

    @Query("DELETE FROM monitored_apps WHERE packageName = :packageName")
    suspend fun deleteByPackage(packageName: String)

    @Query("UPDATE monitored_apps SET isEnabled = :isEnabled WHERE packageName = :packageName")
    suspend fun setEnabledStatus(packageName: String, isEnabled: Boolean)

    @Query("UPDATE monitored_apps SET lastSessionStartTime = :startTime, activeSessionId = :sessionId WHERE packageName = :packageName")
    suspend fun updateSessionStart(packageName: String, startTime: Long, sessionId: Long)
}
