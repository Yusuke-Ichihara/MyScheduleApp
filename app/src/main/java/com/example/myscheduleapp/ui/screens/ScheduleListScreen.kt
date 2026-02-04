package com.example.myscheduleapp.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.* // ★一括インポートに整理（AlertDialog, TextButton等を含む）
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
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
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp) //カード間のすきま
        ) {
            itemsIndexed(
                schedules, key = {_, schedule -> schedule.id}
            ) { index, schedule ->
                ScheduleCardItem(
                    schedule = schedule,
                    onClick = {
                      viewModel.selectSchedule(index)
                      onBack()
                    },
                    onDelete = { viewModel.deleteSchedule(schedule) }
                )
            }
        }
    }
}