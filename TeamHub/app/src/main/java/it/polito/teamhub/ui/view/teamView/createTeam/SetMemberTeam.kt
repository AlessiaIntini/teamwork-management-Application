package it.polito.teamhub.ui.view.teamView.createTeam


import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import it.polito.teamhub.R
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.dataClass.team.TeamMember
import it.polito.teamhub.dataClass.team.TimeParticipation
import it.polito.teamhub.ui.view.component.profileIcon.RenderProfile
import it.polito.teamhub.ui.view.component.profileIcon.TypeProfileIcon
import it.polito.teamhub.ui.view.component.simpleVerticalScrollbar
import it.polito.teamhub.viewmodel.TeamViewModel

@Composable
fun MemberPanel(
    memberPanel: MutableState<Boolean>,
    vmTeam: TeamViewModel,
    newMemberTeam: SnapshotStateList<Long>?,
    memberLogged: Member,
    memberList: List<Member>,
) {

    val searchText = rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()
    val teamList by vmTeam.teamList.collectAsState()
    if (memberList.isEmpty()) return
    val loggedMemberTeams = teamList.filter { team ->
        team.members.any { it.idMember == memberLogged.id && it.isMember }
    }
    val listNewMember = loggedMemberTeams.flatMap { it.members }
        .filter { it.idMember != memberLogged.id && it.isMember }
        .distinctBy { it.idMember }
        .toMutableList()
    Dialog(
        onDismissRequest = {
            memberPanel.value = false
        },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column {
                Box {
                    IconButton(
                        onClick = {
                            memberPanel.value = false
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

                Column(
                    verticalArrangement = Arrangement.SpaceBetween,
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    AddMemberYourContact(
                        searchText,
                        listState,
                        listNewMember,
                        newMemberTeam,
                        memberList
                    )
                }
            }

            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (listNewMember.isNotEmpty()) {
                    Button(onClick = {
                        memberPanel.value = false
                        val listId = newMemberTeam!!.toMutableList()
                        listId.add(memberLogged.id)
                        val names =
                            memberList.map { member -> Pair(member.id, member.fullname) }
                        val joinedList = listId.mapNotNull { id ->
                            val name = names.find { it.first == id }?.second
                            if (name != null) Pair(id, name) else null
                        }
                        val list = mutableListOf<TeamMember>()
                        joinedList.forEach { pair ->
                            val member = vmTeam.listMember.find { it.idMember == pair.first }
                            if (member != null) {
                                list.add(member)
                            } else {
                                list.add(
                                    TeamMember(
                                        pair.first,
                                        pair.second,
                                        null,
                                        TimeParticipation.FULL_TIME
                                    )
                                )
                            }

                        }
                        vmTeam.addNewMember(list)
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

@Composable
fun AddMemberYourContact(
    searchText: MutableState<String>,
    listState: LazyListState,
    listNewMember: List<TeamMember>,
    newMemberTeam: SnapshotStateList<Long>?,
    memberList: List<Member>
) {
    if (memberList.isEmpty()) return
    if (listNewMember.isNotEmpty()) {
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
                .padding(horizontal = 24.dp, vertical = 8.dp)
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
                    listNewMember,
                ) { _, teamMember ->
                    if (memberList.find { it.id == teamMember.idMember }?.fullname
                            ?.contains(
                                searchText.value,
                                ignoreCase = true
                            )!!
                    ) {
                        /*val member = vmMember.getMemberById(teamMember.idMember)*/
                        RenderMember(
                            memberList.find { it.id == teamMember.idMember }!!,
                            newMemberTeam,
                        )
                    }
                }
            }
        }
    } else {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = 20.dp, bottom = 60.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "You don't have any contact or all your contacts are already in the team",
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onBackground,
                modifier = Modifier.padding(20.dp)
            )
        }
    }
}

@Composable
fun RenderMember(
    member: Member,
    newMemberTeam: SnapshotStateList<Long>?,
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 24.dp, vertical = 15.dp)
            .fillMaxWidth()
            .clickable {
                if (newMemberTeam!!.contains(member.id)) {
                    newMemberTeam.remove(member.id)
                } else {
                    newMemberTeam.add(member.id)
                }
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
                    typeProfile = TypeProfileIcon.PERSON,
                )

                if (newMemberTeam!!.contains(member.id)) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(Color.Black.copy(alpha = 0.80f), CircleShape)
                    ) {
                        /* Box(
                             modifier = Modifier
                                 .size(40.dp)
                                 .background(member.colorBrush, CircleShape)
                                 .align(Alignment.Center)
                         )*/
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
