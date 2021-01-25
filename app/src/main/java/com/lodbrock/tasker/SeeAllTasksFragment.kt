package com.lodbrock.tasker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import com.google.android.material.snackbar.Snackbar
import com.lodbrock.tasker.aircalendar.OnDaySelectionListener
import com.lodbrock.tasker.data.model.Task
import com.lodbrock.tasker.databinding.FragmentSeeAllTasksBinding
import com.lodbrock.tasker.ui.adapters.ItemClickListener
import com.lodbrock.tasker.ui.adapters.TaskArchiveRecyclerAdapter
import com.lodbrock.tasker.ui.adapters.TaskItemCallback
import com.lodbrock.tasker.util.TaskDialog
import com.lodbrock.tasker.util.TextUtil
import com.lodbrock.tasker.util.YearDayMonth
import com.lodbrock.tasker.viewmodels.SeeAllTasksViewModel
import java.text.DateFormat
import java.util.*


class SeeAllTasksFragment : Fragment() {

    private lateinit var binding : FragmentSeeAllTasksBinding

    private lateinit var taskDialog: TaskDialog

    private lateinit var taskArchiveAdapter : TaskArchiveRecyclerAdapter

    private val viewModel : SeeAllTasksViewModel by viewModels()

    private lateinit var listener : ItemClickListener

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSeeAllTasksBinding.inflate(layoutInflater, container, false)
        taskDialog = TaskDialog(activity, layoutInflater)

        listener = object : ItemClickListener {
            override fun onClick(task: Task, position: Int) {
                val action = SeeAllTasksFragmentDirections
                    .actionSeeAllTasksFragmentToTaskViewFragment(taskToViewId = task.id ?: -1)
                Navigation.findNavController(binding.root).navigate(action)
            }
        }

        taskArchiveAdapter = TaskArchiveRecyclerAdapter(listOf(), listener)

        registerObservables()

        binding.archiveStatusLabel.text =  resources.getString(R.string.all_tasks_for,
            resources.getString(R.string.today_text))

        binding.airCalendar.adapter.setOnDayClickListener( object : OnDaySelectionListener {
            override fun onDaySelected(calendar: Calendar) {
                viewModel.switchDayLiveData(YearDayMonth.fromCalendar(calendar))
                val format = DateFormat.getDateInstance().format(calendar.time)
                binding.archiveStatusLabel.text = resources.getString(R.string.all_tasks_for, format)
            }
        })

        binding.taskArchiveRecycler.isNestedScrollingEnabled = false
        binding.taskArchiveRecycler.adapter = taskArchiveAdapter
        ItemTouchHelper(getItemTouchCallback()).attachToRecyclerView(binding.taskArchiveRecycler)

        binding.addFloatingBtn.setOnClickListener {
            val dateSelectedCalendar = binding.airCalendar.adapter.getSelectedDate()

            val dateText = DateFormat.getDateInstance().format(dateSelectedCalendar.time)
            val dateSelected = YearDayMonth.fromCalendar(
                dateSelectedCalendar
            )

            taskDialog.showTaskDialog(

                resources.getString(R.string.add_task_for, dateText),
                resources.getString(R.string.add_text),
                object : TaskDialog.OnDialogClickListener {
                    override fun onClick(task: Task?) {
                        task?.let {
                            it.setToDate = dateSelected
                            viewModel.addTask(it)
                        }
                    }
                },
                null,
                YearDayMonth.compare(YearDayMonth.today(), dateSelected) > 0
            )

        }

        return binding.root
    }

    private fun registerObservables() {
        viewModel.getAllTasksForToday().observe(viewLifecycleOwner, { list ->
            taskArchiveAdapter.setTasks(list)
            if (taskArchiveAdapter.itemCount == 0) showEmptyListHint() else hideEmptyListHint()
        })

        viewModel.getAllEventDates().observe(viewLifecycleOwner, { list ->
            binding.airCalendar.adapter.setEventList(list.map { YearDayMonth.fromCalendar(it) })
            binding.airCalendar.adapter.notifyDataSetChanged()
        })
    }

    private fun hideEmptyListHint() {
        binding.archiveEmptyListLabel.visibility = GONE
    }

    private fun showEmptyListHint() {
        binding.archiveEmptyListLabel.visibility = VISIBLE
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

    private fun getItemTouchCallback() : ItemTouchHelper.SimpleCallback {
        return object : TaskItemCallback(taskArchiveAdapter) {
            override fun onSwipeLeft(selectedTask: Task?, itemView: View) =
                deleteOnSwipe(selectedTask, itemView)

            override fun onSwipeRight(selectedTask: Task?, itemView: View) =
                deleteOnSwipe(selectedTask, itemView)
        }
    }
}


