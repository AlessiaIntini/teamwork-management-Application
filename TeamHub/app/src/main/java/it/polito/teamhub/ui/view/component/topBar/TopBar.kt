package it.polito.teamhub.ui.view.component.topBar

import androidx.compose.foundation.layout.size
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.polito.teamhub.R
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.dataClass.team.Team
import it.polito.teamhub.ui.theme.PurpleBlue
import it.polito.teamhub.ui.view.chatView.NewChatPane
import it.polito.teamhub.ui.view.component.profileIcon.TypeProfileIcon
import it.polito.teamhub.ui.view.component.topBar.action.RenderActionPersonalInfo
import it.polito.teamhub.ui.view.component.topBar.action.RenderActionTask
import it.polito.teamhub.ui.view.component.topBar.action.RenderActionTeam
import it.polito.teamhub.viewmodel.ChatViewModel
import it.polito.teamhub.viewmodel.MemberViewModel
import it.polito.teamhub.viewmodel.TaskViewModel
import it.polito.teamhub.viewmodel.TeamViewModel
import kotlinx.coroutines.launch

data class TopBarValue(
    val title: String,
    var home: Boolean = false,
    val team: Boolean = false,
    val backArrow: Boolean = false,
    var iconTeam: Boolean = false,
    val color: Color = PurpleBlue,
    val vmTask: TaskViewModel? = null,
    val vmMember: MemberViewModel? = null,
    var vmTeam: TeamViewModel? = null,
    var imageTeam: String = "",
    var plus: Boolean = false,
    var popUpRender: MutableState<Boolean> = mutableStateOf(false),
    var vmChat: ChatViewModel? = null,
    var chatMode: Boolean = false,
    var typeChat: TypeProfileIcon? = null,
    var teamChat: Team? = null,
    var memberChat: Member? = null,
    var popUpAttachment: MutableState<Boolean> = mutableStateOf(false),
    var clickable: Boolean = true
)


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopBar(
    navController: NavController,
    topBarParameter: TopBarValue,
    memberLogged: Member
) {
    val backgroundColor = MaterialTheme.colorScheme.background
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val currentRoute = navController.currentBackStackEntry?.destination?.route

    val showConfirmDialog = remember { mutableStateOf(false) }
    val deletedTask = remember { mutableStateOf<Long?>(null) }

    var showChatBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(showChatBottomSheet)
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = showChatBottomSheet) {
        if (showChatBottomSheet) {
            scope.launch {
                sheetState.show()
            }
        } else {
            scope.launch {
                sheetState.hide()
            }
        }
    }

    if (showChatBottomSheet) {
        NewChatPane(
            vmMember = topBarParameter.vmMember!!,
            vmTeam = topBarParameter.vmTeam!!,
            vmChat = topBarParameter.vmChat!!,
            sheetState = sheetState,
            onDismissRequest = {
                showChatBottomSheet = false
            },
            navController = navController,
            memberLogged = memberLogged
        )
    }

    CenterAlignedTopAppBar(
        title = {
            RenderTitle(
                navController = navController,
                home = topBarParameter.home,
                team = topBarParameter.team,
                iconTeam = topBarParameter.iconTeam,
                imageTeam = topBarParameter.imageTeam,
                title = topBarParameter.title,
                color = topBarParameter.color,
                chatMode = topBarParameter.chatMode,
                typeChat = topBarParameter.typeChat,
                teamChat = topBarParameter.teamChat,
                memberChat = topBarParameter.memberChat,
                clickable = topBarParameter.clickable
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(containerColor = backgroundColor),
        modifier = Modifier
            .drawWithContent {
                drawContent()
                drawLine(
                    color = onSurfaceColor,
                    start = Offset(x = 0f, y = size.height - 1.dp.toPx()),
                    end = Offset(x = size.width, y = size.height - 1.dp.toPx()),
                    strokeWidth = 1.dp.toPx()
                )
            },
        navigationIcon = {
            if (topBarParameter.backArrow) {
                RenderBackArrow(
                    navController = navController,
                    vmMember = topBarParameter.vmMember,
                    showConfirmDialog = showConfirmDialog,
                )
            }
            if (showConfirmDialog.value) {
                var onDismissRequest: () -> Unit = {}
                if (topBarParameter.vmTask != null) {
                    onDismissRequest = { showConfirmDialog.value = false }

                } else if (topBarParameter.vmTeam != null) {
                    onDismissRequest = { }
                }
                RenderConfirmActionDialog(
                    navController = navController,
                    vmTask = topBarParameter.vmTask,
                    onDismissRequest = onDismissRequest,
                    showConfirmDialog = showConfirmDialog,
                    deletedTask = deletedTask,
                    vmTeam = topBarParameter.vmTeam
                )
            }
        },
        actions = {
            if (topBarParameter.plus) {
                IconButton(
                    onClick = {
                        showChatBottomSheet = true
                    },
                ) {
                    Icon(
                        painterResource(id = R.drawable.plus),
                        contentDescription = "Add Team",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

            when (currentRoute) {
                "profile/create", "profile/personalInfo/{userId}", "profile/personalInfo/{userId}/edit" -> {
                    RenderActionPersonalInfo(
                        navController = navController,
                        vmMember = topBarParameter.vmMember,
                        currentRoute = currentRoute,
                        memberLogged = memberLogged,
                        vmTeam = topBarParameter.vmTeam,
                    )
                }

                "team/create", "team/{teamId}/edit", "team/{teamId}" -> {
                    RenderActionTeam(
                        navController = navController,
                        vmTeam = topBarParameter.vmTeam,
                        currentRoute = currentRoute,
                    )
                }

                "task/{taskId}", "team/{teamId}/tasks/create/{isDuplicate}", "task/{taskId}/edit" -> {
                    RenderActionTask(
                        navController = navController,
                        vmTask = topBarParameter.vmTask,
                        vmTeam = topBarParameter.vmTeam,
                        vmMember = topBarParameter.vmMember,
                        currentRoute = currentRoute,
                        showConfirmDialog = showConfirmDialog,
                        deletedTask = deletedTask,
                        popUpRender = topBarParameter.popUpRender,
                        popUpAttachment = topBarParameter.popUpAttachment,
                        memberLogged = memberLogged

                    )
                }
            }
        }
    )
}