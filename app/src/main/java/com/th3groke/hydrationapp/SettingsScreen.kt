package com.th3groke.hydrationapp

import android.Manifest
import android.os.Build
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.th3groke.hydrationapp.viewmodel.SettingsViewModel
import kotlin.math.roundToInt

@Composable
fun SettingsScreen(viewModel: SettingsViewModel) {
    val millilitersDailyGoal by viewModel.dailyGoal.collectAsState()
    val isEnabled by viewModel.isReminderEnabled.collectAsState()
    val frequency by viewModel.reminderFrequency.collectAsState()
    val startHour by viewModel.startHour.collectAsState()
    val endHour by viewModel.endHour.collectAsState()
    val millilitersCupSize by viewModel.cupSize.collectAsState()

    val context = LocalContext.current
    val permissionDeniedMessage = stringResource(id = R.string.settings_reminders_no_permissions)

    // 1. The Permission Launcher
    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(), 
        onResult = { isGranted ->
            if (isGranted) {
                // Permission granted: turn on the reminders!
                viewModel.toggleReminders(true, frequency)
            } else {
                // Permission denied: tell the user why it failed.
                Toast.makeText(context, permissionDeniedMessage, Toast.LENGTH_SHORT).show()
                // Ensure the switch stays off in the ViewModel
                viewModel.toggleReminders(false, frequency)
            }
        }
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id = R.string.settings),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(32.dp))
        
        // --- Daily Goal ---
        Text(
            text = stringResource(id = R.string.settings_daily_goal, millilitersDailyGoal), 
            style = MaterialTheme.typography.titleMedium
        )
        Slider(
            value = millilitersDailyGoal.toFloat(),
            onValueChange = { viewModel.updateDailyGoal(it.roundToInt()) },
            valueRange = 1000f..4000f,
            steps = 29 // 100ml steps
        )
        Spacer(modifier = Modifier.height(24.dp))

        // --- Cup Size ---
        Text(
            text = stringResource(id = R.string.settings_cup_size, millilitersCupSize), 
            style = MaterialTheme.typography.titleMedium
        )
        Slider(
            value = millilitersCupSize.toFloat(), 
            onValueChange = { viewModel.updateCupSize(it.roundToInt()) }, 
            valueRange = 50f..500f, 
            steps = 8 // 50ml steps
        )

        Spacer(modifier = Modifier.height(24.dp))
        
        // --- Notification Master Toggle ---
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(id = R.string.settings_enable_reminders), 
                style = MaterialTheme.typography.titleLarge
            )
            Switch(
                checked = isEnabled, 
                onCheckedChange = { isChecking ->
                    if (isChecking) {
                        // 2. The OS Check (Android 13+ / Tiramisu)
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                            permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        } else {
                            viewModel.toggleReminders(true, frequency)
                        }
                    } else {
                        viewModel.toggleReminders(false, frequency)
                    }
                }
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(24.dp))
        
        // --- Dependent Settings (Only visible if enabled) ---
        if (isEnabled) {
            // Frequency Slider
            Text(
                text = stringResource(R.string.settings_reminders_freq, frequency), 
                style = MaterialTheme.typography.titleMedium
            )
            Slider(
                value = frequency.toFloat(),
                onValueChange = { viewModel.updateFrequency(it.roundToInt()) },
                valueRange = 15f..120f,
                steps = 6 // 15, 30, 45, 60, 75, 90, 105, 120
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Start Time Slider
            Text(
                text = stringResource(id = R.string.settings_reminders_start, formatHour(startHour)),
                style = MaterialTheme.typography.titleMedium
            )
            Slider(
                value = startHour.toFloat(), 
                onValueChange = {
                    val newHour = it.roundToInt()
                    if (newHour < endHour) viewModel.updateStartHour(newHour)
                }, 
                valueRange = 0f..23f, 
                steps = 22
            )

            Spacer(modifier = Modifier.height(16.dp))

            // End Time Slider
            Text(
                text = stringResource(id = R.string.settings_reminders_end, formatHour(endHour)),
                style = MaterialTheme.typography.titleMedium
            )
            Slider(
                value = endHour.toFloat(), 
                onValueChange = {
                    val newHour = it.roundToInt()
                    if (newHour > startHour) viewModel.updateEndHour(newHour)
                }, 
                valueRange = 0f..23f, 
                steps = 22
            )
        }
    }
}

fun formatHour(hour: Int): String {
    return "${hour.toString().padStart(2, '0')}:00"
}
