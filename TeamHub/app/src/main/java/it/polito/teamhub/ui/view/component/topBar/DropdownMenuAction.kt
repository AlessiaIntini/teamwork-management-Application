package it.polito.teamhub.ui.view.component.topBar

import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.painterResource
import androidx.navigation.NavController
import it.polito.teamhub.R
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.dataClass.task.State
import it.polito.teamhub.dataClass.team.Role
import it.polito.teamhub.ui.theme.RedOrange
import it.polito.teamhub.viewmodel.TaskViewModel
import it.polito.teamhub.viewmodel.TeamViewModel

@Composable
fun ShowMenuTaskDetails(
    navController: NavController,
    vmTask: TaskViewModel,
    vmTeam: TeamViewModel,
    taskId: Long,
    popUpRender: MutableState<Boolean>,
    popUpAttachment: MutableState<Boolean>,
    menuState: MutableState<Boolean>,
    showConfirmDialog: MutableState<Boolean>,
    memberLogged: Member,
    deletedTask: (Long) -> Unit,
) {
    val task by vmTask.task.collectAsState()

    if (task != null) {
        val memberLoggedRole by vmTeam.getRoleOfMemberLoggedByTeamId(task!!.idTeam).collectAsState(
            initial = Role.MEMBER
        )
        DropdownMenu(
            expanded = menuState.value,
            onDismissRequest = { menuState.value = false },
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = "Show history",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                onClick = {
                    menuState.value = false
                    navController.navigate("task/${taskId}/history")
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.history),
                        contentDescription = "show history icon",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            )
            if (task!!.state != State.COMPLETED && (task!!.members.contains(memberLogged.id) || memberLoggedRole == Role.ADMIN)) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "Edit task",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    onClick = {
                        menuState.value = false
                        navController.navigate("task/${taskId}/edit")

                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.edit),
                            contentDescription = "edit task icon",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                )
            }
            if (task!!.state != State.COMPLETED && memberLoggedRole != Role.GUEST) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "Duplicate task",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    onClick = {
                        menuState.value = false
                        val isDuplicate = true
                        navController.navigate("team/${task!!.idTeam}/tasks/create/${isDuplicate}")

                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.copy),
                            contentDescription = "duplicate task icon",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                )

                DropdownMenuItem(
                    text = {
                        Text(
                            text = "Add attachment",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    onClick = {
                        menuState.value = false
                        popUpAttachment.value = true
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.attach),
                            contentDescription = "delete task icon",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                )
            }
            if (task!!.state == State.COMPLETED &&
                task!!.members.contains(memberLogged.id) &&
                !task!!.mapReview.containsKey(memberLogged.id)
            ) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "Leave Review",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                    },
                    onClick = {
                        menuState.value = false
                        popUpRender.value = true
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.leave_review),
                            contentDescription = "edit task icon",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                )

            }
            if (memberLoggedRole == Role.ADMIN) {
                DropdownMenuItem(
                    text = {
                        Text(
                            text = "Delete task",
                            style = MaterialTheme.typography.bodyMedium,
                            color = RedOrange
                        )
                    },
                    onClick = {
                        showConfirmDialog.value = true
                        deletedTask(taskId)
                    },
                    leadingIcon = {
                        Icon(
                            painter = painterResource(id = R.drawable.delete),
                            contentDescription = "delete task icon",
                            tint = RedOrange
                        )
                    }
                )
            }
        }
    }
}