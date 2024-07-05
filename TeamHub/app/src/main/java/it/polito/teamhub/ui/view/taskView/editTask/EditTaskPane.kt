package it.polito.teamhub.ui.view.taskView.editTask

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.navigation.NavController
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.dataClass.task.Category
import it.polito.teamhub.dataClass.task.Tag
import it.polito.teamhub.ui.view.component.topBar.TopBar
import it.polito.teamhub.ui.view.component.topBar.TopBarValue
import it.polito.teamhub.ui.view.taskView.createTask.ColumnLayout
import it.polito.teamhub.ui.view.taskView.createTask.RowLayout
import it.polito.teamhub.viewmodel.CategoryViewModel
import it.polito.teamhub.viewmodel.MemberViewModel
import it.polito.teamhub.viewmodel.TagViewModel
import it.polito.teamhub.viewmodel.TaskViewModel
import it.polito.teamhub.viewmodel.TeamViewModel

@Composable
fun EditTaskPane(
    navController: NavController,
    vmTask: TaskViewModel,
    vmTag: TagViewModel,
    vmCategory: CategoryViewModel,
    vmMember: MemberViewModel,
    vmTeam: TeamViewModel,
    memberLogged: Member
) {
    //from MainActivity
    val memberList by vmMember.getMembers().collectAsState(emptyList())
    val team by vmTeam.getTeamById(vmTask.currentTask.value.idTeam).collectAsState(initial = null)
    val currentCategory by vmCategory.getCategoryById(vmTask.currentTask.value.category!!).collectAsState(initial = Category("", -2))
    val currentTags by vmTag.getListTagById(vmTask.currentTask.value.tag).collectAsState(initial = listOf(Tag("", -2)))

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
            vmTask.idEdit = vmTask.currentTask.value.id
            vmTask.updateTitle(vmTask.currentTask.value.title)
            vmTask.updateDescription(vmTask.currentTask.value.description)
            vmTask.updateStateCreation(vmTask.currentTask.value.state)
            vmTask.updatePriority(vmTask.currentTask.value.priority)
            vmTask.updateDueDate(vmTask.currentTask.value.dueDate)
            vmTask.updateTeamMembers(vmTask.currentTask.value.members)
            vmTask.updateUrl(vmTask.currentTask.value.url)
            vmTask.updateAttachment(vmTask.currentTask.value.attachment)
            vmTask.isEditing = true
        }
    }


    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            val topBarParameter = TopBarValue(
                title = "Edit Task",
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
                    ColumnLayout(
                        vmTask = vmTask,
                        vmTag = vmTag,
                        vmCategory = vmCategory,
                        teamId = vmTask.currentTask.value.idTeam,
                        memberList= memberList,
                        team = team,
                        memberLogged = memberLogged
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                        .verticalScroll(scrollState),
                    contentAlignment = Alignment.Center
                ) {
                    RowLayout(
                        vmTask = vmTask,
                        vmTag = vmTag,
                        vmCategory = vmCategory,
                        teamId = vmTask.currentTask.value.idTeam,
                        memberList= memberList,
                        team = team,
                        memberLogged = memberLogged
                    )
                }
            }
        }
    }
}