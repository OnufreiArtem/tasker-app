package com.lodbrock.tasker

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.MutableLiveData
import androidx.navigation.fragment.findNavController
import com.lodbrock.tasker.data.model.Task
import com.lodbrock.tasker.databinding.FragmentAddTaskBinding
import com.lodbrock.tasker.util.YearDayMonth
import com.lodbrock.tasker.viewmodels.AddTaskViewModel
import java.util.*

class AddTaskFragment : Fragment() {

    private lateinit var binding : FragmentAddTaskBinding

    private val viewModel : AddTaskViewModel by activityViewModels()

    private var dateToAddTask = MutableLiveData<YearDayMonth>(null)

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddTaskBinding.inflate(inflater, container, false)

        configSwitch()

        dateToAddTask.observe(viewLifecycleOwner, {
            val selectedDate = dateToAddTask.value
            if(selectedDate != null) {
                val btnText =
                    "${selectedDate.year}:${selectedDate.month}:${selectedDate.day}"
                binding.selectDateBtn.text = btnText
            }
        })

        binding.selectDateBtn.setOnClickListener{
            val date = dateToAddTask.value ?: YearDayMonth.today()
            val datePickerDialog = DatePickerDialog(
                requireContext(),
                {_: DatePicker, year: Int, month: Int, day: Int
                    -> dateToAddTask.value = YearDayMonth(year, month, day) },
                date.year,
                date.month,
                date.day)

            datePickerDialog.show()
        }

        binding.addTaskBtn.setOnClickListener{

            var mDateToAdd = dateToAddTask.value

            if(binding.switchIsCurrentDate.isChecked) {
                mDateToAdd = YearDayMonth.today()
            }

            if(mDateToAdd == null) {
                activity?.let {
                    val builder = AlertDialog.Builder(it)
                    builder.apply {
                        setPositiveButton("OK") { _, _ -> {} }
                        setMessage("You need to specify date for your task")
                    }
                }?.show()
                return@setOnClickListener
            }

            val task = Task(
                title = binding.titleEdit.text.toString(),
                description = binding.descriptionEdit.text.toString(),
                setToDate = mDateToAdd
            )

            viewModel.addTask(task)
            Toast.makeText(context, "Task was added", Toast.LENGTH_SHORT).show()
            findNavController().navigate(R.id.action_addTaskFragment_to_taskViewFragment)
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