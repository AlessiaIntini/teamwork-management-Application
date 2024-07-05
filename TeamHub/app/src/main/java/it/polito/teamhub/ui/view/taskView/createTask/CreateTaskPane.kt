package it.polito.teamhub.ui.view.taskView.createTask

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.dataClass.task.Category
import it.polito.teamhub.dataClass.task.Tag
import it.polito.teamhub.dataClass.task.Task
import it.polito.teamhub.dataClass.team.Team
import it.polito.teamhub.ui.view.component.topBar.TopBar
import it.polito.teamhub.ui.view.component.topBar.TopBarValue
import it.polito.teamhub.viewmodel.CategoryViewModel
import it.polito.teamhub.viewmodel.TagViewModel
import it.polito.teamhub.viewmodel.TaskViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ColumnLayout(
    vmTask: TaskViewModel,
    vmTag: TagViewModel,
    vmCategory: CategoryViewModel,
    teamId: Long,
    memberList: List<Member>,
    team: Team?,
    memberLogged: Member
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 20.dp),
    ) {
        SetTextualInfo(vmTask)
        SetMembers(vmTask, memberList, team)
        SetExpirationDate(vmTask)
        SetState(vmTask)
        SetPriority(vmTask)
        SetTags(vmTask, vmTag, teamId, memberLogged)
        SetCategory(vmTask, vmCategory, teamId)
        SetUrl(vmTask)
        SetAttachments(vmTask)

    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RowLayout(
    vmTask: TaskViewModel,
    vmTag: TagViewModel,
    vmCategory: CategoryViewModel,
    teamId: Long,
    memberList: List<Member>,
    team: Team?,
    memberLogged: Member

) {
    Column(
        modifier = Modifier
            .fillMaxWidth(.8f)
            .padding(horizontal = 16.dp, vertical = 20.dp)
    ) {
        SetTextualInfo(vmTask)
        SetMembers(vmTask, memberList, team)
        SetExpirationDate(vmTask)
        SetState(vmTask)
        SetPriority(vmTask)
        SetTags(vmTask, vmTag, teamId, memberLogged)
        SetCategory(vmTask, vmCategory, teamId)
        SetUrl(vmTask)
        SetAttachments(vmTask)
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CreateTaskPane(
    navController: NavController,
    vmTask: TaskViewModel,
    vmTag: TagViewModel,
    vmCategory: CategoryViewModel,
    memberList: List<Member>,
    team: Team?,
    isDuplicate: Boolean,
    memberLogged: Member
) {
    val scrollState = rememberScrollState()
    val teamId = navController.currentBackStackEntry
        ?.arguments?.getString("teamId")?.toLongOrNull()
    if(isDuplicate){
        val task by vmTask.task.collectAsState()
        if (task != null) {
            vmTask.currentTask.value = task as Task

            val currentCategory by vmCategory.getCategoryById(vmTask.currentTask.value.category!!).collectAsState(initial = Category("", -2))
            val currentTags by vmTag.getListTagById(vmTask.currentTask.value.tag).collectAsState(initial = listOf(
                Tag("", -2)
            ))

            LaunchedEffect(currentTags) {
                if(currentTags.isNotEmpty() && currentTags[0].teamId != -2L)
                    vmTask.updateTag(currentTags.toMutableList())
            }
            LaunchedEffect(currentCategory) {
                if (currentCategory.teamId != (-2).toLong())
                    vmTask.updateCategory(currentCategory)
            }
            LaunchedEffect(key1 = true) {
                if (vmTask.title == "") {
                    vmTask.updateTitle(vmTask.currentTask.value.title)
                    vmTask.updateDescription(vmTask.currentTask.value.description)
                    vmTask.updatePriority(vmTask.currentTask.value.priority)
                    vmTask.updateTeamMembers(vmTask.currentTask.value.members)
                    vmTask.updateAttachment(vmTask.currentTask.value.attachment)
                    vmTask.updateUrl(vmTask.currentTask.value.url)
                    vmTask.isEditing = false
                }
            }
        }
    }

    Scaffold(
        topBar = {
            val topBarParameter = TopBarValue(
                title = "New Task",
                backArrow = true,
                iconTeam = false,
                vmTask = vmTask,
            )
            TopBar(navController, topBarParameter, memberLogged)
        }) { innerPadding ->
        BoxWithConstraints {
            if (this.maxHeight > this.maxWidth) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(scrollState),
                ) {
                    if(teamId != null) {
                        ColumnLayout(
                            vmTask = vmTask,
                            vmTag = vmTag,
                            vmCategory = vmCategory,
                            teamId = teamId,
                            memberList,
                            team,
                            memberLogged
                        )
                    }
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(scrollState),
                    contentAlignment = Alignment.Center
                ) {
                    if(teamId != null) {
                        RowLayout(
                            vmTask = vmTask,
                            vmTag = vmTag,
                            vmCategory = vmCategory,
                            teamId = teamId,
                            memberList,
                            team,
                            memberLogged
                        )
                    }
                }
            }
        }
    }
}