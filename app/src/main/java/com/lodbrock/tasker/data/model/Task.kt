package com.lodbrock.tasker.data.model

import java.util.*

class Task(
        var id : Long? = null,
        var title : String,
        var description : String ="",
        var done : Boolean = false,
        var setToDate : Calendar,
        var modifiedAt : Calendar = Calendar.getInstance(),
        var createAt : Calendar = Calendar.getInstance()
);