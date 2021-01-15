package com.lodbrock.tasker.data.repositories

import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.MutableLiveData
import com.lodbrock.tasker.data.database.AppDatabase
import com.lodbrock.tasker.data.model.Task
import com.lodbrock.tasker.util.YearDayMonth
import java.util.*

class AppRepository(context: Context) {

    private val tag = "APP_REPOSITORY"

    private val database = AppDatabase.getDatabase(context)
    private val taskDao = database.taskDao()

    fun allTasks() : LiveData<List<Task>> {
        return taskDao?.allTasks() ?: MutableLiveData(listOf())
    }

    fun getTasksForDate(date: YearDayMonth) : LiveData<List<Task>> {
        return taskDao?.tasksForDate(date) ?: MutableLiveData(listOf())
    }

    fun getTaskById(id: Long) : LiveData<Task?>{
        var found = false
        val taskLiveData = MediatorLiveData<Task?>()

        taskDao?.let {
            taskLiveData.addSource(taskDao.taskById(id)) { taskLiveData.value = it }
            taskLiveData.value?.let { found = true }

        } ?:

        Log.d(tag, if(found) "Found ${taskLiveData.value}" else "Unable to find task with id=$id")

        return taskLiveData
    }


    suspend fun addTasks(task: Task){
        taskDao?.let {
            it.addTask(task)
            Log.d(tag, "Added $task")
        } ?: Log.d(tag, "Unable add $task")
    }


    suspend fun editTask(task: Task) {
        taskDao?.let {
            task.modifiedAt = Calendar.getInstance()
            it.updateTask(task)
            Log.d(tag, "Edited to $task")
        } ?: Log.d(tag, "Unable to edit $task")

    }

    suspend fun makeTaskDone(task: Task){
        taskDao?.let {
            it.updateTask( task.apply {
                done = true
                modifiedAt = Calendar.getInstance()
            } )
            Log.d(tag, "Task $task was updated and made done")
        } ?: Log.d(tag, "Unable to update (make not done) $task")
    }

    suspend fun makeTaskNotDone(task: Task){
        taskDao?.let {
            if(it.updateTask( task.apply {
                done = false
                modifiedAt = Calendar.getInstance()
            } ) > 0)
                Log.d(tag, "Task $task was updated and made not done")
        } ?: Log.d(tag, "Unable to update (make done) $task")
    }

    suspend fun deleteTask(task: Task) {
        taskDao?.let {
            if( it.deleteTask(task) > 0 ) Log.d(tag, "Task $task was deleted")
        } ?: Log.d(tag, "Unable to delete $task")
    }

    fun allDatesWithTasks() : LiveData<List<YearDayMonth>> {
        return taskDao?.allDatesWithTasks() ?: MutableLiveData(listOf())
    }
}