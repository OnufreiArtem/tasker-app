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
    }


}