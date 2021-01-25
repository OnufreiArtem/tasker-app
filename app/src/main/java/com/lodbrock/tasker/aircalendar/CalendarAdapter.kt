package com.lodbrock.tasker.aircalendar

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.asynclayoutinflater.view.AsyncLayoutInflater
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

    private var outColor : Int = 0
    private var inColor : Int = 0
    private var todayColor : Int = 0
    private var selectionColor : Int = 0


    init {
        val sAttr = ctx.theme.obtainStyledAttributes(R.styleable.AirCalendarView)
        outColor = sAttr.getColor(R.styleable.AirCalendarView_outColor, 0)
        inColor = sAttr.getColor(R.styleable.AirCalendarView_inColor, 0)
        todayColor = sAttr.getColor(R.styleable.AirCalendarView_todayColor, 0)
        selectionColor = sAttr.getColor(R.styleable.AirCalendarView_selectedColor, 0)
    }


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

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView
            ?: inflater.inflate(R.layout.aircalendar_day_layout, parent, false)

        val binding = AircalendarDayLayoutBinding.bind(view)

        binding.aircalendarDayText.text = dates[position].day.toString()

        val isSelected = YearDayMonth.compare(dates[position], YearDayMonth.fromCalendar(selectedDate)) == 0
        val isIn = dates[position].month == cursor.get(Calendar.MONTH) && dates[position].year == cursor.get(Calendar.YEAR)
        val isToday = YearDayMonth.compare(YearDayMonth.today(), dates[position]) == 0

        binding.daySelectedBackground.setImageDrawable(ColorDrawable(Color.TRANSPARENT))
        binding.aircalendarDayImg.setImageResource(0)


        if(!isIn) binding.aircalendarDayText.setTextColor(outColor)
        else{
            view.setOnClickListener {
                selectDayView(dates[position].toCalendar())
            }

            when {
                isSelected -> {
                    binding.aircalendarDayText.setTextColor(selectionColor)
                    binding.daySelectedBackground.setImageDrawable(
                        ContextCompat.getDrawable(ctx, R.drawable.bcg_circle)
                    )
                }
                isToday -> {
                    binding.aircalendarDayText.setTextColor(todayColor)

                }
                else -> {
                    binding.aircalendarDayText.setTextColor(inColor)
                }
            }
        }

        if (events.contains(dates[position])) {
            binding.aircalendarDayImg.setImageResource(R.drawable.ic_circle)
        }

        return view
    }

    private fun selectDayView(selectedDate: Calendar? = null) {

        selectedDate?.let {
            this.selectedDate = selectedDate
            onDayClickListener?.onDaySelected(selectedDate)
            notifyDataSetChanged()
        }
    }

}