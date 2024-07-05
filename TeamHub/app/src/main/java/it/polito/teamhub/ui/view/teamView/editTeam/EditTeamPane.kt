package it.polito.teamhub.ui.view.teamView.editTeam

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.dataClass.team.Team
import it.polito.teamhub.ui.view.teamView.createTeam.CreateTeamPane
import it.polito.teamhub.viewmodel.MemberViewModel
import it.polito.teamhub.viewmodel.TeamViewModel

@Composable
fun EditTeamPane(
    vmTeam: TeamViewModel,
    teamId: Long,
    team: Team?,
    navController: NavController,
    vmMember: MemberViewModel,
    memberList: List<Member>,
    memberLogged: Member
) {
    LaunchedEffect(key1 = true) {
        vmTeam.setCurrentValue(team)
    }
    CreateTeamPane(
        navController = navController,
        vmTeam = vmTeam,
        vmMember = vmMember,
        teamId = teamId,
        memberList = memberList,
        memberLogged = memberLogged
    )
}