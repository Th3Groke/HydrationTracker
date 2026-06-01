package com.th3groke.hydrationapp

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.systemBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.th3groke.hydrationapp.data.WaterLog
import com.th3groke.hydrationapp.viewmodel.WaterViewModel
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

@Composable
fun HydrationScreen(viewModel: WaterViewModel) {
    val totalWater by viewModel.totalWaterDrank.collectAsState(initial = 0)
    val history by viewModel.hydrationHistory.collectAsState(initial = emptyList())
    val goal by viewModel.dailyGoal.collectAsState()
    val cupSize by viewModel.cupSize.collectAsState()

    val currentTotal = totalWater
    val progress = if (goal > 0) (currentTotal.toFloat() / goal.toFloat()).coerceIn(0f, 1f) else 0f

    Column(
        modifier = Modifier
            .fillMaxSize()
            .systemBarsPadding()
            .padding(16.dp)
    ) {
        // --- TOP SECTION: Summary & Action ---
        Column(
            modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = stringResource(id = R.string.home_title), 
                style = MaterialTheme.typography.headlineMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(24.dp),
                trackColor = MaterialTheme.colorScheme.surfaceVariant,
                color = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = stringResource(id = R.string.home_progress, currentTotal, goal), 
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(modifier = Modifier.height(24.dp))

            // Upgraded Button with an Icon
            Button(
                onClick = { viewModel.addWater(cupSize) }, 
                modifier = Modifier.fillMaxWidth(0.6f)
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Water")
                Spacer(modifier = Modifier.width(8.dp))
                Text(text = stringResource(id = R.string.home_add_drink, cupSize))
            }
        }

        Spacer(modifier = Modifier.height(32.dp))
        HorizontalDivider()
        Spacer(modifier = Modifier.height(16.dp))

        // --- BOTTOM SECTION: History Ledger ---
        Text(
            text = stringResource(id = R.string.home_list_title),
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(8.dp))

        // The LazyColumn handles efficient scrolling
        LazyColumn(
            modifier = Modifier.fillMaxSize(), 
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            items(history, key = { it.id }) { log ->
                HistoryCard(
                    log = log, 
                    onDelete = { viewModel.removeLog(log) }
                )
            }
        }
    }
}

// A reusable UI component for each item in the list
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryCard(log: WaterLog, onDelete: () -> Unit) {
    // 1. Create the state that monitors the user's finger swipe
    val dismissState = rememberSwipeToDismissBoxState(
        confirmValueChange = { dismissValue ->
            if (dismissValue == SwipeToDismissBoxValue.EndToStart) {
                onDelete()
                true
            } else {
                false
            }
        })

    // 2. The Swipe Container
    SwipeToDismissBox(
        state = dismissState,
        enableDismissFromStartToEnd = false,
        backgroundContent = {
            val color by animateColorAsState(
                targetValue = if (dismissState.targetValue == SwipeToDismissBoxValue.EndToStart) {
                    MaterialTheme.colorScheme.errorContainer
                } else {
                    Color.Transparent
                }, label = "swipe_color"
            )

            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(color, shape = CardDefaults.shape)
                    .padding(horizontal = 20.dp),
                contentAlignment = Alignment.CenterEnd
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete Log",
                    tint = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        },
        content = {
            Card(
                modifier = Modifier.fillMaxWidth(), 
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "+ ${log.amountInMilliliters} mL",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = formatTime(log.timestamp),
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }
        })
}

fun formatTime(timestamp: Long): String {
    val formatter = DateTimeFormatter.ofPattern("HH:mm").withZone(ZoneId.systemDefault())
    return formatter.format(Instant.ofEpochMilli(timestamp))
}
