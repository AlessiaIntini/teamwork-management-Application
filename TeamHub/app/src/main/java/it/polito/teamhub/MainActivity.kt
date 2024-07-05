package it.polito.teamhub

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.annotation.RequiresApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navDeepLink
import com.google.firebase.auth.FirebaseAuth
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.dataClass.team.Role
import it.polito.teamhub.dataClass.team.Team
import it.polito.teamhub.dataClass.team.TeamMember
import it.polito.teamhub.dataClass.team.TimeParticipation
import it.polito.teamhub.dataClass.team.listOfRolePrintable
import it.polito.teamhub.ui.theme.TeamHubTheme
import it.polito.teamhub.ui.view.calendarView.CalendarPane
import it.polito.teamhub.ui.view.chatView.ChatDetailsPane
import it.polito.teamhub.ui.view.chatView.ChatViewPane
import it.polito.teamhub.ui.view.chatView.ShowChatPane
import it.polito.teamhub.ui.view.component.BottomBar
import it.polito.teamhub.ui.view.component.ErrorPage
import it.polito.teamhub.ui.view.homePage.HomePagePane
import it.polito.teamhub.ui.view.profile.Profile
import it.polito.teamhub.ui.view.profile.add_personal_info.CreateProfilePane
import it.polito.teamhub.ui.view.profile.edit_personal_info.EditProfilePane
import it.polito.teamhub.ui.view.profile.view_personal_info.ViewProfilePane
import it.polito.teamhub.ui.view.profile.view_personal_info.statistics.ShowStatistics
import it.polito.teamhub.ui.view.taskView.createTask.CreateTaskPane
import it.polito.teamhub.ui.view.taskView.editTask.EditTaskPane
import it.polito.teamhub.ui.view.taskView.personalTasks.PersonalTasksPane
import it.polito.teamhub.ui.view.taskView.taskDetails.History
import it.polito.teamhub.ui.view.taskView.taskDetails.ShowTaskDetails
import it.polito.teamhub.ui.view.taskView.teamTasks.TeamTasksPane
import it.polito.teamhub.ui.view.teamView.createTeam.CreateTeamPane
import it.polito.teamhub.ui.view.teamView.editTeam.EditTeamPane
import it.polito.teamhub.ui.view.teamView.teamDetails.TeamDetailsPane
import it.polito.teamhub.ui.view.teamView.teamDetails.teamStatistics.ShowTeamStatistics
import it.polito.teamhub.ui.view.teamView.teamInvitationPage.TeamInvitationPage
import it.polito.teamhub.utils.wrapContextLocale
import it.polito.teamhub.viewmodel.CategoryViewModel
import it.polito.teamhub.viewmodel.ChatViewModel
import it.polito.teamhub.viewmodel.MemberViewModel
import it.polito.teamhub.viewmodel.TagViewModel
import it.polito.teamhub.viewmodel.TaskViewModel
import it.polito.teamhub.viewmodel.TeamViewModel

class MainActivity : ComponentActivity() {
    override fun attachBaseContext(newBase: Context) {
        super.attachBaseContext(wrapContextLocale(newBase, "en"))
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val currentUser = FirebaseAuth.getInstance().currentUser
        setContent {
            val navController = rememberNavController()
            val showBottomBar = remember { mutableStateOf(true) }
            val teamViewModel: TeamViewModel =
                viewModel(factory = Factory(LocalContext.current.applicationContext))
            val taskViewModel: TaskViewModel =
                viewModel(factory = Factory(LocalContext.current.applicationContext))
            val memberViewModel: MemberViewModel =
                viewModel(factory = Factory(LocalContext.current.applicationContext))
            val chatViewModel: ChatViewModel =
                viewModel(factory = Factory(LocalContext.current.applicationContext))
            val categoryViewModel: CategoryViewModel =
                viewModel(factory = Factory(LocalContext.current.applicationContext))
            val tagViewModel: TagViewModel =
                viewModel(factory = Factory(LocalContext.current.applicationContext))

            val memberList by memberViewModel.getMembers().collectAsState(initial = emptyList())
            var localMember: Any? = null
            if (currentUser != null) {
                val memberAlreadyRegistered by memberViewModel.isMemberAlreadyRegistered(
                    currentUser.uid
                ).collectAsState(
                    initial = null
                )

                // Use LaunchedEffect to navigate when memberAlreadyRegistered changes
                LaunchedEffect(memberAlreadyRegistered) {
                    if (memberAlreadyRegistered != null && memberAlreadyRegistered == false) {
                        navController.navigate("profile/create")
                    }
                }

                localMember = memberAlreadyRegistered
                if (localMember is Member) {
                    memberViewModel.updateMemberLogged(localMember)
                    teamViewModel.updateTeamList(localMember.id)
                }

                if (memberViewModel.invitationTeam != null && memberViewModel.invitationRole != null && navController.previousBackStackEntry != null && navController.previousBackStackEntry!!.destination.route == "profile/create") {
                    navController.navigate("invite/${memberViewModel.invitationRole}/${memberViewModel.invitationTeam}")
                }
            }

            TeamHubTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Scaffold(
                        bottomBar = {
                            if (showBottomBar.value && localMember is Member) {
                                BottomBar(
                                    navController = navController,
                                    vmChat = chatViewModel,
                                    memberLogged = localMember
                                )
                            }
                        }
                    ) { innerPadding ->
                        Box(modifier = Modifier.padding(innerPadding)) {
                            NavHost(navController, startDestination = "home") {
                                composable("home") {
                                    if (localMember is Member) {
                                        HomePagePane(
                                            navController,
                                            teamViewModel,
                                            taskViewModel,
                                            categoryViewModel,
                                            tagViewModel,
                                            localMember
                                        )
                                        showBottomBar.value = true
                                    }
                                }
                                composable("tasks") {
                                    if (localMember is Member) {
                                        PersonalTasksPane(
                                            navController,
                                            taskViewModel,
                                            teamViewModel,
                                            memberList,
                                            localMember
                                        )
                                        showBottomBar.value = true
                                    }
                                }
                                composable("calendar") {
                                    if (localMember is Member) {
                                        CalendarPane(
                                            navController,
                                            taskViewModel,
                                            teamViewModel,
                                            memberList,
                                            localMember
                                        )
                                        showBottomBar.value = true
                                    }
                                }
                                composable("chat") {
                                    if (localMember is Member) {
                                        ChatViewPane(
                                            navController,
                                            chatViewModel,
                                            memberViewModel,
                                            teamViewModel,
                                            memberList,
                                            localMember
                                        )
                                        showBottomBar.value = true
                                    }
                                }
                                composable("chat/{chatId}") { backStackEntry ->
                                    val chatId =
                                        backStackEntry.arguments!!.getString("chatId")
                                    if (chatId != null) {
                                        if (localMember is Member) {
                                            ShowChatPane(
                                                navController,
                                                chatViewModel,
                                                memberViewModel,
                                                teamViewModel,
                                                chatId.toLong(),
                                                memberList,
                                                localMember
                                            )
                                            showBottomBar.value = false
                                        }
                                    }
                                }
                                composable("chat/{chatId}/chatInfo") { backStackEntry ->
                                    val chatId =
                                        backStackEntry.arguments!!.getString("chatId")
                                    if (chatId != null) {
                                        if (localMember is Member) {
                                            ChatDetailsPane(
                                                navController,
                                                chatViewModel,
                                                teamViewModel,
                                                chatId.toLong(),
                                                memberList,
                                                localMember
                                            )
                                            showBottomBar.value = false
                                        }
                                    }
                                }
                                composable("team/{teamId}") { backStackEntry ->
                                    val teamId =
                                        backStackEntry.arguments!!.getString("teamId")
                                    if (teamId != null && localMember is Member) {
                                        TeamDetailsPane(
                                            navController,
                                            teamViewModel,
                                            teamId.toLong(),
                                            memberList,
                                            localMember
                                        )
                                        showBottomBar.value = false
                                    }
                                }
                                composable("team/{teamId}/tasks") { backStackEntry ->
                                    val teamId =
                                        backStackEntry.arguments!!.getString("teamId")
                                    if (teamId != null && localMember is Member) {
                                        TeamTasksPane(
                                            navController,
                                            teamId.toLong(),
                                            taskViewModel,
                                            teamViewModel,
                                            tagViewModel,
                                            categoryViewModel,
                                            memberViewModel,
                                            memberList,
                                            localMember
                                        )
                                        showBottomBar.value = false
                                    }
                                }
                                composable("task/{taskId}") { backStackEntry ->
                                    val taskId =
                                        backStackEntry.arguments!!.getString("taskId")
                                    if (taskId != null && localMember is Member) {
                                        ShowTaskDetails(
                                            navController,
                                            taskId.toLong(),
                                            taskViewModel,
                                            tagViewModel,
                                            categoryViewModel,
                                            memberViewModel,
                                            teamViewModel,
                                            memberList,
                                            localMember
                                        )
                                        showBottomBar.value = false
                                    }
                                }
                                composable("task/{taskId}/history") { backStackEntry ->
                                    val taskId =
                                        backStackEntry.arguments!!.getString("taskId")
                                    if (taskId != null && localMember is Member) {
                                        History(
                                            navController,
                                            taskViewModel,
                                            teamViewModel,
                                            memberList,
                                            localMember
                                        )
                                        showBottomBar.value = false
                                    }
                                }
                                composable("team/{teamId}/tasks/create/{isDuplicate}") { backStackEntry ->
                                    val teamId =
                                        backStackEntry.arguments!!.getString("teamId")
                                    val isDuplicate =
                                        backStackEntry.arguments?.getString("isDuplicate")
                                            .toBoolean()
                                    if (teamId != null) {
                                        val team by teamViewModel.getTeamById(teamId.toLong())
                                            .collectAsState(initial = null)
                                        if (team != null && localMember is Member) {
                                            CreateTaskPane(
                                                navController,
                                                taskViewModel,
                                                tagViewModel,
                                                categoryViewModel,
                                                memberList,
                                                team,
                                                isDuplicate,
                                                localMember
                                            )
                                            showBottomBar.value = false
                                        }
                                    }
                                }
                                composable("team/create") {
                                    if (localMember is Member) {
                                        teamViewModel.addNewMember(
                                            mutableListOf(
                                                TeamMember(
                                                    localMember.id,
                                                    localMember.fullname,
                                                    Role.ADMIN,
                                                    TimeParticipation.FULL_TIME
                                                )
                                            )
                                        )
                                        CreateTeamPane(
                                            navController,
                                            teamViewModel,
                                            memberViewModel,
                                            memberList = memberList,
                                            memberLogged = localMember
                                        )
                                        showBottomBar.value = false
                                    }
                                }
                                composable("team/{teamId}/edit") { backStackEntry ->
                                    val teamId =
                                        backStackEntry.arguments!!.getString("teamId")
                                    if (teamId != null) {
                                        val team by teamViewModel.getTeamById(teamId.toLong())
                                            .collectAsState(initial = null)
                                        if (team != null && localMember is Member) {
                                            EditTeamPane(
                                                vmTeam = teamViewModel,
                                                teamId = teamId.toLong(),
                                                team = team,
                                                navController = navController,
                                                vmMember = memberViewModel,
                                                memberList = memberList,
                                                memberLogged = localMember
                                            )
                                            showBottomBar.value = false
                                        }
                                    }
                                }
                                composable("team/{teamId}/statistics") { backStackEntry ->
                                    val teamId =
                                        backStackEntry.arguments!!.getString("teamId")
                                    if (teamId != null && localMember is Member) {
                                        ShowTeamStatistics(
                                            navController = navController,
                                            teamId = teamId.toLong(),
                                            vmTeam = teamViewModel,
                                            vmTask = taskViewModel,
                                            memberList = memberList,
                                            memberLogged = localMember
                                        )
                                        showBottomBar.value = false
                                    }
                                }
                                composable("task/{taskId}/edit") { backStackEntry ->
                                    val taskId =
                                        backStackEntry.arguments!!.getString("taskId")
                                    if (taskId != null && localMember is Member) {
                                        EditTaskPane(
                                            navController,
                                            taskViewModel,
                                            tagViewModel,
                                            categoryViewModel,
                                            memberViewModel,
                                            teamViewModel,
                                            localMember
                                        )
                                        showBottomBar.value = false
                                    }

                                }
                                composable("profile") {
                                    if (localMember is Member) {
                                        Profile(
                                            navController,
                                            memberViewModel,
                                            taskViewModel,
                                            currentUser!!.uid,
                                            localMember
                                        ) { logout() }
                                        showBottomBar.value = true
                                    }
                                }
                                composable("profile/create") {
                                    CreateProfilePane(
                                        navController,
                                        memberViewModel,
                                        taskViewModel,
                                        teamViewModel
                                    )
                                    showBottomBar.value = false

                                }
                                composable("profile/personalInfo/{userId}") { backStackEntry ->
                                    val userId =
                                        backStackEntry.arguments!!.getString("userId")
                                    if (userId != null && localMember is Member) {
                                        ViewProfilePane(
                                            navController,
                                            userId.toLong(),
                                            memberViewModel,
                                            taskViewModel,
                                            teamViewModel,
                                            localMember
                                        )
                                        showBottomBar.value = false
                                    }
                                }
                                composable("profile/personalInfo/{userId}/edit") { backStackEntry ->
                                    val userId =
                                        backStackEntry.arguments!!.getString("userId")
                                    if (userId != null && localMember is Member) {
                                        EditProfilePane(
                                            navController,
                                            memberViewModel,
                                            taskViewModel,
                                            teamViewModel,
                                            localMember
                                        )
                                        showBottomBar.value = false
                                    }
                                }
                                composable("profile/{userId}/personalStats") { backStackEntry ->
                                    val userId =
                                        backStackEntry.arguments!!.getString("userId")
                                    if (userId != null && localMember is Member) {
                                        ShowStatistics(
                                            navController,
                                            userId.toLong(),
                                            taskViewModel,
                                            teamViewModel,
                                            categoryViewModel,
                                            tagViewModel,
                                            localMember,
                                        )
                                        showBottomBar.value = false
                                    }
                                }
                                composable(
                                    "invite/{role}/{teamId}",
                                    deepLinks = listOf(navDeepLink {
                                        uriPattern =
                                            "https://www.teamhub.com/invite/{role}/{teamId}"
                                    }),
                                ) { backStackEntry ->
                                    val teamId =
                                        backStackEntry.arguments?.getString("teamId")
                                            ?.toLongOrNull()
                                    val role =
                                        backStackEntry.arguments?.getString("role").toString()

                                    memberViewModel.updateInvitationTeam(teamId)
                                    memberViewModel.updateInvitationRole(role)

                                    if (teamId != null && listOfRolePrintable().contains(
                                            role
                                        )
                                    ) {
                                        val team by teamViewModel.getTeamById(teamId)
                                            .collectAsState(initial = Team())
                                        if (team.name != "" && localMember is Member) {
                                            TeamInvitationPage(
                                                navController,
                                                teamViewModel,
                                                memberViewModel,
                                                teamId,
                                                role,
                                                localMember
                                            )
                                            showBottomBar.value = false
                                        } else {
                                            if (localMember is Member) {
                                                // Error page
                                                ErrorPage(
                                                    navController = navController,
                                                    localMember
                                                )
                                                showBottomBar.value = false
                                            }
                                        }
                                    } else {
                                        if (localMember is Member) {
                                            // Error page
                                            ErrorPage(navController = navController, localMember)
                                            showBottomBar.value = false
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun logout() {
        // Sign out from Firebase
        FirebaseAuth.getInstance().signOut()

        // Redirect to LoginActivity
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        finish() // Close MainActivity
    }
}
