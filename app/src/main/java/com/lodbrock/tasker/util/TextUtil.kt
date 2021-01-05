package com.lodbrock.tasker.util

class TextUtil {

    companion object {

        fun threeDotLine(line: String, maxNumberOfCharacters: Int) : String {
            if (maxNumberOfCharacters < 0) return ""

            val preparedLine = line.trim()
            return when(line.length) {
                in 0..maxNumberOfCharacters -> preparedLine
                else -> line.subSequence(0, maxNumberOfCharacters-3).trim().toString() + "..."
            }
        }



    }

}