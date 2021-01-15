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
import androidx.navigation.Navigation
import androidx.navigation.fragment.navArgs
import com.lodbrock.tasker.data.model.Task
import com.lodbrock.tasker.databinding.FragmentEditTaskBinding
import com.lodbrock.tasker.util.YearDayMonth
import com.lodbrock.tasker.viewmodels.EditTaskViewModel
import java.text.DateFormat
import java.util.*

class EditTaskFragment : Fragment() {

    private lateinit var binding : FragmentEditTaskBinding

    private val viewModel : EditTaskViewModel by activityViewModels()

    private val args: EditTaskFragmentArgs by navArgs()

    private var dateToAddTask = MutableLiveData<YearDayMonth>(null)

    private var task : Task? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditTaskBinding.inflate(inflater, container, false)

        dateToAddTask.observe(viewLifecycleOwner, {
            val selectedDate = dateToAddTask.value
            selectedDate?.let {
                val btnText = DateFormat.getDateInstance().format(selectedDate.toCalendar().time)
                binding.selectDateBtn.text = btnText
            }
        })

        viewModel.initTask(args.taskToEditId)

        viewModel.getSelectedTask().observe(viewLifecycleOwner) {
            it?.let { task ->
                binding.apply {
                    taskEditTitle.text.clear()
                    taskEditTitle.text.append(task.title)
                    taskEditDescription.text.clear()
                    taskEditDescription.text.append(task.description)
                    switchIsTaskDone.isChecked = task.done
                    dateToAddTask.value = task.setToDate
                }
                this.task = task
            } ?: run {
                Toast.makeText(context, "Unable to find selected task", Toast.LENGTH_SHORT)
                    .show()
                Navigation.findNavController(binding.root).navigateUp()
            }
        }

        configSwitch()

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

        binding.editTaskBtn.setOnClickListener{

            var mDateToAdd = dateToAddTask.value

            if(binding.switchIsCurrentDate.isChecked) {
                mDateToAdd = YearDayMonth.today()
            }

            if(mDateToAdd == null) {
                activity?.let {
                    val builder = AlertDialog.Builder(it)
                    builder.apply {
                        setPositiveButton("OK") { _, _ -> run{} }
                        setMessage("You need to specify date for your task")
                    }
                }?.show()
                return@setOnClickListener
            }

            task?.let {
                it.apply {
                    title = binding.taskEditTitle.text.toString()
                    description = binding.taskEditDescription.text.toString()
                    setToDate = mDateToAdd
                    done = binding.switchIsTaskDone.isChecked
                }
                viewModel.editTask(it)
                Toast.makeText(context, "Task was edited", Toast.LENGTH_SHORT).show()
                Navigation.findNavController(binding.root).navigateUp()
            }
        }

        return binding.root
    }

    private fun configSwitch(){
        binding.switchIsCurrentDate.setOnCheckedChangeListener {
                _, isChecked -> binding.selectDateBtn.isEnabled = !isChecked
        }
    }


}