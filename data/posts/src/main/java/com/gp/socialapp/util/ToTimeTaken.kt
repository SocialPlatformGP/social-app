package com.gp.socialapp.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneId
import java.time.temporal.ChronoUnit
import java.util.Locale
import java.util.TimeZone

object ToTimeTaken {

    @RequiresApi(Build.VERSION_CODES.O)
    fun calculateTimeDifference(dateTimeString: String): String {
        val currentDate = LocalDateTime.now(ZoneId.of("UTC"))
        val targetDate = LocalDateTime.parse(dateTimeString)

        val minutesDifference = ChronoUnit.MINUTES.between(targetDate, currentDate)
        val hoursDifference = ChronoUnit.HOURS.between(targetDate, currentDate)
        val daysDifference = ChronoUnit.DAYS.between(targetDate, currentDate)
        val monthsDifference = ChronoUnit.MONTHS.between(targetDate, currentDate)

        return when {
            monthsDifference > 0 -> "$monthsDifference months"
            daysDifference > 0 -> "$daysDifference days"
            hoursDifference > 0 -> "$hoursDifference hours"
            minutesDifference > 0 -> "$minutesDifference minutes"
            else -> "less than a minute"
        }
    }

}