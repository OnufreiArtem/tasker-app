package com.lodbrock.tasker.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.lodbrock.tasker.data.model.Task
import com.lodbrock.tasker.data.repositories.AppRepository
import kotlinx.coroutines.launch

class AddTaskViewModel(application: Application) : AndroidViewModel(application) {

    private val tag = "ADD_TASK_VIEW_MODEL"

    private val repository = AppRepository(application)

    fun addTask(task: Task) {
        viewModelScope.launch {
            repository.addTasks(task)
        }
    }
}