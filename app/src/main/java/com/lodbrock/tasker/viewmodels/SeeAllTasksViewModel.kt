package com.lodbrock.tasker.viewmodels

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.lodbrock.tasker.data.dao.TaskDao
import com.lodbrock.tasker.data.database.AppDatabase
import com.lodbrock.tasker.data.model.Task
import com.lodbrock.tasker.util.YearDayMonth
import java.util.*

class SeeAllTasksViewModel(application: Application) : AndroidViewModel(application) {

    private val tag = "SEE_ALL_TASKS_VIEW_MODEL"

    private lateinit var liveDataToClean : LiveData<List<Task>>

    private var taskDao : TaskDao = AppDatabase.getDatabase(application).taskDao()!!

    private var _allTasks : MutableLiveData<List<Task>> = MutableLiveData(listOf())

    private var _allDates : MutableLiveData<List<Calendar>> = MutableLiveData(listOf())

    val allTasksForToday: LiveData<List<Task>>
        get() = _allTasks

    val allEventDates: LiveData<List<Calendar>>
        get() = _allDates

    fun getTaskForDate(ydm: YearDayMonth) : List<Task> {
        return (_allTasks.value ?: listOf()).filter { it.setToDate == ydm }
    }


    private val dbSyncObserver = Observer<List<Task>>(){ list ->
        _allTasks.value = list
        _allDates.value = list.map { it.setToDate }.distinct().map { it.toCalendar() }
        Log.d(tag, "Got new data from database")
    }


    init {
        liveDataToClean = taskDao.allTasks()
        liveDataToClean.observeForever(dbSyncObserver)
    }

    override fun onCleared() {
        liveDataToClean.removeObserver(dbSyncObserver)
        super.onCleared()
    }


}