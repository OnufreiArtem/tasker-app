package com.lodbrock.tasker.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import java.util.*
import com.lodbrock.tasker.data.model.Task

class HomeViewModel : ViewModel() {

    private var _tasksForToday = MutableLiveData<List<Task>>(readAllTasks())

    val tasksForToday : LiveData<List<Task>>
        get() = _tasksForToday

    val inProgressTasks : LiveData<List<Task>>
        get() = MutableLiveData(_tasksForToday.value!!.filter { !it.done })

    val doneTasks : LiveData<List<Task>>
        get() = MutableLiveData(_tasksForToday.value!!.filter { it.done })

    val allTasksNumber : LiveData<Int>
        get() = MutableLiveData(_tasksForToday.value!!.size)

    val inProgressTasksNumber : LiveData<Int>
        get() = MutableLiveData(inProgressTasks.value!!.size)


    fun readAllTasks(): List<Task> {

        return listOf(
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