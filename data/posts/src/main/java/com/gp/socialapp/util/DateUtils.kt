package com.gp.socialapp.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

object DateUtils {

    fun calculateTimeDifference(inputDateTimeString: String): String {
        if (inputDateTimeString.isBlank()) return ""
        else {
            val sdf = SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'XXX yyyy", Locale.ENGLISH)
            sdf.timeZone = TimeZone.getTimeZone("UTC")

            var inputDate: Date? = null
            try {
                inputDate = sdf.parse(inputDateTimeString)
            } catch (e: ParseException) {
                e.printStackTrace()
            }

            if (inputDate != null) {
                val currentDate = Date()
                val timeDifference = currentDate.time - inputDate.time
                val secondsDifference = timeDifference / 1000
                val minutesDifference = secondsDifference / 60
                val hoursDifference = minutesDifference / 60
                val daysDifference = hoursDifference / 24
                val monthsDifference = daysDifference / 30 // Approximate months

                return when {
                    monthsDifference > 0 -> "$monthsDifference months"
                    daysDifference > 0 -> "$daysDifference days"
                    hoursDifference > 0 -> "$hoursDifference hours"
                    minutesDifference > 0 -> "$minutesDifference minutes"
                    else -> "less than a minute"
                }
            } else {
                return "Invalid Date Format"
            }
        }
    }
    fun convertStringToDate(dateString: String): Date {
        val format = SimpleDateFormat("EEE MMM dd HH:mm:ss 'GMT'XXX yyyy", Locale.US)
        format.timeZone = TimeZone.getTimeZone("GMT")
        return format.parse(dateString)?:Date()
    }
}
