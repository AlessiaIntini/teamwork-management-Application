package it.polito.teamhub.ui.view.calendarView

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.rounded.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import io.github.boguszpawlowski.composecalendar.CalendarState
import io.github.boguszpawlowski.composecalendar.SelectableCalendar
import io.github.boguszpawlowski.composecalendar.day.DayState
import io.github.boguszpawlowski.composecalendar.header.MonthState
import io.github.boguszpawlowski.composecalendar.rememberSelectableCalendarState
import io.github.boguszpawlowski.composecalendar.selection.DynamicSelectionState
import it.polito.teamhub.R
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.dataClass.task.State
import it.polito.teamhub.dataClass.task.Task
import it.polito.teamhub.ui.theme.linearGradient
import it.polito.teamhub.ui.view.component.topBar.TopBar
import it.polito.teamhub.ui.view.component.topBar.TopBarValue
import it.polito.teamhub.ui.view.taskView.personalTasks.CardRenderTeam
import it.polito.teamhub.viewmodel.TaskViewModel
import it.polito.teamhub.viewmodel.TeamViewModel
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.TextStyle.SHORT
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CalendarPane(
    navController: NavController,
    vmTask: TaskViewModel,
    vmTeam: TeamViewModel,
    memberList: List<Member>,
    memberLogged: Member
) {
    Scaffold(
        topBar = {
            val topBarParameter = TopBarValue(
                title = "Calendar",
                vmTask = vmTask
            )
            TopBar(navController, topBarParameter, memberLogged)
        },
    ) { innerPadding ->

        BoxWithConstraints(modifier = Modifier.padding(innerPadding)) {
            if (this.maxHeight > this.maxWidth) {
                ColumnLayout(
                    navController,
                    vmTask,
                    vmTeam,
                    memberList,
                    memberLogged
                )
            } else {
                RowLayout(
                    navController,
                    vmTask,
                    vmTeam,
                    memberList,
                    memberLogged
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ColumnLayout(
    navController: NavController,
    vmTask: TaskViewModel,
    vmTeam: TeamViewModel,
    memberList: List<Member>,
    memberLogged: Member
) {
    val teamList by vmTeam.teamList.collectAsState()
    //if (teamList.isEmpty()) return

    val taskList by vmTask.getMemberLoggedNotCompletedTasks().collectAsState(initial = listOf())
    val taskListFiltered = taskList.filter { task ->
        teamList.any { team ->
            team.members.any { member ->
                member.idMember == memberLogged.id && task.idTeam == team.id && member.isMember
            }
        }
    }

    val calendarState = rememberSelectableCalendarState(
        initialSelection = setOf(LocalDate.now()).toList()
    )

    // Calculate the number of tasks expiring on each date
    val tasksExpiringOnDate = taskListFiltered.groupBy {
        it.dueDate.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }.mapValues { (_, tasks) ->
        // count just tasks not completed
        tasks.count { it.state != State.COMPLETED }
    }
    Column(
        modifier = Modifier
            .padding(top = 10.dp, bottom = 16.dp)
    ) {
        ElevatedCard(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .background(MaterialTheme.colorScheme.background)
                .shadow(2.dp, shape = RoundedCornerShape(8.dp)),
            elevation = CardDefaults.elevatedCardElevation(0.dp)
        ) {
            SelectableCalendar(
                calendarState = calendarState,
                monthHeader = { monthState -> CustomMonthHeader(monthState) },
                weekHeader = { weekState -> CustomWeekHeader(weekState) },
                dayContent = { dayState ->
                    CustomDayContent(
                        state = dayState,
                        numExpiringTasks = tasksExpiringOnDate[dayState.date]
                    )
                },
                modifier = Modifier.padding(
                    top = 6.dp,
                    bottom = 16.dp,
                    start = 16.dp,
                    end = 16.dp
                )
            )
        }
        ExpiringTasks(
            navController,
            calendarState,
            vmTask,
            vmTeam,
            taskListFiltered,
            isColumnLayout = true,
            memberList = memberList,
            memberLogged = memberLogged
        )
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RowLayout(
    navController: NavController,
    vmTask: TaskViewModel,
    vmTeam: TeamViewModel,
    memberList: List<Member>,
    memberLogged: Member
) {
    val teamList by vmTeam.teamList.collectAsState()
    //if (teamList.isEmpty()) return

    val taskList by vmTask.getMemberLoggedNotCompletedTasks().collectAsState(initial = listOf())
    val taskListFiltered = taskList.filter { task ->
        teamList.any { team ->
            team.members.any { member ->
                member.idMember == memberLogged.id && task.idTeam == team.id && member.isMember
            }
        }
    }

    val calendarState = rememberSelectableCalendarState(
        initialSelection = setOf(LocalDate.now()).toList()
    )

    // Calculate the number of tasks expiring on each date
    val tasksExpiringOnDate = taskListFiltered.groupBy {
        it.dueDate.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
    }.mapValues { (_, tasks) ->
        // count just tasks not completed
        tasks.count { it.state != State.COMPLETED }
    }
    val scrollState = rememberScrollState()
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 20.dp, start = 16.dp, end = 16.dp)
                .verticalScroll(scrollState),
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                ElevatedCard(
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .background(MaterialTheme.colorScheme.background)
                        .shadow(2.dp, shape = RoundedCornerShape(8.dp)),
                    elevation = CardDefaults.elevatedCardElevation(0.dp)
                ) {
                    SelectableCalendar(
                        calendarState = calendarState,
                        monthHeader = { monthState -> CustomMonthHeader(monthState) },
                        weekHeader = { weekState -> CustomWeekHeader(weekState) },
                        dayContent = { dayState ->
                            CustomDayContent(
                                state = dayState,
                                numExpiringTasks = tasksExpiringOnDate[dayState.date]
                            )
                        },
                        modifier = Modifier.padding(
                            top = 6.dp,
                            bottom = 16.dp,
                            start = 16.dp,
                            end = 16.dp
                        )
                    )
                }
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                ExpiringTasks(
                    navController,
                    calendarState,
                    vmTask,
                    vmTeam,
                    taskListFiltered,
                    isColumnLayout = false,
                    memberList = memberList,
                    memberLogged = memberLogged,
                    isRowLayout = true
                )
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ExpiringTasks(
    navController: NavController,
    calendarState: CalendarState<DynamicSelectionState>,
    vmTask: TaskViewModel,
    vmTeam: TeamViewModel,
    taskListFiltered: List<Task>,
    isColumnLayout: Boolean,
    memberList: List<Member>,
    memberLogged: Member,
    isRowLayout: Boolean = false
) {
    Column(
        modifier = Modifier
            .padding(start = 16.dp, end = 16.dp, top = 16.dp)
    ) {
        Text(
            text = "Deadlines",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold,
        )
    }
    if (taskListFiltered.isNotEmpty())
        SelectionControls(
            navController = navController,
            selectionState = calendarState.selectionState,
            vmTask = vmTask,
            vmTeam = vmTeam,
            taskListFiltered = taskListFiltered,
            isColumnLayout = isColumnLayout,
            memberList = memberList,
            memberLogged = memberLogged
        )
    else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = if (isRowLayout) 40.dp else 0.dp),
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
                    text = "There are no tasks expiring in the selected date",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }

    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
private fun SelectionControls(
    navController: NavController,
    selectionState: DynamicSelectionState,
    vmTask: TaskViewModel,
    vmTeam: TeamViewModel,
    taskListFiltered: List<Task>,
    isColumnLayout: Boolean,
    memberList: List<Member>,
    memberLogged: Member
) {
    // Filter the tasks that expire on the selected date
    val tasksExpiringOnSelectedDate = taskListFiltered.filter { task ->
        val taskDueDate = task.dueDate.toInstant()
            .atZone(ZoneId.systemDefault())
            .toLocalDate()
        taskDueDate in selectionState.selection
    }

    if (isColumnLayout) {
        // Show the tasks that expire on the selected date
        if (tasksExpiringOnSelectedDate.isNotEmpty()) {
            LazyColumn {
                items(tasksExpiringOnSelectedDate) { task ->
                    CardRenderTeam(
                        navController = navController,
                        vmTask = vmTask,
                        vmTeam = vmTeam,
                        item = task,
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
                        text = "There are no tasks expiring in the selected date",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    } else {
        if (tasksExpiringOnSelectedDate.isNotEmpty()) {
            tasksExpiringOnSelectedDate.forEach { task ->
                CardRenderTeam(
                    navController = navController,
                    vmTask = vmTask,
                    vmTeam = vmTeam,
                    item = task,
                    memberList = memberList,
                    memberLogged = memberLogged
                )
            }
        } else {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 40.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier.fillMaxSize()
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
                        text = "There are no tasks expiring in the selected date",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CustomMonthHeader(monthState: MonthState) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(onClick = { monthState.currentMonth = monthState.currentMonth.minusMonths(1) }) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowLeft,
                contentDescription = "Previous month",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
        Text(
            text = monthState.currentMonth.month.name.lowercase()
                .replaceFirstChar { c -> c.uppercase() } + " " + monthState.currentMonth.year,
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.SemiBold
        )
        IconButton(onClick = { monthState.currentMonth = monthState.currentMonth.plusMonths(1) }) {
            Icon(
                imageVector = Icons.AutoMirrored.Rounded.KeyboardArrowRight,
                contentDescription = "Previous month",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CustomWeekHeader(weekState: List<DayOfWeek>) {
    Row {
        weekState.forEach { dayOfWeek ->
            Text(
                text = dayOfWeek.getDisplayName(SHORT, Locale.ROOT),
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentHeight()
            )
        }
    }
}


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun CustomDayContent(
    state: DayState<DynamicSelectionState>,
    numExpiringTasks: Int?
) {
    val date = state.date
    val selectionState = state.selectionState
    val isSelected = selectionState.isDateSelected(date)

    Card(
        modifier = Modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clickable {
                selectionState.onDateSelected(date)
            },
        border = if (isSelected) BorderStroke(
            1.5.dp,
            MaterialTheme.colorScheme.primary
        ) else null,
        colors = CardDefaults.cardColors(
            contentColor = if (state.isCurrentDay) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
            containerColor = MaterialTheme.colorScheme.surface,
        ),
        elevation = if (state.isFromCurrentMonth) CardDefaults.cardElevation(6.dp) else CardDefaults.cardElevation(
            0.dp
        ),
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = date.dayOfMonth.toString(),
                fontWeight = if (state.isCurrentDay) FontWeight.SemiBold else null
            )
            if (numExpiringTasks != null && numExpiringTasks > 0) {
                Box(
                    modifier = Modifier
                        .size(10.dp)
                        .clip(CircleShape)
                        .background(linearGradient)
                )
            }
        }
    }
}