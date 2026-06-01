package com.th3groke.hydrationapp.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.work.ExistingPeriodicWorkPolicy
import androidx.work.PeriodicWorkRequestBuilder
import androidx.work.WorkManager
import com.th3groke.hydrationapp.data.UserPreferencesRepository
import com.th3groke.hydrationapp.worker.ReminderWorker
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.WhileSubscribed
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.concurrent.TimeUnit

class SettingsViewModel(
    private val repository: UserPreferencesRepository,
    private val context: Context
) : ViewModel() {

    val isReminderEnabled: StateFlow<Boolean> = repository.isReminderEnabled
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = false
        )

    val reminderFrequency: StateFlow<Int> = repository.reminderFrequency
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 60
        )

    val startHour: StateFlow<Int> = repository.startHour.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 8
    )

    val endHour: StateFlow<Int> = repository.endHour.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 20
    )

    val dailyGoal: StateFlow<Int> = repository.dailyGoal.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 2000
    )

    val cupSize: StateFlow<Int> = repository.cupSize.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = 250
    )

    fun updateDailyGoal(milliliters: Int){
        viewModelScope.launch { repository.saveDailyGoal(milliliters) }
    }

    fun toggleReminders(enabled: Boolean) {
        viewModelScope.launch {
            repository.saveReminderEnabled(enabled)
        }
    }

    fun updateStartHour(hour: Int) {
        viewModelScope.launch { repository.saveStartHour(hour) }
    }

    fun updateEndHour(hour: Int) {
        viewModelScope.launch { repository.saveEndHour(hour) }
    }

    fun toggleReminders(enabled: Boolean, currentFrequency: Int) {
        viewModelScope.launch {
            repository.saveReminderEnabled(enabled)
            if (enabled) {
                scheduleWork(currentFrequency)
            } else {
                WorkManager.getInstance(context).cancelUniqueWork("WaterReminder")
            }
        }
    }
    fun updateCupSize(milliliters: Int){
        viewModelScope.launch { repository.saveCupSize(milliliters) }
    }

    fun updateFrequency(minutes: Int) {
        viewModelScope.launch {
            repository.saveReminderFrequency(minutes)

            scheduleWork(minutes)
        }
    }


    private fun scheduleWork(minutes: Int) {
        val workRequest = PeriodicWorkRequestBuilder<ReminderWorker>(
            minutes.toLong(), TimeUnit.MINUTES
        ).build()

        WorkManager.getInstance(context).enqueueUniquePeriodicWork(
            "WaterReminder",
            ExistingPeriodicWorkPolicy.CANCEL_AND_REENQUEUE,
            workRequest
        )
    }
}