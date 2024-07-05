package it.polito.teamhub.ui.view.teamView.teamInvitationPage

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.dataClass.team.Role
import it.polito.teamhub.dataClass.team.Team
import it.polito.teamhub.dataClass.team.TeamMember
import it.polito.teamhub.dataClass.team.TimeParticipation
import it.polito.teamhub.ui.view.component.topBar.TopBar
import it.polito.teamhub.ui.view.component.topBar.TopBarValue
import it.polito.teamhub.ui.view.teamView.teamDetails.TeamImage
import it.polito.teamhub.viewmodel.MemberViewModel
import it.polito.teamhub.viewmodel.TeamViewModel
import java.util.Locale

@Composable
fun TeamInvitationPage(
    navController: NavController,
    vmTeam: TeamViewModel,
    vmMember: MemberViewModel,
    teamId: Long,
    role: String,
    memberLogged: Member
) {
    val scrollState = rememberScrollState()
    var isColumnLayout: Boolean
    Scaffold(
        topBar = {
            val topBarParameter = TopBarValue(
                title = "Team Invitation",
            )
            TopBar(navController, topBarParameter, memberLogged)
        },
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier.padding(innerPadding)
        ) {
            if (this.maxHeight > this.maxWidth) {
                isColumnLayout = true
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    TeamInvitationContent(
                        navController,
                        isColumnLayout,
                        vmMember,
                        vmTeam,
                        teamId,
                        role,
                    )
                }
            } else {
                isColumnLayout = false
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp)
                        .verticalScroll(scrollState),
                ) {
                    TeamInvitationContent(
                        navController,
                        isColumnLayout,
                        vmMember,
                        vmTeam,
                        teamId,
                        role,
                    )
                }
            }
        }
    }
}


@Composable
fun TeamInvitationContent(
    navController: NavController,
    isColumnLayout: Boolean,
    vmMember: MemberViewModel,
    vmTeam: TeamViewModel,
    teamId: Long,
    role: String,
) {
    val configuration = LocalConfiguration.current
    val team by vmTeam.getTeamById(teamId).collectAsState(initial = Team())
    if (team.name == "") return
    val member by vmMember.memberLogged.collectAsState(initial = null)
    if (member == null) return

    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        ElevatedCard(
            modifier = if (isColumnLayout)
                Modifier
                    .padding(vertical = 30.dp)
                    .fillMaxWidth(1f)
                    .height((configuration.screenHeightDp * 0.85).dp)
            else
                Modifier
                    .padding(vertical = 30.dp)
                    .fillMaxWidth(0.8f)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxHeight(1 / 4f)
                        .padding(top = 20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    TeamImage(
                        imageTeam = team.imageTeam,
                        defaultImage = team.defaultImage,
                        color = team.color,
                    )
                }

                Text(
                    text = team.name,
                    style = MaterialTheme.typography.headlineMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                )


                Text(
                    text = "Invited you to join the team",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp)
                )

                Text(
                    text = "Role: $role",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.W500,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 20.dp, bottom = 10.dp),
                    textAlign = TextAlign.Center
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, bottom = 30.dp),
                    horizontalArrangement = Arrangement.Center
                ) {
                    Button(
                        onClick = {
                            vmMember.updateInvitationTeam(null)
                            vmMember.updateInvitationRole(null)
                            val newTeamMember = TeamMember(
                                idMember = member!!.id,
                                fullname = member!!.fullname,
                                role = Role.valueOf(role.uppercase(Locale.getDefault())),
                                timeParticipation = TimeParticipation.FULL_TIME,
                            )
                            vmTeam.addMember(newTeamMember, teamId)
                            vmMember.updateMemberLogged(member!!)
                            navController.navigate("home")
                        },
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(20.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp)
                    ) {
                        Text(text = "Join Team", style = MaterialTheme.typography.bodyLarge)
                    }
                }
            }
        }
    }
}