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
            taskDialog.showTaskDialog(
                resources.getString(R.string.add_task_for, resources.getString(R.string.today_text)),
                resources.getString(R.string.add_text),
                object : TaskDialog.OnDialogClickListener{
                    override fun onClick(task: Task?) {
                        task?.let {
                            viewModel.addTask(it)
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
        binding.homeTaskNumberLabel.text = resources.getString(
            R.string.home_page_number_header_text, tasksDoneNumber, allTasksNumber)
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
                        val sAttr = viewHolder.itemView.context.theme
                            .obtainStyledAttributes(R.styleable.TaskerColors)

                        val textColor = sAttr.getColor(R.styleable.TaskerColors_snackbarTextColor, 0)
                        val bcgColor = sAttr.getColor(R.styleable.TaskerColors_snackbarBcg, 0)
                        val actionColor = sAttr.getColor(R.styleable.TaskerColors_snackbarActionColor, 0)

                        val snackbarText = resources.getString(
                            R.string.was_deleted_text,
                            TextUtil.threeDotLine(task.title, 15)
                        )

                        Snackbar.make(viewHolder.itemView,
                            snackbarText,
                            Snackbar.LENGTH_LONG)
                            .setTextColor(textColor)
                            .setBackgroundTint(bcgColor)
                            .setActionTextColor(actionColor)
                            .setAction(resources.getString(R.string.undo_text)) {
                                viewModel.addTask(task)
                            }
                            .show()
                    } else {
                        Toast.makeText(context,
                            resources.getString(R.string.failed_to_delete_task_text),
                            Toast.LENGTH_SHORT)
                            .show()
                    }

                }

                ItemTouchHelper.RIGHT -> {
                    if (!viewModel.makeTaskDone(task)) {
                        Toast.makeText(context,
                            resources.getString(R.string.failed_to_make_task_done_text),
                            Toast.LENGTH_SHORT)
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
                        val sAttr = viewHolder.itemView.context.theme
                            .obtainStyledAttributes(R.styleable.TaskerColors)

                        val textColor = sAttr.getColor(R.styleable.TaskerColors_snackbarTextColor, 0)
                        val bcgColor = sAttr.getColor(R.styleable.TaskerColors_snackbarBcg, 0)
                        val actionColor = sAttr.getColor(R.styleable.TaskerColors_snackbarActionColor, 0)

                        val snackbarText = resources.getString(
                            R.string.was_deleted_text,
                            TextUtil.threeDotLine(task.title, 15)
                        )

                        Snackbar.make(viewHolder.itemView,
                            snackbarText,
                            Snackbar.LENGTH_LONG)
                            .setTextColor(textColor)
                            .setBackgroundTint(bcgColor)
                            .setActionTextColor(actionColor)
                            .setAction(resources.getText(R.string.undo_text)) {
                                viewModel.addTask(task)
                            }
                            .show()
                    } else {
                        Toast.makeText(context,
                            resources.getString(R.string.failed_to_delete_task_text),
                            Toast.LENGTH_SHORT)
                            .show()
                    }

                }

                ItemTouchHelper.RIGHT -> {
                    if (!viewModel.makeTaskNotDone(task)) {
                        Toast.makeText(context,
                            resources.getString(R.string.failed_to_make_task_not_done_text),
                            Toast.LENGTH_SHORT)
                            .show()
                    }
                }

            }
        }
    }

}