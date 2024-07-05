package it.polito.teamhub.ui.view.component.card

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import it.polito.teamhub.R
import it.polito.teamhub.dataClass.condition.Condition
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.dataClass.task.Action
import it.polito.teamhub.dataClass.task.History
import it.polito.teamhub.dataClass.task.Priority
import it.polito.teamhub.dataClass.task.State
import it.polito.teamhub.dataClass.task.Task
import it.polito.teamhub.dataClass.task.listOfState
import it.polito.teamhub.dataClass.team.Role
import it.polito.teamhub.ui.theme.PurpleBlue
import it.polito.teamhub.ui.view.component.review.RenderPopUpChangeState
import it.polito.teamhub.ui.view.component.review.RenderPopUpReview
import it.polito.teamhub.ui.view.component.topBar.RenderConfirmActionDialog
import it.polito.teamhub.utils.isBeforeCurrentDate
import it.polito.teamhub.viewmodel.CategoryViewModel
import it.polito.teamhub.viewmodel.TagViewModel
import it.polito.teamhub.viewmodel.TaskViewModel
import it.polito.teamhub.viewmodel.TeamViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.internal.format
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardRender(
    navController: NavController,
    vmTask: TaskViewModel,
    vmTeam: TeamViewModel,
    vmCategory: CategoryViewModel,
    vmTag: TagViewModel,
    item: Task,
    applyFilter: MutableState<Boolean>,
    conditions: MutableState<List<Condition>>,
    memberList: List<Member>,
    memberLogged: Member
) {
    val format = SimpleDateFormat("dd MMM", Locale.ITALY)
    val day = format.format(item.dueDate).split(" ")[0]
    val month = format.format(item.dueDate).split(" ")[1].replaceFirstChar { it.uppercase() }

    var newState by remember(item.id) { mutableStateOf(item.state) }
    var changedTaskId by remember { mutableIntStateOf(-1) }
    val teamList by vmTeam.teamList.collectAsState()
    if (teamList.isEmpty()) return
    val membersOfTeam = teamList.find { it.id == item.idTeam }?.members!!

    val memberLoggedRole by
    vmTeam.getRoleOfMemberLoggedByTeamId(item.idTeam).collectAsState(initial = Role.MEMBER)

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


    if (showDeleteDialog.value)
        RenderConfirmActionDialog(
            navController = navController,
            vmTask = vmTask,
            onDismissRequest = { showDeleteDialog.value = false },
            showConfirmDialog = showDeleteDialog,
            deletedTask = remember { mutableStateOf(selectedTask.id) },
            navigate = false
        )

    AnimatedVisibility(
        visible =
        if (!applyFilter.value) {
            newState != State.COMPLETED
        } else {
            conditions.value.all { it.condition(item) }
        },
        enter = fadeIn(animationSpec = tween(durationMillis = 500)),
        exit =
        slideOutHorizontally(
            targetOffsetX = { it },
            animationSpec = tween(durationMillis = 500)
        )
    ) {
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
                        navController.navigate("task/${item.id}")
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
                modifier = Modifier
                    .padding(8.dp)
            ) {

                RenderTitle(item.title)
                RenderDescription(item.description)
                if (item.state != State.COMPLETED) {
                    Row(
                        modifier = Modifier
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            modifier = Modifier
                                .padding(start = 6.dp)
                                .weight(.3f),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (isBeforeCurrentDate(item.dueDate))
                                ExpiredDate(day, month)
                            else
                                NotExpiredDate(day, month)
                        }
                        Row(
                            modifier = Modifier
                                .weight(.4f),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            StateDropdownMenu(
                                vmTask = vmTask,
                                task = item,
                                onStateChange = { newState = it },
                                onIdChange = { changedTaskId = it },
                                memberLoggedRole = memberLoggedRole,
                                memberLogged = memberLogged
                            )
                        }
                        Row(
                            horizontalArrangement = Arrangement.End,
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .offset((3 * if (item.members.size < 3) (item.members.size - 1) else 2).dp)
                                .padding(end = 6.dp)
                                .weight(.3f)
                        ) {
                            RenderMember(item, membersOfTeam, memberList)
                        }
                    }
                } else {
                    RenderLastRowCompleted(item, vmTask, membersOfTeam, memberList, memberLogged)
                }
            }
        }
    }
}

@Composable
fun StateDropdownMenu(
    vmTask: TaskViewModel,
    task: Task,
    onStateChange: (State) -> Unit,
    onIdChange: (Int) -> Unit,
    memberLoggedRole: Role?,
    memberLogged: Member
) {
    val items = listOfState()
    var expanded by remember { mutableStateOf(false) }
    var selectedIndex by remember { mutableIntStateOf(0) }
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    val popUpRender = remember { mutableStateOf(false) }
    val returnedPopUpValue = MutableStateFlow<Boolean?>(null)
    val review = remember {
        mutableFloatStateOf(
            0f
        )
    }

    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    Column {
        Row(
            modifier = Modifier
                .onGloballyPositioned { coordinates ->
                    //This value is used to assign to the DropDown the same width
                    textFieldSize = coordinates.size.toSize()
                }
                .background(MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp))
                .border(1.dp, Color.White, RoundedCornerShape(10.dp))
                .clip(RoundedCornerShape(10.dp))
                .clickable { expanded = !expanded }
                .padding(horizontal = 8.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(20.dp)
                    .clip(CircleShape)
                    .border(1.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .background(task.state.getState())
            )
            Text(
                text = task.state.getStateString(),
                style = MaterialTheme.typography.bodySmall,
                fontSize = 10.sp,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            )
            if (memberLoggedRole == Role.ADMIN ||
                task.members.contains(memberLogged.id)
            )
                Icon(
                    icon, "contentDescription",
                    tint = Color.White
                )
        }
        if (memberLoggedRole == Role.ADMIN ||
            task.members.contains(memberLogged.id)
        ) {
            DropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
                modifier = Modifier
                    .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
            ) {
                items.forEachIndexed { indexChoose, s ->
                    DropdownMenuItem(
                        onClick = {
                            selectedIndex = indexChoose
                            expanded = false
                            vmTask.currentTask.value = task
                            if (State.valueOf(items[selectedIndex]) == State.COMPLETED) {
                                popUpRender.value = true
                            } else {
                                vmTask.updateStateById(task.id, State.valueOf(items[selectedIndex]))
                                onStateChange(State.valueOf(items[selectedIndex]))
                                onIdChange(task.id.toInt())
                            }
                        }
                    ) {
                        Box(
                            modifier = Modifier
                                .size(20.dp)
                                .clip(CircleShape)
                                .border(
                                    1.dp,
                                    if (isSystemInDarkTheme()) MaterialTheme.colorScheme.background else MaterialTheme.colorScheme.primary,
                                    CircleShape
                                )
                                .background(
                                    State
                                        .valueOf(s)
                                        .getState()
                                )
                        )
                        Text(
                            text = State.valueOf(s).getStateString(),
                            style = MaterialTheme.typography.bodySmall,
                            fontSize = 10.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
        if (popUpRender.value) {
            if (task.members.contains(memberLogged.id)) {
                RenderPopUpReview(popUpRender, returnedPopUpValue, review)
                LaunchedEffect(returnedPopUpValue) {
                    returnedPopUpValue.collect { value ->
                        if (value == true) {
                            val taskEdited = task.copy(
                                state = State.COMPLETED,
                                mapReview = (task.mapReview + Pair(
                                    memberLogged.id,
                                    review.floatValue
                                )).toMutableMap(),
                                histories = (task.histories + mutableListOf(
                                    History(
                                        author = memberLogged.id,
                                        action = Action.UPDATE_STATUS,
                                        date = Date(),
                                        description = Action.UPDATE_STATUS.getAction(State.COMPLETED.getStateString())
                                    ),
                                    History(
                                        author = memberLogged.id,
                                        action = Action.ADD_REVIEW,
                                        date = Date(),
                                        description = Action.ADD_REVIEW.getAction(
                                            format("%.2f", review.floatValue)
                                        )
                                    )
                                )).toMutableList()
                            )
                            taskEdited.id = task.id
                            vmTask.currentTask.value = taskEdited
                            vmTask.updateTask(id = taskEdited.id)
                            onStateChange(State.COMPLETED)
                            onIdChange(task.id.toInt())
                        } else if (value == false) {
                            vmTask.updateStateById(task.id, State.COMPLETED)
                            onStateChange(State.COMPLETED)
                            onIdChange(task.id.toInt())
                        }
                    }
                }
            } else {
                RenderPopUpChangeState(popUpRender, returnedPopUpValue)
                LaunchedEffect(returnedPopUpValue) {
                    returnedPopUpValue.collect { value ->
                        if (value == true) {
                            vmTask.updateStateById(task.id, State.COMPLETED)
                            onStateChange(State.COMPLETED)
                            onIdChange(task.id.toInt())
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun TaskMenu(
    showMenu: MutableState<Boolean>,
    showDeleteDialog: MutableState<Boolean>,
) {
    val shape = RoundedCornerShape(20.dp)
    Dialog(
        onDismissRequest = {
            showMenu.value = false
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .clip(shape)
                .background(MaterialTheme.colorScheme.background),
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showDeleteDialog.value = true
                            showMenu.value = false
                        }
                        .padding(horizontal = 15.dp, vertical = 15.dp)
                ) {
                    Text(
                        "Delete task", Modifier
                            .align(Alignment.CenterVertically)
                            .weight(1f),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.error
                    )
                    Icon(
                        painter = painterResource(R.drawable.delete),
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.CenterVertically),
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}