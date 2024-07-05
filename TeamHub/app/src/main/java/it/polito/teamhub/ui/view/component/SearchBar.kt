package it.polito.teamhub.ui.view.component

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.polito.teamhub.R
import it.polito.teamhub.dataClass.condition.Condition
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.ui.view.taskView.teamTasks.CompletedTasksPane
import it.polito.teamhub.viewmodel.CategoryViewModel
import it.polito.teamhub.viewmodel.TagViewModel
import it.polito.teamhub.viewmodel.TaskViewModel
import it.polito.teamhub.viewmodel.TeamViewModel

data class SearchBarValue(
    val filter: Boolean,
    val groupIcon: Boolean = false,
    val archiveIcon: Boolean = false,
)

@Composable
fun SearchBar(
    searchBarParameter: SearchBarValue,
    filterPanel: MutableState<Boolean>? = null,
    conditions: MutableState<List<Condition>>? = null,
    changedFilterValue: MutableState<Boolean>? = null,
    searchText: MutableState<String> = remember { mutableStateOf("") },
    vmTask: TaskViewModel?,
    vmTeam: TeamViewModel,
    vmTag: TagViewModel,
    vmCategory: CategoryViewModel,
    teamId: Long,
    navController: NavController,
    applyFilter: MutableState<Boolean>,
    checkedValues: Map<String, List<MutableState<Boolean>>>,
    dateValues: Map<String, MutableState<String>>,
    memberFiltered: MutableList<Long>,
    memberList: List<Member>,
    memberLogged: Member
) {
    var showCompletedTask by remember { mutableStateOf(false) }
    Column(
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.Start,
        modifier = Modifier
            .fillMaxWidth()
        //.padding(vertical = 20.dp, horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier.padding(top = 20.dp, bottom = 10.dp, start = 16.dp, end = 8.dp)
        ) {
            TextField(
                value = searchText.value,
                onValueChange = { newText: String ->
                    searchText.value = newText
                    changedFilterValue?.value = true
                },
                placeholder = {
                    Text(
                        text = "Search",
                        color = if (isSystemInDarkTheme()) Color.White else Color.Gray
                    )
                },
                modifier = Modifier
                    .padding(end = 8.dp)
                    .weight(2f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(50)),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    focusedContainerColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.onSurface,
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon",
                        tint = if (isSystemInDarkTheme()) Color.White else Color.Gray,
                    )
                },
                trailingIcon = {
                    if (searchText.value != "") {
                        IconButton(
                            onClick = { searchText.value = "" },
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Clear Icon",
                                tint = if (isSystemInDarkTheme()) Color.White else Color.Gray,
                            )
                        }
                    }
                }
            )
            if (searchBarParameter.filter && !showCompletedTask) {
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    IconButton(
                        onClick = { filterPanel!!.value = true },
                    ) {
                        Icon(
                            painterResource(R.drawable.filter),
                            contentDescription = "Filter Icon",
                            tint = if (isSystemInDarkTheme()) Color.White else Color.Gray,
                            modifier = Modifier
                                .size(32.dp)
                        )
                    }
                }
            }
        }
        if (searchBarParameter.archiveIcon) {
            Row(
                modifier = Modifier
                    .height(46.dp)
                    .clickable {
                        showCompletedTask = !showCompletedTask
                        searchText.value = ""
                        conditions!!.value = listOf()
                        applyFilter.value = false
                    }
                    .padding(top = 8.dp, start = 16.dp, end = 16.dp, bottom = 10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (!showCompletedTask) {
                    Icon(
                        painterResource(R.drawable.folder),
                        contentDescription = "Folder Icon",
                        modifier = Modifier.padding(start = 4.dp, end = 16.dp),
                        tint = if (isSystemInDarkTheme()) Color.White else Color.Gray,
                    )
                    Text(
                        text = "Completed Tasks",
                        color = if (isSystemInDarkTheme()) Color.White else Color.Gray,
                        modifier = Modifier.weight(1f)
                    )
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "Arrow Right",
                        tint = if (isSystemInDarkTheme()) Color.White else Color.Gray,
                        modifier = Modifier.padding(end = 8.dp),
                    )
                } else {
                    checkedValues.forEach { (_, list) ->
                        list.forEach { it.value = false }
                    }
                    dateValues.forEach { (_, value) ->
                        value.value = ""
                    }
                    //searchText.value = ""
                    memberFiltered.clear()
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                        contentDescription = "Arrow Left",
                        tint = if (isSystemInDarkTheme()) Color.White else Color.Gray,
                        modifier = Modifier.padding(start = 4.dp),
                    )
                    Text(
                        text = "To do Tasks",
                        color = if (isSystemInDarkTheme()) Color.White else Color.Gray,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }
    }

    if (showCompletedTask) {
        CompletedTasksPane(
            vmTask = vmTask!!,
            vmTeam = vmTeam,
            vmTag = vmTag,
            vmCategory = vmCategory,
            searchText = searchText,
            teamId = teamId,
            navController = navController,
            memberList = memberList,
            memberLogged = memberLogged
        )
    }
}