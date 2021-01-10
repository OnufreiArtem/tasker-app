package com.lodbrock.tasker

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.observe
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.lodbrock.tasker.data.model.Task
import com.lodbrock.tasker.databinding.FragmentHomeBinding
import com.lodbrock.tasker.ui.adapters.TaskRecyclerAdapter
import com.lodbrock.tasker.util.TextUtil
import com.lodbrock.tasker.util.YearDayMonth
import com.lodbrock.tasker.viewmodels.HomeViewModel


class HomeFragment : Fragment() {

    private lateinit var binding: FragmentHomeBinding

    private lateinit var inProgressRecyclerAdapter: TaskRecyclerAdapter
    private lateinit var doneRecyclerAdapter: TaskRecyclerAdapter

    private val viewModel : HomeViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)

        //Setting RecyclerViews------------------------
        inProgressRecyclerAdapter = TaskRecyclerAdapter(
            viewModel.inProgressTasks,
            TaskRecyclerAdapter.TaskRecyclerType.PROGRESS
        )

        doneRecyclerAdapter = TaskRecyclerAdapter(
            viewModel.doneTasks,
            TaskRecyclerAdapter.TaskRecyclerType.DONE
        )

        binding.tasksInProgressList.adapter = inProgressRecyclerAdapter
        inProgressRecyclerAdapter.notifyDataSetChanged()

        binding.tasksDoneList.adapter = doneRecyclerAdapter
        doneRecyclerAdapter.notifyDataSetChanged()

        ItemTouchHelper(inProgressItemTouchCallback).attachToRecyclerView(binding.tasksInProgressList)
        //------------------------------------------------------>

        registerObservables()


        binding.addFloatingBtn.setOnClickListener {
            viewModel.addTask(Task(title = "Something", setToDate = YearDayMonth.today()))
            Toast.makeText(this.context, "Added new Task", Toast.LENGTH_SHORT).show()
        }

        return binding.root
    }

    private fun registerObservables(){

        viewModel.allTasksForToday.observe(viewLifecycleOwner) {
            inProgressRecyclerAdapter.setTasks(viewModel.inProgressTasks)
            doneRecyclerAdapter.setTasks(viewModel.doneTasks)

            inProgressRecyclerAdapter.notifyDataSetChanged()
            doneRecyclerAdapter.notifyDataSetChanged()
            Log.i("HomeFragment", "List changed")

            setHeaderTaskNumberLabel(
                viewModel.doneTasks.size,
                viewModel.allTasksForToday.value!!.size
            )
        }
    }

    private fun setHeaderTaskNumberLabel(tasksDoneNumber: Int, allTasksNumber: Int){
        binding.homeTaskNumberLabel.text = "$tasksDoneNumber/$allTasksNumber"
    }

    private val inProgressItemTouchCallback = object : ItemTouchHelper.SimpleCallback(
        0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
        override fun onMove(
            recyclerView: RecyclerView,
            viewHolder: RecyclerView.ViewHolder,
            target: RecyclerView.ViewHolder
        ): Boolean {
            return false
        }

        override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
            val task = inProgressRecyclerAdapter.getTaskAtPosition(viewHolder.adapterPosition)

            when(direction) {
                ItemTouchHelper.LEFT -> {
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

                ItemTouchHelper.RIGHT -> {
                    if(!viewModel.makeTaskDone(task)) {
                        Toast.makeText(context, "Failed to make task done", Toast.LENGTH_SHORT)
                            .show()
                    }
                }

            }
        }
    }

}