package com.lodbrock.tasker.viewmodels

import android.app.Application
import androidx.lifecycle.*
import com.lodbrock.tasker.data.dao.TaskDao
import com.lodbrock.tasker.data.database.AppDatabase
import com.lodbrock.tasker.data.model.Task
import com.lodbrock.tasker.util.YearDayMonth


class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private var taskDao : TaskDao = AppDatabase.getDatabase(application).taskDao()!!

    private var _tasksForToday : MutableLiveData<List<Task>> = MutableLiveData(listOf())

    val allTasksForToday: LiveData<List<Task>>
        get() = _tasksForToday

    val inProgressTasks : List<Task>
        get() = _tasksForToday.value!!.filter { !it.done }

    val doneTasks : List<Task>
        get() = _tasksForToday.value!!.filter { it.done }

    private val dbSyncObserver = Observer<List<Task>>(){
        _tasksForToday.value = it
    }

    init {
        taskDao.tasksForDate(YearDayMonth.today()).observeForever(dbSyncObserver)
    }

    fun addTask(taskToAdd: Task) {
        taskDao.addTask(taskToAdd)
    }

    fun makeTaskDone(task: Task){
        task.done = true
        taskDao.updateTask(task)
    }

    override fun onCleared() {
        allTasksForToday.removeObserver(dbSyncObserver)
        super.onCleared()
    }
}