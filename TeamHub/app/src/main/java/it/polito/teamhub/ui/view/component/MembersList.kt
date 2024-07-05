package it.polito.teamhub.ui.view.component

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import it.polito.teamhub.R
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.dataClass.team.Role
import it.polito.teamhub.dataClass.team.TeamMember
import it.polito.teamhub.dataClass.team.TimeParticipation
import it.polito.teamhub.ui.view.component.profileIcon.RenderProfile
import it.polito.teamhub.ui.view.component.profileIcon.TypeProfileIcon
import it.polito.teamhub.viewmodel.TeamViewModel

@Composable
fun MembersList( //da usare anche per la chat
    navController: NavController,
    listMembersOfTeam: List<Member>,
    teamMembers: List<TeamMember>,
    menu: Boolean, //true se è la lista dei membri del team, false se è la lista dei membri della chat
    vmTeam: TeamViewModel,
    teamId: Long,
    memberLogged: Member
) {
    val currentRoute = navController.currentBackStackEntry?.destination?.route
    val memberLoggedRole =
        teamMembers.find { it.idMember == memberLogged.id }?.role ?: Role.MEMBER
    teamMembers.forEachIndexed { index, member ->
        if (member.isMember) {
            val personalInfo = listMembersOfTeam.find { it.id == member.idMember }!!
            val showMenu = remember { mutableStateOf(false) }
            Row(
                modifier = if (currentRoute?.startsWith("chat") == true) {
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate("profile/personalInfo/${member.idMember}")
                        }
                        .padding(
                            top = if (index == 0) 0.dp else 16.dp,
                            bottom = if (index == teamMembers.size - 1) 0.dp else 16.dp
                        )
                } else {
                    Modifier
                        .fillMaxWidth()
                        .clickable {
                            navController.navigate("profile/personalInfo/${member.idMember}")
                        }
                        .padding(
                            top = if (index == 0) 0.dp else 9.dp,
                            bottom = if (index == teamMembers.size - 1) 0.dp else 9.dp
                        )
                },
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    modifier = Modifier
                        .padding(start = 8.dp),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically

                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(personalInfo.colorBrush, CircleShape),
                        contentAlignment = Alignment.Center
                    )
                    {
                        RenderProfile(
                            imageProfile = personalInfo.userImage,
                            initialsName = personalInfo.initialsName,
                            backgroundColor = Color.White,
                            backgroundBrush = personalInfo.colorBrush,
                            sizeTextPerson = 14.sp,
                            sizeImage = 36.dp,
                            typeProfile = TypeProfileIcon.PERSON
                        )
                    }
                    Text(
                        text = personalInfo.fullname,
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
                Row(
                    horizontalArrangement = Arrangement.End,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Text(
                        text = if (menu) "${member.role!!.getRoleString()}\n${member.timeParticipation.getTimeParticipationString()}"
                        else "${member.role!!.getRoleString()} - ${member.timeParticipation.getTimeParticipationString()}",
                        color = MaterialTheme.colorScheme.tertiary,
                        style = MaterialTheme.typography.bodySmall,
                        modifier = if (menu) Modifier.padding(
                            start = 8.dp,
                            end = 16.dp
                        ) else Modifier.padding(start = 8.dp, end = 32.dp)
                    )
                    if (menu) {
                        if (memberLoggedRole == Role.ADMIN || member.idMember == memberLogged.id) {
                            IconButton(
                                onClick = { showMenu.value = true },
                            ) {
                                Icon(
                                    painterResource(id = R.drawable.menu),
                                    contentDescription = "Member menu",
                                    tint = MaterialTheme.colorScheme.onBackground,
                                )
                            }

                            if (showMenu.value) {
                                ShowMenuTeamMember(
                                    menuState = showMenu,
                                    memberId = member.idMember,
                                    vmTeam = vmTeam,
                                    member = member,
                                    fullname = personalInfo.fullname,
                                    memberLogged = memberLogged,
                                    memberLoggedRole = memberLoggedRole,
                                    teamId = teamId,
                                    teamMembers = teamMembers
                                )
                            }
                        } else {
                            Spacer(modifier = Modifier.width(48.dp))
                        }
                    }
                }
            }
            if (index != teamMembers.size - 1) {
                /*if(currentRoute?.startsWith("chat") == true){
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 14.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                else {*/
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                //}

            }
        }
    }

}

@Composable
fun ShowMenuTeamMember(
    menuState: MutableState<Boolean>,
    memberId: Long,
    vmTeam: TeamViewModel,
    member: TeamMember,
    fullname: String,
    memberLogged: Member,
    memberLoggedRole: Role,
    teamId: Long,
    teamMembers: List<TeamMember>
) {
    val showChangeRoleDialog = remember { mutableStateOf(false) }
    val showChangeTimeDialog = remember { mutableStateOf(false) }
    val showRemoveDialog = remember { mutableStateOf(false) }

    DropdownMenu(
        expanded = menuState.value,
        onDismissRequest = { menuState.value = false },
    ) {
        if (memberLoggedRole == Role.ADMIN && member.idMember != memberLogged.id) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = "Remove from team",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                onClick = {
                    showRemoveDialog.value = true
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.logout),
                        contentDescription = "remove from team",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            )
        }
        if (memberLoggedRole == Role.ADMIN && member.role != Role.GUEST
            && teamMembers.filter { it.isMember }.size > 1
        ) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = "Change role",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                onClick = {
                    showChangeRoleDialog.value = true
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.id_card),
                        contentDescription = "change role",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            )
        }
        if (member.idMember == memberLogged.id) {
            DropdownMenuItem(
                text = {
                    Text(
                        text = if (member.timeParticipation == TimeParticipation.FULL_TIME)
                            "Switch to Part-Time"
                        else "Switch to Full-Time",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                onClick = {
                    showChangeTimeDialog.value = true
                },
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.time),
                        contentDescription = "change time participation",
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
            )
        }
    }
    if (showChangeRoleDialog.value)
        ConfirmActionDialog(
            fullname,
            onDismissRequest = {
                menuState.value = false
                showChangeRoleDialog.value = false
            },
            showChangeRoleDialog,
            showChangeTimeDialog,
            member,
            vmTeam,
            teamId,
            memberLogged,
            memberLoggedRole,
            teamMembers
        )
    if (showChangeTimeDialog.value)
        ConfirmActionDialog(
            fullname,
            onDismissRequest = {
                menuState.value = false
                showChangeTimeDialog.value = false
            },
            showChangeRoleDialog,
            showChangeTimeDialog,
            member,
            vmTeam,
            teamId,
            memberLogged,
            memberLoggedRole,
            teamMembers

        )

    if (showRemoveDialog.value)
        RemoveDialog(
            fullname,
            onDismissRequest = {
                menuState.value = false
                showRemoveDialog.value = false
            },
            vmTeam,
            memberId,
            teamId,
            teamMembers
        )
}

@Composable
fun ConfirmActionDialog(
    fullname: String,
    onDismissRequest: () -> Unit,
    showChangeRoleDialog: MutableState<Boolean>,
    showChangeTimeDialog: MutableState<Boolean>,
    member: TeamMember,
    vmTeam: TeamViewModel,
    teamId: Long,
    memberLogged: Member,
    memberLoggedRole: Role,
    teamMembers: List<TeamMember>

) {
    var expanded by remember { mutableStateOf(false) }
    var selectedRole by remember { mutableStateOf(member.role) }
    var selectedTime by remember { mutableStateOf(member.timeParticipation) }
    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown
    Dialog(
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = if (showChangeRoleDialog.value) "Change Role"
                    else "Change Time Participation",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (showChangeRoleDialog.value) "Select new role for $fullname"
                    else "Select new time participation",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Column {
                    Row(
                        modifier = Modifier
                            .background(
                                MaterialTheme.colorScheme.background,
                                RoundedCornerShape(10.dp)
                            )
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.onBackground,
                                RoundedCornerShape(10.dp)
                            )
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { expanded = !expanded }
                            .width(100.dp)
                            .height(30.dp),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = if (showChangeRoleDialog.value) selectedRole!!.getRoleString()
                            else selectedTime.getTimeParticipationString(),
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 5.dp),
                        )
                        Icon(
                            icon, "arrow menu",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .align(Alignment.CenterVertically)
                                .padding(start = 5.dp)
                        )
                    }
                    if (showChangeRoleDialog.value) { //dropdown per il cambio ruolo
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                        ) {

                            Role.entries.filter { it != Role.GUEST }.forEach { role ->
                                //if (memberLoggedRole == Role.ADMIN) {
                                DropdownMenuItem(
                                    onClick = {
                                        expanded = false
                                        selectedRole = role
                                    },
                                    text = {
                                        Text(
                                            text = role.getRoleString(),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                )
                                //}
                                /*else {
                                    if (role != Role.ADMIN) {
                                        DropdownMenuItem(
                                            onClick = {
                                                expanded = false
                                                selectedRole = role
                                            },
                                            text = {
                                                Text(
                                                    text = role.getRoleString(),
                                                    style = MaterialTheme.typography.bodyMedium,
                                                    color = MaterialTheme.colorScheme.onBackground
                                                )
                                            }
                                        )


                                    }
                                }*/
                            }
                        }
                    }
                    if (showChangeTimeDialog.value) { //dropdown per il cambio time partecipation
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false },
                        ) {
                            TimeParticipation.entries.forEach { time ->
                                DropdownMenuItem(
                                    onClick = {
                                        expanded = false
                                        selectedTime = time
                                    },
                                    text = {
                                        Text(
                                            text = time.getTimeParticipationString(),
                                            style = MaterialTheme.typography.bodyMedium,
                                            color = MaterialTheme.colorScheme.onBackground
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            onDismissRequest()
                        },
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onBackground
                        ),
                    ) {
                        Text(text = "Cancel", color = MaterialTheme.colorScheme.onBackground)
                    }
                    Button(
                        onClick = {
                            if (showChangeRoleDialog.value) {
                                if (member.idMember == memberLogged.id && memberLoggedRole == Role.ADMIN && selectedRole != Role.ADMIN) {
                                    //controllo per assicurare che ci sia sempre almeno un admin
                                    if (teamMembers.count { it.role == Role.ADMIN } == 1) {
                                        val firstDifferentMemberId =
                                            teamMembers.firstOrNull { it.idMember != memberLogged.id && it.isMember }?.idMember
                                        if (firstDifferentMemberId != null) {
                                            vmTeam.changeRole(
                                                Role.ADMIN,
                                                firstDifferentMemberId,
                                                teamId
                                            )
                                        }
                                    }
                                }
                                vmTeam.changeRole(selectedRole, member.idMember, teamId)
                            } else
                                vmTeam.changeTimeParticipation(
                                    selectedTime,
                                    member.idMember,
                                    teamId
                                )
                            onDismissRequest()
                        },
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                            contentColor = MaterialTheme.colorScheme.onPrimary
                        ),
                    ) {
                        Text(text = "Save", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }

            }

        }
    }
}

@Composable
fun RemoveDialog(
    fullname: String,
    onDismissRequest: () -> Unit,
    vmTeam: TeamViewModel,
    memberId: Long,
    teamId: Long,
    teamMembers: List<TeamMember>

) {
    val isLastMember = teamMembers.filter { it.isMember }.size == 1
    Dialog(
        onDismissRequest = { onDismissRequest() },
        properties = DialogProperties(dismissOnBackPress = false, dismissOnClickOutside = false)
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = "Remove from team",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = "Are you sure you want to remove\n${fullname} from the team?",
                    textAlign = TextAlign.Center,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            onDismissRequest()
                        },
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onBackground
                        ),
                    ) {
                        Text(text = "Cancel", color = MaterialTheme.colorScheme.onBackground)
                    }
                    Button(
                        onClick = {
                            vmTeam.removeMember(memberId, teamId, fullname, isLastMember, true)
                            onDismissRequest()
                        },
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        ),
                    ) {
                        Text(text = "Remove", color = MaterialTheme.colorScheme.onError)
                    }
                }
            }
        }
    }
}

