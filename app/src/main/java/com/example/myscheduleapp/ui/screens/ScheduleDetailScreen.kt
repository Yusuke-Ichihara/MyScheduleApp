package com.example.myscheduleapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import java.util.Locale
import com.example.myscheduleapp.ui.viewmodel.ScheduleViewModel
import com.example.myscheduleapp.data.Task

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleDetailScreen(
    scheduleId: Long,
    onBack: () -> Unit,
    viewModel: ScheduleViewModel
) {

    val tasks by viewModel.getTasks(scheduleId).collectAsState(initial = emptyList())
    var taskTime by remember { mutableStateOf("") }
    var taskTitle by remember { mutableStateOf("") }
    var editingTask by remember { mutableStateOf<Task?>(null) }
    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(is24Hour = true)

    if (showTimePicker) {
        AlertDialog (
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    taskTime = String.format(Locale.getDefault(), "%02d:%02d", timePickerState.hour, timePickerState.minute)
                    showTimePicker = false
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showTimePicker = false }) { Text("キャンセル") }
            },
            text = {
                TimePicker(state = timePickerState)
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("スケジュール詳細") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "戻る")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .padding(16.dp)
        ) {
            val containerColor = if (editingTask != null) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }

            ElevatedCard (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = containerColor
                )
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // 編集モード中であることを示すラベルとキャンセルボタン
                    if (editingTask != null) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically){
                                Icon(
                                    Icons.Default.Edit,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = "タスクを編集中...",
                                    style = MaterialTheme.typography.labelLarge,
                                    color = MaterialTheme.colorScheme.primary,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            TextButton(
                                onClick = {
                                    editingTask = null
                                    taskTime = ""
                                    taskTitle = ""
                                }) {
                                    Text(
                                        text ="キャンセルして新規作成",
                                        color = MaterialTheme.colorScheme.error,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        TextField(
                            value = taskTime,
                            onValueChange = { },
                            label = { Text("時間") },
                            modifier = Modifier
                                .weight(1f)
                                .clickable { showTimePicker = true },
                            enabled = false,
                            colors = TextFieldDefaults.colors(
                                disabledTextColor = MaterialTheme.colorScheme.onSurface,
                                disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                            ),
                            placeholder = { Text("例: 10:00") },
                            leadingIcon = { Icon(Icons.Default.Schedule, null, modifier = Modifier.size(16.dp)) }
                        )
                        TextField(
                            value = taskTitle,
                            onValueChange = { taskTitle = it },
                            label = { Text("やること") },
                            modifier = Modifier.weight(1f),
                            placeholder = { Text("例: 掃除する") }
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(
                        onClick = {
                        if (editingTask == null) {
                            viewModel.addTask(scheduleId, taskTime, taskTitle)
                        } else {
                            viewModel.updateTask(editingTask!!.copy(startTime = taskTime, title = taskTitle))
                            editingTask = null
                        }
                            taskTime = ""
                            taskTitle = ""
                    },
                        modifier = Modifier.align(Alignment.End),
                        enabled = taskTime.isNotBlank() && taskTitle.isNotBlank()
                    ) {
                        Text(if (editingTask == null) "タスク追加" else "更新保存")
                    }
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
            ) {
                items(tasks) { task ->
                    TaskTimelineItem(
                        task = task,
                        onEdit = {
                            editingTask = task
                            taskTime = task.startTime
                            taskTitle = task.title
                        },
                        onDelete = { viewModel.deleteTask(task) }
                    )
                }
            }
        }
    }
}

@Composable
fun TaskTimelineItem(
    task: Task,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() }
            .padding(vertical = 4.dp),
        verticalAlignment = Alignment.Top
    ) {
        // タイムラインの「線とドット」
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(40.dp)
        ) {
            Box(
               modifier = Modifier
                   .size(12.dp)
                   .background(MaterialTheme.colorScheme.primary, CircleShape)
           )
            Box(
                modifier = Modifier
                    .width(2.dp)
                    .height(60.dp)
                    .background(MaterialTheme.colorScheme.outlineVariant)
            )
        }
        // タスクの中身
        Column(modifier = Modifier.weight(1f)){
            AssistChip(
                onClick = { },
                label = { Text(task.startTime, fontWeight = FontWeight.Bold) },
                leadingIcon = { Icon(Icons.Default.Schedule, null, modifier = Modifier.size(16.dp)) }
            )
            Text(
                text = task.title,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(vertical = 4.dp)
            )
        }

        IconButton(onClick = onDelete){
            Icon(Icons.Default.Delete, contentDescription = "削除", tint = MaterialTheme.colorScheme.error)

        }

    }
}