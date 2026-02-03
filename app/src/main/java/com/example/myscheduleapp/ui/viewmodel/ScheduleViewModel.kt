package com.example.myscheduleapp.ui.viewmodel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myscheduleapp.data.Schedule
import com.example.myscheduleapp.data.ScheduleDao
import com.example.myscheduleapp.data.ScheduleRepository
import com.example.myscheduleapp.data.Task
import com.example.myscheduleapp.ui.screens.timeToMinutes
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

//Serviceクラス的な役割
@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {

    val allSchedules = scheduleRepository.allSchedules
    var taskErrorMessage by mutableStateOf<String?>(null)
        private set

    fun addSchedule(name: String) {
        viewModelScope.launch {
            scheduleRepository.addSchedule(name)
        }
    }

    fun updateSchedule(schedule: Schedule) {
        viewModelScope.launch {
            scheduleRepository.updateSchedule(schedule)
        }
    }

    fun deleteSchedule(schedule: Schedule) {
        viewModelScope.launch {
            scheduleRepository.deleteSchedule(schedule)
        }
    }

    fun getTasks(scheduleId: Long): Flow<List<Task>> {
        return scheduleRepository.getTasks(scheduleId)
    }

    fun addTask(scheduleId: Long, startTime: String, endTime : String, title: String) {
        // 開始と終了が空なら何もしない
        if (startTime.isBlank() || endTime.isBlank()) return

        taskErrorMessage = null

        viewModelScope.launch {
            //.first()でFlowの値を取得しList型の変数へ代入
            val currentTasks: List<Task> = scheduleRepository.getTasks(scheduleId).first()

            val newStart = timeToMinutes(startTime)
            val newEnd = timeToMinutes(endTime)

            // 開始が終了より後の場合は登録拒否
            if (newStart > newEnd) {
                taskErrorMessage = "終了時間は開始時間より後にしてください"
                return@launch
            }

            val isOverlap = currentTasks.any { existing ->
                val exStart = timeToMinutes(existing.startTime)
                val exEnd = timeToMinutes(existing.endTime)
                newStart < exEnd && exStart < newEnd
            }

            if (isOverlap) {
                taskErrorMessage = "時間が被っています"
                return@launch
            } else {
                scheduleRepository.addTask(Task(scheduleId = scheduleId, startTime = startTime, endTime = endTime, title = title))
                taskErrorMessage = null
            }
        }
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            scheduleRepository.updateTask(task)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            scheduleRepository.deleteTask(task)
        }
    }

    fun clearTaskError() {
        taskErrorMessage = null
    }
}