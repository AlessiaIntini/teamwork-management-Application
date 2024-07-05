package it.polito.teamhub.ui.view.homePage

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.HorizontalDivider
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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.LineBreak
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import it.polito.teamhub.R
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.dataClass.team.Role
import it.polito.teamhub.dataClass.team.Team
import it.polito.teamhub.ui.theme.CustomFontFamily
import it.polito.teamhub.ui.theme.linearGradient
import it.polito.teamhub.ui.view.component.profileIcon.RenderProfile
import it.polito.teamhub.ui.view.component.profileIcon.TypeProfileIcon
import it.polito.teamhub.ui.view.teamView.teamDetails.ConfirmActionDialog
import it.polito.teamhub.viewmodel.CategoryViewModel
import it.polito.teamhub.viewmodel.TagViewModel
import it.polito.teamhub.viewmodel.TeamViewModel

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun SearchBarTeam(
    navController: NavController,
    vmTeam: TeamViewModel,
    vmCategory: CategoryViewModel,
    vmTag: TagViewModel,
    memberLogged: Member
) {
    var searchText by remember { mutableStateOf("") }
    val teamList by vmTeam.teamList.collectAsState()
    val showDeleteDialog = remember { mutableStateOf(false) }
    val showLeaveDialog = remember { mutableStateOf(false) }
    val showMenu = remember { mutableStateOf(false) }
    var selectedTeam by remember {
        mutableStateOf(
            Team()
        )
    }

    if (showMenu.value) {
        TeamMenu(
            selectedTeam,
            navController,
            showMenu,
            showDeleteDialog,
            showLeaveDialog,
            teamList,
            memberLogged
        )
    }
    if (showDeleteDialog.value) {
        ConfirmActionDialog(
            navController = navController,
            onDismissRequest = { showDeleteDialog.value = false },
            vmTeam = vmTeam,
            memberLogged = memberLogged,
            teamId = selectedTeam.id,
            showLeaveDialog = remember {
                mutableStateOf(false)
            },
            showDeleteDialog = remember {
                mutableStateOf(true)
            },
            teamMembers = teamList.find { it.id == selectedTeam.id }?.members ?: mutableListOf(),
        )
    }

    if (showLeaveDialog.value) {
        val teamMembers =
            teamList.find { it.id == selectedTeam.id }?.members?.filter { it.isMember }
        /*val memberLoggedRole = teamMembers?.find { it.idMember == memberLogged.id }?.role
        if (memberLoggedRole == Role.ADMIN && teamMembers.count { it.role == Role.ADMIN } == 1) {
            val members = teamMembers.filter { it.role == Role.MEMBER && it.isMember }
            val firstDifferentMemberId =
                members.firstOrNull { it.idMember != memberLogged.id }?.idMember
            if (firstDifferentMemberId != null) {
                vmTeam.changeRole(Role.ADMIN, firstDifferentMemberId, selectedTeam.id)
            } else //memberLogged is teh last member in the team
                vmTeam.deleteTeam(selectedTeam.id)*/

       // }

        ConfirmActionDialog(
            navController = navController,
            onDismissRequest = {
                showLeaveDialog.value = false
            },
            vmTeam = vmTeam,
            memberLogged = memberLogged,
            teamId = selectedTeam.id,
            showLeaveDialog = showLeaveDialog,
            showDeleteDialog = showDeleteDialog,
            teamMembers!!
        )
    }

    if (teamList.isNotEmpty()) {
        Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start,
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 16.dp, end = 16.dp, top = 20.dp)
        ) {
            Row {
                TextField(
                    value = searchText,
                    onValueChange = { newText -> searchText = newText },
                    placeholder = {
                        Text(
                            "Search",
                            color = if (isSystemInDarkTheme()) Color.White else Color.Gray
                        )
                    },
                    modifier = Modifier
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
                            tint = if (isSystemInDarkTheme()) Color.White else Color.Gray
                        )
                    },
                    trailingIcon = {
                        if (searchText != "") {
                            IconButton(
                                onClick = { searchText = "" },
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear Icon",
                                    tint = if (isSystemInDarkTheme()) Color.White else Color.Gray
                                )
                            }
                        }
                    }
                )
            }
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
                modifier = Modifier.padding(top = 20.dp, start = 13.dp)
            ) {
                Icon(
                    painterResource(R.drawable.group),
                    contentDescription = "Group Icon",
                    tint = if (isSystemInDarkTheme()) Color.White else Color.Gray
                )
                Text(
                    text = "Teams",
                    modifier = Modifier.padding(start = 16.dp),
                    color = if (isSystemInDarkTheme()) Color.White else Color.Gray
                )
            }

            Spacer(modifier = Modifier.size(8.dp))

            val filteredTeams = teamList.filter { team ->
                team.name.contains(searchText, ignoreCase = true)
            }

            LazyColumn {
                itemsIndexed(filteredTeams) { _, team ->
                    Row(
                        modifier = Modifier
                            .combinedClickable(
                                onClick = {
                                    navController.navigate("team/${team.id}/tasks")
                                    vmCategory.updateIdTeam(team.id)
                                    vmTag.updateIdTeam(team.id)
                                },
                                onLongClick = {
                                    showMenu.value = true
                                    selectedTeam = team
                                }
                            )
                            .padding(horizontal = 10.dp, vertical = 20.dp)
                            .fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(0.9f),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Start
                        ) {
                            RenderProfile(
                                imageProfile = team.imageTeam,
                                backgroundColor = team.color,
                                sizeTextPerson = 16.sp,
                                sizeImage = 50.dp,
                                typeProfile = TypeProfileIcon.TEAM,
                            )
                            Text(
                                text = team.name,
                                color = MaterialTheme.colorScheme.onBackground,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                                modifier = Modifier
                                    .padding(start = 16.dp)
                            )
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.End,
                        ) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = "Arrow Right",
                                modifier = Modifier.align(Alignment.CenterVertically),
                                tint = MaterialTheme.colorScheme.onBackground
                            )
                        }
                    }
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onSurface,
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                }
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .padding(horizontal = 20.dp)
                    .fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.group_2),
                    contentDescription = "team icon",
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
                    text = "No teams found",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleSmall,
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = "Create a new team to start working together with your colleagues",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = TextStyle(
                        lineBreak = LineBreak.Paragraph.copy(strategy = LineBreak.Strategy.Balanced),
                        fontFamily = CustomFontFamily,
                        fontWeight = FontWeight.Normal,
                        fontSize = 12.sp,
                        lineHeight = 16.sp,
                        letterSpacing = 0.5.sp
                    ),
                    textAlign = TextAlign.Center,
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

@Composable
fun TeamMenu(
    team: Team,
    navController: NavController,
    showMenu: MutableState<Boolean>,
    showDeleteDialog: MutableState<Boolean>,
    showLeaveDialog: MutableState<Boolean>,
    teamList: List<Team>,
    memberLogged: Member?
) {
    val teamMembers = teamList.find { it.id == team.id }?.members?.filter { it.isMember }
    val memberLoggedRole = teamMembers?.find { it.idMember == memberLogged?.id }?.role
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
                            navController.navigate("team/${team.id}")
                            showMenu.value = false
                        }
                        .padding(horizontal = 15.dp, vertical = 15.dp)
                ) {
                    Text(
                        "Show information",
                        Modifier
                            .align(Alignment.CenterVertically)
                            .weight(1f),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Icon(
                        painter = painterResource(R.drawable.group),
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onSurface,
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showLeaveDialog.value = true
                            showMenu.value = false
                        }
                        .padding(horizontal = 15.dp, vertical = 15.dp)
                ) {
                    Text(
                        "Leave team", Modifier
                            .align(Alignment.CenterVertically)
                            .weight(1f),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Icon(
                        painter = painterResource(R.drawable.logout),
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.CenterVertically),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                if (memberLoggedRole == Role.ADMIN) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onSurface,
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
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
                            "Delete team", Modifier
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
}