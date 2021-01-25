package com.lodbrock.tasker.viewmodels

import android.app.Application
import android.view.Display
import androidx.lifecycle.*
import com.lodbrock.tasker.data.model.Task
import com.lodbrock.tasker.data.repositories.AppRepository
import com.lodbrock.tasker.util.YearDayMonth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.Calendar

class SeeAllTasksViewModel(application: Application) : AndroidViewModel(application) {

    private val tag = "SEE_ALL_TASKS_VIEW_MODEL"

    private val repository = AppRepository(application)

    private var currentTasksLiveData : LiveData<List<Task>>

    private var _allTasksForToday = MediatorLiveData<List<Task>>()
    private var _allEventDates = MediatorLiveData<List<Calendar>>()

    fun getAllTasksForToday(): LiveData<List<Task>> {
       return _allTasksForToday
    }

    fun getAllEventDates(): LiveData<List<Calendar>> {
        return _allEventDates
    }

    fun addTask(task: Task) {
        viewModelScope.launch(Dispatchers.IO) { repository.addTasks(task) }
    }

    fun deleteTask(task: Task) : Boolean {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteTask(task)
        }
        return true
    }

    private val allTasksObserver : Observer<List<Task>> = Observer(){
        _allTasksForToday.value = it
    }

    fun switchDayLiveData(dateToSwitch: YearDayMonth) {
        _allTasksForToday.removeSource(currentTasksLiveData)
        currentTasksLiveData = repository.getTasksForDate(dateToSwitch)
        _allTasksForToday.addSource(currentTasksLiveData, allTasksObserver)
    }

    init {
        currentTasksLiveData = repository.getTasksForDate(YearDayMonth.today())
        _allTasksForToday.addSource(currentTasksLiveData, allTasksObserver)
        _allEventDates.addSource(repository.allDatesWithTasks()) {
            viewModelScope.launch(Dispatchers.Default) {
                val calendarList = it.map { item -> item.toCalendar() }
                withContext(Dispatchers.Main){
                    _allEventDates.value = calendarList
                }
            }
        }
    }


}