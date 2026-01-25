package com.example.myscheduleapp.data

import com.example.myscheduleapp.data.Schedule
import com.example.myscheduleapp.data.ScheduleDao
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScheduleRepository @Inject constructor (
    private val scheduleDao: ScheduleDao
){

    val allSchedules: Flow<List<Schedule>> = scheduleDao.getAllSchedules()

    suspend fun addSchedule(name: String) {
        scheduleDao.insertSchedule(Schedule(name = name))
    }

    suspend fun updateSchedule(schedule: Schedule) {
        scheduleDao.updateSchedule(schedule)
    }

    suspend fun deleteSchedule(schedule: Schedule) {
        scheduleDao.deleteSchedule(schedule)
    }

    fun getTasks(scheduleId: Long): Flow<List<Task>> {
        return scheduleDao.getTasksForSchedule(scheduleId)
    }

    suspend fun addTask(task: Task) {
        scheduleDao.insertTask(task)
    }

    suspend fun updateTask(task: Task) {
        scheduleDao.updateTask(task)
    }

    suspend fun deleteTask(task: Task) {
        scheduleDao.deleteTask(task)
    }

}