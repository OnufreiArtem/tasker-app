package com.lodbrock.tasker.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.lodbrock.tasker.util.Converters
import com.lodbrock.tasker.util.YearDayMonth
import java.util.*

@Entity
data class Task(
        @PrimaryKey(autoGenerate = true)
        var id : Long? = null,
        var title : String,
        var description : String ="",
        var done : Boolean = false,
        var setToDate : YearDayMonth,
        var modifiedAt : Calendar = Calendar.getInstance(),
        var createAt : Calendar = Calendar.getInstance()
) {

    override fun toString() = "Task(id=$id," +
            " title='$title'," +
            " description='$description'," +
            " done=$done," +
            " setToDate=$setToDate," +
            " modifiedAt=$modifiedAt," +
            " createAt=$createAt)"

}
