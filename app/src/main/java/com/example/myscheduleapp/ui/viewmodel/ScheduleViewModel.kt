package com.example.myscheduleapp.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.myscheduleapp.data.Schedule
import com.example.myscheduleapp.data.ScheduleDao
import com.example.myscheduleapp.data.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

//Serviceクラス的な役割
@HiltViewModel
class ScheduleViewModel @Inject constructor(
    private val dao: ScheduleDao
) : ViewModel() {

    val schedules: StateFlow<List<Schedule>> = dao.getAllSchedules()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun getTasks(scheduleId: Long): StateFlow<List<Task>> {
        return dao.getTasksForSchedule(scheduleId)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())
    }

    fun addSchedule(name: String) {
        viewModelScope.launch {
            dao.insertSchedule(Schedule(name = name))
        }
    }

    fun addTask(scheduleId: Long, startTime: String, title: String) {
        viewModelScope.launch {
            dao.insertTask(Task(scheduleId = scheduleId, startTime = startTime, title = title))
        }
    }

    fun deleteSchedule(schedule: Schedule) {
        viewModelScope.launch {
            dao.deleteSchedule(schedule)
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            dao.deleteTask(task)
        }
    }

}