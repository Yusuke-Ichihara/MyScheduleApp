package com.example.myscheduleapp.ui.screens

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myscheduleapp.data.Schedule
import com.example.myscheduleapp.data.Task
import com.example.myscheduleapp.ui.viewmodel.ScheduleViewModel
import kotlinx.coroutines.launch
import java.util.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun SchedulePagerScreen(
    viewModel: ScheduleViewModel, onNavigateToManage: () -> Unit
) {
    // 1. 全スケジュールを監視
    val schedules by viewModel.allSchedules.collectAsState(initial = emptyList())

    // 2. Pagerの状態管理
    val pagerState = rememberPagerState(pageCount = { schedules.size })
    val scope = rememberCoroutineScope()

    // 3. スケジュール新規作成用のダイアログ管理
    var showNewScheduleDialog by remember { mutableStateOf(false) }
    var newScheduleName by remember { mutableStateOf("") }

    if (showNewScheduleDialog) {
        AlertDialog(
            onDismissRequest = { showNewScheduleDialog = false },
            title = { Text("新しいスケジュール(日)を作成") },
            text = {
                TextField(
                    value = newScheduleName,
                    onValueChange = { newScheduleName = it },
                    placeholder = { Text("例: 2026年2月1日") },
                    singleLine = true
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    if (newScheduleName.isNotBlank()) {
                        viewModel.addSchedule(newScheduleName)
                        newScheduleName = ""
                        showNewScheduleDialog = false
                    }
                }) { Text("作成") }
            },
            dismissButton = {
                TextButton(onClick = { showNewScheduleDialog = false }) { Text("キャンセル") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    // スケジュール切り替え「＜ 名前 ＞」部分
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center
                    ) {
                        if (schedules.isNotEmpty()) {
                            IconButton(
                                onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage - 1) } },
                                enabled = pagerState.currentPage > 0
                            ) { Icon(Icons.Default.ChevronLeft, "前へ") }

                            AutoResizingText(
                                text = schedules[pagerState.currentPage].name,
                                modifier = Modifier.weight(1f)
                            )

                            IconButton(
                                onClick = { scope.launch { pagerState.animateScrollToPage(pagerState.currentPage + 1) } },
                                enabled = pagerState.currentPage < schedules.size - 1
                            ) { Icon(Icons.Default.ChevronRight, "次へ") }
                        } else {
                            Text("スケジュールがありません")
                        }
                    }
                },
                actions = {
                    IconButton(onClick = onNavigateToManage) {
                        Icon(Icons.Default.List, contentDescription = "スケジュール管理")
                    }

                    // ★ スケジュール（日）の新規追加：マクロな操作
                    IconButton(onClick = { showNewScheduleDialog = true }) {
                        Icon(Icons.Default.PostAdd, contentDescription = "日を追加")
                    }
                }
            )
        },
        bottomBar = {
            // 現在のページのスケジュールIDを取得して、タスク入力欄を表示
            val currentScheduleId = schedules.getOrNull(pagerState.currentPage)?.id
            if (currentScheduleId != null) {
                TaskInputSection(scheduleId = currentScheduleId, viewModel = viewModel)
            }
        }
    ) { padding ->
        if (schedules.isEmpty()) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text("右上のアイコンからスケジュールを作成してください")
            }
        } else {
            HorizontalPager(
                state = pagerState,
                modifier = Modifier.fillMaxSize().padding(padding)
            ) { pageIndex ->
                // 各ページの中身（タスク一覧）
                val scheduleId = schedules[pageIndex].id
                TaskListSection(scheduleId = scheduleId, viewModel = viewModel)
            }
        }
    }
}

// --- 以下、役割ごとに Composable を分離（リファクタリング） ---

@Composable
fun TaskListSection(scheduleId: Long, viewModel: ScheduleViewModel) {
    val tasks by viewModel.getTasks(scheduleId).collectAsState(initial = emptyList())

    if (tasks.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Text("タスクがありません。下から追加しましょう！", color = Color.Gray)
        }
    } else {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(16.dp)
        ) {
            itemsIndexed(tasks) { index, task ->
                TaskTimelineItem(
                    task = task,
                    isFirst = index == 0,
                    isLast = index == tasks.lastIndex,
                    onEdit = { /* 編集ロジック（必要に応じて実装） */ },
                    onDelete = { viewModel.deleteTask(task) }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskInputSection(scheduleId: Long, viewModel: ScheduleViewModel) {
    var taskTime by remember { mutableStateOf("") }
    var taskTitle by remember { mutableStateOf("") }
    var showTimePicker by remember { mutableStateOf(false) }
    val timePickerState = rememberTimePickerState(is24Hour = true)

    if (showTimePicker) {
        AlertDialog(
            onDismissRequest = { showTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    taskTime = String.format(Locale.getDefault(), "%02d:%02d", timePickerState.hour, timePickerState.minute)
                    showTimePicker = false
                }) { Text("OK") }
            },
            text = { TimePicker(state = timePickerState) }
        )
    }

    Surface(tonalElevation = 3.dp, shadowElevation = 8.dp) {
        Column(modifier = Modifier.padding(16.dp).navigationBarsPadding().imePadding()) {
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextField(
                    value = taskTime,
                    onValueChange = {},
                    label = { Text("時間") },
                    modifier = Modifier.weight(0.4f).clickable { showTimePicker = true },
                    enabled = false,
                    leadingIcon = { Icon(Icons.Default.Schedule, null) }
                )
                TextField(
                    value = taskTitle,
                    onValueChange = { taskTitle = it },
                    label = { Text("やること") },
                    modifier = Modifier.weight(0.6f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        if (taskTime.isNotBlank() && taskTitle.isNotBlank()) {
                            viewModel.addTask(scheduleId, taskTime, taskTitle)
                            taskTitle = ""
                            taskTime = ""
                        }
                    })
                )
            }
            Button(
                onClick = {
                    viewModel.addTask(scheduleId, taskTime, taskTitle)
                    taskTime = ""
                    taskTitle = ""
                },
                modifier = Modifier.align(Alignment.End).padding(top = 8.dp),
                enabled = taskTime.isNotBlank() && taskTitle.isNotBlank()
            ) {
                Text("タスク追加")
            }
        }
    }
}

@Composable
fun AutoResizingText(
    text: String,
    modifier: Modifier = Modifier
){
    // 1. フォントサイズの初期値を定義
    var fontSize by remember { mutableStateOf(22.sp) } // titleLarge相当の初期値
    var readyToDraw by remember { mutableStateOf(false) }

    Text(
        text = text,
        modifier = modifier.drawWithContent {
            // 3. サイズ調整が終わるまで描画を隠す（チラつき防止）
            if (readyToDraw) drawContent()
        },
        fontSize = fontSize,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
        maxLines = 1, // 1行に収める
        softWrap = false, // 自動改行をオフ
        onTextLayout = { textLayoutResult ->
            // 2. 「はみ出しているか」を判定
            if (textLayoutResult.hasVisualOverflow) {
                // はみ出していたら 0.9倍 して再計算
                fontSize = fontSize * 0.9f
            } else {
                // 収まったら描画OK
                readyToDraw = true
            }
        }
    )
}