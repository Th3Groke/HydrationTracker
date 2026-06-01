package com.th3groke.hydrationapp.worker

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.th3groke.hydrationapp.MainActivity
import com.th3groke.hydrationapp.data.UserPreferencesRepository
import com.th3groke.hydrationapp.data.WaterDatabase
import com.th3groke.hydrationapp.data.dataStore
import kotlinx.coroutines.flow.first
import java.util.Calendar
import java.util.concurrent.TimeUnit

class ReminderWorker(
    private val context: Context,
    params: WorkerParameters
) : CoroutineWorker(context, params) {

    override suspend fun doWork(): Result {
        // 1. Read the current settings from DataStore
        val prefs = context.dataStore.data.first()
        val isEnabled = prefs[UserPreferencesRepository.REMINDERS_ENABLED] ?: false
        val startHour = prefs[UserPreferencesRepository.START_HOUR] ?: 8
        val endHour = prefs[UserPreferencesRepository.END_HOUR] ?: 20
        val frequencyMinutes = prefs[UserPreferencesRepository.REMINDER_FREQUENCY] ?: 60

        // 2. If reminders are turned off, stop here.
        if (!isEnabled) {
            return Result.success()
        }

        // 3. Check when the last water log was recorded
        val database = WaterDatabase.getDatabase(context)
        val lastLogTimestamp = database.waterLogDao().getLastLogTimestamp() ?: 0L
        val currentTime = System.currentTimeMillis()
        val minutesSinceLastLog = TimeUnit.MILLISECONDS.toMinutes(currentTime - lastLogTimestamp)

        // 4. Check if we should skip this reminder because a log was added recently
        // We only notify if enough time has passed since the last log
        if (minutesSinceLastLog < (frequencyMinutes - 5)) { 
            // We subtract 5 minutes as a "grace period" for the scheduler's precision
            return Result.success()
        }

        // 5. Check if the current time falls within the user's active hours
        val currentHour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY)

        if (currentHour in startHour until endHour) {
            showNotification(context)
        }

        return Result.success()
    }
}


fun showNotification(context: Context) {
    val channelId = "hydration_channel"
    val notificationManager =
        context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

    // Android 8.0+ requires a Notification Channel
    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
        val channel = NotificationChannel(
            channelId,
            "Hydration Reminders",
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationManager.createNotificationChannel(channel)
    }

    val intent = Intent(context, MainActivity::class.java).apply {
        flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    }

    val pendingIntent: PendingIntent = PendingIntent.getActivity(
        context,
        0,
        intent,
        PendingIntent.FLAG_IMMUTABLE
    )

    val notification = NotificationCompat.Builder(context, channelId)
        .setSmallIcon(android.R.drawable.ic_dialog_info) // Default icon for now
        .setContentTitle("Time to Hydrate!")
        .setContentText("Grab a glass of water and log your progress.")
        .setPriority(NotificationCompat.PRIORITY_DEFAULT)
        .setContentIntent(pendingIntent)
        .setAutoCancel(true)
        .build()

    notificationManager.notify(1, notification)
}
