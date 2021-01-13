package com.lodbrock.tasker.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.lodbrock.tasker.data.model.Task
import com.lodbrock.tasker.util.Converters
import com.lodbrock.tasker.util.YearDayMonth
import java.util.*

@Dao
interface TaskDao {

    @Query("SELECT * FROM task")
    fun allTasks() : LiveData<List<Task>>

    @Query("SELECT * FROM task WHERE setToDate = :date")
    fun tasksForDate(date: YearDayMonth) : LiveData<List<Task>>

    @Query("SELECT DISTINCT setToDate FROM task ")
    fun allDatesWithTasks() : LiveData<List<YearDayMonth>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addTask(task: Task)

    @Delete
    suspend fun deleteTask(task: Task) : Int

    @Update
    suspend fun updateTask(task: Task) : Int

}