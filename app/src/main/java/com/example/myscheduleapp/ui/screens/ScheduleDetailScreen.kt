package com.example.myscheduleapp.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.border
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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
        },
        bottomBar = {
            Surface(
                tonalElevation = 3.dp,
                shadowElevation = 8.dp,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .navigationBarsPadding()
                        .imePadding()
                ) {
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
                            placeholder = { Text("例: 掃除する") },
                            modifier = Modifier.weight(1f),
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                            keyboardActions = KeyboardActions(
                                onDone = {
                                    if (taskTitle.isNotBlank()) {
                                        viewModel.addTask(scheduleId, taskTime, taskTitle)
                                        taskTitle = ""
                                    }
                                }
                            )
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
        }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            val containerColor = if (editingTask != null) {
                MaterialTheme.colorScheme.primaryContainer
            } else {
                MaterialTheme.colorScheme.surfaceVariant
            }
            if (tasks.isEmpty()) {
                Box (
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text ="タスクがありません。下から追加しましょう！",
                        color = MaterialTheme.colorScheme.outline
                    )
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                ) {
                    itemsIndexed(tasks) { index, task ->
                        val isFirstItem = index == 0
                        val isLastItem = index == tasks.lastIndex
                        TaskTimelineItem(
                            task = task,
                            isFirst = isFirstItem,
                            isLast = isLastItem,
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
}

@Composable
fun TaskTimelineItem(
    task: Task,
    isFirst: Boolean,
    isLast: Boolean,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    val timelineColor = MaterialTheme.colorScheme.primary
    val cardColors = getCardColorsByTime(task.startTime)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(IntrinsicSize.Min)
    ) {
        //左側（線と点）
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.width(40.dp)
        ) {
            //上に向かう線
            if (!isFirst) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(timelineColor)
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
            Box(
                modifier = Modifier
                    .size(12.dp)
                    .background(timelineColor, CircleShape)
                    .border(2.dp, MaterialTheme.colorScheme.surface, CircleShape)
            )
            //下に向かう線
            if (!isLast) {
                Box(
                    modifier = Modifier
                        .width(2.dp)
                        .weight(1f)
                        .background(timelineColor)
                )
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }
        }
        //右側
        ElevatedCard(
            onClick = onEdit,
            modifier = Modifier
                .weight(1f)
                .padding(start = 8.dp, bottom = 16.dp),
            colors = cardColors,
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 0.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    //時刻表示（少し控えめ）
                    Text(
                        text = task.startTime,
                        style = MaterialTheme.typography.headlineSmall,
                        color = cardColors.contentColor.copy(alpha = 1f),
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 2.sp
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    //タスク名（大きくハッキリ）
                    Text(
                        text = task.title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontWeight = FontWeight.Medium
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(
                        Icons.Default.DeleteOutline,
                        contentDescription = "削除",
                        tint = MaterialTheme.colorScheme.error.copy(alpha = 0.7f)
                    )
                }
            }
        }
    }
}

@Composable
fun getCardColorsByTime(startTime: String): CardColors {
    //"HH:mm"の最初の2文字をIntに変換
    val hour = startTime.take(2).toIntOrNull() ?:0

    return when (hour) {
        in 5..10 -> {
            CardDefaults.elevatedCardColors(
                containerColor = Color(0xFFE8F5E9), // 優しい緑
                contentColor = Color(0xFF2E7D32)    // 濃い緑（文字用）
            )
        }
        in 11..18 -> {
            CardDefaults.elevatedCardColors(
                containerColor = Color(0xFFFFF3E0), // 優しいオレンジ
                contentColor = Color(0xFFE65100)    // 濃いオレンジ（文字用）
            )
        }
        else -> {
            CardDefaults.elevatedCardColors(
                containerColor = Color(0xFFE3F2FD), // 優しい青
                contentColor = Color(0xFF1565C0)    // 濃い青（文字用）
            )
        }
    }
}