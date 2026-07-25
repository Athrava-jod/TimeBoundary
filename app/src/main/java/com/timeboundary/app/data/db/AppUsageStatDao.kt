package com.timeboundary.app.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.timeboundary.app.data.model.AppUsageStat
import kotlinx.coroutines.flow.Flow

@Dao
interface AppUsageStatDao {

    @Query("SELECT * FROM app_usage_stats WHERE dateString = :dateString")
    fun getStatsForDate(dateString: String): Flow<List<AppUsageStat>>

    @Query("SELECT * FROM app_usage_stats WHERE packageName = :packageName AND dateString = :dateString LIMIT 1")
    suspend fun getStat(packageName: String, dateString: String): AppUsageStat?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrUpdate(stat: AppUsageStat)

    @Query("DELETE FROM app_usage_stats")
    suspend fun clearAllStats()
}
