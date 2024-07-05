package it.polito.teamhub.ui.view.component.topBar.action

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.dataClass.task.Task
import it.polito.teamhub.ui.view.component.topBar.ShowMenuTaskDetails
import it.polito.teamhub.viewmodel.MemberViewModel
import it.polito.teamhub.viewmodel.TaskViewModel
import it.polito.teamhub.viewmodel.TeamViewModel

@Composable
fun RenderActionTask(
    navController: NavController,
    vmTask: TaskViewModel?,
    vmTeam: TeamViewModel?,
    vmMember: MemberViewModel?,
    currentRoute: String,
    showConfirmDialog: MutableState<Boolean>,
    deletedTask: MutableState<Long?>,
    popUpRender: MutableState<Boolean>,
    popUpAttachment: MutableState<Boolean>,
    memberLogged: Member
) {

    val showMenu = remember { mutableStateOf(false) }
    when (currentRoute) {
        "task/{taskId}" -> {
            IconButton(
                onClick = { showMenu.value = true },
            ) {
                Icon(
                    imageVector = Icons.Filled.MoreVert,
                    contentDescription = "",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(32.dp)
                )
            }
            if (showMenu.value) {
                RenderActionTaskDetail(
                    navController = navController,
                    vmTask = vmTask,
                    vmTeam = vmTeam,
                    vmMember = vmMember,
                    showMenu = showMenu,
                    showConfirmDialog = showConfirmDialog,
                    deletedTask = deletedTask,
                    popUpRender = popUpRender,
                    popUpAttachment = popUpAttachment,
                    memberLogged = memberLogged
                )
            }
        }

        "team/{teamId}/tasks/create/{isDuplicate}", "task/{taskId}/edit" -> {
            if (vmTask != null) {
                val teamId: Long?
                if (currentRoute.startsWith("team/{teamId}/tasks/create/{isDuplicate}")) {
                    teamId = navController.currentBackStackEntry?.arguments?.getString("teamId")
                        ?.toLongOrNull()
                } else {
                    val taskId = navController.currentBackStackEntry?.arguments?.getString("taskId")
                        ?.toLongOrNull()
                    val task = vmTask.getTaskById(taskId!!).collectAsState(initial = Task()).value
                    teamId = task.idTeam
                }

                Button(
                    onClick = {
                        if (teamId != null && vmTask.validate(teamId)) {
                            val isDuplicate = navController.currentBackStackEntry?.arguments?.getString("isDuplicate").toBoolean()
                            if(isDuplicate){
                                navController.navigate("team/${teamId}/tasks")
                            }
                            else{
                                navController.navigateUp()
                            }

                        }
                    },
                    contentPadding = PaddingValues(horizontal = 10.dp),
                    shape = MaterialTheme.shapes.small,
                    modifier = Modifier.padding(end = 10.dp)
                ) {
                    if (currentRoute.startsWith("team/{teamId}/tasks/create/{isDuplicate}"))
                        Text(text = "Create")
                    else
                        Text(text = "Save")
                }

            }
        }
    }
}

@Composable
private fun RenderActionTaskDetail(
    navController: NavController,
    vmTask: TaskViewModel?,
    vmTeam: TeamViewModel?,
    vmMember: MemberViewModel?,
    showMenu: MutableState<Boolean>,
    showConfirmDialog: MutableState<Boolean>,
    deletedTask: MutableState<Long?>,
    popUpRender: MutableState<Boolean>,
    popUpAttachment: MutableState<Boolean>,
    memberLogged: Member

) {

    val taskId =
        navController.currentBackStackEntry?.arguments?.getString("taskId")
            ?.toLongOrNull()
    if (taskId != null && showMenu.value && vmTask != null && vmTeam != null && vmMember != null) {
        ShowMenuTaskDetails(
            navController,
            vmTask,
            vmTeam,
            taskId,
            popUpRender = popUpRender,
            popUpAttachment = popUpAttachment,
            showMenu,
            showConfirmDialog,
            memberLogged
        ) {
            deletedTask.value = it
            showMenu.value = false
        }
    }
}