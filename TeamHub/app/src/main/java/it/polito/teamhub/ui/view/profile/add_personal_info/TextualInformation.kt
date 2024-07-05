package it.polito.teamhub.ui.view.profile.add_personal_info

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.widget.DatePicker
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import it.polito.teamhub.R
import it.polito.teamhub.dataClass.member.getGenderList
import it.polito.teamhub.ui.theme.PurpleBlue
import it.polito.teamhub.ui.theme.linearGradient
import it.polito.teamhub.viewmodel.MemberViewModel
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Composable
fun SetMainInfo(vmMember: MemberViewModel) {
    OutlinedTextField(
        value = vmMember.fullNameValue,
        onValueChange = vmMember::setFullName,
        label = { Text("*Full Name") },
        placeholder = { Text("Your Full Name", color = MaterialTheme.colorScheme.onSurface) },
        trailingIcon = {
            Icon(
                painterResource(id = R.drawable.id_card),
                contentDescription = "Full Name",
                modifier = Modifier
                    .graphicsLayer(alpha = 0.99f)
                    .drawWithCache {
                        onDrawWithContent {
                            drawContent()
                            drawRect(
                                linearGradient,
                                blendMode = BlendMode.SrcAtop
                            )
                        }
                    }
                    .size(32.dp)
            )
        },
        isError = vmMember.fullNameError.isNotBlank(),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
            errorTextColor = MaterialTheme.colorScheme.onBackground
        )
    )
    if (vmMember.fullNameError.isNotBlank()) {
        Text(
            vmMember.fullNameError,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.labelMedium
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        value = vmMember.nicknameValue,
        onValueChange = vmMember::setNickname,
        label = { Text("*Nickname") },
        placeholder = { Text("Your Nickname", color = MaterialTheme.colorScheme.onSurface) },
        trailingIcon = {
            Icon(
                painterResource(id = R.drawable.person),
                contentDescription = "Nickname",
                modifier = Modifier
                    .graphicsLayer(alpha = 0.99f)
                    .drawWithCache {
                        onDrawWithContent {
                            drawContent()
                            drawRect(
                                linearGradient,
                                blendMode = BlendMode.SrcAtop
                            )
                        }
                    }
                    .size(32.dp)
            )
        },
        isError = vmMember.nicknameError.isNotBlank(),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
            errorTextColor = MaterialTheme.colorScheme.onBackground
        )
    )
    if (vmMember.nicknameError.isNotBlank()) {
        Text(
            vmMember.nicknameError,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.labelMedium
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        value = vmMember.emailValue,
        onValueChange = vmMember::setEmail,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email),
        label = { Text("*Email") },
        placeholder = { Text("Your Email", color = MaterialTheme.colorScheme.onSurface) },
        trailingIcon = {
            Icon(
                painterResource(id = R.drawable.mail),
                contentDescription = "Email",
                modifier = Modifier
                    .graphicsLayer(alpha = 0.99f)
                    .drawWithCache {
                        onDrawWithContent {
                            drawContent()
                            drawRect(
                                linearGradient,
                                blendMode = BlendMode.SrcAtop
                            )
                        }
                    }
                    .size(32.dp)
            )
        },
        isError = vmMember.emailError.isNotBlank(),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
            errorTextColor = MaterialTheme.colorScheme.onBackground
        )
    )
    if (vmMember.emailError.isNotBlank()) {
        Text(
            vmMember.emailError,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.labelMedium
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        value = vmMember.jobTitleValue,
        onValueChange = vmMember::setJobTitle,
        label = { Text("*Job Title") },
        placeholder = { Text("Your Job Title", color = MaterialTheme.colorScheme.onSurface) },
        trailingIcon = {
            Icon(
                painterResource(id = R.drawable.work),
                contentDescription = "Job Title",
                modifier = Modifier
                    .graphicsLayer(alpha = 0.99f)
                    .drawWithCache {
                        onDrawWithContent {
                            drawContent()
                            drawRect(
                                linearGradient,
                                blendMode = BlendMode.SrcAtop
                            )
                        }
                    }
                    .size(32.dp)
            )
        },
        isError = vmMember.jobTitleError.isNotBlank(),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
            errorTextColor = MaterialTheme.colorScheme.onBackground
        )
    )
    if (vmMember.jobTitleError.isNotBlank()) {
        Text(
            vmMember.jobTitleError,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.labelMedium
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        value = vmMember.descriptionValue,
        onValueChange = vmMember::setDescription,
        label = { Text("*Description") },
        placeholder = { Text("Your Description", color = MaterialTheme.colorScheme.onSurface) },
        trailingIcon = {
            Icon(
                painterResource(id = R.drawable.description),
                contentDescription = "Description",
                modifier = Modifier
                    .graphicsLayer(alpha = 0.99f)
                    .drawWithCache {
                        onDrawWithContent {
                            drawContent()
                            drawRect(
                                linearGradient,
                                blendMode = BlendMode.SrcAtop
                            )
                        }
                    }
                    .size(32.dp)
            )
        },
        isError = vmMember.descriptionError.isNotBlank(),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
            errorTextColor = MaterialTheme.colorScheme.onBackground
        )
    )
    if (vmMember.descriptionError.isNotBlank()) {
        Text(
            vmMember.descriptionError,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.labelMedium
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

}

@Composable
fun SetAdditionalPersonalInfo(vmMember: MemberViewModel) {

    OutlinedTextField(
        value = vmMember.locationValue,
        onValueChange = vmMember::setLocation,
        label = { Text("*Location") },
        placeholder = { Text("Your Location", color = MaterialTheme.colorScheme.onSurface) },
        trailingIcon = {
            Icon(
                painterResource(id = R.drawable.location_on),
                contentDescription = "Location",
                modifier = Modifier
                    .graphicsLayer(alpha = 0.99f)
                    .drawWithCache {
                        onDrawWithContent {
                            drawContent()
                            drawRect(
                                linearGradient,
                                blendMode = BlendMode.SrcAtop
                            )
                        }
                    }
                    .size(32.dp)
            )
        },
        isError = vmMember.locationError.isNotBlank(),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
            errorTextColor = MaterialTheme.colorScheme.onBackground
        )
    )
    if (vmMember.locationError.isNotBlank()) {
        Text(
            vmMember.locationError,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.labelMedium
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    OutlinedTextField(
        value = vmMember.phoneNumberValue ?: "",
        onValueChange = vmMember::setPhoneNumber,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
        label = { Text("Phone Number") },
        placeholder = { Text("Your Phone number", color = MaterialTheme.colorScheme.onSurface) },
        trailingIcon = {
            Icon(
                painterResource(id = R.drawable.call),
                contentDescription = "Phone Number",
                modifier = Modifier
                    .graphicsLayer(alpha = 0.99f)
                    .drawWithCache {
                        onDrawWithContent {
                            drawContent()
                            drawRect(
                                linearGradient,
                                blendMode = BlendMode.SrcAtop
                            )
                        }
                    }
                    .size(32.dp)
            )
        },
        isError = vmMember.phoneNumberError.isNotBlank(),
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
            errorTextColor = MaterialTheme.colorScheme.onBackground
        )
    )
    if (vmMember.phoneNumberError.isNotBlank()) {
        Text(
            vmMember.phoneNumberError,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.labelMedium
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    val interactionSource = remember { MutableInteractionSource() }
    val isPressed: Boolean by interactionSource.collectIsPressedAsState()
    var selectedDate by rememberSaveable { mutableStateOf(vmMember.birthDateValue ?: Date()) }
    val calendar = Calendar.getInstance()
    calendar.time = selectedDate
    val year: Int = calendar.get(Calendar.YEAR)
    val month: Int = calendar.get(Calendar.MONTH)
    val day: Int = calendar.get(Calendar.DAY_OF_MONTH)
    calendar.time = Date()

    val context = LocalContext.current
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
    val datePickerDialog =
        DatePickerDialog(
            context,
            R.style.CustomDatePickerDialogTheme,
            { _: DatePicker, pickedYear: Int, pickedMonth: Int, pickedDay: Int ->
                val newDate = Calendar.getInstance()
                newDate.set(pickedYear, pickedMonth, pickedDay)
                selectedDate = newDate.time//"$dayOfMonth/$month/$year"
                vmMember.setBirthDate(selectedDate)
            },
            year,
            month,
            day
        )

    datePickerDialog.setButton(DatePickerDialog.BUTTON_NEUTRAL, "Clear") { _, _ ->
        selectedDate = Date()
        vmMember.setBirthDate(null)
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

    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
            errorTextColor = MaterialTheme.colorScheme.onBackground
        ),
        readOnly = true,
        value = vmMember.birthDateValue?.let { format.format(it) } ?: "",
        onValueChange = {},
        label = { Text("Birth Date") },
        placeholder = { Text("Your Birth Date", color = MaterialTheme.colorScheme.onSurface) },
        trailingIcon = {
            Icon(
                painterResource(id = R.drawable.cake),
                contentDescription = "Birth Date",
                modifier = Modifier
                    .graphicsLayer(alpha = 0.99f)
                    .drawWithCache {
                        onDrawWithContent {
                            drawContent()
                            drawRect(
                                linearGradient,
                                blendMode = BlendMode.SrcAtop
                            )
                        }
                    }
                    .size(32.dp)
            )
        },
        isError = vmMember.birthDateError.isNotBlank(),
        interactionSource = interactionSource,
    )

    if (isPressed) {
        datePickerDialog.show()
    }
    if (vmMember.birthDateError.isNotBlank()) {
        Text(
            vmMember.birthDateError,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.labelMedium
        )
    }

    Spacer(modifier = Modifier.height(16.dp))

    val options = getGenderList()
    var expanded by remember { mutableStateOf(false) }
    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    BoxWithConstraints {
        val textFieldWidth = this.maxWidth
        OutlinedTextField(
            modifier = Modifier
                .fillMaxWidth(),
            label = { Text("Gender") },
            placeholder = { Text("Your Gender", color = MaterialTheme.colorScheme.onSurface) },
            readOnly = true,
            value = vmMember.genderValue.getGenderString(),
            onValueChange = {},
            isError = vmMember.genderError.isNotBlank(),
            trailingIcon = {
                Icon(icon, "gender",
                    Modifier
                        .clickable { expanded = !expanded }
                        .size(32.dp)
                )
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
            )
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(textFieldWidth)
                .background(MaterialTheme.colorScheme.surface)
        ) {
            options.forEach { selectionOption ->
                val backgroundColor =
                    if (vmMember.genderValue.getGenderString() == selectionOption.getGenderString()) {
                        if (isSystemInDarkTheme()) {
                            MaterialTheme.colorScheme.primary
                        } else {
                            MaterialTheme.colorScheme.onSurface
                        }

                    } else {
                        MaterialTheme.colorScheme.surface
                    }
                DropdownMenuItem(
                    text = { Text(selectionOption.getGenderString()) },
                    onClick = {
                        vmMember.setGender(selectionOption)
                        expanded = false
                    },
                    modifier = Modifier
                        .align(alignment = Alignment.End)
                        .background(backgroundColor),
                    colors = MenuDefaults.itemColors(
                        textColor = MaterialTheme.colorScheme.onBackground,
                    )
                )
            }
        }
    }

    if (vmMember.genderError.isNotBlank()) {
        Text(
            vmMember.genderError,
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.labelMedium
        )
    }
}