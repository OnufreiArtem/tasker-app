package com.lodbrock.tasker.aircalendar

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import com.lodbrock.tasker.R
import com.lodbrock.tasker.util.YearDayMonth
import java.text.SimpleDateFormat
import java.util.*

class CalendarView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    //Parent
    private lateinit var header : ConstraintLayout
    // Children --->
    private lateinit var nextImg : ImageView
    private lateinit var prevImg : ImageView
    private lateinit var headerTitle : TextView
    // <-----------

    private lateinit var weekDaysContainer : LinearLayout
    private lateinit var monthDaysContainer : GridView

    private val cursor = Calendar.getInstance()

    val adapter = CalendarAdapter(
        context,
        cursor,
        mutableListOf()
    )

    init {
        initControls()
    }

    private fun assignUiElements() {
        header = findViewById(R.id.aircalendar_header)

        nextImg = findViewById(R.id.aircalendar_header_next)
        prevImg = findViewById(R.id.aircalendar_header_previous)

        nextImg.setOnClickListener {
            cursor.add(Calendar.MONTH, 1)
            updateCalendarHeader()
            updateDates()
        }

        prevImg.setOnClickListener {
            cursor.add(Calendar.MONTH, -1)
            updateCalendarHeader()
            updateDates()
        }

        headerTitle = findViewById(R.id.aircalendar_header_title)

        updateCalendarHeader()

        weekDaysContainer = findViewById(R.id.aircalendar_week_days_container)
        monthDaysContainer = findViewById(R.id.aircalendar_month_days_container)

        updateDates()

        monthDaysContainer.adapter = adapter
    }

    private fun initControls() {
        val inflater = context.getSystemService(
            Context.LAYOUT_INFLATER_SERVICE
        ) as LayoutInflater
        inflater.inflate(R.layout.aircalendar_layout, this)
        assignUiElements()
    }

    private fun updateCalendarHeader() {
        val current = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            context.resources.configuration.locales.get(0)
        else
            context.resources.configuration.locale

        val format = SimpleDateFormat("MMM, y", current).format(cursor.time)

        headerTitle.text = format
    }

    private fun updateDates() {
        val calendar : Calendar = cursor.clone() as Calendar
        adapter.setCursor(cursor)
        calendar.set(Calendar.DAY_OF_MONTH, 1)
        val day = calendar.get(Calendar.DAY_OF_WEEK)
        val daysBefore = (if(day == Calendar.SUNDAY) 8 else day) - Calendar.MONDAY

        calendar.set(Calendar.DAY_OF_YEAR, calendar.get(Calendar.DAY_OF_YEAR) - daysBefore - 1)

        val dates = mutableListOf<YearDayMonth>()
        for (i in 1..(7*6)) {
            calendar.add(Calendar.DAY_OF_YEAR, 1)
            dates.add(YearDayMonth.fromCalendar(calendar))

        }
        adapter.setDateList(dates)
        adapter.notifyDataSetChanged()
    }

}