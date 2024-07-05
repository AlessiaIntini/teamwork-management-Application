package it.polito.teamhub.ui.view.taskView.createTask

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import it.polito.teamhub.R
import it.polito.teamhub.ui.theme.PurpleBlue
import it.polito.teamhub.ui.theme.linearGradient
import it.polito.teamhub.viewmodel.TaskViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun SetExpirationDate(vmTask: TaskViewModel) {
    var selectedDate by rememberSaveable { mutableStateOf(vmTask.dueDate ?: Date()) }
    LaunchedEffect(vmTask.dueDate) {
        if (vmTask.dueDate != null) {
            selectedDate = vmTask.dueDate!!
        }
    }

    val calendar = Calendar.getInstance()
    calendar.time = selectedDate
    val year: Int = calendar.get(Calendar.YEAR)
    val month: Int = calendar.get(Calendar.MONTH)
    val day: Int = calendar.get(Calendar.DAY_OF_MONTH)

    val context = LocalContext.current
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val datePickerDialog =
        DatePickerDialog(
            context,
            R.style.CustomDatePickerDialogTheme,
            { _: DatePicker, pickedYear: Int, pickedMonth: Int, pickedDay: Int ->
                val newDate = Calendar.getInstance()
                newDate.set(pickedYear, pickedMonth, pickedDay)
                selectedDate = newDate.time
                vmTask.updateDueDate(selectedDate)
            },
            year,
            month,
            day
        ).apply {
            if (selectedDate.time >= System.currentTimeMillis()) {
                datePicker.minDate = System.currentTimeMillis() - 1000
            } else {
                datePicker.minDate = selectedDate.time - 1000
            }
        }

    datePickerDialog.setButton(DatePickerDialog.BUTTON_NEUTRAL, "Clear") { _, _ ->
        selectedDate = Date()
        vmTask.updateDueDate(null)
    }

    val tertiaryColor = MaterialTheme.colorScheme.tertiary

    datePickerDialog.setOnShowListener {
        val positiveButton = datePickerDialog.getButton(AlertDialog.BUTTON_POSITIVE)
        val negativeButton = datePickerDialog.getButton(AlertDialog.BUTTON_NEGATIVE)
        val clearButton = datePickerDialog.getButton(AlertDialog.BUTTON_NEUTRAL)

        // Customize positive button
        positiveButton.setTextColor(PurpleBlue.toArgb())
        positiveButton.text = context.getString(R.string.save)

        // Customize negative button
        negativeButton.setTextColor(Color.Black.toArgb())
        negativeButton.text = context.getString(R.string.cancel)

        // Customize clear button
        clearButton.setTextColor(tertiaryColor.toArgb())
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.weight(.6f)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.expiration_date),
                contentDescription = "Expiration date icon",
                modifier = if (vmTask.dueDateError.isNotBlank()) Modifier.padding(end = 15.dp)
                else
                    Modifier
                        .padding(end = 15.dp)
                        .graphicsLayer(alpha = 0.99f)
                        .drawWithCache {
                            onDrawWithContent {
                                drawContent()
                                drawRect(
                                    linearGradient,
                                    blendMode = BlendMode.SrcAtop
                                )
                            }
                        },
                tint = if (vmTask.dueDateError.isNotBlank()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Expiration Date",
                color = if (vmTask.dueDateError.isNotBlank()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium,
            )
        }
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(.4f)
        ) {
            Button(
                onClick = { datePickerDialog.show() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                ),
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50.dp))
                    .border(
                        width = 1.dp,
                        color = if (vmTask.dueDateError.isNotBlank()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(50.dp)
                    ),
                contentPadding = PaddingValues(8.dp)
            ) {
                Text(
                    text = vmTask.dueDate?.let { format.format(it) } ?: "Select Date",
                    color = if (vmTask.dueDateError.isNotBlank()) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }

    if (vmTask.dueDateError.isNotBlank()) {
        Text(
            vmTask.dueDateError,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.error
        )
    }

    HorizontalDivider(
        color = MaterialTheme.colorScheme.onSurface,
        thickness = 1.dp,
    )
}