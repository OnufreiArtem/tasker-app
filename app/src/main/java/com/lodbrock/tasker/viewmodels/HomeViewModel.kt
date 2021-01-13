package com.lodbrock.tasker.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.lodbrock.tasker.data.model.Task
import com.lodbrock.tasker.data.repositories.AppRepository
import com.lodbrock.tasker.util.YearDayMonth
import kotlinx.coroutines.launch


class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val tag = "HOME_VIEW_MODEL"

    private val repository = AppRepository(application)

    private var _tasksForToday = MediatorLiveData<List<Task>>()

    fun getAllTasksForToday(): LiveData<List<Task>> = _tasksForToday

    fun getInProgressTasks() : List<Task>
        = _tasksForToday.value?.let { it.filter { item -> !item.done } } ?: listOf()

    fun getDoneTasks() : List<Task>
        = _tasksForToday.value?.let { it.filter { item -> item.done } } ?: listOf()

    private val dbSyncObserver = Observer<List<Task>>(){
        _tasksForToday.value = it
        Log.d(tag, "Got new data from database")
    }

    init {
        _tasksForToday.addSource(repository.getTasksForDate(YearDayMonth.today()), dbSyncObserver)
    }

    fun addTask(task: Task) {
        viewModelScope.launch { repository.addTasks(task) }
    }

    fun makeTaskDone(task: Task) : Boolean{
        viewModelScope.launch {
            repository.makeTaskDone(task)
        }
        return true
    }

    fun makeTaskNotDone(task: Task) : Boolean{
        viewModelScope.launch {
            repository.makeTaskNotDone(task)
        }
        return true
    }

    fun deleteTask(task: Task) : Boolean {
        viewModelScope.launch {
            repository.deleteTask(task)
        }
        return true
    }
}