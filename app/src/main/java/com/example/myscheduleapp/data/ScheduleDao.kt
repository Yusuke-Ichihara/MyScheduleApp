package com.example.myscheduleapp.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ScheduleDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSchedule(schedule: Schedule): Long

    @Query("SELECT * FROM schedules ORDER BY id DESC")
    fun getAllSchedules(): Flow<List<Schedule>>

    @Update
    suspend fun updateSchedule(schedule: Schedule)

    @Delete
    suspend fun deleteSchedule(schedule: Schedule)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTask(task: Task)

    @Query("SELECT * FROM tasks WHERE scheduleId = :scheduleId ORDER BY startTime")
    fun getTasksForSchedule(scheduleId: Long): Flow<List<Task>>

    @Update
    suspend fun updateTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task)

}