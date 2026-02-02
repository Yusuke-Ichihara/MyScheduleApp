package com.example.myscheduleapp.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditCalendar
import androidx.compose.material3.* // ★一括インポートに整理（AlertDialog, TextButton等を含む）
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.*
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.myscheduleapp.data.Schedule // ★追加
import com.example.myscheduleapp.ui.viewmodel.ScheduleViewModel


@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ScheduleListScreen(
    viewModel: ScheduleViewModel,
    onBack: () -> Unit
) {
    val schedules by viewModel.allSchedules.collectAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("スケジュールの管理") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "戻る")
                    }
                }
            )
        }
    ){
        padding ->
        LazyColumn(modifier = Modifier.padding(padding)) {
            items(schedules) { schedule ->
                ListItem(
                    headlineContent = { Text(schedule.name) },
                    trailingContent = {
                        IconButton(onClick = {viewModel.deleteSchedule(schedule)}){
                            Icon(Icons.Default.Delete, tint = Color.Red, contentDescription = "削除")
                        }
                    }
                )
                HorizontalDivider()
            }
        }
    }

}