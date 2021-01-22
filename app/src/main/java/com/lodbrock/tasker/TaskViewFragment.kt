package com.lodbrock.tasker

import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.lodbrock.tasker.databinding.FragmentTaskViewBinding
import com.lodbrock.tasker.viewmodels.SeeAllTasksViewModel
import com.lodbrock.tasker.viewmodels.TaskViewViewModel
import java.text.DateFormat

class TaskViewFragment : Fragment() {

    private lateinit var binding : FragmentTaskViewBinding

    private val args: TaskViewFragmentArgs by navArgs()

    private val viewModel : TaskViewViewModel by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = FragmentTaskViewBinding.inflate(inflater, container, false)

        viewModel.initTask(args.taskToViewId)

        viewModel.getSelectedTask().observe(viewLifecycleOwner, { task ->
            task?.let {
                val formattedDate = DateFormat.getDateInstance().format(it.setToDate.toCalendar().time)
                binding.apply {
                    taskViewTitle.text.clear()
                    taskViewTitle.text.append(it.title)
                    taskViewDescription.text.clear()
                    taskViewDescription.text.append(it.description)
                    viewSwitchIsTaskDone.isChecked = task.done
                    taskViewDate.text = resources.getString(R.string.set_to_text, formattedDate)
                }

            } ?: run {
                Toast.makeText(context, resources.getString(R.string.unable_to_find_sel_task), Toast.LENGTH_SHORT)
                    .show()
                Navigation.findNavController(binding.root).navigateUp()
            }
        })

        return binding.root
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.task_view_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.editTaskFragment -> {
                val action = TaskViewFragmentDirections
                    .actionTaskViewFragmentToEditTaskFragment(args.taskToViewId)
                Navigation.findNavController(binding.root).navigate(action)
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}