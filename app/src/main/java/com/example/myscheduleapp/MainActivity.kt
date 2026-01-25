package com.example.myscheduleapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.myscheduleapp.ui.theme.MyScheduleAppTheme
import com.example.myscheduleapp.ui.viewmodel.ScheduleViewModel
import com.example.myscheduleapp.ui.screens.ScheduleListScreen
import com.example.myscheduleapp.ui.screens.ScheduleDetailScreen
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
    NavHost(navController, startDestination = "scheduleList") {
        // 一覧画面
        composable("scheduleList") {
            ScheduleListScreen(
                viewModel = viewModel,
                onNavigateToDetail = { id ->
                    navController.navigate("detail/$id")
                }
            )
        }
        // 詳細画面
        composable(
            route = "detail/{scheduleId}",
            arguments = listOf(navArgument("scheduleId") { type = NavType.LongType })
        ){ backStackEntry ->
                val id = backStackEntry.arguments?.getLong("scheduleId") ?: 0L

                ScheduleDetailScreen(
                    scheduleId = id,
                    onBack = { navController.popBackStack() },
                    viewModel = viewModel
                )
        }
    }
}



