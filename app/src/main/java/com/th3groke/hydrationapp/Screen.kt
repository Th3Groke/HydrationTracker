package com.th3groke.hydrationapp

import androidx.annotation.StringRes
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, @StringRes val titleRes: Int, val icon: ImageVector) {
    object Home : Screen("home", R.string.home, Icons.Default.Home)
    object Calendar : Screen("calendar", R.string.calendar, Icons.Default.CalendarMonth)
    object Settings : Screen("settings", R.string.settings, Icons.Default.Settings)
}
