package com.lodbrock.tasker.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.lodbrock.tasker.data.model.Task
import com.lodbrock.tasker.data.repositories.AppRepository

class TaskViewViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = AppRepository(application)
    private val _selectedTask = MediatorLiveData<Task?>()

    fun getSelectedTask() : LiveData<Task?> = _selectedTask

    fun initTask(id: Long) : Task? {
        _selectedTask.addSource(repository.getTaskById(id)) { _selectedTask.value = it }
        return _selectedTask.value
    }


}