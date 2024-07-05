package it.polito.teamhub.ui.view.taskView.createTask

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import it.polito.teamhub.viewmodel.TaskViewModel


@Composable
fun SetTextualInfo(vm: TaskViewModel) {
    OutlinedTextField(
        value = vm.title,
        onValueChange = vm::updateTitle,
        label = { Text("Title", style = MaterialTheme.typography.bodyMedium) },
        placeholder = {
            Text(
                "Title",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        isError = vm.titleError.isNotBlank(),
        modifier = Modifier
            .fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
            errorTextColor = MaterialTheme.colorScheme.onBackground,
        ),
        trailingIcon = {
            if (vm.title != "") {
                IconButton(
                    onClick = { vm.updateTitle("") },
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Clear,
                        contentDescription = "Clear Icon",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    )
    if (vm.titleError.isNotBlank()) {
        Text(
            vm.titleError,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.error
        )
    }

    Spacer(modifier = Modifier.height(10.dp))

    OutlinedTextField(
        value = vm.description,
        onValueChange = vm::updateDescription,
        label = { Text("Description", style = MaterialTheme.typography.bodyMedium) },
        placeholder = {
            Text(
                "Description",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        isError = vm.descriptionError.isNotBlank(),
        minLines = 5,
        modifier = Modifier
            .fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
            errorTextColor = MaterialTheme.colorScheme.onBackground
        ),
        trailingIcon = {
            if (vm.description != "") {
                IconButton(
                    onClick = { vm.updateDescription("") },
                ) {
                    Icon(
                        imageVector = Icons.Rounded.Clear,
                        contentDescription = "Clear Icon",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    )
    if (vm.descriptionError.isNotBlank()) {
        Text(
            vm.descriptionError,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.error
        )
    }

    HorizontalDivider(
        modifier = Modifier.padding(top = 20.dp),
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.onSurface
    )
}