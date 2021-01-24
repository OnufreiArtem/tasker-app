package com.lodbrock.tasker.ui.adapters

import com.lodbrock.tasker.data.model.Task

interface TaskRecyclerAdapter {
    fun setTasks(tasks: List<Task>)
    fun getTaskAtPosition(position: Int) : Task?
}