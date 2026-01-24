package com.example.myscheduleapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.myscheduleapp.ui.theme.MyScheduleAppTheme
import com.example.myscheduleapp.ui.viewmodel.ScheduleViewModel
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
            SimpleScheduleScreen(
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

                SimpleDetailScreen(
                    scheduleId = id,
                    onBack = { navController.popBackStack() }
                )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleScheduleScreen(
    viewModel: ScheduleViewModel,
    onNavigateToDetail: (Long) -> Unit
) {
    // 入力中のテキスト（JavaでのFormオブジェクトのようなもの）
    var inputText by remember { mutableStateOf("") }
    // データベースから取得したリスト（常に最新が流れてくる）
    val schedules by viewModel.schedules.collectAsState(initial = emptyList())

    Scaffold(
        topBar = { TopAppBar(title = { Text("大人の時間割") }) }
    ) { padding ->
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Row(modifier = Modifier.fillMaxWidth()) {
                TextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("スケジュール名を入力") }
                )
                Button(onClick = {
                    if (inputText.isNotBlank()) {
                        viewModel.addSchedule(inputText)
                        inputText = ""
                    }
                }) { Text("追加") }
            }

            Spacer(modifier = Modifier.height(16.dp))

            LazyColumn {
                items(schedules) { schedule ->
                    Card(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp).clickable { onNavigateToDetail(schedule.id) }
                    ) {
                        Text(schedule.name, modifier = Modifier.padding(16.dp))
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SimpleDetailScreen(scheduleId: Long, onBack: () -> Unit) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("詳細 (ID: $scheduleId)") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier
            .padding(padding)
            .fillMaxSize()) {
            Text("ここは ID: $scheduleId の詳細画面です。今後タスク入力を追加します。",
                modifier = Modifier.padding(16.dp))
        }
    }
}