package com.th3groke.hydrationapp

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.th3groke.hydrationapp.viewmodel.WaterViewModel
import java.time.LocalDate
import java.time.YearMonth

@Composable
fun CalendarScreen(viewModel: WaterViewModel) {
    // 1. Observe the data from the ViewModel
    val dailyTotals by viewModel.dailyTotals.collectAsState()
    val dailyGoal by viewModel.dailyGoal.collectAsState()

    // 2. Month navigation state
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }

    val daysInMonth = currentMonth.lengthOfMonth()
    val firstDayOfWeek = currentMonth.atDay(1).dayOfWeek.value // 1 = Mon, 7 = Sun

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(
            text = stringResource(id=R.string.calendar_title),
            style = MaterialTheme.typography.headlineMedium,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 3. Interactive Month Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { currentMonth = currentMonth.minusMonths(1) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "Previous Month"
                )
            }

            Text(
                text = "${currentMonth.month} ${currentMonth.year}",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold
            )

            IconButton(onClick = { currentMonth = currentMonth.plusMonths(1) }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = "Next Month"
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // 4. Days of the Week header
        Row(modifier = Modifier.fillMaxWidth()) {
            val daysOfWeek = listOf("M", "T", "W", "T", "F", "S", "S")
            daysOfWeek.forEach { day ->
                Text(
                    text = day,
                    modifier = Modifier.weight(1f),
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // 5. The Calendar Grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(7),
            modifier = Modifier.fillMaxWidth()
        ) {
            // Spacer for the first day of the week
            items(firstDayOfWeek - 1) {
                Spacer(modifier = Modifier.size(40.dp))
            }

            items(daysInMonth) { dayOffset ->
                val dayOfMonth = dayOffset + 1
                val date = currentMonth.atDay(dayOfMonth)
                
                // Determine progress for this date
                val intake = dailyTotals[date] ?: 0
                val progress = if (dailyGoal > 0) (intake.toFloat() / dailyGoal.toFloat()).coerceIn(0f, 1f) else 0f
                val isToday = date == LocalDate.now()
                val hasIntake = intake > 0

                Box(
                    modifier = Modifier
                        .aspectRatio(1f)
                        .padding(4.dp),
                    contentAlignment = Alignment.Center
                ) {
                    // Outer Circle (Track)
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.surfaceVariant)
                    )

                    // Growing Inner Circle (Progress)
                    if (hasIntake) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(progress) // Grows from 0% to 100%
                                .clip(CircleShape)
                                .background(MaterialTheme.colorScheme.primary)
                        )
                    }

                    // Day Number
                    Text(
                        text = dayOfMonth.toString(),
                        // Change to White/onPrimary if any water was tracked (inner circle exists)
                        color = if (hasIntake) Color.White else MaterialTheme.colorScheme.onSurface,
                        fontWeight = if (isToday) FontWeight.ExtraBold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
