package com.lodbrock.tasker

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.lodbrock.tasker.databinding.FragmentTaskViewBinding
import java.text.DateFormat

class TaskViewFragment : Fragment() {

    private lateinit var binding : FragmentTaskViewBinding
    private val args: TaskViewFragmentArgs by navArgs()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentTaskViewBinding.inflate(inflater, container, false)
        args.taskToView?.let{
            binding.taskViewTitle.text.append(it.title)
            binding.taskViewDescription.text.append(it.description)
            val formattedDate = DateFormat.getDateInstance().format(it.setToDate.toCalendar().time)
            binding.taskViewDate.text = "Set to: " + formattedDate
        }

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.task_view_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.addTaskFragment -> {
                Navigation.findNavController(binding.root)
                    .navigate(R.id.action_taskViewFragment_to_addTaskFragment)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}