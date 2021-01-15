package com.lodbrock.tasker.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import androidx.lifecycle.viewModelScope
import com.lodbrock.tasker.data.model.Task
import com.lodbrock.tasker.data.repositories.AppRepository
import kotlinx.coroutines.launch

class EditTaskViewModel(application: Application) : AndroidViewModel(application) {

    private val tag = "ADD_TASK_VIEW_MODEL"

    private val repository = AppRepository(application)

    private val _selectedTask = MediatorLiveData<Task?>()

    fun getSelectedTask() : LiveData<Task?> = _selectedTask

    fun initTask(id: Long) : Task? {
        _selectedTask.addSource(repository.getTaskById(id)) { _selectedTask.value = it }
        return _selectedTask.value
    }

    fun editTask(task: Task) {
        viewModelScope.launch {
            repository.editTask(task)
        }
    }
}