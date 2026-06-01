package com.th3groke.hydrationapp.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "water_logs")
data class WaterLog(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val amountInMilliliters: Int,
    val timestamp: Long
)
