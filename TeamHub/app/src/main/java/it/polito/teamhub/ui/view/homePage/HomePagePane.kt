package it.polito.teamhub.ui.view.homePage


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.ui.view.component.FloatingActionButtonWithoutBottomBar
import it.polito.teamhub.ui.view.component.topBar.TopBar
import it.polito.teamhub.ui.view.component.topBar.TopBarValue
import it.polito.teamhub.viewmodel.CategoryViewModel
import it.polito.teamhub.viewmodel.TagViewModel
import it.polito.teamhub.viewmodel.TaskViewModel
import it.polito.teamhub.viewmodel.TeamViewModel

@Composable
fun HomePagePane(
    navController: NavController,
    vmTeam: TeamViewModel,
    vmTask: TaskViewModel,
    vmCategory: CategoryViewModel,
    vmTag: TagViewModel,
    memberLogged: Member
) {
    Scaffold(
        topBar = {
            val topBarParameter = TopBarValue(
                title = "TeamHUB",
                home = true,
                vmTask = vmTask,
                vmTeam = vmTeam,
            )
            TopBar(navController, topBarParameter, memberLogged)
        },

        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            FloatingActionButtonWithoutBottomBar(
                navController,
                addTeam = true
            )
        },
    ) { innerPadding ->
        BoxWithConstraints {
            if (this.maxHeight > this.maxWidth) {
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxWidth()
                ) {
                    SearchBarTeam(navController, vmTeam, vmCategory, vmTag, memberLogged)
                }
            } else {
                Box(
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxWidth()
                        .align(Alignment.Center),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier.fillMaxWidth(.8f),
                        contentAlignment = Alignment.Center
                    ) {
                        SearchBarTeam(navController, vmTeam, vmCategory, vmTag, memberLogged)
                    }
                }
            }
        }
    }
}