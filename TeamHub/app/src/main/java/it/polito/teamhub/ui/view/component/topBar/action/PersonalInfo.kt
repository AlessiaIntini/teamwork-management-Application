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
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import it.polito.teamhub.R
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.viewmodel.MemberViewModel
import it.polito.teamhub.viewmodel.TeamViewModel

@Composable
fun RenderActionPersonalInfo(
    navController: NavController,
    vmMember: MemberViewModel?,
    vmTeam: TeamViewModel?,
    currentRoute: String,
    memberLogged: Member
) {
    val listTeams = vmTeam?.teamList?.value?.map { it.id } ?: emptyList()
    when (currentRoute) {
        "profile/create" -> {
            Button(
                onClick = {
                    if (vmMember != null) {
                        val currentUser = FirebaseAuth.getInstance().currentUser?.uid
                        if (vmMember.validate(documentId = currentUser, listTeams = emptyList()))
                            navController.navigate("home")
                    }
                },
                contentPadding = PaddingValues(horizontal = 10.dp),
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.padding(end = 10.dp)
            ) {
                Text("Done")
            }
        }

        "profile/personalInfo/{userId}" -> {
            val userId = navController.currentBackStackEntry!!.arguments?.getString("userId")
                ?.toLong()
            if (vmMember != null) {
                val previousRoute = navController.previousBackStackEntry?.destination?.route
                if (memberLogged.id == userId && previousRoute == "profile") {
                    IconButton(
                        onClick = { navController.navigate("profile/personalInfo/${userId}/edit") },
                    ) {
                        Icon(
                            painter = (painterResource(id = R.drawable.edit_square)),
                            contentDescription = "Edit",
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }

        "profile/personalInfo/{userId}/edit" -> {
            Button(
                onClick = {
                    if (vmMember != null) {
                        val userId =
                            navController.currentBackStackEntry?.arguments?.getString("userId")
                                ?.toLongOrNull()
                        if (userId != null) {
                            if (vmMember.validate(memberId = userId, listTeams = listTeams))
                                navController.navigateUp()
                        }
                    }
                },
                contentPadding = PaddingValues(horizontal = 10.dp),
                shape = MaterialTheme.shapes.small,
                modifier = Modifier.padding(end = 10.dp)
            ) {
                Text("Done")
            }
        }
    }
}