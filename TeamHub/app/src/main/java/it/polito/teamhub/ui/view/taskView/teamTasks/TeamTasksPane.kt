package it.polito.teamhub.ui.view.taskView.teamTasks


import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FabPosition
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
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
import it.polito.teamhub.dataClass.condition.Condition
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.dataClass.member.convertMemberFilteredInCondition
import it.polito.teamhub.dataClass.member.memberInOr
import it.polito.teamhub.dataClass.task.Category
import it.polito.teamhub.dataClass.task.State
import it.polito.teamhub.dataClass.task.Tag
import it.polito.teamhub.dataClass.task.categoryInOr
import it.polito.teamhub.dataClass.task.convertCategoryCheckedInCondition
import it.polito.teamhub.dataClass.task.convertPriorityCheckedInCondition
import it.polito.teamhub.dataClass.task.convertStateCheckedInCondition
import it.polito.teamhub.dataClass.task.convertTagCheckedInCondition
import it.polito.teamhub.dataClass.task.listOfPriority
import it.polito.teamhub.dataClass.task.listOfState
import it.polito.teamhub.dataClass.task.priorityInOr
import it.polito.teamhub.dataClass.task.stateInOr
import it.polito.teamhub.dataClass.task.tagInOr
import it.polito.teamhub.dataClass.team.Team
import it.polito.teamhub.ui.theme.linearGradient
import it.polito.teamhub.ui.view.component.FilterPanel
import it.polito.teamhub.ui.view.component.FloatingActionButtonWithoutBottomBar
import it.polito.teamhub.ui.view.component.SearchBar
import it.polito.teamhub.ui.view.component.SearchBarValue
import it.polito.teamhub.ui.view.component.card.CardRender
import it.polito.teamhub.ui.view.component.topBar.TopBar
import it.polito.teamhub.ui.view.component.topBar.TopBarValue
import it.polito.teamhub.utils.convertCreationDateInCondition
import it.polito.teamhub.utils.convertExpirationDateInCondition
import it.polito.teamhub.viewmodel.CategoryViewModel
import it.polito.teamhub.viewmodel.MemberViewModel
import it.polito.teamhub.viewmodel.TagViewModel
import it.polito.teamhub.viewmodel.TaskViewModel
import it.polito.teamhub.viewmodel.TeamViewModel
import kotlinx.coroutines.launch


@Composable
fun TeamTasksPane(
    navController: NavController,
    teamId: Long,
    vmTask: TaskViewModel,
    vmTeam: TeamViewModel,
    vmTag: TagViewModel,
    vmCategory: CategoryViewModel,
    vmMember: MemberViewModel,
    memberList: List<Member>,
    memberLogged: Member
) {
    val team by vmTeam.getTeamById(teamId).collectAsState(initial = Team())
    if (team.name != "") {
        Scaffold(
            topBar = {
                val topBarParameter = TopBarValue(
                    team = true,
                    title = team.name,
                    color = team.color,
                    backArrow = true,
                    iconTeam = true,
                    vmTeam = vmTeam,
                    imageTeam = team.imageTeam,
                    vmTask = vmTask,
                )
                TopBar(navController, topBarParameter, memberLogged)
            },
            floatingActionButtonPosition = FabPosition.End,
            floatingActionButton = { FloatingActionButtonWithoutBottomBar(navController, teamId) },
        ) { innerPadding ->

            BoxWithConstraints {
                if (this.maxHeight > this.maxWidth) {
                    Column(
                        verticalArrangement = Arrangement.Top,
                        horizontalAlignment = Alignment.Start,
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxWidth()
                    ) {
                        TeamTasksPaneContent(
                            navController,
                            vmTask,
                            vmTeam,
                            vmTag,
                            vmCategory,
                            vmMember,
                            teamId,
                            memberList,
                            memberLogged,
                        )
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .padding(innerPadding)
                            .fillMaxWidth()
                            .align(Alignment.Center),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            modifier = Modifier.fillMaxWidth(.8f),
                        ) {
                            TeamTasksPaneContent(
                                navController,
                                vmTask,
                                vmTeam,
                                vmTag,
                                vmCategory,
                                vmMember,
                                teamId,
                                memberList,
                                memberLogged,
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TeamTasksPaneContent(
    navController: NavController,
    vmTask: TaskViewModel,
    vmTeam: TeamViewModel,
    vmTag: TagViewModel,
    vmCategory: CategoryViewModel,
    vmMember: MemberViewModel,
    teamId: Long,
    memberList: List<Member>,
    memberLogged: Member,
) {
    val filterPanel = remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(filterPanel.value)
    val conditions: MutableState<List<Condition>> =
        rememberSaveable { mutableStateOf(listOf()) }
    val scope = rememberCoroutineScope()
    val changedFilterValue = rememberSaveable { mutableStateOf(false) }

    val valueSorting = listOf("Expiration date", "Creation date", "Priority")

    val categories by vmCategory.getCategoryTeamId(teamId).collectAsState(initial = listOf())
    val tags by vmTag.getTagTeamId(teamId).collectAsState(initial = listOf())

    var checkedValues by remember {
        mutableStateOf<Map<String, List<MutableState<Boolean>>>>(
            emptyMap()
        )
    }
    LaunchedEffect(categories, tags) {
        checkedValues =
            mapOf(
                "Sort by" to List(valueSorting.size) { mutableStateOf(false) },
                "State" to List(listOfState().size) { mutableStateOf(false) },
                "Category" to
                        List(categories.size) {
                            mutableStateOf(
                                false
                            )
                        },
                "Tag" to List(tags.size) { mutableStateOf(false) },
                "Priority" to List(listOfPriority().size) { mutableStateOf(false) }
            )

    }

    val dateValues: Map<String, MutableState<String>> = remember {
        mapOf(
            "Expiration date" to mutableStateOf(""),
            "Creation date" to mutableStateOf("")
        )
    }

    val memberFiltered: MutableList<Long> = remember { mutableListOf() }

    val searchText = remember { mutableStateOf("") }

    val filterTaskCompleted = remember { mutableIntStateOf(0) }
    val applyFilter = remember { mutableStateOf(false) }

    val teamTaskList by vmTask.getTasksByTeamId(teamId).collectAsState(initial = listOf())

    LaunchedEffect(key1 = filterPanel.value) {
        if (filterPanel.value) {
            scope.launch {
                sheetState.show()
            }
        } else {
            scope.launch {
                sheetState.hide()
            }
        }
    }
    val team by vmTeam.getTeamById(teamId).collectAsState(initial = Team())
    if (team.name != "") {
        if (teamTaskList.isNotEmpty()) {
            val memberListOfGroup = team.members.filter { it.isMember }.map { it.idMember }
            val searchBarParameter = SearchBarValue(filter = true, archiveIcon = true)
            SearchBar(
                searchBarParameter = searchBarParameter,
                filterPanel = filterPanel,
                conditions = conditions,
                changedFilterValue = changedFilterValue,
                searchText = searchText,
                vmTask = vmTask,
                vmTeam = vmTeam,
                vmTag = vmTag,
                vmCategory = vmCategory,
                teamId = teamId,
                navController = navController,
                applyFilter = applyFilter,
                checkedValues = checkedValues,
                dateValues = dateValues,
                memberFiltered = memberFiltered,
                memberList = memberList,
                memberLogged = memberLogged
            )
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface
            )
            LazyColumn {
                val sortedTeamTaskList = when {
                    checkedValues.getValue("Sort by")[0].value -> teamTaskList.sortedBy { it.dueDate }
                    checkedValues.getValue("Sort by")[1].value -> teamTaskList.sortedBy { it.creationDate }
                    checkedValues.getValue("Sort by")[2].value -> teamTaskList.sortedBy { it.priority }
                    else -> teamTaskList.sortedBy { it.id }
                }
                itemsIndexed(
                    sortedTeamTaskList
                ) { _, item ->
                    CardRender(
                        navController = navController,
                        vmTask = vmTask,
                        vmTeam = vmTeam,
                        vmCategory = vmCategory,
                        vmTag = vmTag,
                        item = item,
                        applyFilter = applyFilter,
                        conditions = conditions,
                        memberList = memberList,
                        memberLogged = memberLogged,
                    )
                }
            }
            if (filterPanel.value) {
                FilterPanel(
                    vmMember,
                    vmTag,
                    vmCategory,
                    filterPanel,
                    changedFilterValue,
                    checkedValues,
                    dateValues,
                    searchText,
                    memberListOfGroup = memberListOfGroup,
                    memberFiltered,
                    filterTaskCompleted,
                    valueSorting,
                    teamId,
                    sheetState,
                    applyFilter
                )
            }
            if (changedFilterValue.value || dateValues.values.any { it.value != "" }) {
                applyFilter.value = true
                PopulateConditions(
                    conditions,
                    checkedValues,
                    dateValues,
                    memberFiltered,
                    searchText,
                    filterTaskCompleted,
                    remember {
                        mutableStateOf(categories)
                    },
                    remember {
                        mutableStateOf(tags)
                    }
                )
                changedFilterValue.value = false
            }
            if (conditions.value.isEmpty()) {
                conditions.value +=
                    Condition(
                        field = "State",
                        condition = { task -> task.state != State.COMPLETED })

                applyFilter.value = false
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
                        text = "Create a new task to start working",
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodySmall
                    )

                    Spacer(modifier = Modifier.size(16.dp))

                    Image(
                        painter = painterResource(id = R.drawable.south_east_arrow),
                        contentDescription = "arrow",
                        modifier = Modifier
                            .size(50.dp)
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
            }
        }
    }
}

@Composable
fun PopulateConditions(
    conditions: MutableState<List<Condition>>,
    checkedValues: Map<String, List<MutableState<Boolean>>>,
    dateValues: Map<String, MutableState<String>>,
    memberFiltered: MutableList<Long>,
    searchText: MutableState<String>,
    filterTaskCompleted: MutableState<Int>,
    categories: MutableState<List<Category>>,
    tags: MutableState<List<Tag>>
) {
    conditions.value = listOf()
    if (checkedValues.getValue("State")[0].value) {
        filterTaskCompleted.value = 1
    }
    val newConditions = mutableListOf<Condition>()

    convertStateCheckedInCondition(checkedValues.getValue("State")).forEach {
        newConditions.add(it)
    }
    convertCategoryCheckedInCondition(
        checkedValues.getValue("Category"),
        categories.value.toMutableList()
    ).forEach {
        newConditions.add(it)
    }
    convertTagCheckedInCondition(
        checkedValues.getValue("Tag"),
        tags.value.toMutableList()
    ).forEach {
        newConditions.add(it)
    }
    convertPriorityCheckedInCondition(checkedValues.getValue("Priority")).forEach {
        newConditions.add(it)
    }
    convertExpirationDateInCondition(dateValues.getValue("Expiration date"))?.let {
        newConditions.add(it)
    }
    convertCreationDateInCondition(dateValues.getValue("Creation date"))?.let {
        newConditions.add(it)
    }
    convertMemberFilteredInCondition(memberFiltered).forEach {
        newConditions.add(it)
    }

    if (searchText.value != "") {
        newConditions.add(
            Condition(
                field = "search",
                condition = { task ->
                    task.title.contains(searchText.value, ignoreCase = true) ||
                            task.description.contains(
                                searchText.value,
                                ignoreCase = true
                            )
                }
            )
        )
    }

    conditions.value = newConditions
    stateInOr(conditions)?.let { conditions.value += it }
    categoryInOr(conditions)?.let { conditions.value += it }
    tagInOr(conditions)?.let { conditions.value += it }
    priorityInOr(conditions)?.let { conditions.value += it }
    memberInOr(conditions)?.let { conditions.value += it }
}
