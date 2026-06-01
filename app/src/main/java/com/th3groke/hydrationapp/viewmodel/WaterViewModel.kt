package com.th3groke.hydrationapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.th3groke.hydrationapp.data.UserPreferencesRepository
import com.th3groke.hydrationapp.data.WaterLog
import com.th3groke.hydrationapp.data.WaterLogDao
import com.th3groke.hydrationapp.worker.ReminderWorker
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.util.concurrent.TimeUnit

class WaterViewModel(
    private val waterLogDao: WaterLogDao,
    private val repository: UserPreferencesRepository,
    private val context: Context
) : ViewModel() {

    val dailyGoal: StateFlow<Int> = repository.dailyGoal
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 2000
        )

    private val startOfDay: Long
        get() = LocalDate.now()
            .atStartOfDay(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()

    val cupSize: StateFlow<Int> = repository.cupSize.stateIn(scope = viewModelScope, started = SharingStarted.WhileSubscribed(5000),
        initialValue = 250)
    val totalWaterDrank: StateFlow<Int> = waterLogDao.getTodayTotalWater(startOfDay)
        .map { it ?: 0 }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val hydrationHistory: StateFlow<List<WaterLog>> = waterLogDao.getTodayLogs(startOfDay)
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun addWater(amount: Int) {
        viewModelScope.launch {
            val newLog = WaterLog(
                amountInMilliliters = amount,
                timestamp = System.currentTimeMillis()
            )
            waterLogDao.insertLog(newLog)

            // Reset the reminder timer!
            resetReminderTimer()
        }
    }

    fun removeLog(log: WaterLog) {
        viewModelScope.launch {
            waterLogDao.deleteLog(log)
        }
    }

    private suspend fun resetReminderTimer() {
        val isEnabled = repository.isReminderEnabled.first()
        if (isEnabled) {
            val frequency = repository.reminderFrequency.first()
            scheduleWork(frequency)
        }
    }

    private fun scheduleWork(minutes: Int) {
        val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
            minutes.toLong(), TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "WaterReminder",
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE, // Restarts the timer cycle
            workRequest
        )
    }

    val dailyTotals: StateFlow<Map<LocalDate, Int>> = waterLogDao.getAllLogs()
        .map { logs ->
            logs.groupBy { log ->
                Instant.ofEpochMilli(log.timestamp)
                    .atZone(ZoneId.systemDefault())
                    .toLocalDate()
            }.mapValues { (_, dailyLogs) ->
                dailyLogs.sumOf { it.amountInMilliliters }
            }
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyMap()
        )
}
