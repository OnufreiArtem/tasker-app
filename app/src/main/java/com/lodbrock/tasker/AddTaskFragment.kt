package com.lodbrock.tasker

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.lodbrock.tasker.databinding.FragmentAddTaskBinding

class AddTaskFragment : Fragment() {

    private lateinit var binding : FragmentAddTaskBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        binding = FragmentAddTaskBinding.inflate(inflater, container, false)

        configSwitch()

        return binding.root
    }

    private fun configSwitch(){
        binding.switchIsCurrentDate.setOnCheckedChangeListener {
                _, isChecked -> binding.selectDateBtn.isEnabled = !isChecked
        }

        binding.switchIsCurrentDate.isChecked = true
    }


}