package it.polito.teamhub.ui.view.taskView.createTask

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import it.polito.teamhub.R
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.dataClass.team.Team
import it.polito.teamhub.ui.theme.linearGradient
import it.polito.teamhub.ui.view.component.profileIcon.RenderProfile
import it.polito.teamhub.ui.view.component.profileIcon.TypeProfileIcon
import it.polito.teamhub.ui.view.component.simpleVerticalScrollbar
import it.polito.teamhub.viewmodel.TaskViewModel


@Composable
fun SetMembers(
    vmTask: TaskViewModel,
    memberList: List<Member>,
    team: Team?
) {
    var showDialog by remember { mutableStateOf(false) }
    if (team != null) {
        val membersTeam = team.members.filter { it.isMember }.map { it.idMember }
        val completeList = memberList.filter { membersTeam.contains(it.id) }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.weight(.6f)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.group_1),
                    contentDescription = "Members icon",
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .graphicsLayer(alpha = 0.99f)
                        .drawWithCache {
                            onDrawWithContent {
                                drawContent()
                                drawRect(
                                    linearGradient,
                                    blendMode = BlendMode.SrcAtop
                                )
                            }
                        },
                )
                Text(
                    text = "Assigned to",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .weight(.4f)
            ) {
                if (vmTask.teamMembers.size == 0) {
                    Button(
                        onClick = { showDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.onBackground,
                        ),
                        shape = RoundedCornerShape(50.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .border(
                                width = 1.dp,
                                color = MaterialTheme.colorScheme.primary,
                                shape = RoundedCornerShape(50.dp)
                            )
                            .clip(RoundedCornerShape(50.dp)),
                        contentPadding = PaddingValues(8.dp)
                    ) {
                        Text(
                            text = "Select Members",
                            color = MaterialTheme.colorScheme.primary,
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                } else {
                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier
                            .offset(
                                if (vmTask.teamMembers.size < 5)
                                    (5 * (vmTask.teamMembers.size - 1)).dp
                                else (5 * (5 - 1)).dp
                            )
                            .weight(.45f)
                    ) {
                        vmTask.teamMembers.take(4).forEachIndexed { index, item ->
                            if (memberList.isNotEmpty()) {
                                val member = memberList.find { it.id == item }!!
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .offset(x = (-index * 5).dp)
                                        .background(
                                            member.colorBrush,
                                            CircleShape
                                        )
                                        .clickable { showDialog = true },
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
                        }
                        if (vmTask.teamMembers.size > 4) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .offset(-(5 * 4).dp)
                                    .background(
                                        color = Color.LightGray,
                                        CircleShape
                                    )
                                    .clickable { showDialog = true },
                                contentAlignment = Alignment.Center
                            )
                            {
                                Text(
                                    text = "+${vmTask.teamMembers.size - 4}",
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
        }

        HorizontalDivider(
            color = MaterialTheme.colorScheme.onSurface,
            thickness = 1.dp,
        )

        if (showDialog) {
            MembersBottomSheet(
                completeList = completeList,
                vmTask = vmTask,
                onDismissRequest = { showDialog = false }
            )
        }
    }
}

@Composable
fun MembersBottomSheet(
    completeList: List<Member>,
    vmTask: TaskViewModel,
    onDismissRequest: () -> Unit,
) {
    var searchText by remember { mutableStateOf("") }
    var selectedMembers by remember { mutableStateOf(vmTask.teamMembers) }
    var shownList by remember { mutableStateOf(completeList) }
    val listState = rememberLazyListState()
    Dialog(
        onDismissRequest = {
            onDismissRequest()
            selectedMembers = vmTask.teamMembers
        }
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier.padding(8.dp, 8.dp)
            ) {
                Box {
                    IconButton(
                        onClick = {
                            onDismissRequest()
                            selectedMembers = vmTask.teamMembers
                        },
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(top = 5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Close Icon"
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
                    value = searchText,
                    onValueChange = { newText ->
                        searchText = newText
                        shownList = completeList.filter { member ->
                            member.fullname.contains(newText, ignoreCase = true)
                        }.toMutableList()
                    },
                    placeholder = {
                        Text(
                            "Search",
                            color = if (isSystemInDarkTheme()) Color.White else Color.Gray
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
                            tint = if (isSystemInDarkTheme()) Color.White else Color.Gray
                        )
                    },
                    trailingIcon = {
                        if (searchText != "") {
                            IconButton(
                                onClick = {
                                    searchText = ""
                                    shownList = completeList.toMutableList()
                                },
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
                Box {
                    LazyColumn(
                        Modifier
                            .fillMaxHeight()
                            .padding(bottom = 70.dp, start = 8.dp, end = 8.dp)
                            .simpleVerticalScrollbar(state = listState),
                        state = listState,
                    ) {
                        itemsIndexed(shownList) { _, member ->
                            Row(
                                modifier = Modifier
                                    .padding(horizontal = 24.dp, vertical = 8.dp)
                                    .fillMaxWidth()
                                    .clickable {
                                        selectedMembers = if (selectedMembers.contains(member.id)) {
                                            selectedMembers
                                                .toMutableList()
                                                .also { it.remove(member.id) }
                                        } else {
                                            selectedMembers
                                                .toMutableList()
                                                .also { it.add(member.id) }
                                        }

                                    },
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(40.dp)
                                            .background(
                                                member.colorBrush,
                                                CircleShape
                                            ),
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
                                        if (selectedMembers.contains(member.id)) {
                                            Box(
                                                modifier = Modifier
                                                    .size(40.dp)
                                                    .background(
                                                        Color.Black.copy(alpha = 0.80f),
                                                        CircleShape
                                                    )
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
                                    modifier = Modifier.weight(3f)
                                ) {
                                    Text(
                                        text = member.fullname,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
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
                        Button(
                            onClick = {
                                vmTask.updateTeamMembers(selectedMembers)
                                onDismissRequest()
                            },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.primary
                            )
                        ) {
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


