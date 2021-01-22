package com.lodbrock.tasker.util

import android.app.Activity
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.SwitchCompat
import com.lodbrock.tasker.R
import com.lodbrock.tasker.data.model.Task

class TaskDialog(private val activity: Activity?, private val inflater: LayoutInflater) {

    fun showTaskDialog(
        dialogTitle: String,
        dialogPositiveBtnText: String,
        positiveClickListener: OnDialogClickListener?,
        negativeClickListener: OnDialogClickListener?,
        withDone: Boolean = false
    ) {
        activity?.let {
            val header: View = inflater.inflate(R.layout.dialog_title, null)
            val content: View = inflater.inflate(
                if(!withDone) R.layout.dialog_add_task else R.layout.dialog_add_task_with_done,
                null
            )

            val headerTitle = header.findViewById<TextView>(R.id.dialog_title)
            headerTitle.text = dialogTitle

            val builder = AlertDialog.Builder(it)
            builder.apply {
                setCustomTitle(header)
                setView(content)
                setPositiveButton(dialogPositiveBtnText)
                { dialog, _ ->
                    run {
                        val title = content.findViewById<EditText>(R.id.dialog_title_edit)
                        val description =
                            content.findViewById<EditText>(R.id.dialog_description_edit)
                        val done = if(!withDone) false else {
                            val switch = content.findViewById<SwitchCompat>(R.id.switch_is_task_done)
                            switch.isChecked
                        }

                        positiveClickListener?.let { positiveClickListener.onClick(Task(
                            title = title.text.toString(),
                            description = description.text.toString(),
                            setToDate = YearDayMonth.today(),
                            done = done
                        )) }

                        dialog.dismiss()
                    }
                }
                setNegativeButton(it.resources.getString(R.string.cancel_text)) { dialog, _ ->
                    run {
                        negativeClickListener?.let { negativeClickListener.onClick(null) }
                        dialog.cancel()
                    }
                }
            }

            builder.create().show()
        }

    }

    interface OnDialogClickListener {
        fun onClick(task: Task?)
    }


}