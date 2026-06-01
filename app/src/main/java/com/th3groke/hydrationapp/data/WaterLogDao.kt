package com.th3groke.hydrationapp.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface WaterLogDao {
    @Insert
    suspend fun insertLog(log: WaterLog)

    @Delete
    suspend fun deleteLog(log: WaterLog)

    @Query("SELECT SUM(amountInMilliliters) FROM water_logs WHERE timestamp >= :startTime")
    fun getTodayTotalWater(startTime: Long): Flow<Int?>

    @Query("SELECT * FROM water_logs WHERE timestamp >= :startTime ORDER BY timestamp DESC")
    fun getTodayLogs(startTime: Long): Flow<List<WaterLog>>

    @Query("SELECT * FROM water_logs ORDER BY timestamp DESC")
    fun getAllLogs(): Flow<List<WaterLog>>

    @Query("SELECT MAX(timestamp) FROM water_logs")
    suspend fun getLastLogTimestamp(): Long?
}
