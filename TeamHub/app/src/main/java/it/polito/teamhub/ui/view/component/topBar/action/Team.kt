package it.polito.teamhub.ui.view.component.topBar.action

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.polito.teamhub.R
import it.polito.teamhub.dataClass.team.Role
import it.polito.teamhub.viewmodel.TeamViewModel

@Composable
fun RenderActionTeam(
    navController: NavController,
    vmTeam: TeamViewModel?,
    currentRoute: String,
) {
    when (currentRoute) {
        "team/create" -> {
            Button(
                onClick = {
                    if (vmTeam != null && vmTeam.validate())
                        navController.navigateUp()
                },
                contentPadding = PaddingValues(horizontal = 10.dp),
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.padding(end = 10.dp)
            ) {
                Text("Create")
            }
        }

        "team/{teamId}/edit" -> {
            val teamId = navController.currentBackStackEntry?.arguments?.getString("teamId")
                ?.toLongOrNull()
            Button(
                onClick = {
                    if (teamId != null && vmTeam != null && vmTeam.validate(teamId))
                        navController.navigate("team/${teamId}")
                },
                contentPadding = PaddingValues(horizontal = 10.dp),
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.padding(end = 10.dp)
            ) {
                Text("Save")
            }
        }

        "team/{teamId}" -> {
            val teamId = navController.currentBackStackEntry?.arguments?.getString("teamId")
                ?.toLongOrNull()
            if (teamId != null) {
                val memberLoggedRole by vmTeam!!.getRoleOfMemberLoggedByTeamId(teamId)
                    .collectAsState(initial = null)
                //TODO
                /*val memberLoggedRole = Role.ADMIN*/
                if (memberLoggedRole != null) {
                    if (vmTeam != null && memberLoggedRole == Role.ADMIN) {
                        IconButton(
                            onClick = { navController.navigate("team/${teamId}/edit") },
                        ) {
                            Icon(
                                painter = (painterResource(id = R.drawable.edit_square)),
                                contentDescription = "",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }
        }
    }
}