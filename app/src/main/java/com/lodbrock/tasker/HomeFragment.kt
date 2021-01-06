package com.lodbrock.tasker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.lodbrock.tasker.data.model.Task
import com.lodbrock.tasker.databinding.FragmentHomeBinding
import com.lodbrock.tasker.ui.adapters.TaskRecyclerAdapter
import com.lodbrock.tasker.viewmodels.HomeViewModel
import java.util.*
import kotlin.collections.ArrayList

class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var inProgressRecyclerAdapter: TaskRecyclerAdapter
    private lateinit var doneRecyclerAdapter: TaskRecyclerAdapter

    private val viewModel : HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        inProgressRecyclerAdapter = TaskRecyclerAdapter(
                viewModel.inProgressTasks.value!!,
                TaskRecyclerAdapter.TaskRecyclerType.PROGRESS
        )

        doneRecyclerAdapter = TaskRecyclerAdapter(
                viewModel.doneTasks.value!!,
                TaskRecyclerAdapter.TaskRecyclerType.DONE
        )

        binding.tasksInProgressList.adapter = inProgressRecyclerAdapter
        inProgressRecyclerAdapter.notifyDataSetChanged()

        binding.tasksDoneList.adapter = doneRecyclerAdapter
        doneRecyclerAdapter.notifyDataSetChanged()

        setHeaderTaskNumberLabel(
                viewModel.doneTasksNumber.value!!,
                viewModel.allTasksNumber.value!!
        )

        registerObservables()

        viewModel.addTask(Task(title = "New Task", setToDate = Calendar.getInstance()))
        viewModel.addTask(Task(title = "New Task2", setToDate = Calendar.getInstance()))
        viewModel.addTask(Task(title = "New Task3", setToDate = Calendar.getInstance()))
        viewModel.addTask(Task(title = "New Task4", setToDate = Calendar.getInstance()))
        viewModel.addTask(Task(title = "New Task5", setToDate = Calendar.getInstance()))
        viewModel.addTask(Task(title = "New Task6", setToDate = Calendar.getInstance()))
        viewModel.addTask(Task(title = "New Task7", setToDate = Calendar.getInstance()))
        viewModel.addTask(Task(title = "New Task8", setToDate = Calendar.getInstance()))

        return binding.root
    }

    private fun registerObservables(){

        viewModel.allTasksForToday.observe( viewLifecycleOwner) {
            inProgressRecyclerAdapter.setTasks(viewModel.inProgressTasks.value!!)
            doneRecyclerAdapter.setTasks(viewModel.doneTasks.value!!)

            binding.tasksDoneList.adapter?.notifyDataSetChanged()
            binding.tasksInProgressList.adapter?.notifyDataSetChanged()
        }

        viewModel.doneTasksNumber.observe( viewLifecycleOwner) {
            setHeaderTaskNumberLabel(
                    viewModel.doneTasksNumber.value!!,
                    viewModel.allTasksNumber.value!!
            )
        }

        viewModel.allTasksNumber.observe( viewLifecycleOwner) {
            setHeaderTaskNumberLabel(
                    viewModel.doneTasksNumber.value!!,
                    viewModel.allTasksNumber.value!!
            )
        }
    }


    private fun setHeaderTaskNumberLabel(tasksDoneNumber: Int, allTasksNumber: Int){
        binding.homeTaskNumberLabel.text = "$tasksDoneNumber/$allTasksNumber"
    }
}