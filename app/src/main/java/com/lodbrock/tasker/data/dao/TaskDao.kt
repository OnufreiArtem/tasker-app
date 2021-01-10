package com.lodbrock.tasker.data.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import com.lodbrock.tasker.data.model.Task
import com.lodbrock.tasker.util.Converters
import com.lodbrock.tasker.util.YearDayMonth

@Dao
interface TaskDao {

    @Query("SELECT * FROM task")
    fun allTasks() : LiveData<List<Task>>

    @Query("SELECT * FROM task WHERE setToDate = :date")
    fun tasksForDate(date: YearDayMonth) : LiveData<List<Task>>

    @Insert
    fun addTask(task: Task)

    @Delete
    fun deleteTask(task: Task)

    @Update
    fun updateTask(task: Task)


}