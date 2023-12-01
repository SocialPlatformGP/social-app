package com.gp.chat.util

import java.text.SimpleDateFormat
import java.util.Date

object DateUtils {
    fun getTimeStamp(date: Date): String {
        val dateFormat = SimpleDateFormat("HH:mm")
        return dateFormat.format(date)
    }
}