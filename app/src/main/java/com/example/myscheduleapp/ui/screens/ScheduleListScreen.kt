package com.example.myscheduleapp.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.* // ★一括インポートに整理（AlertDialog, TextButton等を含む）
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
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
                    ElevatedCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 6.dp),
                        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 4.dp)
                    ) {
                        ListItem (
                            modifier = Modifier.combinedClickable(
                                onClick = {
                                    onNavigateToDetail(schedule.id)
                                },
                                onLongClick = {
                                    editingSchedule = schedule
                                    newScheduleName = schedule.name
                                    showEditDialog = true
                                }
                            ),
                            headlineContent = {
                                Text(
                                    text = schedule.name,
                                    style = MaterialTheme.typography.titleMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            },
                            supportingContent = {
                                Text(
                                    "タップしてタスクを確認",
                                    style = MaterialTheme.typography.bodySmall
                                )
                            },
                            leadingContent = {
                                Surface(
                                    color = MaterialTheme.colorScheme.primaryContainer,
                                    shape = CircleShape,
                                    modifier = Modifier.size(40.dp)
                                ){
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.DateRange,
                                            contentDescription = null,
                                            tint = MaterialTheme.colorScheme.onPrimaryContainer
                                        )
                                    }
                                }
                            },
                            trailingContent = {
                                IconButton(onClick = { viewModel.deleteSchedule(schedule) }) {
                                    Icon(
                                        imageVector = Icons.Default.Delete,
                                        contentDescription = "削除",
                                        tint = MaterialTheme.colorScheme.error
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    }
}