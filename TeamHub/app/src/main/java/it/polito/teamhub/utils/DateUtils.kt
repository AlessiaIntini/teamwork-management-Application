package it.polito.teamhub.utils

import android.icu.text.SimpleDateFormat
import android.icu.util.Calendar
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.runtime.MutableState
import it.polito.teamhub.dataClass.condition.Condition
import java.text.ParseException
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.temporal.TemporalAdjusters
import java.util.Date
import java.util.Locale

fun isDifferentYear(date: String?): Boolean {
    if (date == null) {
        return false
    }

    val currentYear = Calendar.getInstance().get(Calendar.YEAR)
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    return try {
        val parsedDate = formatter.parse(date)
        val calendar = Calendar.getInstance()
        calendar.time = parsedDate
        val yearOfDate = calendar.get(Calendar.YEAR)

        yearOfDate != currentYear
    } catch (e: ParseException) {
        false
    }
}

fun isToday(date: String): Boolean {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val parsedDate = formatter.parse(date)

    val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

    calendar.time = parsedDate
    val yearOfDate = calendar.get(Calendar.YEAR)
    val monthOfDate = calendar.get(Calendar.MONTH)
    val dayOfDate = calendar.get(Calendar.DAY_OF_MONTH)

    return yearOfDate == currentYear && monthOfDate == currentMonth && dayOfDate == currentDay
}

fun isYesterday(date: String): Boolean {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val parsedDate = formatter.parse(date)

    val calendar = Calendar.getInstance()
    calendar.add(Calendar.DAY_OF_MONTH, -1) // Sottrai un giorno dalla data corrente
    val yesterdayYear = calendar.get(Calendar.YEAR)
    val yesterdayMonth = calendar.get(Calendar.MONTH)
    val yesterdayDay = calendar.get(Calendar.DAY_OF_MONTH)

    calendar.time = parsedDate
    val yearOfDate = calendar.get(Calendar.YEAR)
    val monthOfDate = calendar.get(Calendar.MONTH)
    val dayOfDate = calendar.get(Calendar.DAY_OF_MONTH)

    return yearOfDate == yesterdayYear && monthOfDate == yesterdayMonth && dayOfDate == yesterdayDay
}

fun isBeforeCurrentDate(date: Date): Boolean {

    val taskCalendar = Calendar.getInstance().apply {
        time = date
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val currentCalendar = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    return taskCalendar.before(currentCalendar)
}

fun getDateWithoutTime(date: Date): Date {
    val calendar = Calendar.getInstance().apply {
        time = date
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    return calendar.time
}

@RequiresApi(Build.VERSION_CODES.O)
fun getDaysOfCurrentWeek(): List<LocalDate> {
    val now = LocalDate.now()
    val startOfWeek = now.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY))
    val daysOfWeek = (0..6).map { startOfWeek.plusDays(it.toLong()) }
    return daysOfWeek
}

fun getMonthName(dateString: String): String {
    val inputFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val date = inputFormat.parse(dateString)
    val outputFormat = SimpleDateFormat("MMM", Locale.getDefault())
    return outputFormat.format(date)
}

fun getDate(date: String): String {
    if (isToday(date)) return "Today"
    if (isYesterday(date)) return "Yesterday"
    val dateParts = date.split("-")
    if (isDifferentYear(date)) return "${dateParts[2].toInt()} ${getMonthName(date)} ${dateParts[0]}"
    val day = dateParts[2]
    return "${day.toInt()} ${getMonthName(date)}"
}

fun convertExpirationDateInCondition(date: MutableState<String>): Condition? {
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    if (date.value.isEmpty()) return null
    return Condition(
        field = "expirationDate",
        condition = { task ->
            format.format(task.dueDate).equals(date.value)
        }
    )
}

fun convertCreationDateInCondition(date: MutableState<String>): Condition? {
    val format = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    if (date.value.isEmpty()) return null
    return Condition(
        field = "creationDate",
        condition = { task ->
            format.format(task.creationDate).equals(date.value)
        }
    )
}

fun getTime(date: Date): String {
    val formatter = SimpleDateFormat("HH:mm", Locale.getDefault())
    return formatter.format(date)
}


fun getDateD(date: Date): String {
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val dateString = formatter.format(date)

    if (isToday(dateString)) return "Today"
    if (isYesterday(dateString)) return "Yesterday"

    val dateParts = dateString.split("-")
    if (isDifferentYear(dateString)) return "${dateParts[2].toInt()} ${getMonthName(dateString)} ${dateParts[0]}"
    val day = dateParts[2]
    return "${day.toInt()} ${getMonthName(dateString)}"
}

fun isTodayD(date: Date): Boolean {
    val calendar = Calendar.getInstance()
    val currentYear = calendar.get(Calendar.YEAR)
    val currentMonth = calendar.get(Calendar.MONTH)
    val currentDay = calendar.get(Calendar.DAY_OF_MONTH)

    calendar.time = date
    val yearOfDate = calendar.get(Calendar.YEAR)
    val monthOfDate = calendar.get(Calendar.MONTH)
    val dayOfDate = calendar.get(Calendar.DAY_OF_MONTH)

    return yearOfDate == currentYear && monthOfDate == currentMonth && dayOfDate == currentDay
}