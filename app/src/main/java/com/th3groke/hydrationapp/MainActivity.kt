package com.th3groke.hydrationapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.SystemBarStyle
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import com.th3groke.hydrationapp.data.UserPreferencesRepository
import com.th3groke.hydrationapp.data.WaterDatabase
import com.th3groke.hydrationapp.viewmodel.SettingsViewModel
import com.th3groke.hydrationapp.viewmodel.WaterViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        enableEdgeToEdge(
            statusBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            ),
            navigationBarStyle = SystemBarStyle.light(
                android.graphics.Color.TRANSPARENT,
                android.graphics.Color.TRANSPARENT
            )
        )
        // 1. Initialize Databases and Repositories
        val database = WaterDatabase.getDatabase(this)
        val dao = database.waterLogDao()
        val prefsRepository = UserPreferencesRepository(this) // Initialize DataStore

        // 2. Create Factories to inject dependencies into ViewModels
        val waterFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return WaterViewModel(dao, prefsRepository, applicationContext) as T
            }
        }

        val settingsFactory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>): T {
                return SettingsViewModel(prefsRepository, applicationContext) as T
            }
        }

        setContent {
            MaterialTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    // 3. Instantiate the ViewModels
                    val waterViewModel: WaterViewModel = viewModel(factory = waterFactory)
                    val settingsViewModel: SettingsViewModel = viewModel(factory = settingsFactory)

                    // 4. Launch the App!
                    HydrationApp(
                        waterViewModel = waterViewModel,
                        settingsViewModel = settingsViewModel
                    )
                }
            }
        }
    }
}
