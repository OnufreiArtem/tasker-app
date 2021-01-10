package com.lodbrock.tasker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.lodbrock.tasker.data.model.Task
import com.lodbrock.tasker.databinding.FragmentAddTaskBinding
import com.lodbrock.tasker.util.YearDayMonth
import com.lodbrock.tasker.viewmodels.AddTaskViewModel
import com.lodbrock.tasker.viewmodels.HomeViewModel
import java.util.*

class AddTaskFragment : Fragment() {

    private lateinit var binding : FragmentAddTaskBinding

    private val viewModel : AddTaskViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAddTaskBinding.inflate(inflater, container, false)

        configSwitch()

        binding.addTaskBtn.setOnClickListener{
            val task = Task(
                title = binding.titleEdit.text.toString(),
                description = binding.descriptionEdit.text.toString(),
                setToDate = YearDayMonth.today()
            )

            viewModel.addTask(task)
            Toast.makeText(context, "Task was added", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_addTaskFragment_to_homeFragment)
        }

        return binding.root
    }

    private fun configSwitch(){
        binding.switchIsCurrentDate.setOnCheckedChangeListener {
                _, isChecked -> binding.selectDateBtn.isEnabled = !isChecked
        }

        binding.switchIsCurrentDate.isChecked = true
    }


}