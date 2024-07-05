package it.polito.teamhub.ui.view.taskView.personalTasks

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
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
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import androidx.navigation.NavController
import it.polito.teamhub.R
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.dataClass.task.Action
import it.polito.teamhub.dataClass.task.History
import it.polito.teamhub.dataClass.task.Priority
import it.polito.teamhub.dataClass.task.State
import it.polito.teamhub.dataClass.task.Task
import it.polito.teamhub.dataClass.task.listOfState
import it.polito.teamhub.dataClass.team.Role
import it.polito.teamhub.dataClass.team.Team
import it.polito.teamhub.ui.theme.PurpleBlue
import it.polito.teamhub.ui.theme.linearGradient
import it.polito.teamhub.ui.view.component.card.ExpiredDate
import it.polito.teamhub.ui.view.component.card.NotExpiredDate
import it.polito.teamhub.ui.view.component.card.RenderDescription
import it.polito.teamhub.ui.view.component.card.RenderLastRowCompleted
import it.polito.teamhub.ui.view.component.card.RenderMember
import it.polito.teamhub.ui.view.component.card.TaskMenu
import it.polito.teamhub.ui.view.component.profileIcon.RenderProfile
import it.polito.teamhub.ui.view.component.profileIcon.TypeProfileIcon
import it.polito.teamhub.ui.view.component.review.RenderPopUpReview
import it.polito.teamhub.ui.view.component.topBar.RenderConfirmActionDialog
import it.polito.teamhub.ui.view.component.topBar.TopBar
import it.polito.teamhub.ui.view.component.topBar.TopBarValue
import it.polito.teamhub.utils.isBeforeCurrentDate
import it.polito.teamhub.viewmodel.TaskViewModel
import it.polito.teamhub.viewmodel.TeamViewModel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.internal.format
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun PersonalTasksPane(
    navController: NavController,
    vmTask: TaskViewModel,
    vmTeam: TeamViewModel,
    memberList: List<Member>,
    memberLogged: Member
) {
    Scaffold(
        topBar = {
            val topBarParameter = TopBarValue(
                title = "My Tasks",
                vmTask = vmTask,
            )
            TopBar(navController, topBarParameter, memberLogged)
        },
    ) { innerPadding ->
        var isColumnLayout: Boolean
        BoxWithConstraints {
            if (this.maxHeight > this.maxWidth) {
                isColumnLayout = true
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .padding(
                            top = innerPadding.calculateTopPadding(),
                            bottom = innerPadding.calculateBottomPadding()
                        )
                        .fillMaxWidth()
                ) {
                    TaskTabs(
                        navController = navController,
                        vmTask = vmTask,
                        vmTeam = vmTeam,
                        isColumnLayout = isColumnLayout,
                        memberList = memberList,
                        memberLogged = memberLogged
                    )
                }

            } else {
                isColumnLayout = false
                Box(
                    modifier = Modifier
                        .padding(
                            top = innerPadding.calculateTopPadding(),
                            bottom = innerPadding.calculateBottomPadding()
                        )
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(.8f),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        TaskTabs(
                            navController = navController,
                            vmTask = vmTask,
                            vmTeam = vmTeam,
                            isColumnLayout = isColumnLayout,
                            memberList = memberList,
                            memberLogged = memberLogged
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun TaskTabs(
    navController: NavController,
    vmTask: TaskViewModel,
    vmTeam: TeamViewModel,
    isColumnLayout: Boolean,
    memberList: List<Member>,
    memberLogged: Member
) {
    val allStates = listOf("ALL") + State.entries.toTypedArray()
    val selectedTabIndex = remember { mutableIntStateOf(0) }
    val taskList by vmTask.getTasks().collectAsState(initial = listOf())
    val personalTasks = taskList.filter { task -> task.members.contains(memberLogged.id) }

    ScrollableTabRow(
        selectedTabIndex = selectedTabIndex.intValue,
        containerColor = MaterialTheme.colorScheme.background,
        contentColor = PurpleBlue,
        edgePadding = 0.dp,
        modifier = Modifier.fillMaxWidth(if (isColumnLayout) 1f else .8f),
    ) {
        allStates.forEachIndexed { index, state ->
            Tab(
                text = {
                    Text(
                        text = if (state is State) state.getStateString() else "All",
                        style = MaterialTheme.typography.bodyLarge
                    )
                },
                selected = selectedTabIndex.intValue == index,
                onClick = { selectedTabIndex.intValue = index },
                modifier = Modifier.fillMaxWidth()
            )
        }
    }


    when (allStates[selectedTabIndex.intValue]) {
        "ALL" -> {
            if (personalTasks.isNotEmpty())
                TaskList(
                    navController,
                    personalTasks,
                    vmTask,
                    vmTeam,
                    null,
                    memberList,
                    memberLogged
                )
            else {
                NoTasksFound()
            }
        }

        State.PENDING -> {

            if (personalTasks.any { it.state == State.PENDING })
                TaskList(
                    navController,
                    personalTasks.filter { it.state == State.PENDING },
                    vmTask, vmTeam, State.PENDING,
                    memberList,
                    memberLogged
                )
            else {
                NoTasksFound(State.PENDING)
            }
        }

        State.IN_PROGRESS -> {

            if (personalTasks.any { it.state == State.IN_PROGRESS })
                TaskList(
                    navController,
                    personalTasks.filter { it.state == State.IN_PROGRESS },
                    vmTask, vmTeam, State.IN_PROGRESS,
                    memberList,
                    memberLogged
                )
            else {
                NoTasksFound(State.IN_PROGRESS)
            }
        }

        State.ON_HOLD -> {

            if (personalTasks.any { it.state == State.ON_HOLD })
                TaskList(
                    navController,
                    personalTasks.filter { it.state == State.ON_HOLD },
                    vmTask, vmTeam, State.ON_HOLD,
                    memberList,
                    memberLogged
                )
            else {
                NoTasksFound(State.ON_HOLD)
            }
        }

        State.COMPLETED -> {

            if (personalTasks.any { it.state == State.COMPLETED })
                TaskList(
                    navController,
                    personalTasks.filter { it.state == State.COMPLETED },
                    vmTask, vmTeam, State.COMPLETED,
                    memberList,
                    memberLogged
                )
            else {
                NoTasksFound(State.COMPLETED)
            }
        }
    }
}

@Composable
fun TaskList(
    navController: NavController,
    tasks: List<Task>,
    vmTask: TaskViewModel,
    vmTeam: TeamViewModel,
    stateCurrent: State? = null,
    memberList: List<Member>,
    memberLogged: Member
) {
    LazyColumn {
        itemsIndexed(tasks) { _, item ->
            CardRenderTeam(
                navController = navController,
                vmTask = vmTask,
                item = item,
                vmTeam = vmTeam,
                stateCurrent = stateCurrent,
                tasks = tasks,
                memberList = memberList,
                memberLogged = memberLogged
            )
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CardRenderTeam(
    navController: NavController,
    vmTask: TaskViewModel,
    vmTeam: TeamViewModel,
    item: Task,
    stateCurrent: State? = null,
    tasks: List<Task> = listOf(),
    memberList: List<Member>,
    memberLogged: Member
) {
    val format = SimpleDateFormat("dd MMM", Locale.ITALY)
    val day = format.format(item.dueDate).split(" ")[0]
    val month = format.format(item.dueDate).split(" ")[1].replaceFirstChar { it.uppercase() }
    var isVisible by remember(item.id) {
        mutableStateOf(true)
    }
    var newState by remember(item.id) {
        mutableStateOf(item.state)
    }
    var changedTaskId by remember { mutableIntStateOf(-1) }
    val team by vmTeam.teamList.collectAsState()
    if (team.isEmpty()) return
    val membersOfTeam = team.find { it.id == item.idTeam }?.members ?: mutableListOf()
    if (membersOfTeam.isEmpty()) return

    val showDeleteDialog = remember { mutableStateOf(false) }
    val showMenu = remember { mutableStateOf(false) }
    val memberLoggedRole by vmTeam.getRoleOfMemberLoggedByTeamId(item.idTeam)
        .collectAsState(initial = Role.MEMBER)
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

    var animationFinished by remember { mutableStateOf(false) }
    LaunchedEffect(newState) {
        isVisible = newState == stateCurrent && tasks.contains(item)


    }
    LaunchedEffect(isVisible) {
        if (!isVisible) {
            delay(500)
            animationFinished = true

        }
    }

    LaunchedEffect(animationFinished) {
        if (animationFinished && newState != item.state) {
            vmTask.updateStateById(item.id, newState)
            animationFinished = false
        }
    }

    val direction =
        if (stateCurrent != null) if (newState.ordinal > stateCurrent.ordinal) 1 else -1 else 0

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
        visible = if (stateCurrent != null) isVisible else true,
        enter = EnterTransition.None,/*fadeIn(animationSpec = tween(durationMillis = 500)),*/
        exit =
        slideOutHorizontally(
            targetOffsetX = { direction * it },
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
                containerColor = PurpleBlue
            ),
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 8.dp, horizontal = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = item.title,
                        color = Color.White,
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier
                            .weight(1f)
                            .padding(top = 4.dp)
                    )
                    Box(
                        modifier = Modifier
                            .background(Color.White, shape = RoundedCornerShape(20.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                            .wrapContentSize()
                            .widthIn(min = 80.dp, max = 140.dp),
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            val currentTeam by vmTeam.getTeamById(item.idTeam)
                                .collectAsState(initial = Team())
                            if (currentTeam.name != "") {
                                Box(
                                    modifier = Modifier
                                        .size(24.dp)
                                        .background(currentTeam.color, shape = CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    RenderProfile(
                                        imageProfile = currentTeam.imageTeam,
                                        backgroundColor = currentTeam.color,
                                        sizeTextPerson = MaterialTheme.typography.bodySmall.fontSize,
                                        sizeImage = 22.dp,
                                        typeProfile = TypeProfileIcon.TEAM,
                                    )
                                }
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = currentTeam.name,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }
                RenderDescription(description = item.description)
                Row(
                    modifier = if (item.state != State.COMPLETED)
                        Modifier
                            .padding(horizontal = 6.dp, vertical = 8.dp)
                    else Modifier,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    when (item.state) {
                        State.COMPLETED -> {
                            RenderLastRowCompleted(
                                item = item,
                                vmTask = vmTask,
                                membersOfTeam = membersOfTeam,
                                memberList = memberList,
                                memberLogged = memberLogged,
                            )
                        }

                        else -> {
                            Row(
                                modifier = Modifier
                                    .weight(.3f),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                if (isBeforeCurrentDate(item.dueDate)) {
                                    ExpiredDate(day = day, month = month)
                                } else {
                                    NotExpiredDate(day = day, month = month)
                                }
                            }
                            Row(
                                modifier = Modifier
                                    .weight(.4f),
                                horizontalArrangement = Arrangement.Center,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                StateDropdownMenu(
                                    vmTask = vmTask,
                                    vmTeam = vmTeam,
                                    item = item,
                                    memberLogged = memberLogged,
                                    onStateChange = { newState = it },
                                    onIdChange = { changedTaskId = it },
                                    stateCurrent = stateCurrent
                                )
                            }
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .offset((3 * if (item.members.size < 3) (item.members.size - 1) else 2).dp)
                            .padding(end = 6.dp)
                            .weight(.3f)
                    ) {
                        RenderMember(
                            task = item,
                            membersOfTeam = membersOfTeam,
                            memberList = memberList
                        )
                    }
                }
            }
        }
    }

}

@Composable
fun StateDropdownMenu(
    vmTask: TaskViewModel,
    vmTeam: TeamViewModel,
    item: Task,
    memberLogged: Member,
    onStateChange: (State) -> Unit,
    onIdChange: (Int) -> Unit,
    stateCurrent: State? = null
) {

    val memberLoggedRole by vmTeam.getRoleOfMemberLoggedByTeamId(item.idTeam)
        .collectAsState(initial = Role.MEMBER)
    var expanded by remember { mutableStateOf(false) }
    val items = listOfState()
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
                    .background(item.state.getState())
            )
            Text(
                text = item.state.getStateString(),
                style = MaterialTheme.typography.bodySmall,
                fontSize = 10.sp,
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 8.dp)
            )
            if (memberLoggedRole == Role.ADMIN ||
                item.members.contains(memberLogged.id)
            )
                Icon(
                    icon, "contentDescription",
                    tint = Color.White
                )
        }
        if (memberLoggedRole == Role.ADMIN ||
            item.members.contains(memberLogged.id)
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
                            vmTask.currentTask.value = item
                            if (State.valueOf(items[selectedIndex]) == State.COMPLETED) {
                                popUpRender.value = true
                            } else {
                                if (stateCurrent == null) {
                                    vmTask.updateStateById(
                                        item.id,
                                        State.valueOf(items[selectedIndex])
                                    )
                                    onStateChange(State.valueOf(items[selectedIndex]))
                                    onIdChange(item.id.toInt())
                                } else {
                                    //vmTask.updateStateById(item.id, State.valueOf(items[selectedIndex]))
                                    onStateChange(State.valueOf(items[selectedIndex]))
                                    onIdChange(item.id.toInt())
                                }
                            }
                        },
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
            RenderPopUpReview(popUpRender, returnedPopUpValue, review)
            LaunchedEffect(returnedPopUpValue) {
                returnedPopUpValue.collect { value ->
                    if (value == true) {
                        val task = item.copy(
                            state = State.COMPLETED,
                            mapReview = (item.mapReview + Pair(
                                memberLogged.id,
                                review.floatValue
                            )).toMutableMap(),
                            histories = (item.histories + mutableListOf(
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
                                        format(
                                            "%.2f",
                                            review.floatValue
                                        )
                                    )
                                )
                            )).toMutableList()
                        )
                        task.id = item.id
                        vmTask.currentTask.value = task
                        vmTask.updateTask(id = item.id)
                        onStateChange(State.COMPLETED)
                        onIdChange(item.id.toInt())
                    } else if (value == false) {
                        vmTask.updateStateById(item.id, State.COMPLETED)
                        onStateChange(State.COMPLETED)
                        onIdChange(item.id.toInt())
                    }
                }
            }
        }
    }
}


@Composable
fun NoTasksFound(
    state: State? = null
) {
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
                text = if (state == null) "There are no tasks assigned to you" else "There are no ${
                    state.getStateString().lowercase()
                } tasks assigned to you",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}