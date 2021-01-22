package com.lodbrock.tasker.aircalendar

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.lodbrock.tasker.R
import com.lodbrock.tasker.databinding.AircalendarDayLayoutBinding
import com.lodbrock.tasker.util.YearDayMonth
import java.util.*

class CalendarAdapter(
    private val ctx: Context,
    private var cursor: Calendar,
    private var dates: MutableList<YearDayMonth>,
    private var onDayClickListener: OnDaySelectionListener? = null,
) : ArrayAdapter<YearDayMonth>(ctx, R.layout.aircalendar_day_layout, dates) {

    private var inflater: LayoutInflater = LayoutInflater.from(context)
    private var events: MutableList<YearDayMonth> = mutableListOf()

    private var selectedDate : Calendar = Calendar.getInstance()

    fun setOnDayClickListener(listener : OnDaySelectionListener) {
        onDayClickListener = listener
    }

    fun setSelectedDate(calendar: Calendar) {
        selectedDate = calendar
        notifyDataSetChanged()
    }

    fun getSelectedDate() = selectedDate

    fun setDateList(dates: List<YearDayMonth>) {
        this.dates.clear()
        this.dates.addAll(dates)
    }

    fun setEventList(events: List<YearDayMonth>) {
        this.events.clear()
        this.events.addAll(events)
    }

    fun setCursor(cursor: Calendar){
        this.cursor =  cursor
    }

    private var prevBinding : AircalendarDayLayoutBinding? = null
    private var prevSelectedIndex: Int? = null
    private var prevTextColorRes: Int = 0

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView
            ?: inflater.inflate(R.layout.aircalendar_day_layout, parent, false)

        val binding = AircalendarDayLayoutBinding.bind(view)

        binding.aircalendarDayText.text = dates[position].day.toString()

        val isSelected = YearDayMonth.compare(dates[position], YearDayMonth.fromCalendar(selectedDate)) == 0
        val isIn = dates[position].month == cursor.get(Calendar.MONTH) && dates[position].year == cursor.get(Calendar.YEAR)
        val isToday = YearDayMonth.compare(YearDayMonth.today(), dates[position]) == 0

        binding.daySelectedBackground.setImageDrawable(ColorDrawable(Color.TRANSPARENT))

        if(!isIn) binding.aircalendarDayText.setTextColor(ctx.resources.getColor(R.color.transparent_light_grey))
        else {
             view.setOnClickListener {
                selectDayView(binding, position, dates[position].toCalendar())
             }
            binding.aircalendarDayText.setTextColor(ctx.resources.getColor(R.color.dark_blue))
        }

         if(isSelected) {
             binding.aircalendarDayText.setTextColor(Color.WHITE)
             drawAsSelectedDate(binding, position)
             binding.daySelectedBackground.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.bcg_circle))
             prevSelectedIndex = position
        } else if(isToday) binding.aircalendarDayText.setTextColor(ctx.resources.getColor(R.color.red_500))

        if (events.contains(dates[position])) {
            binding.aircalendarDayImg.setImageResource(R.drawable.ic_circle)
        }
        else {
            binding.aircalendarDayImg.setImageResource(0)
        }

        return view
    }

    private fun selectDayView(binding: AircalendarDayLayoutBinding, currentPosition: Int, selectedDate: Calendar? = null) {

        selectedDate?.let {
            this.selectedDate = selectedDate
            notifyDataSetChanged()
            onDayClickListener?.onDaySelected(selectedDate)
        }
        drawAsSelectedDate(binding, currentPosition)
    }

    private fun drawAsSelectedDate(binding: AircalendarDayLayoutBinding, currentPosition: Int) {
        try {
            prevSelectedIndex?.let { lsi ->
                if (currentPosition == lsi) {
                    return

                } else {
                    prevBinding?.daySelectedBackground?.setImageDrawable(ColorDrawable(Color.TRANSPARENT))
                    prevBinding?.aircalendarDayText?.setTextColor(prevTextColorRes)
                    prevSelectedIndex = currentPosition
                }
            }

            prevBinding = binding
            prevTextColorRes = binding.aircalendarDayText.currentTextColor

            binding.daySelectedBackground.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.bcg_circle))
            binding.aircalendarDayText.setTextColor(Color.WHITE)

        } catch (e: Exception) { }
    }

}