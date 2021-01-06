package com.lodbrock.tasker.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*
import com.lodbrock.tasker.data.model.Task

class HomeViewModel : ViewModel() {

    private var _tasksForToday = MutableLiveData<MutableList<Task>>(readAllTasks())

    val allTasksForToday : LiveData<List<Task>>
        get() = MutableLiveData(_tasksForToday.value!!)

    val inProgressTasks : LiveData<List<Task>>
        get() = MutableLiveData(_tasksForToday.value!!.filter { !it.done })

    val doneTasks : LiveData<List<Task>>
        get() = MutableLiveData(_tasksForToday.value!!.filter { it.done })

    val allTasksNumber : LiveData<Int>
        get() = MutableLiveData(_tasksForToday.value!!.size)

    val doneTasksNumber : LiveData<Int>
        get() = MutableLiveData(doneTasks.value!!.size)

    fun addTask(taskToAdd: Task) {
        _tasksForToday.value!!.add(taskToAdd)
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