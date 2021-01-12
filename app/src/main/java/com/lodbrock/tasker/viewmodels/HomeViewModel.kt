package com.lodbrock.tasker.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.*
import com.lodbrock.tasker.data.dao.TaskDao
import com.lodbrock.tasker.data.database.AppDatabase
import com.lodbrock.tasker.data.model.Task
import com.lodbrock.tasker.util.YearDayMonth


class HomeViewModel(application: Application) : AndroidViewModel(application) {

    private val tag = "HOME_VIEW_MODEL"

    private var taskDao : TaskDao = AppDatabase.getDatabase(application).taskDao()!!

    private var _tasksForToday : MutableLiveData<List<Task>> = MutableLiveData(listOf())

    private lateinit var liveDataToClear : LiveData<List<Task>>

    val allTasksForToday: LiveData<List<Task>>
        get() = _tasksForToday

    val inProgressTasks : List<Task>
        get() = _tasksForToday.value!!.filter { !it.done }

    val doneTasks : List<Task>
        get() = _tasksForToday.value!!.filter { it.done }

    private val dbSyncObserver = Observer<List<Task>>(){
        _tasksForToday.value = it
        Log.d(tag, "Got new data from database")
    }

    init {
        liveDataToClear = taskDao.tasksForDate(YearDayMonth.today())
        liveDataToClear.observeForever(dbSyncObserver)
    }

    fun addTask(task: Task) {
        taskDao.addTask(task)
        Log.d(tag, "Added $task")
    }

    fun makeTaskDone(task: Task) : Boolean{
        task.done = true
        val done = taskDao.updateTask(task) != 0
        Log.d(tag,  if (done) "Updated $task" else "Failed to update $task to be done")
        return done
    }

    fun makeTaskNotDone(task: Task) : Boolean{
        task.done = false
        val done = taskDao.updateTask(task) != 0
        Log.d(tag,  if (done) "Updated $task" else "Failed to update $task to be not done")
        return done
    }

    fun deleteTask(task: Task) : Boolean {
        val done = taskDao.deleteTask(task) != 0
        Log.d(tag, if (done)"$task was deleted" else "Failed to delete $task")
        return done
    }

    override fun onCleared() {
        liveDataToClear.removeObserver(dbSyncObserver)
        super.onCleared()
    }
}