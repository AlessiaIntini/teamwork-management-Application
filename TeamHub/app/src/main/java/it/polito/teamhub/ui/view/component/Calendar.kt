package it.polito.teamhub.ui.view.component

import android.annotation.SuppressLint
import android.app.DatePickerDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import it.polito.teamhub.R
import java.util.Calendar

@SuppressLint("DefaultLocale")
@Composable
fun calendarRender(
    choosenDate: MutableState<Boolean>,
): String? {
    var dateDialogShown by remember { mutableStateOf(true) }
    val calendar = Calendar.getInstance()
    var selectedYear by remember { mutableIntStateOf(calendar.get(Calendar.YEAR)) }
    var selectedMonth by remember { mutableIntStateOf(calendar.get(Calendar.MONTH)) }
    var selectedDay by remember { mutableIntStateOf(calendar.get(Calendar.DAY_OF_MONTH)) }
    var wasCancelled by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (dateDialogShown) {
        val datePickerDialog = DatePickerDialog(
            context,
            R.style.CustomDatePickerDialogTheme,
            { _, year, month, day ->
                selectedYear = year
                selectedMonth = month
                selectedDay = day
            }, selectedYear, selectedMonth, selectedDay
        )

        datePickerDialog.show()
        datePickerDialog.setOnCancelListener {
            choosenDate.value = true
            wasCancelled = true
        }
        datePickerDialog.setOnDismissListener {
            choosenDate.value = true

        }

        dateDialogShown = false
    }

    if (wasCancelled) {
        return null
    }

    return String.format("%d-%02d-%02d", selectedYear, selectedMonth + 1, selectedDay)
}
