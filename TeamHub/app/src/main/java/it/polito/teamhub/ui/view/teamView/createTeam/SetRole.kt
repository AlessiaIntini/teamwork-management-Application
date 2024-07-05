package it.polito.teamhub.ui.view.teamView.createTeam

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import it.polito.teamhub.dataClass.team.Role
import it.polito.teamhub.dataClass.team.TeamMember
import it.polito.teamhub.viewmodel.TeamViewModel


@Composable
fun RoleDropdownMenu(
    teamMember: TeamMember,
    vmTeam: TeamViewModel
) {
    var expanded by remember { mutableStateOf(false) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    val options =
        if (teamMember.role != null) Role.entries.filter { it != Role.GUEST } else Role.entries
    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    val showConfirmDialog = remember { mutableStateOf(false) }

    Column {
        Box {
            Row(
                modifier = Modifier
                    .onGloballyPositioned { coordinates ->
                        //This value is used to assign to the DropDown the same width
                        textFieldSize = coordinates.size.toSize()
                    }
                    .background(
                        MaterialTheme.colorScheme.background,
                        RoundedCornerShape(10.dp)
                    )
                    .border(
                        1.dp,
                        MaterialTheme.colorScheme.primary,
                        RoundedCornerShape(10.dp)
                    )
                    .clip(RoundedCornerShape(10.dp))
                    .clickable { if (teamMember.role != Role.GUEST) expanded = !expanded }
                    .padding(horizontal = 6.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (teamMember.role != null) teamMember.role!!.getRoleString() else "",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier
                        .weight(1f)
                        .padding(start = 8.dp)
                )
                if (teamMember.role != Role.GUEST) {
                    Icon(
                        icon, "contentDescription",
                        tint = MaterialTheme.colorScheme.primary,
                    )
                }
            }
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
            ) {
                options.forEachIndexed { _, label ->
                    DropdownMenuItem(onClick = {
                        if (label != Role.GUEST) {
                            vmTeam.updateRoleMember(
                                label,
                                teamMember.idMember
                            )
                            expanded = false
                        } else {
                            showConfirmDialog.value = true
                            expanded = false
                        }
                    }
                    ) {
                        Text(
                            text = label.getRoleString(),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(start = 2.dp)
                        )
                    }
                }
            }
        }
    }
    if (showConfirmDialog.value) {
        ConfirmGuestRoleDialog(
            fullname = teamMember.fullname,
            onDismissRequest = { showConfirmDialog.value = false },
            role = Role.GUEST,
            idMember = teamMember.idMember,
            vmTeam = vmTeam
        )
    }
}

@Composable
fun ConfirmGuestRoleDialog(
    fullname: String,
    onDismissRequest: () -> Unit,
    role: Role,
    idMember: Long,
    vmTeam: TeamViewModel
) {
    Dialog(
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Confirm selection",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Are you sure you want to add\n${fullname} as a guest?",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "This action is irreversible.\n" +
                            "Once you add $fullname as a guest,\nthe role can no longer be changed",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            onDismissRequest()
                        },
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onBackground
                        ),
                    ) {
                        Text(text = "Cancel", color = MaterialTheme.colorScheme.onBackground)
                    }
                    Button(
                        onClick = {
                            vmTeam.updateRoleMember(
                                role,
                                idMember
                            )
                            onDismissRequest()
                        },
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                    ) {
                        Text(text = "Save", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }
            }
        }
    }
}
