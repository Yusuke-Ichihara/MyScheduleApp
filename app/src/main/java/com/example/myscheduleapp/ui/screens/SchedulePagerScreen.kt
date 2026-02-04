package com.example.myscheduleapp.ui.screens

import android.util.Log
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
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
    viewModel: ScheduleViewModel,
    onNavigateToManage: () -> Unit
) {
    // 1. 全スケジュールを監視
    val schedules by viewModel.allSchedules.collectAsState(initial = emptyList())

    // 2. Pagerの状態管理
    val pagerState = rememberPagerState(
        initialPage =0,
        pageCount = { schedules.size }
    )
    val scope = rememberCoroutineScope()

    // 3. スケジュール新規作成用のダイアログ管理
    var showNewScheduleDialog by remember { mutableStateOf(false) }
    var newScheduleName by remember { mutableStateOf("") }

    // 管理画面からの選択を反映させる（データ読み込み完了時も走るように schedules をキーにする）
    LaunchedEffect(viewModel.selectedIndex, schedules) {
        if (schedules.isNotEmpty() && viewModel.selectedIndex < schedules.size) {
            pagerState.scrollToPage(viewModel.selectedIndex)
        }
    }
    // 手動スワイプを ViewModel に反映させる
    LaunchedEffect(pagerState.currentPage) {
        if (schedules.isNotEmpty()) {
            viewModel.selectSchedule(pagerState.currentPage)
        }
    }

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
    var taskStartTime by remember { mutableStateOf("") }
    var taskEndTime by remember { mutableStateOf("") }
    var taskTitle by remember { mutableStateOf("") }
    // ViewModelからエラーメッセージを取得
    val errorMessage = viewModel.taskErrorMessage

    // どの時間を編集しているかを管理するフラグ
    var pickingTimeType by remember { mutableStateOf<String?>(null) }

    if (pickingTimeType != null) {
        TimeListPickerDialog(
            initialTime = if (pickingTimeType == "start") taskStartTime else taskEndTime,
            onTimeSelected = { selectedTime ->
                if (pickingTimeType == "start") taskStartTime = selectedTime
                else taskEndTime = selectedTime
                pickingTimeType = null
            },
            onDismiss = { pickingTimeType = null }
        )
    }

    Surface(tonalElevation = 3.dp, shadowElevation = 8.dp) {
        Column(modifier = Modifier.padding(16.dp).navigationBarsPadding().imePadding()) {
            if (errorMessage != null) {
                Text(
                    text = errorMessage,
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelMedium,
                    modifier = Modifier.padding(bottom = 10.dp)
                )
            }
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                //開始時間
                TextField(
                    value = taskStartTime,
                    onValueChange = {},
                    label = { Text("時間") },
                    modifier = Modifier.weight(0.4f)
                        .clickable {
                            pickingTimeType = "start"
                            viewModel.clearTaskError()
                        },
                    enabled = false,
                    colors = TextFieldDefaults.colors(disabledTextColor = MaterialTheme.colorScheme.onSurface)
                )
                //終了時間
                TextField(
                    value = taskEndTime,
                    onValueChange = {},
                    label = { Text("終了") },
                    modifier = Modifier.weight(0.4f)
                        .clickable {
                            pickingTimeType = "end"
                            viewModel.clearTaskError()
                        },
                    enabled = false,
                    colors = TextFieldDefaults.colors(disabledTextColor = MaterialTheme.colorScheme.onSurface)
                )
                TextField(
                    value = taskTitle,
                    onValueChange = { taskTitle = it },
                    label = { Text("やること") },
                    modifier = Modifier.weight(0.6f),
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                    keyboardActions = KeyboardActions(onDone = {
                        if (taskStartTime.isNotBlank() && taskTitle.isNotBlank()) {
                            viewModel.addTask(scheduleId, taskStartTime, taskEndTime, taskTitle)
                            taskTitle = ""
                            taskStartTime = ""
                            taskEndTime = ""
                        }
                    })
                )
            }
            Button(
                onClick = {
                    viewModel.addTask(scheduleId, taskStartTime, taskEndTime, taskTitle)
                    taskStartTime = ""
                    taskEndTime = ""
                    taskTitle = ""
                },
                modifier = Modifier.align(Alignment.End).padding(top = 8.dp),
                enabled = taskStartTime.isNotBlank() && taskTitle.isNotBlank()
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

@Composable
fun TimeListPickerDialog(
    initialTime: String,
    onTimeSelected: (String) -> Unit,
    onDismiss: () -> Unit
){
    val parts = initialTime.split(":")
    var selectedHour by remember { mutableIntStateOf(parts.getOrNull(0)?.toIntOrNull() ?: 0) }
    var selectedMinute by remember { mutableIntStateOf(parts.getOrNull(1)?.toIntOrNull() ?: 0) }

    AlertDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(onClick = {
                // %：ここに変数を入れる合図
                // 0：桁が足りない場合は0で埋める
                // 2：最低でも2桁の幅を確保
                // d：流し込むデータは整数
                val formattedTime = String.format(Locale.getDefault(), "%02d:%02d", selectedHour, selectedMinute)
                onTimeSelected(formattedTime)
            })
            {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(
                onClick = onDismiss
            ){
                Text("キャンセル")
            }
        },
        title = {
            Text("時間を設定")
        },
        text = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                //"時"用にパラメータを渡す
                NumberPickerList(
                    range = 0..29,
                    selectedValue = selectedHour,
                    onValueChange = { selectedHour = it },
                    label = "時"
                )
                Text(
                    text = ":",
                    style = MaterialTheme.typography.headlineMedium
                )
                //"分"用にパラメータを渡す
                NumberPickerList(
                    range = 0..59,
                    selectedValue = selectedMinute,
                    onValueChange = { selectedMinute = it },
                    label = "分"
                )
            }
        }
    )
}

@Composable
fun NumberPickerList(
    range: IntRange, //選択可能な時間の範囲
    selectedValue: Int, // 現在選択されている値
    onValueChange: (Int) -> Unit, //値が選ばれたときに親に通知するイベント（コールバック）
    label: String // "時"、"分"
){
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            label,
            style = MaterialTheme.typography.labelSmall
        )
        LazyColumn(
            modifier = Modifier
                .width(64.dp)
                .border(1.dp,Color.LightGray.copy(alpha = 0.3f))
        ){
            // range（0..28）をリストの各項目としてループ処理
            items(range.toList()){ value ->
                // 現在の項目が「選択されているか」を判定
                val isSelected = value == selectedValue
                // 各数字の表示
                Text(
                    text = String.format(Locale.getDefault(), "%02d", value),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onValueChange(value) } // タップされたら親の関数を呼び出す
                        .background(
                            if (isSelected) MaterialTheme.colorScheme.primaryContainer
                            else Color.Transparent
                        )
                        .padding(8.dp),
                    textAlign = TextAlign.Center,
                    style = if (isSelected) MaterialTheme.typography.titleLarge
                            else MaterialTheme.typography.bodyLarge,
                    color = if (isSelected) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurface
                )
            }
        }
    }
}