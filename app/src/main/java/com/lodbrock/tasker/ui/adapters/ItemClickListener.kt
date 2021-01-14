package com.lodbrock.tasker.ui.adapters

import com.lodbrock.tasker.data.model.Task

interface ItemClickListener {
    fun onClick(task: Task, position: Int)
}