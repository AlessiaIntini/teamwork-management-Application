package it.polito.teamhub.ui.view.component

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import it.polito.teamhub.R
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.dataClass.task.listOfPriorityPrint
import it.polito.teamhub.dataClass.task.listOfStatePrint
import it.polito.teamhub.ui.theme.Gray3
import it.polito.teamhub.ui.theme.gradientList
import it.polito.teamhub.ui.view.component.profileIcon.RenderProfile
import it.polito.teamhub.ui.view.component.profileIcon.TypeProfileIcon
import it.polito.teamhub.viewmodel.CategoryViewModel
import it.polito.teamhub.viewmodel.MemberViewModel
import it.polito.teamhub.viewmodel.TagViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterPanel(
    vmMember: MemberViewModel,
    vmTag: TagViewModel,
    vmCategory: CategoryViewModel,
    filterPanel: MutableState<Boolean>,
    changedFilterValue: MutableState<Boolean>,
    checkedValues: Map<String, List<MutableState<Boolean>>>,
    dateValues: Map<String, MutableState<String>>,
    searchText: MutableState<String>,
    memberListOfGroup: List<Long?>?,
    memberFiltered: MutableList<Long>,
    filterTaskCompleted: MutableState<Int>,
    valueSorting: List<String>,
    teamId: Long,
    sheetState: SheetState,
    applyFilter: MutableState<Boolean>
) {

    val scrollState = rememberScrollState()
    ModalBottomSheet(
        modifier = Modifier
            .fillMaxHeight(0.9f),
        onDismissRequest = {
            filterPanel.value = false
        },
        sheetState = sheetState,
        tonalElevation = 0.dp
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    "Filters",
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.CenterVertically)
                        .padding(top = 8.dp, bottom = 20.dp),
                    style = MaterialTheme.typography.titleMedium,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
            HorizontalDivider(thickness = 0.4.dp, color = MaterialTheme.colorScheme.onSurface)

            ElementFiltering(
                vmMember = null,
                text = "Sort by",
                value = valueSorting,
                typeOfButton = "Radio",
                changedFilterValue = changedFilterValue,
                checkedValues = checkedValues,
                dateValues = dateValues,
                memberFiltered = memberFiltered,
                valueToPrint = "Sort by"
            )
            ElementFiltering(
                vmMember = null,
                text = "State",
                value = listOfStatePrint(),
                typeOfButton = "Checkbox",
                changedFilterValue = changedFilterValue,
                checkedValues = checkedValues,
                dateValues = dateValues,
                memberFiltered = memberFiltered,
                valueToPrint = "State"
            )

            ElementFiltering(
                vmMember = vmMember,
                text = "Members",
                value = memberListOfGroup?.map { it.toString() },
                typeOfButton = "Popup",
                changedFilterValue = changedFilterValue,
                checkedValues = checkedValues,
                dateValues = dateValues,
                memberFiltered = memberFiltered,
                valueToPrint = "Members"
            )
            ElementFiltering(
                vmMember = null,
                text = "Category",
                value = vmCategory.getCategoryTeamId(teamId)
                    .collectAsState(initial = listOf()).value.map { it.name },
                typeOfButton = "Checkbox",
                changedFilterValue = changedFilterValue,
                checkedValues = checkedValues,
                dateValues = dateValues,
                memberFiltered = memberFiltered,
                valueToPrint = "Category"
            )
            ElementFiltering(
                vmMember = null,
                text = "Expiration date",
                value = null,
                typeOfButton = "Calendar",
                changedFilterValue = changedFilterValue,
                checkedValues = checkedValues,
                dateValues = dateValues,
                memberFiltered = memberFiltered,
                valueToPrint = "Expiration date"
            )
            ElementFiltering(
                vmMember = null,
                text = "Creation date",
                value = null,
                typeOfButton = "Calendar",
                changedFilterValue = changedFilterValue,
                checkedValues = checkedValues,
                dateValues = dateValues,
                memberFiltered = memberFiltered,
                valueToPrint = "Creation date"
            )
            ElementFiltering(
                vmMember = null,
                text = "Tag",
                value = vmTag.getTagTeamId(teamId)
                    .collectAsState(initial = listOf()).value.map { it.name },
                typeOfButton = "Checkbox",
                changedFilterValue = changedFilterValue,
                checkedValues = checkedValues,
                dateValues = dateValues,
                memberFiltered = memberFiltered,
                valueToPrint = "Tag"
            )
            ElementFiltering(
                vmMember = null,
                text = "Priority",
                value = listOfPriorityPrint(),
                typeOfButton = "Checkbox",
                changedFilterValue = changedFilterValue,
                checkedValues = checkedValues,
                dateValues = dateValues,
                memberFiltered = memberFiltered,
                valueToPrint = "Priority"
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp, bottom = 20.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = {
                    checkedValues.forEach { (_, list) ->
                        list.forEach { it.value = false }
                    }
                    dateValues.forEach { (_, value) ->
                        value.value = ""
                    }
                    searchText.value = ""
                    memberFiltered.clear()
                    filterPanel.value = false
                    changedFilterValue.value = true
                    filterTaskCompleted.value = 0
                    applyFilter.value = false
                }) {
                    Text(
                        "Reset",
                        textAlign = TextAlign.End,
                    )
                }
            }
        }
    }
}

@Composable
fun ElementFiltering(
    vmMember: MemberViewModel?,
    text: String,
    value: List<String>? = null,
    typeOfButton: String? = null,
    changedFilterValue: MutableState<Boolean>,
    checkedValues: Map<String, List<MutableState<Boolean>>>,
    dateValues: Map<String, MutableState<String>>,
    memberFiltered: MutableList<Long>,
    valueToPrint: String? = null
) {
    val isClicked = remember { mutableStateOf(false) }

    val choosenDate = remember { mutableStateOf(false) }
    val memberPanel = remember { mutableStateOf(true) }
    val memberFilteredChoosen = remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start
        ) {
            Text(
                text,
                modifier = Modifier
                    //.weight(2f)
                    // .align(Alignment.CenterHorizontally)
                    .padding(start = 20.dp, top = 20.dp, bottom = 20.dp),
                style = MaterialTheme.typography.titleSmall,
                textAlign = TextAlign.Start,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
        Column(
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier.padding(start = 8.dp)
        ) {
            IconButton(
                onClick = { isClicked.value = !isClicked.value },
            ) {
                Icon(
                    imageVector = if (isClicked.value) Icons.Filled.KeyboardArrowUp else Icons.Filled.KeyboardArrowDown,
                    contentDescription = "Arrow",
                    tint = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.size(32.dp)
                )
            }
        }
        Column(
            //verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.End,
        ) {
            when (valueToPrint) {
                "Sort by" -> {
                    val sortBySelected =
                        value!!.getOrNull(checkedValues.getValue(text).indexOfFirst { it.value })
                    if (sortBySelected != null) {
                        Text(
                            text = sortBySelected,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.End,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.small)
                                .border(
                                    1.dp,
                                    color = Gray3,
                                    shape = MaterialTheme.shapes.small
                                )
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                        )
                    }
                }

                "State" -> {
                    val stateSelected: MutableList<String> = mutableListOf()
                    for (index in value!!.indices) {
                        if (checkedValues.getValue(text)[index].value) {
                            stateSelected.add(value[index])
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                    )
                    {
                        stateSelected.forEachIndexed { _, it ->

                            Box(
                                Modifier
                                    .padding(end = 8.dp)
                            ) {
                                Text(
                                    text = it,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.End,
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier
                                        .clip(MaterialTheme.shapes.small)
                                        .border(
                                            1.dp,
                                            color = Gray3,
                                            shape = MaterialTheme.shapes.small
                                        )
                                        .padding(horizontal = 6.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                "Members" -> {
                    if (memberFiltered.size > 0) {
                        val memberSelected by vmMember!!.getAllMemberById(memberFiltered)
                            .collectAsState(
                                initial = emptyList()
                            )
                        Row(
                            horizontalArrangement = Arrangement.End,
                            //verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .offset((5 * memberSelected.size).dp)
                            //.weight(.45f)
                        ) {
                            memberSelected.take(5).forEachIndexed { index, member ->
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .offset(x = (-index * 5).dp)
                                        .background(
                                            member.colorBrush,
                                            CircleShape
                                        ),
                                    //.clickable { showDialog = true },
                                    contentAlignment = Alignment.Center
                                )
                                {
                                    RenderProfile(
                                        imageProfile = member.userImage,
                                        initialsName = member.initialsName,
                                        backgroundColor = Color.White,
                                        backgroundBrush = member.colorBrush,
                                        sizeTextPerson = 10.sp,
                                        sizeImage = 30.dp,
                                        typeProfile = TypeProfileIcon.PERSON
                                    )
                                }
                            }
                            if (memberSelected.size > 5) {
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .offset(-(4 * memberSelected.size).dp)
                                        .background(
                                            gradientList[5 % gradientList.size],
                                            CircleShape
                                        ),
                                    // .clickable { showDialog = true },
                                    contentAlignment = Alignment.Center
                                )
                                {
                                    Text(
                                        text = "+${memberSelected.size - 5}",
                                        style = MaterialTheme.typography.headlineLarge,
                                        fontWeight = FontWeight.Medium,
                                        fontSize = 10.sp,
                                        color = MaterialTheme.colorScheme.onPrimary,
                                    )
                                }
                            }

                        }
                    }
                }

                "Tag" -> {
                    val tagSelected: MutableList<String> = mutableListOf()
                    for (index in value!!.indices) {
                        if (checkedValues.getValue(text).size > index) {
                            if (checkedValues.getValue(text)[index].value) {
                                tagSelected.add(value[index])
                            }
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                    )
                    {
                        tagSelected.forEachIndexed { _, it ->

                            Box(
                                Modifier
                                    .padding(end = 8.dp)
                            ) {
                                Text(
                                    text = it,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.End,
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier
                                        .clip(MaterialTheme.shapes.small)
                                        .border(
                                            1.dp,
                                            color = Gray3,
                                            shape = MaterialTheme.shapes.small
                                        )
                                        .padding(horizontal = 6.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                "Category" -> {
                    val categorySelected: MutableList<String> = mutableListOf()
                    for (index in value!!.indices) {
                        if (checkedValues.getValue(text).size > index) {
                            if (checkedValues.getValue(text)[index].value) {
                                categorySelected.add(value[index])
                            }
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                    )
                    {
                        categorySelected.forEachIndexed { _, it ->

                            Box(
                                Modifier
                                    .padding(end = 8.dp)
                            ) {
                                Text(
                                    text = it,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.End,
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier
                                        .clip(MaterialTheme.shapes.small)
                                        .border(
                                            1.dp,
                                            color = Gray3,
                                            shape = MaterialTheme.shapes.small
                                        )
                                        .padding(horizontal = 6.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                "Priority" -> {
                    val prioritySelected: MutableList<String> = mutableListOf()
                    for (index in value!!.indices) {
                        if (checkedValues.getValue(text)[index].value) {
                            prioritySelected.add(value[index])
                        }
                    }
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                    )
                    {
                        prioritySelected.forEachIndexed { _, it ->

                            Box(
                                Modifier
                                    .padding(end = 8.dp)
                            ) {
                                Text(
                                    text = it,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.End,
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier
                                        .clip(MaterialTheme.shapes.small)
                                        .border(
                                            1.dp,
                                            color = Gray3,
                                            shape = MaterialTheme.shapes.small
                                        )
                                        .padding(horizontal = 6.dp, vertical = 4.dp)
                                )
                            }
                        }
                    }
                }

                "Expiration date" -> {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                    )
                    {
                        if (dateValues.getValue(text).value.isNotEmpty()) {
                            val date = dateValues.getValue(text).value.split("-")
                                .reversed()
                                .joinTo(StringBuilder(), separator = "/").toString()
                            Box(
                                Modifier
                                    .padding(end = 8.dp)
                            ) {
                                Text(
                                    text = date,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.End,
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier
                                        .clip(MaterialTheme.shapes.small)
                                        .border(
                                            1.dp,
                                            color = Gray3,
                                            shape = MaterialTheme.shapes.small
                                        )
                                        .padding(horizontal = 6.dp, vertical = 4.dp)
                                )
                            }

                        }
                    }
                }

                "Creation date" -> {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                    )
                    {
                        if (dateValues.getValue(text).value.isNotEmpty()) {
                            val date = dateValues.getValue(text).value.split("-")
                                .reversed()
                                .joinTo(StringBuilder(), separator = "/").toString()
                            Box(
                                Modifier
                                    .padding(end = 8.dp)
                            ) {
                                Text(
                                    text = date,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.End,
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier
                                        .clip(MaterialTheme.shapes.small)
                                        .border(
                                            1.dp,
                                            color = Gray3,
                                            shape = MaterialTheme.shapes.small
                                        )
                                        .padding(horizontal = 6.dp, vertical = 4.dp)
                                )
                            }

                        }
                    }
                }

                else -> {
                    Log.e("FilterPanel", "Error in valueToPrint")
                }
            }
        }

    }
    if (isClicked.value) {
        Column {
            when (typeOfButton) {
                "Calendar" -> {
                    val date = calendarRender(choosenDate)
                    if (choosenDate.value) {
                        if (date != null) {
                            dateValues.getValue(text).value = date
                            changedFilterValue.value = true
                        }
                        isClicked.value = false
                        choosenDate.value = false
                    }
                }

                "Popup" -> {
                    MembersFilter(
                        vmMember!!,
                        memberPanel,
                        value,
                        memberFiltered,
                        memberFilteredChoosen,
                        changedFilterValue
                    )

                    if (memberFilteredChoosen.value) {
                        if (memberFiltered.isNotEmpty()) {
                            changedFilterValue.value = true
                        }
                        isClicked.value = false
                        memberFilteredChoosen.value = false
                    }
                }

                else -> {
                    for (index in value!!.indices) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 30.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            Text(
                                value[index],
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(top = 8.dp, bottom = 8.dp),
                                style = MaterialTheme.typography.bodyMedium,
                                textAlign = TextAlign.Start,
                                color = MaterialTheme.colorScheme.onBackground
                            )
                            when (typeOfButton) {
                                "Radio" -> {
                                    RadioButton(
                                        selected = checkedValues.getValue("Sort by")[index].value,
                                        onClick = {
                                            changedFilterValue.value = true
                                            checkedValues.getValue("Sort by")
                                                .forEachIndexed { i, record ->
                                                    if (i != index) record.value = false
                                                    else record.value = !record.value
                                                }
                                        },
                                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                                    )
                                }

                                "Checkbox" -> {
                                    Checkbox(
                                        checked = checkedValues.getValue(text)[index].value,
                                        onCheckedChange = {
                                            changedFilterValue.value = true
                                            checkedValues.getValue(text)[index].value = it
                                        },
                                        colors = CheckboxDefaults.colors(
                                            checkedColor = MaterialTheme.colorScheme.primary,
                                            uncheckedColor = MaterialTheme.colorScheme.secondary
                                        ),
                                        modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    HorizontalDivider(thickness = 0.4.dp, color = MaterialTheme.colorScheme.onSurface)
}


@Composable
fun MembersFilter(
    vmMember: MemberViewModel,
    memberPanel: MutableState<Boolean>,
    memberListOfGroup: List<String>?,
    memberFiltered: MutableList<Long>,
    memberFilteredChoosen: MutableState<Boolean>,
    changedFilterValue: MutableState<Boolean>,
) {
    val searchText = rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()
    val memberList by vmMember.getMembers().collectAsState(initial = emptyList())
    if (memberList.isEmpty()) return

    Dialog(
        onDismissRequest = {
            memberPanel.value = false
        },
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier.padding(8.dp, 8.dp)
            ) {
                Box {
                    IconButton(
                        onClick = {
                            memberFiltered.clear()
                            memberPanel.value = false
                            memberFilteredChoosen.value = true
                            changedFilterValue.value = true
                        },
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(top = 5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Close Icon",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Row(
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .fillMaxWidth()
                            .align(Alignment.Center),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Members of team",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }


                TextField(
                    value = searchText.value,
                    onValueChange = { newText: String ->
                        searchText.value = newText
                    },
                    placeholder = {
                        Text(
                            text = "Search",
                            color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onBackground else Color.Gray
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 24.dp, end = 24.dp, bottom = 16.dp, top = 8.dp)
                        .clip(MaterialTheme.shapes.small),
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.onSurface,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    ),
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search Icon",
                            tint = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onBackground else Color.Gray
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
                                    tint = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onBackground else Color.Gray
                                )
                            }
                        }
                    }
                )


                Box {
                    LazyColumn(
                        Modifier
                            .fillMaxHeight()
                            .fillMaxWidth()
                            .padding(bottom = 70.dp, start = 8.dp, end = 8.dp)
                            .simpleVerticalScrollbar(state = listState),
                        state = listState,
                    ) {
                        itemsIndexed(
                            memberListOfGroup ?: listOf(),
                        ) { _, memberId ->

                            if (memberList.find { it.id == memberId.toLong() }?.fullname
                                    ?.contains(
                                        searchText.value,
                                        ignoreCase = true
                                    )!!
                            ) {
                                RenderMember(
                                    memberList.find { it.id == memberId.toLong() }!!,
                                    memberFiltered,
                                    changedFilterValue
                                )
                            }
                        }
                    }


                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {

                        Button(onClick = {
                            memberPanel.value = false
                            memberFilteredChoosen.value = true
                            changedFilterValue.value = true
                        }) {
                            Text(
                                text = "Save",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onPrimary
                            )
                        }
                    }


                }
            }

        }
    }
}


@Composable
fun RenderMember(
    member: Member,
    memberFiltered: MutableList<Long>,
    changedFilterValue: MutableState<Boolean>,
) {

    Row(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 15.dp)
            .fillMaxWidth()
            .clickable {
                if (memberFiltered.contains(member.id)) {
                    memberFiltered.remove(member.id)
                } else {
                    memberFiltered.add(member.id)
                }
                changedFilterValue.value = true
            },
        verticalAlignment = Alignment.CenterVertically

    ) {
        Column(
            modifier = Modifier
                .weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(member.colorBrush, CircleShape),
                contentAlignment = Alignment.Center
            )
            {
                RenderProfile(
                    imageProfile = member.userImage,
                    initialsName = member.initialsName,
                    backgroundColor = Color.White,
                    backgroundBrush = member.colorBrush,
                    sizeTextPerson = 16.sp,
                    sizeImage = 40.dp,
                    typeProfile = TypeProfileIcon.PERSON
                )
                if (memberFiltered.contains(member.id)) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.Black.copy(alpha = 0.80f), CircleShape)
                    ) {
                        Icon(
                            painterResource(id = R.drawable.check),
                            contentDescription = "Arrow",
                            tint = Color.White,
                            modifier = Modifier
                                .size(30.dp)
                                .align(Alignment.Center)
                        )
                    }
                }
            }
        }
        Column(
            modifier = Modifier
                .weight(3f),
        ) {
            Text(
                member.fullname,
                modifier = Modifier.padding(start = 8.dp),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onBackground
            )
        }
    }
}
