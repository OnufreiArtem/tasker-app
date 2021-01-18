package com.lodbrock.tasker.aircalendar

import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.lodbrock.tasker.R
import com.lodbrock.tasker.util.YearDayMonth
import java.util.*

class CalendarAdapter(
    private val ctx: Context,
    private val targetMonth: Int,
    private var dates: MutableList<YearDayMonth>,
    private var onDayClickListener: OnDaySelectionListener? = null,
) : ArrayAdapter<YearDayMonth>(ctx, R.layout.aircalendar_day_layout, dates) {

    private var inflater: LayoutInflater = LayoutInflater.from(context)
    private var events: MutableList<YearDayMonth> = mutableListOf()

    private var selectedDate : Calendar? = null

    fun setDayClickListener(listener : OnDaySelectionListener) {
        onDayClickListener = listener
    }

    fun setSelectedDate(calendar: Calendar) {
        selectedDate = calendar
        notifyDataSetChanged()
    }

    fun setDateList(dates: List<YearDayMonth>) {
        this.dates.clear()
        this.dates.addAll(dates)
    }

    fun setEventList(events: List<YearDayMonth>) {
        this.events.clear()
        this.events.addAll(events)
    }

    fun addEvent(yearDayMonth: YearDayMonth) {
        this.events.add(yearDayMonth)
    }

    fun removeEvent(yearDayMonth: YearDayMonth) {
        this.events.remove(yearDayMonth)
    }

    fun removeAllEvents() {
        this.events.clear()
    }

    private var lastSelectedView: View? = null
    private var lastSelectedItemBcg: ImageView? = null
    private var lastSelectedItemText: TextView? = null
    private var lastTextColorRes: Int = 0

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        val view = convertView
            ?: inflater.inflate(R.layout.aircalendar_day_layout, parent, false)

        val label = view.findViewById<TextView>(R.id.aircalendar_day_text)
        val image = view.findViewById<ImageView>(R.id.aircalendar_day_img)

        label.text = dates[position].day.toString()

        if (YearDayMonth.compare(YearDayMonth.today(), dates[position]) == 0) {
            label.setTextColor(ctx.resources.getColor(R.color.red_500))
        }

        selectedDate?.let {
            if(dates[position] == YearDayMonth.fromCalendar(it)) {
                drawSelectedDate(view)
            }
        }


        if (events.contains(dates[position]))
            image.setImageResource(R.drawable.ic_circle)

        if (dates[position].month != targetMonth) {
            label.setTextColor(ctx.resources.getColor(R.color.transparent_grey))
        } else {
            view.setOnClickListener {
                selectDayView(view, dates[position].toCalendar())
            }
        }

        return view
    }

    private fun selectDayView(view: View, selectedDate: Calendar? = null) {

        selectedDate?.let {
            onDayClickListener?.onDaySelected(selectedDate)
        }

        drawSelectedDate(view)
    }

    private fun drawSelectedDate(view: View) {
        try {
            val label = view.findViewById<TextView>(R.id.aircalendar_day_text)
            val bcg = view.findViewById<ImageView>(R.id.daySelectedBackground)

            lastSelectedView?.let { lsw ->
                if (view != lsw) {
                    lastSelectedItemBcg?.setImageDrawable(ColorDrawable(Color.TRANSPARENT))
                    lastSelectedItemText?.setTextColor(lastTextColorRes)
                }
            }

            lastSelectedView = view
            lastSelectedItemText = label
            lastSelectedItemBcg = bcg
            lastTextColorRes = label.currentTextColor

            bcg.setImageDrawable(ContextCompat.getDrawable(ctx, R.drawable.bcg_circle))
            label.setTextColor(Color.WHITE)

        } catch (e: Exception) { }
    }

}