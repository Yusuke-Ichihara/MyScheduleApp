package com.example.myscheduleapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myscheduleapp.ui.viewmodel.ScheduleViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleListScreen(
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