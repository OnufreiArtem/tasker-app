package com.lodbrock.tasker.util

import androidx.room.TypeConverter
import java.util.*

class Converters {

    @TypeConverter
    fun fromCalendar(calendar: Calendar?): Long? {
        return calendar?.timeInMillis
    }

    @TypeConverter
    fun toCalendar(data: Long?): Calendar? {
        if (data == null) return null
        val calendar: Calendar = Calendar.getInstance()
        calendar.timeInMillis = data
        return calendar
    }

    @TypeConverter
    fun fromYearDayMonth(ydm: YearDayMonth?): String? {
        return ydm?.toString()
    }

    @TypeConverter
    fun toYearDayMonth(data: String?): YearDayMonth? {
        if (data == null) return null
        val nums = data.split(":")
        return try {
            YearDayMonth(
                nums[0].toInt(),
                nums[1].toInt(),
                nums[2].toInt()
            )
        } catch (ex : Exception) {
            null
        }

    }



}