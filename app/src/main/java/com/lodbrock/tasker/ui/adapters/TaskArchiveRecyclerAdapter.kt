package com.lodbrock.tasker.ui.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.lodbrock.tasker.R
import com.lodbrock.tasker.data.model.Task
import com.lodbrock.tasker.util.TextUtil
import com.lodbrock.tasker.util.YearDayMonth
import java.lang.Exception

class TaskArchiveRecyclerAdapter(private var taskList : List<Task>,
                                 private var itemClickListener: ItemClickListener? = null)
    : RecyclerView.Adapter<TaskArchiveRecyclerAdapter.ViewHolder>() {

    fun setTaskList(taskList: List<Task>) {
        this.taskList = taskList
        notifyDataSetChanged()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int)
    : ViewHolder {
        val itemView: View = LayoutInflater
            .from(parent.context)
            .inflate(R.layout.task_archive_item, parent, false)

        return ViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    fun getTaskAtPosition(position: Int) : Task? {
        return try {
            taskList[position]
        } catch (e: Exception) {
            null
        }
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

        val icResource: Int

        icResource = if(task.done)
            R.drawable.ic_done
        else {
            if(YearDayMonth.compare(task.setToDate, YearDayMonth.today()) >= 0)
                R.drawable.ic_in_progress
            else
                R.drawable.ic_missed
        }

        holder.itemIconImage.setImageResource(icResource)

        itemClickListener?.let { listener ->
            holder.itemView.setOnClickListener{ listener.onClick(task, position)  }
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var itemTextView: TextView = itemView.findViewById(R.id.task_archive_item_title)
        var itemIconImage: ImageView = itemView.findViewById(R.id.task_archive_item_icon)

    }

}