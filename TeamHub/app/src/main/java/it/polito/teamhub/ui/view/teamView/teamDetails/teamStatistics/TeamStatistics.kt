package it.polito.teamhub.ui.view.teamView.teamDetails.teamStatistics

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.capitalize
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.intl.Locale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.navigation.NavController
import com.github.mikephil.charting.animation.Easing
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.HorizontalBarChart
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.ValueFormatter
import it.polito.teamhub.R
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.dataClass.task.State
import it.polito.teamhub.dataClass.team.Team
import it.polito.teamhub.ui.theme.Gray2
import it.polito.teamhub.ui.theme.Gray4
import it.polito.teamhub.ui.theme.Green
import it.polito.teamhub.ui.theme.LightPurple
import it.polito.teamhub.ui.theme.Orange
import it.polito.teamhub.ui.theme.PurpleBlue
import it.polito.teamhub.ui.theme.RedOrange
import it.polito.teamhub.ui.theme.linearGradient
import it.polito.teamhub.ui.theme.radialGradient
import it.polito.teamhub.ui.view.component.profileIcon.RenderProfile
import it.polito.teamhub.ui.view.component.profileIcon.TypeProfileIcon
import it.polito.teamhub.ui.view.component.review.RenderRate
import it.polito.teamhub.ui.view.component.statistics.CustomLegend
import it.polito.teamhub.ui.view.component.statistics.CustomMarkerView
import it.polito.teamhub.ui.view.component.topBar.TopBar
import it.polito.teamhub.ui.view.component.topBar.TopBarValue
import it.polito.teamhub.ui.view.profile.view_personal_info.statistics.StatsTasksState
import it.polito.teamhub.viewmodel.TaskViewModel
import it.polito.teamhub.viewmodel.TeamViewModel
import java.text.SimpleDateFormat
import java.time.LocalDate

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ShowTeamStatistics(
    navController: NavController,
    teamId: Long,
    vmTeam: TeamViewModel,
    vmTask: TaskViewModel,
    memberList: List<Member>,
    memberLogged: Member
) {
    val scrollState = rememberScrollState()
    var isColumnLayout: Boolean
    Scaffold(
        topBar = {
            val topBarParameter = TopBarValue(
                title = "Team Statistics",
                backArrow = true,
                vmTeam = vmTeam,
            )
            TopBar(navController, topBarParameter, memberLogged)
        },
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier.padding(innerPadding)
        ) {
            if (this.maxHeight > this.maxWidth) { //Posizione verticale
                //column
                isColumnLayout = true
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                        .verticalScroll(scrollState),
                ) {
                    TeamStatistics(
                        navController,
                        isColumnLayout,
                        teamId,
                        vmTeam,
                        vmTask,
                        memberList
                    )
                }
            } else {
                //row
                isColumnLayout = false
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                        .verticalScroll(scrollState),
                ) {
                    TeamStatistics(
                        navController,
                        isColumnLayout,
                        teamId,
                        vmTeam,
                        vmTask,
                        memberList
                    )
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun TeamStatistics(
    navController: NavController,
    isColumnLayout: Boolean,
    teamId: Long,
    vmTeam: TeamViewModel,
    vmTask: TaskViewModel,
    memberList: List<Member>
) {
    val teamList by vmTeam.teamList.collectAsState()
    val taskList by vmTask.getTasks().collectAsState(initial = listOf())
    if (teamList.isEmpty() || memberList.isEmpty()) return
    val teamStatisticsData = getTeamStatisticsData(teamList, taskList, memberList, teamId)
    val tabTitles = listOf("Tasks", "Members")
    var selectedTabIndex by remember { mutableIntStateOf(0) }

    Column(modifier = Modifier.fillMaxWidth()) {
        ElevatedCard(
            modifier = Modifier
                .padding(top = 16.dp, bottom = 16.dp)
                .fillMaxWidth(if (isColumnLayout) 1f else 0.8f)
                .align(Alignment.CenterHorizontally)
        ) {
            Column {
                TabRow(selectedTabIndex = selectedTabIndex) {
                    tabTitles.forEachIndexed { index, title ->
                        Tab(
                            text = { Text(title) },
                            selected = selectedTabIndex == index,
                            onClick = { selectedTabIndex = index }
                        )
                    }
                }
                when (selectedTabIndex) {
                    0 -> {
                        if (taskList.any { it.idTeam == teamId }) {
                            TasksStatistics(teamStatisticsData)
                        } else {
                            EmptyData()
                        }
                    }

                    1 -> {
                        if (taskList.any { it.idTeam == teamId }) {
                            MembersStatistics(
                                navController,
                                teamStatisticsData,
                                teamList,
                                teamId,
                                memberList
                            )
                        } else {
                            EmptyData()
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TasksStatistics(
    teamStatisticsData: TeamStatisticsData,
) {
    val format = SimpleDateFormat("dd/MM/yyyy", java.util.Locale.getDefault())

    Row(
        modifier = Modifier.padding(top = 16.dp),
    ) {
        Text(
            text = "Tasks State",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
    Row(modifier = Modifier.padding(bottom = 10.dp)) {
        StatsTasksState(teamStatisticsData.totStatesValues)
    }

    HorizontalDivider(
        modifier = Modifier.padding(5.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.onSurface
    )

    Row(
        modifier = Modifier.padding(10.dp),
    ) {
        Text(
            text = "Tasks expiring this week",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
    if (teamStatisticsData.thisWeekTasks.isNotEmpty()) {
        Row(modifier = Modifier.padding(bottom = 10.dp)) {
            ExpiringThisWeekTasks(
                teamStatisticsData.thisWeekTasksWithZeros.values.map { it.toFloat() }
                    .toFloatArray(),
                teamStatisticsData.thisWeekCompletedTasksWithZeros.values.map { it.toFloat() }
                    .toFloatArray(),
                teamStatisticsData.thisWeekDays
            )
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "There are no tasks expiring this week",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
        }
    }

    HorizontalDivider(
        modifier = Modifier.padding(5.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.onSurface
    )

    Row(
        modifier = Modifier.padding(10.dp),
    ) {
        Text(
            text = "Average tasks rate",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
    if (teamStatisticsData.teamTaskList.filter { it.state == State.COMPLETED }
            .all { it.mapReview.isEmpty() }) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No tasks have been rated yet",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            RenderRate(
                rating = teamStatisticsData.avgTasksRate.toFloat(),
                size = 30.dp,
                activeColor = PurpleBlue,
                inactiveColor = Color.White,
                brush = linearGradient
            )
        }
    }

    HorizontalDivider(
        modifier = Modifier.padding(5.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.onSurface
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Text(
            text = "Most productive day",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
    if (teamStatisticsData.mostProductiveDay != null) {
        Text(
            text = format.format(
                teamStatisticsData.mostProductiveDay.key
            ),
            textAlign = TextAlign.Center,
            style = TextStyle(
                brush = radialGradient,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        )
        Text(
            text = if (teamStatisticsData.mostProductiveDay.value > 1) "${teamStatisticsData.mostProductiveDay.value} tasks completed!" else "${teamStatisticsData.mostProductiveDay.value} task completed!",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            color = PurpleBlue,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, top = 6.dp)
        )
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No completed tasks",
                style = TextStyle(
                    brush = radialGradient,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 6.dp)
            )
        }
    }
}

@Composable
fun MembersStatistics(
    navController: NavController,
    teamStatisticsData: TeamStatisticsData,
    teamList: List<Team>,
    teamId: Long,
    memberList: List<Member>
) {
    /*val memberList by vmMember.getMembers().collectAsState(
        emptyList()
    )*/
    if (memberList.isEmpty()) return
    Row(
        modifier = Modifier.padding(top = 16.dp),
    ) {
        Text(
            text = "Current Members Assignments",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
    if (teamStatisticsData.teamTaskList.any { it.members.isNotEmpty() }) {
        Row(modifier = Modifier.padding(bottom = 10.dp)) {
            StatsMembersAssignments(
                teamStatisticsData.currentMembersCompletedOnTimeTasksWithZeros.values.map { it.toFloat() }
                    .toFloatArray(),
                teamStatisticsData.currentMembersCompletedBehindScheduleTasksWithZeros.values.map { it.toFloat() }
                    .toFloatArray(),
                teamStatisticsData.currentMembersOverdueTasksWithZeros.values.map { it.toFloat() }
                    .toFloatArray(),
                teamStatisticsData.currentMembersToDoTasksWithZeros.values.map { it.toFloat() }
                    .toFloatArray(),
                teamStatisticsData.currentMembers
            )
        }
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No tasks have been assigned to the team members",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp)
            )
        }
    }

    if (teamStatisticsData.pastMembers.isNotEmpty()) {
        HorizontalDivider(
            modifier = Modifier.padding(5.dp),
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Row(
            modifier = Modifier.padding(10.dp),
        ) {
            Text(
                text = "Past Members Assignments",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Row(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp),
        ) {
            Text(
                text = "These tasks concern users who are no longer part of the team",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
        Row(modifier = Modifier.padding(bottom = 10.dp)) {
            StatsMembersAssignments(
                teamStatisticsData.pastMembersCompletedOnTimeTasksWithZeros.values.map { it.toFloat() }
                    .toFloatArray(),
                teamStatisticsData.pastMembersCompletedBehindScheduleTasksWithZeros.values.map { it.toFloat() }
                    .toFloatArray(),
                teamStatisticsData.pastMembersOverdueTasksWithZeros.values.map { it.toFloat() }
                    .toFloatArray(),
                teamStatisticsData.pastMembersToDoTasksWithZeros.values.map { it.toFloat() }
                    .toFloatArray(),
                teamStatisticsData.pastMembers
            )
        }
    }

    HorizontalDivider(
        modifier = Modifier.padding(5.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.onSurface
    )

    Row(
        modifier = Modifier.padding(10.dp),
    ) {
        Text(
            text = "Members time participation",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
    Row(modifier = Modifier.padding(bottom = 10.dp)) {
        StatsParticipationTime(teamStatisticsData.totParticipationTimeValues)
    }

    HorizontalDivider(
        modifier = Modifier.padding(5.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.onSurface
    )

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp),
    ) {
        Text(
            text = if (teamStatisticsData.mostProductiveMembers.size > 1) "Most productive members" else "Most productive member",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
    if (teamStatisticsData.mostProductiveMembers.isNotEmpty()) {
        val mostProductiveMembers =
            teamStatisticsData.mostProductiveMembers.map {
                /* vmMember.getMemberById(it.key)*/
                memberList.find { member -> member.id == it.key }!!
            }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            mostProductiveMembers.forEachIndexed { index, member ->
                val teamMember =
                    teamList.find { it.id == teamId }?.members?.find { it.idMember == member.id }
                Box(
                    modifier = if (teamMember?.isMember == true)
                        Modifier
                            .clip(CircleShape)
                            .clickable {
                                navController.navigate(
                                    "profile/personalInfo/${member.id}"
                                )
                            }
                    else Modifier
                        .clip(CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    RenderProfile(
                        imageProfile = if (teamMember?.isMember == true) member.userImage else "",
                        initialsName = if (teamMember?.isMember == true) member.initialsName else "-",
                        backgroundColor = PurpleBlue,
                        backgroundBrush = if (teamMember?.isMember == true) member.colorBrush
                        else Brush.linearGradient(
                            colors = listOf(Gray2, Gray4),
                        ),
                        sizeTextPerson = 20.sp,
                        sizeImage = 50.dp,
                        typeProfile = TypeProfileIcon.PERSON,
                    )
                }

                if (index < mostProductiveMembers.size - 1) {
                    VerticalDivider(
                        modifier = Modifier.padding(3.dp),
                        thickness = 0.5.dp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
        val maxCompletedTasks =
            teamStatisticsData.mostProductiveMembers.values.maxOrNull()
        Text(
            text = if (maxCompletedTasks == 1) "$maxCompletedTasks task completed!" else "$maxCompletedTasks tasks completed!",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            color = PurpleBlue,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp, top = 6.dp)
        )
    } else {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 10.dp),
            horizontalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No completed tasks",
                style = TextStyle(
                    brush = radialGradient,
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 18.sp
                ),
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 6.dp)
            )
        }
    }
}

@Composable
fun EmptyData() {
    val configuration = LocalConfiguration.current
    Column(
        modifier = Modifier
            .height((configuration.screenHeightDp * 0.83).dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.db_warning),
                contentDescription = "Empty db image",
                modifier = Modifier
                    .size(100.dp)
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
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 16.dp),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "No Data Available",
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "There is no data to show you right now.",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
            )
        }
    }
}

@Composable
fun StatsMembersAssignments(
    values1: FloatArray,
    values2: FloatArray,
    values3: FloatArray,
    values4: FloatArray,
    members: List<Member>
) {
    val colorCompletedOnTime = Green.toArgb()
    val colorCompletedBehindSchedule = Orange.toArgb()
    val colorOverdue = RedOrange.toArgb()
    val colorToDo = Gray4.toArgb()
    val color = MaterialTheme.colorScheme.onBackground
    Column {
        AndroidView(
            modifier = Modifier
                .height((50 * values1.size).dp)
                .fillMaxWidth(),
            factory = { context ->
                HorizontalBarChart(context).apply {
                    val entries = values1.mapIndexed { index, value ->
                        BarEntry(
                            (index + 1).toFloat(),
                            floatArrayOf(value, values2[index], values3[index], values4[index])
                        )
                    }
                    val barDataSet = BarDataSet(entries, "").apply {
                        setColors(
                            intArrayOf(
                                colorCompletedOnTime,
                                colorCompletedBehindSchedule,
                                colorOverdue,
                                colorToDo
                            ),
                            255
                        )
                        valueTextColor = color.toArgb()
                        valueTextSize = 15f
                        valueFormatter = object : ValueFormatter() {
                            override fun getFormattedValue(value: Float): String {
                                if (value == 0f) return ""
                                return value.toInt().toString()
                            }
                        }
                    }
                    data = BarData(barDataSet)
                    description.text = ""
                    data.barWidth = 0.6f
                    legend.isEnabled = false
                    animateY(500)
                    setTouchEnabled(false)

                    // Customize the X-axis
                    xAxis.apply {
                        position = XAxis.XAxisPosition.BOTTOM
                        setDrawGridLines(false)
                        granularity = 1f // min interval between values
                        textSize = 15f
                        textColor = color.toArgb()
                        valueFormatter = object : ValueFormatter() {
                            private val labels = members.map { it.fullname }.toTypedArray()
                            override fun getFormattedValue(value: Float): String {
                                val index = value.toInt()
                                if (index > 0 && index <= labels.size) {
                                    return labels[index - 1]
                                }
                                // Return a default value or handle this situation
                                return ""
                            }
                        }
                    }
                    axisLeft.apply {
                        setAxisMinimum(0f)
                    }

                    axisRight.isEnabled = false
                    axisLeft.isEnabled = false
                }
            }
        )

        CustomLegend(
            colors = listOf(Green, Orange, RedOrange, Gray4),
            labels = listOf("Completed on time", "Completed behind schedule", "Overdue", "To do")
        )
    }
}

@Composable
fun ExpiringThisWeekTasks(values1: FloatArray, values2: FloatArray, weekDays: List<LocalDate>) {
    val colorCompleted = intArrayOf(Green.toArgb())
    val colorTotal = intArrayOf(Gray4.toArgb())
    val color = MaterialTheme.colorScheme.onBackground
    Column {
        AndroidView(
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth(),
            factory = { context ->
                BarChart(context).apply {
                    val entriesTotal = values1.mapIndexed { index, value ->
                        BarEntry((index + 1).toFloat(), value)
                    }
                    val entriesCompleted = values2.mapIndexed { index, value ->
                        BarEntry((index + 1).toFloat(), value)
                    }
                    val barDataSetTotal = BarDataSet(entriesTotal, "Total").apply {
                        setColors(colorTotal, 255)
                        valueTextColor = color.toArgb()
                        valueTextSize = 15f
                        valueFormatter = object : ValueFormatter() {
                            override fun getFormattedValue(value: Float): String {
                                if (value == 0f) return ""
                                return value.toInt().toString()
                            }
                        }
                    }
                    val barDataSetCompleted = BarDataSet(entriesCompleted, "Completed").apply {
                        setColors(colorCompleted, 255)
                        valueTextColor = color.toArgb()
                        valueTextSize = 15f
                        valueFormatter = object : ValueFormatter() {
                            override fun getFormattedValue(value: Float): String {
                                if (value == 0f) return ""
                                return value.toInt().toString()
                            }
                        }
                    }

                    data = BarData(barDataSetTotal, barDataSetCompleted)
                    description.text = ""
                    data.barWidth = .7f
                    legend.isEnabled = false
                    animateY(500)
                    setTouchEnabled(false)

                    // Personalizza l'asse X
                    xAxis.apply {
                        position = XAxis.XAxisPosition.BOTTOM
                        setDrawGridLines(false)
                        granularity = 1f // min intervallo tra i valori
                        textSize = 15f
                        textColor = color.toArgb()
                        valueFormatter = object : ValueFormatter() {
                            @RequiresApi(Build.VERSION_CODES.O)
                            private val labels =
                                weekDays.map {
                                    if (it.dayOfWeek.name == LocalDate.now().dayOfWeek.name) "Today" else it.dayOfWeek.name.substring(
                                        0,
                                        3
                                    ).lowercase().capitalize(Locale.current)
                                }.toTypedArray()

                            @RequiresApi(Build.VERSION_CODES.O)
                            override fun getFormattedValue(value: Float): String {
                                return labels[value.toInt() - 1]
                            }
                        }
                    }
                    axisLeft.apply {
                        setAxisMinimum(0f)
                    }

                    axisRight.isEnabled = false
                    axisLeft.isEnabled = false
                }
            }
        )
        CustomLegend(
            colors = listOf(Gray4, Green),
            labels = listOf("Total", "Completed")
        )
    }
}

@Composable
fun StatsParticipationTime(values: FloatArray) {
    val colorsParticipationTime =
        intArrayOf(PurpleBlue.toArgb(), LightPurple.toArgb())
    val entries = listOf(
        PieEntry(values[0], "Full-time"),
        PieEntry(values[1], "Part-time"),
    )
    Column {
        AndroidView(
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth(),
            factory = { context ->
                PieChart(context).apply {
                    val total = values.sum()

                    val pieDataSet = PieDataSet(entries, "").apply {
                        setColors(colorsParticipationTime, 255)
                        setHoleColor(Color.Transparent.toArgb())
                        setDrawEntryLabels(false)
                        valueTextColor = Color.White.toArgb()
                        valueTextSize = 10f
                        valueFormatter = object : ValueFormatter() {
                            override fun getFormattedValue(value: Float): String {
                                val percentage = value / total * 100
                                return if (percentage == 0f) {
                                    ""
                                } else {
                                    String.format(java.util.Locale.US, "%.1f%%", percentage)
                                }
                            }
                        }
                    }

                    data = PieData(pieDataSet)
                    description.text = ""
                    animateY(1400, Easing.EaseInOutQuad)

                    // Style the legend
                    legend.apply {
                        isEnabled = false // Disable the legend
                    }

                    // Custom marker view
                    val markerView = CustomMarkerView(context, R.layout.marker_view, member = true)
                    marker = markerView
                }
            }
        )
        CustomLegend(colorsParticipationTime.map { Color(it) }, entries.map { it.label })
    }
}
