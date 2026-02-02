package com.example.myscheduleapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.*
import androidx.navigation.compose.*
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.myscheduleapp.ui.screens.ScheduleListScreen
import com.example.myscheduleapp.ui.theme.MyScheduleAppTheme
import com.example.myscheduleapp.ui.viewmodel.ScheduleViewModel
import com.example.myscheduleapp.ui.screens.SchedulePagerScreen
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyScheduleAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    AppNavigation()
                }
            }
        }
    }
}

@Composable
fun AppNavigation(viewModel: ScheduleViewModel = hiltViewModel()) {
    val navController = rememberNavController()
    NavHost(navController = navController, startDestination = "pager") {
        composable("pager") {
            SchedulePagerScreen(
                viewModel = viewModel,
                onNavigateToManage = { navController.navigate("manage") }
            )
        }
        composable("manage") {
            ScheduleListScreen(
                viewModel = viewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}