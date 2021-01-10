package com.lodbrock.tasker.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import com.lodbrock.tasker.data.dao.TaskDao
import com.lodbrock.tasker.data.database.AppDatabase
import com.lodbrock.tasker.data.model.Task

class AddTaskViewModel(application: Application) : AndroidViewModel(application) {

    private val tag = "ADD_TASK_VIEW_MODEL"

    private var taskDao : TaskDao = AppDatabase.getDatabase(application).taskDao()!!

    fun addTask(task: Task) {
        taskDao.addTask(task)
        Log.d(tag, "Added $task")
    }
}