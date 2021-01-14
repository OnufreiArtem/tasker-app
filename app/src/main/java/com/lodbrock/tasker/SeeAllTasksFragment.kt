package com.lodbrock.tasker

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.applandeo.materialcalendarview.EventDay
import com.lodbrock.tasker.databinding.FragmentSeeAllTasksBinding
import com.lodbrock.tasker.ui.adapters.TaskArchiveRecyclerAdapter
import com.lodbrock.tasker.util.YearDayMonth
import com.lodbrock.tasker.viewmodels.SeeAllTasksViewModel

class SeeAllTasksFragment : Fragment() {

    private lateinit var binding : FragmentSeeAllTasksBinding

    private val taskArchiveAdapter = TaskArchiveRecyclerAdapter(listOf())

    private val viewModel : SeeAllTasksViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentSeeAllTasksBinding.inflate(layoutInflater, container, false)

        registerObservables()

        binding.taskArchiveRecycler.adapter = taskArchiveAdapter

        binding.applandeoCalendar.setOnDayClickListener {
            val date = YearDayMonth.fromCalendar(it.calendar)
            viewModel.switchDayLiveData(date)
            val status = "All tasks for ${date.month+1}.${date.day}"
            binding.archiveStatusLabel.text = status
        }

        return binding.root
    }

    private fun registerObservables() {
        viewModel.getAllTasksForToday().observe(viewLifecycleOwner, { list ->
            taskArchiveAdapter.setTaskList(list)
            if (taskArchiveAdapter.itemCount == 0) showEmptyListHint() else hideEmptyListHint()
        })

        viewModel.getAllEventDates().observe(viewLifecycleOwner, { dates ->
            val events: MutableList<EventDay> = ArrayList()

            dates.forEach { date ->
                events.add(EventDay(date, R.drawable.ic_circle, R.color.red_200))
            }

            binding.applandeoCalendar.setEvents(events)
        })

    }

    private fun hideEmptyListHint() {
        binding.archiveEmptyListLabel.visibility = GONE
    }

    private fun showEmptyListHint() {
        binding.archiveEmptyListLabel.visibility = VISIBLE
    }

}