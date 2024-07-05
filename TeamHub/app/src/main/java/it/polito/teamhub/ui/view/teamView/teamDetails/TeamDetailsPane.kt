package it.polito.teamhub.ui.view.teamView.teamDetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.dataClass.team.Role
import it.polito.teamhub.ui.view.component.topBar.TopBar
import it.polito.teamhub.ui.view.component.topBar.TopBarValue
import it.polito.teamhub.viewmodel.TeamViewModel

@Composable
fun TeamDetailsPane(
    navController: NavController,
    vmTeam: TeamViewModel,
    teamId: Long,
    memberList: List<Member>,
    memberLogged: Member
) {
    //from MainActivity
    //val memberList by vmMember.getMembers().collectAsState(initial = emptyList())
    val scrollState = rememberScrollState()
    Scaffold(
        topBar = {
            val topBarParameter = TopBarValue(
                title = "",
                backArrow = true,
                vmTeam = vmTeam,
            )
            TopBar(navController, topBarParameter, memberLogged)
        },
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier.padding(innerPadding)
        ) {
            if (this.maxHeight > this.maxWidth) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                ) {
                    ColumnLayout(
                        navController,
                        vmTeam,
                        teamId,
                        memberList,
                        memberLogged
                    )
                }
            } else {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .verticalScroll(scrollState),
                    contentAlignment = Alignment.Center
                ) {
                    RowLayout(
                        navController,
                        vmTeam,
                        teamId,
                        memberList,
                        memberLogged
                    )
                }
            }

        }
    }
}

@Composable
fun ColumnLayout(
    navController: NavController,
    vmTeam: TeamViewModel,
    teamId: Long,
    memberList: List<Member>,
    memberLogged: Member
) {
        val memberLoggedRole by vmTeam.getRoleOfMemberLoggedByTeamId(teamId)
            .collectAsState(initial = Role.MEMBER)
        val teamList by vmTeam.teamList.collectAsState()
        val team = teamList.find { it.id == teamId }
        if (team != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxHeight(1 / 6f)
                        .padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    TeamImage(
                        imageTeam = team.imageTeam,
                        defaultImage = team.defaultImage,
                        color = team.color,
                    )
                }
                TextInfo(team.name, team.description, team.members.filter { it.isMember }.size)
                if (memberLoggedRole == Role.ADMIN)
                    InviteNewMember(teamId)
                else
                    Spacer(modifier = Modifier.padding(bottom = 16.dp))
                MembersListTeam(navController, vmTeam, teamId, memberList, memberLogged)
                BottomMenu(navController, vmTeam, teamId, memberLogged)
            }
        }

}

@Composable
fun RowLayout(
    navController: NavController,
    vmTeam: TeamViewModel,
    teamId: Long,
    memberList: List<Member>,
    memberLogged: Member
) {
        val teamList by vmTeam.teamList.collectAsState()
        val team = teamList.find { it.id == teamId }
        if (team != null) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(.8f)
                    .padding(start = 16.dp, end = 16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxHeight(1 / 6f)
                        .padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    TeamImage(
                        imageTeam = team.imageTeam,
                        defaultImage = team.defaultImage,
                        color = team.color,
                    )
                }
                TextInfo(team.name, team.description, team.members.filter { it.isMember }.size)
                InviteNewMember(teamId)
                MembersListTeam(navController, vmTeam, teamId, memberList, memberLogged)
                BottomMenu(navController, vmTeam, teamId, memberLogged)
            }
        }

}