package it.polito.teamhub.ui.view.teamView.createTeam


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import it.polito.teamhub.viewmodel.TeamViewModel

@Composable
fun SetInformationTeam(
    vmTeam: TeamViewModel,
) {

    OutlinedTextField(
        value = vmTeam.nameValue,
        onValueChange = vmTeam::updateNameTeam,
        label = { Text("Team name", style = MaterialTheme.typography.bodyMedium) },
        placeholder = {
            Text(
                "Team name",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        },
        isError = vmTeam.nameError.isNotBlank(),
        modifier = Modifier
            .fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = MaterialTheme.colorScheme.onBackground,
            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
            errorTextColor = MaterialTheme.colorScheme.onBackground,
        ),
        trailingIcon = {
            if (vmTeam.nameValue != "") {
                IconButton(
                    onClick = { vmTeam.updateNameTeam("") },
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
    if (vmTeam.nameError.isNotBlank()) {
        Text(
            vmTeam.nameError,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.error
        )
    }

    Spacer(modifier = Modifier.height(10.dp))
    Box(
        modifier = Modifier
            .heightIn(max = 200.dp)
            .verticalScroll(rememberScrollState())
    ) {
        OutlinedTextField(
            value = vmTeam.descriptionValue,
            onValueChange = vmTeam::updateDescriptionTeam,
            label = { Text("Description", style = MaterialTheme.typography.bodyMedium) },
            placeholder = {
                Text(
                    "Description",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurface
                )
            },
            isError = vmTeam.descriptionError.isNotBlank(),
            minLines = 5,
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth(),
            /*modifier = Modifier
                .fillMaxWidth(),*/
            colors = OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onBackground,
                unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                errorTextColor = MaterialTheme.colorScheme.onBackground
            ),
            trailingIcon = {
                if (vmTeam.descriptionValue != "") {
                    IconButton(
                        onClick = { vmTeam.updateDescriptionTeam("") },
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
    }
    if (vmTeam.descriptionError.isNotBlank()) {
        Text(
            vmTeam.descriptionError,
            style = MaterialTheme.typography.labelMedium,
            color = MaterialTheme.colorScheme.error
        )
    }

}