package com.example.myscheduleapp.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.* // ★一括インポートに整理（AlertDialog, TextButton等を含む）
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.myscheduleapp.data.Schedule // ★追加
import com.example.myscheduleapp.ui.viewmodel.ScheduleViewModel


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ScheduleListScreen(
    viewModel: ScheduleViewModel,
    onNavigateToDetail: (Long) -> Unit
) {
    // 入力中のテキスト（JavaでのFormオブジェクトのようなもの）
    var inputText by remember { mutableStateOf("") }
    // データベースから取得したリスト（常に最新が流れてくる）
    val schedules by viewModel.schedules.collectAsState(initial = emptyList())

    var showEditDialog by remember { mutableStateOf(false) }
    var editingSchedule by remember { mutableStateOf<Schedule?>(null) }
    var newScheduleName by remember { mutableStateOf("") }

    if (showEditDialog && editingSchedule != null) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("スケジュール名変更") },
            text = {
                TextField(
                    value = newScheduleName,
                    onValueChange = { newScheduleName = it },
                    label = { Text("新しいスケジュール名") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newScheduleName.isNotBlank()) {
                        viewModel.updateSchedule(editingSchedule!!.copy(name = newScheduleName))
                        showEditDialog = false
                    }
                }) {
                    Text("保存")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showEditDialog = false }
                ) {
                    Text("キャンセル")
                }
            }
        )
    }

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
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .combinedClickable (
                                onClick = {
                                    onNavigateToDetail(schedule.id)
                                },
                                onLongClick = {
                                    editingSchedule = schedule
                                    newScheduleName = schedule.name
                                    showEditDialog = true
                                }
                            )
                    ) {
                        Row (verticalAlignment = Alignment.CenterVertically) {
                            Text(schedule.name, modifier = Modifier.padding(16.dp).weight(1f))
                        IconButton(onClick = { viewModel.deleteSchedule(schedule) }) {
                            Icon(Icons.Default.Delete, contentDescription = "削除")
                        }
                        }
                    }
                }
            }
        }
    }
}