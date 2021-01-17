package com.lodbrock.tasker.aircalendar

import android.content.Context
import android.os.Build
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.GridLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.lodbrock.tasker.R
import java.text.SimpleDateFormat
import java.util.*

class CalendarView(context: Context, attrs: AttributeSet) : LinearLayout(context, attrs) {

    //Parent
    private lateinit var header : LinearLayout
    // Children --->
    private lateinit var nextImg : ImageView
    private lateinit var prevImg : ImageView
    private lateinit var headerTitle : TextView
    // <-----------

    private lateinit var weekDaysContainer : LinearLayout
    private lateinit var monthDaysContainer : GridLayout

    private val calendar = Calendar.getInstance()

    init {
        println("I am here")
        initControls()
        println("I am here 2")
    }


    private fun assignUiElements() {
        header = findViewById(R.id.aircalendar_header)
        nextImg = findViewById(R.id.aircalendar_header_next)
        prevImg = findViewById(R.id.aircalendar_header_previous)
        headerTitle = findViewById(R.id.aircalendar_header_title)

        // Set header title ----------
        val current = if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N)
            context.resources.configuration.locales.get(0)
        else
            context.resources.configuration.locale

        val format = SimpleDateFormat("MMMM, y", current).format(calendar.time)
        headerTitle.text = format
        // ---------------------------

        weekDaysContainer = findViewById(R.id.aircalendar_week_days_container)
        monthDaysContainer = findViewById(R.id.aircalendar_month_days_container)

    }

    private fun initControls() {
        val inflater = context.getSystemService(
            Context.LAYOUT_INFLATER_SERVICE
        ) as LayoutInflater
        inflater.inflate(R.layout.aircalendar_layout, this)
        assignUiElements()
    }

}