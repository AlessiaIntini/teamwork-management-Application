package it.polito.teamhub.ui.view.taskView.teamTasks

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.polito.teamhub.R
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.dataClass.task.Priority
import it.polito.teamhub.dataClass.task.State
import it.polito.teamhub.dataClass.task.Task
import it.polito.teamhub.dataClass.team.Role
import it.polito.teamhub.ui.theme.PurpleBlue
import it.polito.teamhub.ui.theme.linearGradient
import it.polito.teamhub.ui.view.component.card.RenderDescription
import it.polito.teamhub.ui.view.component.card.RenderLastRowCompleted
import it.polito.teamhub.ui.view.component.card.RenderTitle
import it.polito.teamhub.ui.view.component.card.TaskMenu
import it.polito.teamhub.ui.view.component.topBar.RenderConfirmActionDialog
import it.polito.teamhub.viewmodel.CategoryViewModel
import it.polito.teamhub.viewmodel.TagViewModel
import it.polito.teamhub.viewmodel.TaskViewModel
import it.polito.teamhub.viewmodel.TeamViewModel
import java.util.Date

@Composable
fun CompletedTasksPane(
    vmTask: TaskViewModel,
    vmTeam: TeamViewModel,
    vmTag: TagViewModel,
    vmCategory: CategoryViewModel,
    searchText: MutableState<String> = remember { mutableStateOf("") },
    teamId: Long,
    navController: NavController,
    memberList: List<Member>,
    memberLogged: Member
) {
    //val memberList by vmMember.getMembers().collectAsState(initial = emptyList())
    val teamCompletedTasksList = vmTask.getTasksCompletedByTeamId(teamId = teamId)
        .collectAsState(initial = emptyList()).value
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
        ) {
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface
            )
            TaskListCompleted(
                navController = navController,
                tasks = teamCompletedTasksList
                    .filter { task ->
                        task.title.contains(searchText.value, ignoreCase = true) ||
                                task.description.contains(
                                    searchText.value,
                                    ignoreCase = true
                                )
                    },
                vmTask = vmTask,
                vmTeam = vmTeam,
                vmTag = vmTag,
                vmCategory = vmCategory,
                memberList = memberList,
                memberLogged = memberLogged
            )
        }
    }
}

@Composable
private fun TaskListCompleted(
    navController: NavController?,
    tasks: List<Task>,
    vmTask: TaskViewModel,
    vmTeam: TeamViewModel,
    vmTag: TagViewModel,
    vmCategory: CategoryViewModel,
    memberList: List<Member>,
    memberLogged: Member
) {
    if (tasks.isNotEmpty()) {
        LazyColumn {
            itemsIndexed(tasks) { _, item ->
                CardRenderCompleted(
                    navController = navController,
                    vmTask = vmTask,
                    vmTag = vmTag,
                    vmCategory = vmCategory,
                    item = item,
                    vmTeam = vmTeam,
                    memberList = memberList,
                    memberLogged = memberLogged
                )
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.tasks),
                    contentDescription = "tasks icon",
                    modifier = Modifier
                        .size(80.dp)
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
                )

                Text(
                    text = "No tasks found",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleSmall
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = "There are no completed tasks in this team",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun CardRenderCompleted(
    navController: NavController?,
    vmTask: TaskViewModel,
    vmTag: TagViewModel,
    vmCategory: CategoryViewModel,
    item: Task,
    vmTeam: TeamViewModel,
    memberList: List<Member>,
    memberLogged: Member
) {
    val teamList by vmTeam.teamList.collectAsState()
    if (teamList.isEmpty()) return
    val membersOfTeam = teamList.find { it.id == item.idTeam }?.members!!
    val memberLoggedRole by vmTeam.getRoleOfMemberLoggedByTeamId(item.idTeam)
        .collectAsState(initial = Role.MEMBER)

    val showDeleteDialog = remember { mutableStateOf(false) }
    val showMenu = remember { mutableStateOf(false) }
    var selectedTask by remember {
        mutableStateOf(
            Task(
                "",
                "",
                mutableListOf(),
                null,
                State.PENDING,
                Priority.LOW,
                mutableListOf(),
                Date(),
                Date(),
                mutableListOf(),
                mutableListOf(
                ),
                0,
                mutableMapOf(),
                mutableListOf()
            )
        )

    }

    if (showMenu.value)
        TaskMenu(showMenu, showDeleteDialog)


    if (showDeleteDialog.value && navController != null)
        RenderConfirmActionDialog(
            navController = navController,
            vmTask = vmTask,
            onDismissRequest = { showDeleteDialog.value = false },
            showConfirmDialog = showDeleteDialog,
            deletedTask = remember { mutableStateOf(selectedTask.id) },
            navigate = false
        )

    Card(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = tween(
                    durationMillis = 500,
                    delayMillis = 100,
                    easing = LinearEasing
                )
            )
            .combinedClickable(
                onClick = {
                    vmTask.currentTask.value = item
                    vmTask.updateIdTask(item.id)
                    vmCategory.updateIdTeam(item.idTeam)
                    vmTag.updateIdTeam(item.idTeam)
                    navController?.navigate("task/${item.id}")
                },
                onLongClick = {
                    if (memberLoggedRole == Role.ADMIN) {
                        showMenu.value = true
                        selectedTask = item
                    }
                }
            ),
        colors = CardDefaults.cardColors(
            containerColor = PurpleBlue,
        ),
    ) {
        Column(
            modifier = Modifier.padding(8.dp)
        ) {
            RenderTitle(title = item.title)
            RenderDescription(description = item.description)
            RenderLastRowCompleted(
                item = item,
                vmTask = vmTask,
                membersOfTeam = membersOfTeam,
                memberList = memberList,
                memberLogged = memberLogged
            )
        }
    }
}