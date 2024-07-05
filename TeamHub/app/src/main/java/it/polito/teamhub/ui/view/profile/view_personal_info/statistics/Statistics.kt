package it.polito.teamhub.ui.view.profile.view_personal_info.statistics

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.ColorUtils
import androidx.navigation.NavController
import com.github.mikephil.charting.animation.Easing
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
import it.polito.teamhub.dataClass.team.Team
import it.polito.teamhub.ui.theme.DarkPurple
import it.polito.teamhub.ui.theme.Gray4
import it.polito.teamhub.ui.theme.Green
import it.polito.teamhub.ui.theme.LightBlue
import it.polito.teamhub.ui.theme.LightPurple
import it.polito.teamhub.ui.theme.Orange
import it.polito.teamhub.ui.theme.PurpleBlue
import it.polito.teamhub.ui.theme.RedOrange
import it.polito.teamhub.ui.theme.RoyalBlue
import it.polito.teamhub.ui.theme.radialGradient
import it.polito.teamhub.ui.view.component.statistics.CustomLegend
import it.polito.teamhub.ui.view.component.statistics.CustomMarkerView
import it.polito.teamhub.ui.view.component.topBar.TopBar
import it.polito.teamhub.ui.view.component.topBar.TopBarValue
import it.polito.teamhub.ui.view.teamView.teamDetails.teamStatistics.EmptyData
import it.polito.teamhub.viewmodel.CategoryViewModel
import it.polito.teamhub.viewmodel.TagViewModel
import it.polito.teamhub.viewmodel.TaskViewModel
import it.polito.teamhub.viewmodel.TeamViewModel
import java.text.SimpleDateFormat
import java.util.Locale


@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ShowStatistics(
    navController: NavController,
    memberId: Long,
    vmTask: TaskViewModel,
    vmTeam: TeamViewModel,
    vmCategory: CategoryViewModel,
    vmTag: TagViewModel,
    memberLogged: Member
) {
    val scrollState = rememberScrollState()
    var isColumnLayout: Boolean
    Scaffold(
        topBar = {
            val topBarParameter = TopBarValue(
                title = "Personal Statistics",
                backArrow = true,
                vmTask = vmTask
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
                    Statistics(isColumnLayout, memberId, vmTask, vmTeam, vmCategory, vmTag)
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
                    Statistics(isColumnLayout, memberId, vmTask, vmTeam, vmCategory, vmTag)
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun Statistics(
    isColumnLayout: Boolean,
    memberId: Long,
    vmTask: TaskViewModel,
    vmTeam: TeamViewModel,
    vmCategory: CategoryViewModel,
    vmTag: TagViewModel
) {
    val teamList by vmTeam.teamList.collectAsState()
    val taskList by vmTask.getTasks().collectAsState(initial = listOf())
    val tagList by vmTag.getTags().collectAsState(initial = listOf())
    val categoryList by vmCategory.getCategories().collectAsState(initial = listOf())
    val statisticsData = getStatisticsData(teamList, taskList, tagList, categoryList, memberId)
    val tabTitles = listOf("Total", "This Month")
    var selectedTabIndex by remember { mutableIntStateOf(0) }
    val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

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
                            onClick = { selectedTabIndex = index },
                        )
                    }
                }
                when (selectedTabIndex) {
                    0 -> {
                        if (statisticsData.personalTaskList.isNotEmpty()) {
                            TotalStatistics(statisticsData, format)
                        } else {
                            EmptyData()
                        }
                    }

                    1 -> {
                        if (statisticsData.thisMonthTaskList.isNotEmpty()) {
                            ThisMonthStatistics(statisticsData, format)
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
fun TotalStatistics(
    statisticsData: StatisticsData,
    format: SimpleDateFormat
) {
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
        StatsTasksState(statisticsData.totStatesValues)
    }

    HorizontalDivider(
        modifier = Modifier.padding(5.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.onSurface
    )

    Row(
        modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp)
    ) {
        Text(
            text = "Completed tasks per current teams",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
    if (statisticsData.currentTeams.isNotEmpty()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 10.dp)
        ) {
            CompletedTasksPerTeam(
                statisticsData.currentTeams,
                statisticsData.totTasksPerCurrentTeam,
                statisticsData.totCompletedTasksPerCurrentTeam
            )
        }
    } else {
        Row(
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                text = "The user is not currently a member of any group",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    if (statisticsData.pastTeams.isNotEmpty()) {
        HorizontalDivider(
            modifier = Modifier.padding(5.dp),
            thickness = 0.5.dp,
            color = MaterialTheme.colorScheme.onSurface
        )

        Row(
            modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp)
        ) {
            Text(
                text = "Completed tasks per past teams",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Row(
            modifier = Modifier.padding(top = 10.dp, start = 16.dp, end = 16.dp)
        ) {
            Text(
                text = "These tasks concern teams of which the user is no longer a member",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 10.dp)
        ) {
            CompletedTasksPerTeam(
                statisticsData.pastTeams,
                statisticsData.totTasksPerPastTeam,
                statisticsData.totCompletedTasksPerPastTeam
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
            text = "Tasks Categories",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
    if (statisticsData.totCategoriesValues.isNotEmpty()) {
        Row(modifier = Modifier.padding(bottom = 10.dp)) {
            StatsTaskCategories(
                statisticsData.totCategoriesValues,
                statisticsData.categories
            )
        }
    } else {
        Row(
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                text = "No categories assigned to tasks",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
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
            text = "Tasks Tags",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
    if (statisticsData.totTagsValues.isNotEmpty()) {
        Row(modifier = Modifier.padding(bottom = 10.dp)) {
            StatsTaskTags(statisticsData.totTagsValues, statisticsData.tags)
        }
    } else {
        Row(
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                text = "No tags assigned to tasks",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
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
    if (statisticsData.mostProductiveDay?.key != null) {
        Text(
            text = format.format(
                statisticsData.mostProductiveDay.key!!
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
            text = if (statisticsData.mostProductiveDay.value > 1) "${statisticsData.mostProductiveDay.value} tasks completed!" else "${statisticsData.mostProductiveDay.value} task completed!",
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
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 6.dp)
            )
        }
    }
}

@Composable
fun ThisMonthStatistics(
    statisticsData: StatisticsData,
    format: SimpleDateFormat
) {
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
        StatsTasksState(statisticsData.thisMonthStatesValues)
    }

    HorizontalDivider(
        modifier = Modifier.padding(5.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.onSurface
    )

    Row(
        modifier = Modifier.padding(top = 10.dp, start = 10.dp, end = 10.dp)
    ) {
        Text(
            text = "Completed tasks per team",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
    if (statisticsData.currentTeams.isNotEmpty()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, bottom = 10.dp)
        ) {
            CompletedTasksPerTeam(
                statisticsData.currentTeams,
                statisticsData.thisMonthTasksPerTeam,
                statisticsData.thisMonthCompletedTasksPerTeam
            )
        }
    } else {
        Row(
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                text = "The user is not currently a member of any group",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
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
            text = "Tasks Categories",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
    if (statisticsData.thisMonthCategoriesValues.isNotEmpty()) {
        Row(modifier = Modifier.padding(bottom = 10.dp)) {
            StatsTaskCategories(
                statisticsData.thisMonthCategoriesValues,
                statisticsData.categories
            )
        }
    } else {
        Row(
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                text = "No categories assigned to tasks",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
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
            text = "Tasks Tags",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
    if (statisticsData.thisMonthTagsValues.isNotEmpty()) {
        Row(modifier = Modifier.padding(bottom = 10.dp)) {
            StatsTaskTags(statisticsData.thisMonthTagsValues, statisticsData.tags)
        }
    } else {
        Row(
            modifier = Modifier.padding(10.dp)
        ) {
            Text(
                text = "No tags assigned to tasks",
                modifier = Modifier.fillMaxWidth(),
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }

    HorizontalDivider(
        modifier = Modifier.padding(5.dp),
        thickness = 0.5.dp,
        color = MaterialTheme.colorScheme.onSurface
    )

    Row(
        modifier = Modifier.padding(10.dp)
    ) {
        Text(
            text = "Most productive day",
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.onBackground
        )
    }
    if (statisticsData.thisMonthMostProductiveDay?.key != null) {
        Text(
            text = format.format(
                statisticsData.thisMonthMostProductiveDay.key!!
            ),
            style = TextStyle(
                brush = radialGradient,
                fontWeight = FontWeight.SemiBold,
                fontSize = 18.sp
            ),
            textAlign = TextAlign.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        )
        Text(
            text = if (statisticsData.thisMonthMostProductiveDay.value > 1) "${statisticsData.thisMonthMostProductiveDay.value} tasks completed!" else "${statisticsData.thisMonthMostProductiveDay.value} task completed!",
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.SemiBold,
            color = PurpleBlue,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 6.dp)
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
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 10.dp, bottom = 6.dp)
            )
        }
    }
}

@Composable
fun StatsTasksState(values: FloatArray) {
    val colorsTotalTasks =
        intArrayOf(Gray4.toArgb(), Green.toArgb(), Orange.toArgb(), RedOrange.toArgb())
    val entries = listOf(
        PieEntry(values[0], "To do"),
        PieEntry(values[1], "Completed on time"),
        PieEntry(values[2], "Completed behind schedule"),
        PieEntry(values[3], "Overdue")
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
                        setColors(colorsTotalTasks, 255)
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
                                    String.format(Locale.US, "%.1f%%", percentage)
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
                    val markerView = CustomMarkerView(context, R.layout.marker_view)
                    marker = markerView
                }
            }
        )
        CustomLegend(colorsTotalTasks.map { Color(it) }, entries.map { it.label })
    }
}

@Composable
fun CompletedTasksPerTeam(teams: List<Team>, values1: FloatArray, values2: FloatArray) {
    val colorCompleted = intArrayOf(Green.toArgb())
    val colorTotal = intArrayOf(Gray4.toArgb())
    val color = MaterialTheme.colorScheme.onBackground
    Column {
        AndroidView(
            modifier = Modifier
                .height((70 * values1.size).dp)
                .fillMaxWidth(),
            factory = { context ->
                HorizontalBarChart(context).apply {
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
                                return if (value == 0f) {
                                    ""
                                } else {
                                    value.toInt().toString()
                                }
                            }
                        }
                    }
                    val barDataSetCompleted = BarDataSet(entriesCompleted, "Completed").apply {
                        setColors(colorCompleted, 255)
                        valueTextColor = color.toArgb()
                        valueTextSize = 15f
                        valueFormatter = object : ValueFormatter() {
                            override fun getFormattedValue(value: Float): String {
                                return if (value == 0f) {
                                    ""
                                } else {
                                    value.toInt().toString()
                                }
                            }
                        }
                    }

                    data = BarData(barDataSetTotal, barDataSetCompleted)
                    description.text = ""
                    data.barWidth =
                        0.6f // The sum of barWidth for all datasets should not exceed 1
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
                            private val labels = teams.map { it.name }.toTypedArray()

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
        CustomLegend(colors = listOf(Gray4, Green), labels = listOf("Total", "Completed"))
    }
}

@Composable
fun StatsTaskCategories(values: FloatArray, categories: List<String>) {
    val initialColor = LightBlue.toArgb()
    val finalColor = RoyalBlue.toArgb()

    val colorsTotalTasks = List(categories.size) { index ->
        if (categories.size == 1) {
            initialColor
        } else {
            val ratio =
                index.toFloat() / (categories.size - 1) // Calculate the ratio based on the index
            ColorUtils.blendARGB(
                initialColor,
                finalColor,
                ratio
            ) // Blend the initial and final colors based on the ratio
        }
    }.toIntArray() // Convert List<Color> to IntArray
    Column {
        AndroidView(
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth(),
            factory = { context ->
                PieChart(context).apply {
                    val total = values.sum()
                    val entries = values.mapIndexed { index, value ->
                        PieEntry(value, categories[index])
                    }
                    val pieDataSet = PieDataSet(entries, "").apply {
                        setColors(colorsTotalTasks, 255)
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
                                    String.format(Locale.US, "%.1f%%", percentage)
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
                    val markerView = CustomMarkerView(context, R.layout.marker_view)
                    marker = markerView
                }
            }
        )
        CustomLegend(colorsTotalTasks.map { Color(it) }, categories)
    }
}

@Composable
fun StatsTaskTags(values: FloatArray, tags: List<String>) {
    val initialColor = LightPurple.toArgb()
    val finalColor = DarkPurple.toArgb()

    val colorsTotalTasks = List(tags.size) { index ->
        if (tags.size == 1) {
            initialColor
        } else {
            val ratio = index.toFloat() / (tags.size - 1) // Calculate the ratio based on the index
            ColorUtils.blendARGB(
                initialColor,
                finalColor,
                ratio
            ) // Blend the initial and final colors based on the ratio
        }
    }.toIntArray() // Convert List<Color> to IntArray
    Column {
        AndroidView(
            modifier = Modifier
                .height(200.dp)
                .fillMaxWidth(),
            factory = { context ->
                PieChart(context).apply {
                    val total = values.sum()
                    val entries = values.mapIndexed { index, value ->
                        PieEntry(value, tags[index])
                    }
                    val pieDataSet = PieDataSet(entries, "").apply {
                        setColors(colorsTotalTasks, 255)
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
                                    String.format(Locale.US, "%.1f%%", percentage)
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
                    val markerView = CustomMarkerView(context, R.layout.marker_view)
                    marker = markerView
                }
            }
        )
        CustomLegend(colorsTotalTasks.map { Color(it) }, tags)
    }
}



