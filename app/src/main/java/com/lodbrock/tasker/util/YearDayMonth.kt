package com.lodbrock.tasker.util

import android.os.Parcelable
import kotlinx.parcelize.Parcelize
import java.util.*
@Parcelize
class YearDayMonth(
    val year: Int,
    val month: Int,
    val day: Int
):Parcelable {

    fun toCalendar() : Calendar {
        val c = Calendar.getInstance()
        c.set(year, month, day)
        return c
    }

    override fun toString(): String {
        return "$year:$month:$day"
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as YearDayMonth

        if (year != other.year) return false
        if (month != other.month) return false
        if (day != other.day) return false

        return true
    }

    override fun hashCode(): Int {
        var result = year
        result = 31 * result + month
        result = 31 * result + day
        return result
    }

    companion object {
        fun today(): YearDayMonth {
            val now = Calendar.getInstance()
            return YearDayMonth(
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
            )
        }

        fun compare(ydm1: YearDayMonth, ydm2: YearDayMonth): Int {
            if(ydm2.year > ydm1.year) return -1
            else if (ydm2.year < ydm1.year) return 1

            if(ydm2.month > ydm1.month) return -1
            else if (ydm2.month < ydm1.month) return 1

            if(ydm2.day > ydm1.day) return -1
            else if (ydm2.day < ydm1.day) return 1

            return 0
        }

        fun from(calendar: Calendar) : YearDayMonth {
            return YearDayMonth(
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)
            )
        }
    }


}