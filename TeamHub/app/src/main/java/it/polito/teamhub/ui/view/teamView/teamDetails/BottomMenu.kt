package it.polito.teamhub.ui.view.teamView.teamDetails

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import it.polito.teamhub.R
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.dataClass.team.Role
import it.polito.teamhub.dataClass.team.TeamMember
import it.polito.teamhub.viewmodel.TeamViewModel

@Composable
fun BottomMenu(
    navController: NavController,
    vmTeam: TeamViewModel,
    teamId: Long,
    memberLogged: Member
) {
        val showLeaveDialog = remember { mutableStateOf(false) }
        val showDeleteDialog = remember { mutableStateOf(false) }
        val teamList by vmTeam.teamList.collectAsState()

        val teamMembers = teamList.find { it.id == teamId }?.members?.filter { it.isMember }
        val memberLoggedRole = teamMembers?.find { it.idMember == memberLogged.id }?.role

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 30.dp)
                .clip(RoundedCornerShape(16.dp))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface,
                    shape = RoundedCornerShape(16.dp)
                )
        ) {
            Column {
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { navController.navigate("team/${teamId}/statistics") }
                ) {
                    Text(
                        text = "Team Statistics",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp)
                    )

                    Icon(
                        painterResource(id = R.drawable.statistics),
                        contentDescription = "Team statistics",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .padding(end = 16.dp)

                    )
                }
                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                Row(
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.clickable { showLeaveDialog.value = true }
                ) {
                    Text(
                        text = "Leave Team",
                        style = MaterialTheme.typography.bodyLarge,
                        modifier = Modifier
                            .weight(1f)
                            .padding(16.dp)
                    )

                    Icon(
                        painterResource(id = R.drawable.logout),
                        contentDescription = "Leave team",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier
                            .padding(end = 16.dp)

                    )
                }
                HorizontalDivider(
                    modifier = Modifier
                        .padding(horizontal = 16.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface,
                )
                if (memberLoggedRole == Role.ADMIN) {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.clickable { showDeleteDialog.value = true }
                    ) {
                        Text(
                            text = "Delete Team",
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .weight(1f)
                                .padding(16.dp)
                        )

                        Icon(
                            painterResource(id = R.drawable.delete),
                            contentDescription = "Team statistics",
                            tint = MaterialTheme.colorScheme.error,
                            modifier = Modifier
                                .padding(end = 16.dp)

                        )
                    }
                }
            }
        }
        if (showLeaveDialog.value) {
            /*if (memberLoggedRole == Role.ADMIN && teamMembers.count { it.role == Role.ADMIN } == 1) {
                val members = teamMembers.filter { it.role == Role.MEMBER && it.isMember }
                val firstDifferentMemberId =
                    members.firstOrNull { it.idMember != memberLogged?.id }?.idMember
                if (firstDifferentMemberId != null) {
                    vmTeam.changeRole(Role.ADMIN, firstDifferentMemberId, teamId)
                } else //memberLogged is teh last member in the team
                    vmTeam.deleteTeam(teamId)

            }*/

            ConfirmActionDialog(
                navController = navController,
                onDismissRequest = {
                    showLeaveDialog.value = false
                },
                vmTeam = vmTeam,
                memberLogged = memberLogged,
                teamId = teamId,
                showLeaveDialog = showLeaveDialog,
                showDeleteDialog = showDeleteDialog,
                teamMembers!!,
                memberLoggedRole
            )
        }
        if (showDeleteDialog.value) {
            ConfirmActionDialog(
                navController = navController,
                onDismissRequest = {
                    showDeleteDialog.value = false
                },
                vmTeam = vmTeam,
                memberLogged = memberLogged,
                teamId = teamId,
                showLeaveDialog = showLeaveDialog,
                showDeleteDialog = showDeleteDialog,
                teamMembers!!
            )
        }

}

@Composable
fun ConfirmActionDialog(
    navController: NavController,
    onDismissRequest: () -> Unit,
    vmTeam: TeamViewModel,
    memberLogged: Member,
    teamId: Long,
    showLeaveDialog: MutableState<Boolean>,
    showDeleteDialog: MutableState<Boolean>,
    teamMembers: List<TeamMember>,
    memberLoggedRole: Role? = null
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
                    text = if (showLeaveDialog.value) "Leave team" else "Delete team",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (showLeaveDialog.value) "Are you sure you want to leave the team?"
                    else "Are you sure you want to delete the team?",
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
                            if (showLeaveDialog.value) {
                                if (memberLoggedRole == Role.ADMIN && teamMembers.count { it.role == Role.ADMIN } == 1) {
                                    val members =
                                        teamMembers.filter { it.role == Role.MEMBER && it.isMember }
                                    val firstDifferentMemberId =
                                        members.firstOrNull { it.idMember != memberLogged.id }?.idMember
                                    if (firstDifferentMemberId != null) {
                                        vmTeam.changeRole(
                                            Role.ADMIN,
                                            firstDifferentMemberId,
                                            teamId
                                        )
                                    } else //memberLogged is teh last member in the team
                                        vmTeam.deleteTeam(teamId)

                                }
                                vmTeam.removeMember(
                                    memberLogged.id,
                                    teamId,
                                    memberLogged.fullname,
                                    isLastMember,
                                    false
                                )
                            }
                            if (showDeleteDialog.value)
                                vmTeam.deleteTeam(teamId)

                            onDismissRequest()
                            if (navController.currentDestination?.route != "home")
                                navController.navigate("home")
                        },
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.error),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                            contentColor = MaterialTheme.colorScheme.onError
                        ),
                    ) {
                        Text(
                            text = if (showLeaveDialog.value) "Leave" else "Delete",
                            color = MaterialTheme.colorScheme.onError
                        )
                    }
                }
            }
        }
    }
}