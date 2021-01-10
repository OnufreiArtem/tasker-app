package com.lodbrock.tasker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.lodbrock.tasker.data.model.Task
import com.lodbrock.tasker.databinding.FragmentAddTaskBinding
import com.lodbrock.tasker.util.YearDayMonth

class AddTaskFragment : Fragment() {

    private lateinit var binding : FragmentAddTaskBinding

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

            val action = AddTaskFragmentDirections
                .actionAddTaskFragmentToHomeFragment(taskToAddOnStart = task)
                findNavController().navigate(action)
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