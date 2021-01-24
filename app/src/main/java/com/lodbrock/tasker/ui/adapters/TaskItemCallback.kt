package com.lodbrock.tasker.ui.adapters

import android.view.View
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.lodbrock.tasker.data.model.Task

abstract class TaskItemCallback (val adapter: TaskRecyclerAdapter)
     : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

     abstract fun onSwipeLeft(selectedTask : Task?, itemView: View)
     abstract fun onSwipeRight(selectedTask : Task?, itemView: View)

     override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder,
    ): Boolean {
        return false
    }

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
        val selectedTask = adapter.getTaskAtPosition(viewHolder.adapterPosition)

        when(direction) {
            ItemTouchHelper.LEFT -> onSwipeLeft(selectedTask, viewHolder.itemView)
            ItemTouchHelper.RIGHT -> onSwipeRight(selectedTask, viewHolder.itemView)
        }
    }
}