package com.th3groke.hydrationapp.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

class UserPreferencesRepository(private val context: Context) {
    companion object {
        val REMINDERS_ENABLED = booleanPreferencesKey("reminders_enabled")
        val REMINDER_FREQUENCY = intPreferencesKey("reminder_frequency_minutes")
        val START_HOUR = intPreferencesKey("reminder_start_hour")
        val END_HOUR = intPreferencesKey("reminder_end_hour")
        val DAILY_GOAL = intPreferencesKey("daily_goal")

        val CUP_SIZE = intPreferencesKey("cup_size")
    }

    val isReminderEnabled: Flow<Boolean> = context.dataStore.data
        .map { preferences ->
            preferences[REMINDERS_ENABLED] ?: false // Default to false if never set
        }

    val reminderFrequency: Flow<Int> = context.dataStore.data
        .map { preferences ->
            preferences[REMINDER_FREQUENCY] ?: 60 // Default to 60 minutes if never set
        }

    val cupSize : Flow<Int> = context.dataStore.data.map { preferences -> preferences[CUP_SIZE]?: 250 }

    suspend fun saveCupSize(milliliters: Int){
        context.dataStore.edit { preferences -> preferences[CUP_SIZE] = milliliters }
    }
    suspend fun saveReminderEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[REMINDERS_ENABLED] = enabled
        }
    }

    suspend fun saveReminderFrequency(minutes: Int) {
        context.dataStore.edit { preferences ->
            preferences[REMINDER_FREQUENCY] = minutes
        }
    }

    val dailyGoal: Flow<Int> = context.dataStore.data.map { preferences -> preferences[DAILY_GOAL]?: 2000 }

    suspend fun saveDailyGoal(milliliters: Int){
        context.dataStore.edit { preferences -> preferences[DAILY_GOAL] = milliliters }
    }
    val startHour: Flow<Int> = context.dataStore.data.map { preferences -> preferences[START_HOUR]?:8 }

    val endHour: Flow<Int> = context.dataStore.data.map{preferences -> preferences[END_HOUR]?: 20}

    suspend fun saveStartHour(hour: Int){
        context.dataStore.edit { preferences -> preferences[START_HOUR]=hour }
    }

    suspend fun saveEndHour(hour: Int){
        context.dataStore.edit { preferences -> preferences[END_HOUR] = hour }
    }
}