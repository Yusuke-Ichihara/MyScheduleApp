package com.example.myscheduleapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myscheduleapp.data.Schedule
import com.example.myscheduleapp.data.ScheduleDao
import com.example.myscheduleapp.data.ScheduleRepository
import com.example.myscheduleapp.data.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

//Serviceクラス的な役割
@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val scheduleRepository: ScheduleRepository
) : ViewModel() {

    val allSchedules = scheduleRepository.allSchedules

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
        viewModelScope.launch {
            scheduleRepository.addTask(Task(scheduleId = scheduleId, startTime = startTime, endTime = endTime, title = title))
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
}