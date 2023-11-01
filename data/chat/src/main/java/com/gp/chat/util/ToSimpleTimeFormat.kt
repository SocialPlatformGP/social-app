package com.gp.chat.util

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object ToSimpleTimeFormat {
    fun convertTimestampStringToFormattedTime(inputTimestamp: String): String {
        val inputFormat = SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'Z yyyy", Locale.ENGLISH)
        val outputFormat = SimpleDateFormat("hh:mm a", Locale.getDefault())

        try {
            val date = inputFormat.parse(inputTimestamp)
            return outputFormat.format(date)
        } catch (e: Exception) {
            e.printStackTrace()
            return ""
        }
    }
}

