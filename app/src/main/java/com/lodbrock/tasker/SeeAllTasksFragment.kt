package com.lodbrock.tasker

import android.os.Bundle
import android.util.Xml
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.Toast
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.applandeo.materialcalendarview.CalendarView
import com.applandeo.materialcalendarview.EventDay
import com.google.android.material.snackbar.Snackbar
import com.lodbrock.tasker.aircalendar.OnDaySelectionListener
import com.lodbrock.tasker.data.model.Task
import com.lodbrock.tasker.databinding.FragmentSeeAllTasksBinding
import com.lodbrock.tasker.ui.adapters.ItemClickListener
import com.lodbrock.tasker.ui.adapters.TaskArchiveRecyclerAdapter
import com.lodbrock.tasker.util.TaskDialog
import com.lodbrock.tasker.util.TextUtil
import com.lodbrock.tasker.util.YearDayMonth
import com.lodbrock.tasker.viewmodels.SeeAllTasksViewModel
import org.xmlpull.v1.XmlPullParser
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class SeeAllTasksFragment : Fragment() {

    private lateinit var binding : FragmentSeeAllTasksBinding

    private lateinit var taskDialog: TaskDialog

    private lateinit var taskArchiveAdapter : TaskArchiveRecyclerAdapter

    private val viewModel : SeeAllTasksViewModel by activityViewModels()

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

        binding.airCalendar.adapter.setOnDayClickListener( object : OnDaySelectionListener {
            override fun onDaySelected(calendar: Calendar) {
                viewModel.switchDayLiveData(YearDayMonth.fromCalendar(calendar))
                val format = DateFormat.getDateInstance().format(calendar.time)
                binding.archiveStatusLabel.text = "All tasks for $format"
            }
        })

        binding.taskArchiveRecycler.adapter = taskArchiveAdapter
        ItemTouchHelper(itemTouchCallback).attachToRecyclerView(binding.taskArchiveRecycler)

        binding.addFloatingBtn.setOnClickListener {
            val dateSelectedCalendar = binding.airCalendar.adapter.getSelectedDate()

            val dateText = DateFormat.getDateInstance().format(dateSelectedCalendar.time)
            val dateSelected = YearDayMonth.fromCalendar(
                dateSelectedCalendar
            )

            taskDialog.showTaskDialog(
                "Add Task For $dateText",
                "Add",
                object : TaskDialog.OnDialogClickListener {
                    override fun onClick(task: Task?) {
                        task?.let {
                            it.setToDate = dateSelected
                            viewModel.addTask(it)
                            println(dateSelected.toString() + " Selected date")
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
            taskArchiveAdapter.setTaskList(list)
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

    private val itemTouchCallback = object : ItemTouchHelper.SimpleCallback(
        0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder,
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val task = taskArchiveAdapter.getTaskAtPosition(viewHolder.adapterPosition)
            task?.let {
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
        }
    }
}


