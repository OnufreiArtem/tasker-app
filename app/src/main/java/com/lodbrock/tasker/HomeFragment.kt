package com.lodbrock.tasker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.lodbrock.tasker.data.model.Task
import com.lodbrock.tasker.databinding.FragmentHomeBinding
import com.lodbrock.tasker.ui.adapters.ItemClickListener
import com.lodbrock.tasker.ui.adapters.TaskRecyclerAdapter
import com.lodbrock.tasker.util.TaskDialog
import com.lodbrock.tasker.util.TextUtil
import com.lodbrock.tasker.viewmodels.HomeViewModel


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var inProgressRecyclerAdapter: TaskRecyclerAdapter
    private lateinit var doneRecyclerAdapter: TaskRecyclerAdapter

    private lateinit var taskDialog: TaskDialog

    private val viewModel: HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        taskDialog = TaskDialog(activity, layoutInflater)

        registerObservables()

        val listener = object : ItemClickListener {
            override fun onClick(task: Task, position: Int) {
                val action = HomeFragmentDirections.actionHomeFragmentToTaskViewFragment(task.id ?: -1)
                Navigation.findNavController(binding.root).navigate(action)
            }
        }

        //Setting RecyclerViews------------------------
        inProgressRecyclerAdapter = TaskRecyclerAdapter(
            viewModel.getInProgressTasks(),
            TaskRecyclerAdapter.TaskRecyclerType.PROGRESS,
            listener
        )

        doneRecyclerAdapter = TaskRecyclerAdapter(
            viewModel.getDoneTasks(),
            TaskRecyclerAdapter.TaskRecyclerType.DONE,
            listener
        )

        binding.tasksInProgressList.adapter = inProgressRecyclerAdapter
        inProgressRecyclerAdapter.notifyDataSetChanged()

        binding.tasksDoneList.adapter = doneRecyclerAdapter
        doneRecyclerAdapter.notifyDataSetChanged()

        ItemTouchHelper(inProgressItemTouchCallback).attachToRecyclerView(binding.tasksInProgressList)
        ItemTouchHelper(doneItemTouchCallback).attachToRecyclerView(binding.tasksDoneList)

        //------------------------------------------------------>

        binding.addFloatingBtn.setOnClickListener {
            //Navigation.findNavController(binding.root).navigate(R.id.action_homeFragment_to_addTaskFragment)
            taskDialog.showTaskDialog(
                "Add Task For Today",
                "Add",
                object : TaskDialog.OnDialogClickListener{
                    override fun onClick(task: Task?) {
                        task?.let {
                            viewModel.addTask(task)
                        }
                    }
                },
                null
                )

        }

        return binding.root
    }

    private fun registerObservables() {

        viewModel.getAllTasksForToday().observe(viewLifecycleOwner, {
            inProgressRecyclerAdapter.setTasks(viewModel.getInProgressTasks())
            doneRecyclerAdapter.setTasks(viewModel.getDoneTasks())

            if(inProgressRecyclerAdapter.itemCount != 0) {
                binding.inProgressTasksStatus.visibility = View.GONE
            } else {
                binding.inProgressTasksStatus.visibility = View.VISIBLE
            }

            if(doneRecyclerAdapter.itemCount != 0) {
                binding.doneTasksStatus.visibility = View.GONE
            } else {
                binding.doneTasksStatus.visibility = View.VISIBLE
            }

            inProgressRecyclerAdapter.notifyDataSetChanged()
            doneRecyclerAdapter.notifyDataSetChanged()
            Log.i("HomeFragment", "List changed")

            setHeaderTaskNumberLabel(
                viewModel.getDoneTasks().size,
                viewModel.getAllTasksForToday().value?.size ?: 0
            )
        })
    }

    private fun setHeaderTaskNumberLabel(tasksDoneNumber: Int, allTasksNumber: Int) {
        binding.homeTaskNumberLabel.text = "$tasksDoneNumber/$allTasksNumber"
    }

    private val inProgressItemTouchCallback = object : ItemTouchHelper.SimpleCallback(
        0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder,
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val task = inProgressRecyclerAdapter.getTaskAtPosition(viewHolder.adapterPosition)

            when (direction) {
                ItemTouchHelper.LEFT -> {
                    val result = viewModel.deleteTask(task)

                    if (result) {
                        val textColor = ContextCompat.getColor(viewHolder.itemView.context,
                            R.color.white)
                        val bcgColor = ContextCompat.getColor(viewHolder.itemView.context,
                            R.color.dark_blue)
                        val actionColor = ContextCompat.getColor(viewHolder.itemView.context,
                            R.color.red_200)

                        Snackbar.make(viewHolder.itemView,
                            "\"${TextUtil.threeDotLine(task.title, 15)}\" was deleted",
                            Snackbar.LENGTH_LONG)
                            .setTextColor(textColor)
                            .setBackgroundTint(bcgColor)
                            .setActionTextColor(actionColor)
                            .setAction("Undo") {
                                viewModel.addTask(task)
                            }
                            .show()
                    } else {
                        Toast.makeText(context, "Failed to delete task", Toast.LENGTH_SHORT)
                            .show()
                    }

                }

                ItemTouchHelper.RIGHT -> {
                    if (!viewModel.makeTaskDone(task)) {
                        Toast.makeText(context, "Failed to make task done", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

            }
        }
    }
    private val doneItemTouchCallback = object : ItemTouchHelper.SimpleCallback(
        0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder,
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val task = doneRecyclerAdapter.getTaskAtPosition(viewHolder.adapterPosition)

            when (direction) {
                ItemTouchHelper.LEFT -> {
                    val result = viewModel.deleteTask(task)

                    if (result) {
                        val textColor = ContextCompat.getColor(viewHolder.itemView.context,
                            R.color.white)
                        val bcgColor = ContextCompat.getColor(viewHolder.itemView.context,
                            R.color.dark_blue)
                        val actionColor = ContextCompat.getColor(viewHolder.itemView.context,
                            R.color.red_200)

                        Snackbar.make(viewHolder.itemView,
                            "\"${TextUtil.threeDotLine(task.title, 15)}\" was deleted",
                            Snackbar.LENGTH_LONG)
                            .setTextColor(textColor)
                            .setBackgroundTint(bcgColor)
                            .setActionTextColor(actionColor)
                            .setAction("Undo") {
                                viewModel.addTask(task)
                            }
                            .show()
                    } else {
                        Toast.makeText(context, "Failed to delete task", Toast.LENGTH_SHORT)
                            .show()
                    }

                }

                ItemTouchHelper.RIGHT -> {
                    if (!viewModel.makeTaskNotDone(task)) {
                        Toast.makeText(context, "Failed to make task not done", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

            }
        }
    }

}