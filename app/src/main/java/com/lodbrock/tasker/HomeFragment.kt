package com.lodbrock.tasker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lodbrock.tasker.data.model.Task
import com.lodbrock.tasker.databinding.FragmentHomeBinding
import com.lodbrock.tasker.ui.adapters.TaskRecyclerAdapter
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var inProgressTaskRecycler : RecyclerView
    private lateinit var doneTaskRecycler : RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        val inProgressTasks : MutableList<Task> = ArrayList()
        inProgressTasks.add(Task(title="Do some work", setToDate = Calendar.getInstance()))
        inProgressTasks.add(Task(title="Do some other work", setToDate = Calendar.getInstance()))
        inProgressTasks.add(Task(title="Do nothing all day long", setToDate = Calendar.getInstance()))

        val inProgressAdapter = TaskRecyclerAdapter(
                inProgressTasks,
                TaskRecyclerAdapter.TaskRecyclerType.PROGRESS
        )

        val doneTasks : MutableList<Task> = ArrayList()
        doneTasks.add(Task(title="I slepped a lot", setToDate = Calendar.getInstance()))

        val doneAdapter = TaskRecyclerAdapter(
                doneTasks,
                TaskRecyclerAdapter.TaskRecyclerType.DONE
        )

        binding.tasksInProgressList.adapter = inProgressAdapter
        inProgressAdapter.notifyDataSetChanged()

        binding.tasksDoneList.adapter = doneAdapter
        doneAdapter.notifyDataSetChanged()

        val layoutManager = LinearLayoutManager(this.context)
        layoutManager.orientation = LinearLayoutManager.VERTICAL

        //binding.tasksInProgressList.layoutManager = layoutManager
        //binding.tasksDoneList.layoutManager = layoutManager




        return binding.root
    }
}