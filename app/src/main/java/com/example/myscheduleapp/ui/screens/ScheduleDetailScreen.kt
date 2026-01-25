package com.example.myscheduleapp.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import java.util.Locale
import com.example.myscheduleapp.ui.viewmodel.ScheduleViewModel

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
    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(is24Hour = true)

    if (showTimePicker) {
        DatePickerDialog (
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    taskTime = String.format(Locale.getDefault(), "%02d:%02d", timePickerState.hour, timePickerState.minute)
                    showTimePicker = false
                }) { Text("OK") }
            }
        ) {
            TimePicker(state = timePickerState)
        }
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
        Column(modifier = Modifier.padding(padding).padding(16.dp)) {
            Card (
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    TextField(
                        value = taskTime,
                        onValueChange = { },
                        label = { Text("開始時間") },
                        modifier = Modifier.fillMaxWidth().clickable { showTimePicker = true },
                        enabled = false,
                        colors = OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    TextField(
                        value = taskTitle,
                        onValueChange = { taskTitle = it },
                        label = { Text("やること") },
                        modifier = Modifier.fillMaxWidth()
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Button(onClick = {
                        if (taskTime.isNotBlank() && taskTitle.isNotBlank()) {
                            viewModel.addTask(scheduleId, taskTime, taskTitle)
                            taskTime = ""; taskTitle = ""

                        }
                    },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("タスク追加")
                    }
                }
            }
            Spacer(modifier = Modifier.height(16.dp))

            Text(text = "タスク一覧", style = MaterialTheme.typography.headlineMedium)

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
            ) {
                items(tasks) { task ->
                    ListItem(
                        headlineContent = { Text(task.title) },
                        supportingContent = { Text(task.startTime) },
                        trailingContent = {
                            IconButton(onClick = { viewModel.deleteTask(task) }) {
                                Icon(Icons.Default.Delete, contentDescription = "削除")
                            }
                        }
                    )
                    HorizontalDivider()
                }
            }
        }
    }
}