package com.gp.chat.util

import android.os.Build
import androidx.annotation.RequiresApi
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.time.temporal.ChronoUnit
import java.util.Date
import java.util.Locale

object DateUtils {
    @RequiresApi(Build.VERSION_CODES.O)
    fun getTimeStamp(zonedDateTime: ZonedDateTime): String {
        val now = ZonedDateTime.now()
        val today = now.toLocalDate()
        val yesterday = today.minusDays(1)

        return when {
            zonedDateTime.toLocalDate() == today -> zonedDateTime.format(
                DateTimeFormatter.ofPattern(
                    "hh:mm a",
                    Locale.ENGLISH
                )
            )

            zonedDateTime.toLocalDate() == yesterday -> "Yesterday"
            else -> zonedDateTime.format(DateTimeFormatter.ofPattern("yyyy/MM/dd", Locale.ENGLISH))
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun getDateHeader(localDate: LocalDate): String {
        val today = LocalDate.now()
        return when {
            localDate == today -> "Today"
            localDate == today.minus(1, ChronoUnit.DAYS) -> "Yesterday"
            localDate.isAfter(today.minus(1, ChronoUnit.WEEKS)) -> localDate.dayOfWeek.toString()
            else -> localDate.format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
        }
    }
}