package it.polito.teamhub.ui.view.component.topBar

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties
import androidx.navigation.NavController
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.dataClass.team.Team
import it.polito.teamhub.ui.theme.CustomFontFamily
import it.polito.teamhub.ui.theme.linearGradient
import it.polito.teamhub.ui.view.component.profileIcon.RenderProfile
import it.polito.teamhub.ui.view.component.profileIcon.TypeProfileIcon
import it.polito.teamhub.viewmodel.MemberViewModel
import it.polito.teamhub.viewmodel.TaskViewModel
import it.polito.teamhub.viewmodel.TeamViewModel

@Composable
fun RenderTitle(
    navController: NavController,
    home: Boolean,
    team: Boolean,
    iconTeam: Boolean,
    imageTeam: String,
    title: String,
    color: Color,
    chatMode: Boolean = false,
    typeChat: TypeProfileIcon? = null,
    memberChat: Member? = null,
    teamChat: Team? = null,
    clickable: Boolean = true
) {
    if (home) {
        Text(
            "TeamHUB",
            style = TextStyle(
                brush = linearGradient,
                fontFamily = CustomFontFamily,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                lineHeight = 28.sp,
                letterSpacing = 0.sp
            ),
            modifier = Modifier.fillMaxWidth(),
            textAlign = TextAlign.Center
        )
    } else {
        if (!chatMode) {
            Row(
                modifier = if (team) Modifier.clickable {
                    val teamId =
                        navController.currentBackStackEntry?.arguments?.getString("teamId")
                    navController.navigate("team/${teamId}")
                }
                else Modifier,
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
            ) {
                if (iconTeam) {
                    IconButton(
                        onClick = {
                            val teamId =
                                navController.currentBackStackEntry?.arguments?.getString("teamId")
                            navController.navigate("team/${teamId}")
                        },
                    ) {
                        RenderProfile(
                            imageProfile = imageTeam,
                            initialsName = "",
                            backgroundColor = color,
                            backgroundBrush = Brush.linearGradient(),
                            sizeTextPerson = 40.sp,
                            sizeImage = 50.dp,
                            typeProfile = TypeProfileIcon.TEAM
                        )
                    }
                }
                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = if (iconTeam) MaterialTheme.typography.titleSmall else MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 6.dp, end = 6.dp),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )

            }
        } else {
            Row(
                modifier = if (typeChat == TypeProfileIcon.TEAM) Modifier.clickable {
                    val chatId =
                        navController.currentBackStackEntry?.arguments?.getString("chatId")
                    navController.navigate("chat/${chatId}/chatInfo")
                }
                else {
                    Modifier.clickable {
                        val userId = memberChat!!.id
                        if (clickable) navController.navigate("profile/personalInfo/${userId}")

                    }
                },
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Start,
            ) {

                IconButton(
                    onClick = {
                    },
                ) {
                    if (typeChat == TypeProfileIcon.TEAM) {
                        RenderProfile(
                            imageProfile = teamChat!!.imageTeam,
                            backgroundColor = teamChat.color,
                            sizeTextPerson = 16.sp,
                            sizeImage = 50.dp,
                            typeProfile = TypeProfileIcon.TEAM,
                        )
                    } else {
                        RenderProfile(
                            imageProfile = memberChat!!.userImage,
                            initialsName = memberChat.initialsName,
                            backgroundColor = Color.White,
                            backgroundBrush = memberChat.colorBrush,
                            sizeTextPerson = 16.sp,
                            sizeImage = 50.dp,
                            typeProfile = TypeProfileIcon.PERSON,
                        )
                    }
                }

                Text(
                    text = title,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = if (iconTeam) MaterialTheme.typography.titleSmall else MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(start = 6.dp, end = 6.dp),
                    overflow = TextOverflow.Ellipsis,
                    maxLines = 1,
                )

            }

        }
    }
}


@Composable
fun RenderBackArrow(
    navController: NavController,
    vmMember: MemberViewModel?,
    showConfirmDialog: MutableState<Boolean>,
) {

    IconButton(
        onClick = {
            when (navController.currentBackStackEntry?.destination?.route) {
                "team/create", "team/{teamId}/edit" -> showConfirmDialog.value =
                    true

                "task/{taskId}/edit", "team/{teamId}/tasks/create/{isDuplicate}" -> {
                    showConfirmDialog.value = true
                }

                "profile/personalInfo/{userId}/edit" -> {
                    vmMember?.showPopup = true
                }

                "team/{teamId}/tasks" -> {
                    navController.navigate("home")
                }

                "team/{teamId}" -> {
                    val teamId =
                        navController.currentBackStackEntry?.arguments?.getString("teamId")
                            ?.toLongOrNull()
                    navController.navigate("team/${teamId}/tasks")
                }


                else -> {
                    navController.popBackStack()
                }
            }

        },
    ) {
        Icon(
            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
            contentDescription = "Back",
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.size(32.dp)
        )
    }
}


@Composable
fun RenderConfirmActionDialog(
    navController: NavController,
    vmTask: TaskViewModel? = null,
    onDismissRequest: () -> Unit,
    showConfirmDialog: MutableState<Boolean>,
    deletedTask: MutableState<Long?>,
    vmTeam: TeamViewModel? = null,
    navigate: Boolean? = true
) {
    Dialog(
        onDismissRequest = { },
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
                    "Confirm Action",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = if (deletedTask.value != null) "Are you sure you want to delete this task?" else "Are you sure you want to exit?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (navController.currentBackStackEntry?.destination?.route == "task/{taskId}/edit") {
                    Text(
                        text = "Your changes will not be saved",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                } else if (navController.currentBackStackEntry?.destination?.route == "team/{teamId}/tasks/create/{isDuplicate}") {
                    Text(
                        text = "The task will not be saved",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                }
                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            showConfirmDialog.value = false
                        },
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onBackground
                        ),
                    ) {
                        Text(text = "No", color = MaterialTheme.colorScheme.onBackground)
                    }
                    Button(
                        onClick = {
                            showConfirmDialog.value = false
                            if (deletedTask.value != null) {
                                vmTask!!.deleteTaskById(deletedTask.value!!)
                            } else {
                                vmTask?.cleanVariables()
                                vmTeam?.cleanVariables()

                            }
                            if (navigate == true) {
                                navController.navigateUp()
                            }
                            onDismissRequest()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                        )
                    ) {
                        Text(text = "Yes")
                    }
                }
            }
        }
    }
}

