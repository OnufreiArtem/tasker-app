package com.lodbrock.tasker.ui.adapters

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.lodbrock.tasker.R
import com.lodbrock.tasker.data.model.Task
import com.lodbrock.tasker.util.TextUtil


class TaskRecyclerAdapter(private var taskList : List<Task>,
                          var taskRecyclerType : TaskRecyclerType)
    : RecyclerView.Adapter<TaskRecyclerAdapter.ViewHolder>() {


    fun setTasks(tasks: List<Task>) {
        taskList = tasks
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val itemView: View = LayoutInflater
                .from(parent.context)
                .inflate(R.layout.task_item, parent, false)

        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val task = taskList[position]

        if("" == task.title) {

            val lineFromDesc = TextUtil.threeDotLine(task.description, 20);

            if (lineFromDesc == "") {
                holder.itemTextView.setText(R.string.no_description_task)
            } else holder.itemTextView.text = lineFromDesc

        } else {
            holder.itemTextView.text = TextUtil.threeDotLine(task.title, 20)
        }

        //holder.itemView.setBackgroundResource(R.drawable.task_item_done_shape)
        //holder.itemTextView.paintFlags = holder
        //    .itemTextView.paintFlags and(Paint.STRIKE_THRU_TEXT_FLAG)

        taskRecyclerType.prepareTaskItem(holder)
    }



    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemTextView: TextView = itemView.findViewById(R.id.task_item_title)
        var itemBcgImage: ImageView = itemView.findViewById(R.id.task_item_bcg)
    }

    enum class TaskRecyclerType {
        PROGRESS{
            override fun prepareTaskItem(holder: ViewHolder) {}
        },

        DONE {
            override fun prepareTaskItem(holder: ViewHolder) {
                val nTextColor = ContextCompat.getColor(holder.itemView.context, R.color.dark_blue)
                holder.itemTextView.setTextColor(nTextColor)
                holder.itemBcgImage.setImageResource(R.drawable.task_item_done_shape)
                holder.itemTextView.paintFlags = holder
                        .itemTextView.paintFlags or Paint.STRIKE_THRU_TEXT_FLAG
            }
        },

        MISSED {
            override fun prepareTaskItem(holder: ViewHolder) {}
        };

        abstract fun prepareTaskItem(holder: ViewHolder)
    }


}