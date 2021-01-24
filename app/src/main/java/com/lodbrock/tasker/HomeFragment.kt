package com.lodbrock.tasker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.snackbar.Snackbar
import com.lodbrock.tasker.data.model.Task
import com.lodbrock.tasker.databinding.FragmentHomeBinding
import com.lodbrock.tasker.ui.adapters.ItemClickListener
import com.lodbrock.tasker.ui.adapters.SimpleTaskRecyclerAdapter
import com.lodbrock.tasker.ui.adapters.TaskItemCallback
import com.lodbrock.tasker.ui.adapters.TaskRecyclerAdapter
import com.lodbrock.tasker.util.TaskDialog
import com.lodbrock.tasker.util.TextUtil
import com.lodbrock.tasker.viewmodels.HomeViewModel


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var inProgressRecyclerAdapter: SimpleTaskRecyclerAdapter
    private lateinit var doneRecyclerAdapter: SimpleTaskRecyclerAdapter

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
        inProgressRecyclerAdapter = SimpleTaskRecyclerAdapter(
            viewModel.getInProgressTasks(),
            SimpleTaskRecyclerAdapter.TaskRecyclerType.PROGRESS,
            listener
        )

        doneRecyclerAdapter = SimpleTaskRecyclerAdapter(
            viewModel.getDoneTasks(),
            SimpleTaskRecyclerAdapter.TaskRecyclerType.DONE,
            listener
        )

        binding.tasksInProgressList.adapter = inProgressRecyclerAdapter
        inProgressRecyclerAdapter.notifyDataSetChanged()

        binding.tasksDoneList.adapter = doneRecyclerAdapter
        doneRecyclerAdapter.notifyDataSetChanged()

        ItemTouchHelper(getAdapterTouchCallback(inProgressRecyclerAdapter, true))
            .attachToRecyclerView(binding.tasksInProgressList)
        ItemTouchHelper(getAdapterTouchCallback(doneRecyclerAdapter, false))
            .attachToRecyclerView(binding.tasksDoneList)

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

    private fun deleteOnSwipe(task: Task?, itemView : View) {
        var result = false

        task?.let{ selectedTask ->
            result = viewModel.deleteTask(selectedTask)

            if (result) {
                val sAttr = itemView.context.theme
                    .obtainStyledAttributes(R.styleable.TaskerColors)

                val textColor = sAttr.getColor(R.styleable.TaskerColors_snackbarTextColor, 0)
                val bcgColor = sAttr.getColor(R.styleable.TaskerColors_snackbarBcg, 0)
                val actionColor = sAttr.getColor(R.styleable.TaskerColors_snackbarActionColor, 0)

                val snackbarText = resources.getString(
                    R.string.was_deleted_text,
                    TextUtil.threeDotLine(selectedTask.title, 15)
                )

                Snackbar.make(itemView,
                    snackbarText,
                    Snackbar.LENGTH_LONG)
                    .setTextColor(textColor)
                    .setBackgroundTint(bcgColor)
                    .setActionTextColor(actionColor)
                    .setAction(resources.getString(R.string.undo_text)) {
                        viewModel.addTask(selectedTask)
                    }
                    .show()
            }
        }

        if(!result) {
            Toast.makeText(context,
                resources.getString(R.string.failed_to_delete_task_text),
                Toast.LENGTH_SHORT)
                .show()
        }
    }
    private fun changeTaskStateOnSwipe(task: Task?, stateToMake: Boolean) {
        var result = false
        task?.let {
            result = if(stateToMake) viewModel.makeTaskDone(it) else viewModel.makeTaskNotDone(it)
        }
        if (!result) {
            Toast.makeText(context,
                resources.getString(R.string.failed_to_make_task_done_text),
                Toast.LENGTH_SHORT)
                .show()
        }
    }

    private fun getAdapterTouchCallback(adapter: TaskRecyclerAdapter, makeTaskState : Boolean)
        : ItemTouchHelper.SimpleCallback{
        return object : TaskItemCallback(adapter) {
            override fun onSwipeLeft(selectedTask: Task?, itemView: View) =
                deleteOnSwipe(selectedTask, itemView)

            override fun onSwipeRight(selectedTask: Task?, itemView: View) =
                changeTaskStateOnSwipe(selectedTask, makeTaskState)
        }
    }
}