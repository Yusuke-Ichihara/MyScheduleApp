package com.example.myscheduleapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.myscheduleapp.data.Schedule
import com.example.myscheduleapp.data.Task
import com.example.myscheduleapp.ui.theme.MyScheduleAppTheme
import com.example.myscheduleapp.ui.viewmodel.ScheduleViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyScheduleAppTheme {
                Scaffold(
                    modifier = Modifier.fillMaxSize()
                ) { paddingValues ->
                    SimpleScheduleScreen(
                        modifier = Modifier.padding(paddingValues)
                    )
                }
            }
        }
    }
}

@Composable
fun SimpleScheduleScreen(
    modifier: Modifier = Modifier,
    viewModel: ScheduleViewModel = hiltViewModel()
) {
    // 入力中のテキスト（JavaでのFormオブジェクトのようなもの）
    var inputText by remember { mutableStateOf("") }
    // データベースから取得したリスト（常に最新が流れてくる）
    val schedules by viewModel.schedules.collectAsState()

    Column(
        modifier = modifier.fillMaxSize().padding(16.dp)
    ){
        Text(text = "大人の時間割（シンプル版）", style = MaterialTheme.typography.headlineMedium)

        Spacer(modifier = Modifier.height(16.dp))

        // 入力フォーム
        Row(modifier = Modifier.fillMaxWidth()) {
            TextField (
                value = inputText,
                onValueChange = { inputText = it },
                modifier = Modifier.weight(1f),
                placeholder = { Text("スケジュール名を入力") }
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(onClick = {
                if (inputText.isNotBlank()) {
                    viewModel.addSchedule(inputText)
                    inputText = ""
                }
            }) {
                Text("追加")
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        Text(text = "登録済みリスト", style = MaterialTheme.typography.headlineMedium)

        LazyColumn(modifier = Modifier.fillMaxWidth()) {
            items(schedules) { schedule ->
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp)
                ){
                    Text (
                        text = schedule.name,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}