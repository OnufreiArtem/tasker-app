package com.lodbrock.tasker.viewmodels

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*
import com.lodbrock.tasker.data.model.Task

class HomeViewModel : ViewModel() {

    private var _tasksForToday = MutableLiveData<MutableList<Task>>(readAllTasks())

    val allTasksForToday: LiveData<List<Task>>
        get() = _tasksForToday as LiveData<List<Task>>

    val inProgressTasks : List<Task>
        get() = _tasksForToday.value!!.filter { !it.done }

    val doneTasks : List<Task>
        get() = _tasksForToday.value!!.filter { it.done }

    fun addTask(taskToAdd: Task) {
        val tasks = _tasksForToday.value!!
        tasks.add(taskToAdd)
        _tasksForToday.value = tasks
        Log.i("HomeViewModel", "Added task $taskToAdd")
    }

    fun makeTaskDone(task: Task){
        for(x in 0 until _tasksForToday.value!!.size) {
            if (_tasksForToday.value!![x] == task) {
                task.done = true
                val tasks = _tasksForToday.value!!
                tasks[x] = task
                _tasksForToday.value = tasks
                Log.i("HomeViewModel", "Such Task was found")
                break
            }
        }
    }

    private fun readAllTasks(): MutableList<Task> {

        return mutableListOf(
                Task(title = "Task 1", setToDate = Calendar.getInstance()),
                Task(title = "Task 2", setToDate = Calendar.getInstance()),
                Task(title = "Task 3", setToDate = Calendar.getInstance(), done = true),
                Task(title = "Task 4", setToDate = Calendar.getInstance()),
                Task(title = "Task 5", setToDate = Calendar.getInstance()),
                Task(title = "Task 6", setToDate = Calendar.getInstance(), done = true),
                Task(title = "Task 7", setToDate = Calendar.getInstance()),
                Task(title = "Task 8", setToDate = Calendar.getInstance(), done = true),
                Task(title = "Task 9", setToDate = Calendar.getInstance(), done = true)
        )
    }






}