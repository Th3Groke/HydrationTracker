package com.th3groke.hydrationapp

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.th3groke.hydrationapp.viewmodel.SettingsViewModel
import com.th3groke.hydrationapp.viewmodel.WaterViewModel

@Composable
fun HydrationApp(waterViewModel: WaterViewModel, settingsViewModel: SettingsViewModel) {
    // 1. Create the engine that controls screen transitions
    val navController = rememberNavController()

    // 2. The Scaffold sets up the overall screen structure
    Scaffold(
        bottomBar = {
            NavigationBar {
                // Find out which screen we are currently looking at
                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route

                // Our list of screens
                val items = listOf(Screen.Home, Screen.Calendar, Screen.Settings)

                // 3. Draw a button for each screen in the list
                items.forEach { screen ->
                    val title = stringResource(id = screen.titleRes)
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = title) },
                        label = { Text(title) },
                        selected = currentRoute == screen.route,
                        onClick = {
                            // 4. Navigate to the selected screen
                            navController.navigate(screen.route) {
                                // These rules ensure we don't open multiple copies of the same screen
                                // and that pressing the physical "Back" button works correctly.
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        }
                    )
                }
            }
        }
    ) { innerPadding ->
        // 5. The NavHost is the blank canvas where the actual screens are drawn
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding) // This prevents screens from drawing under the bottom bar
        ) {
            composable(Screen.Home.route) {
                // We pass the innerPadding to HydrationScreen if you want it to
                // replace the systemBarsPadding() we added earlier!
                HydrationScreen(viewModel = waterViewModel)
            }
            composable(Screen.Calendar.route){
                CalendarScreen(viewModel = waterViewModel)
            }
            composable(Screen.Settings.route) {
                SettingsScreen(viewModel = settingsViewModel)
            }
        }
    }
}
