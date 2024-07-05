package it.polito.teamhub.ui.view.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import it.polito.teamhub.ui.view.component.profileIcon.RenderProfile
import it.polito.teamhub.ui.view.component.profileIcon.TypeProfileIcon
import it.polito.teamhub.ui.view.component.topBar.TopBar
import it.polito.teamhub.ui.view.component.topBar.TopBarValue
import it.polito.teamhub.viewmodel.MemberViewModel
import it.polito.teamhub.viewmodel.TaskViewModel


@Composable
fun Profile(
    navController: NavController,
    vmMember: MemberViewModel,
    vmTask: TaskViewModel,
    currentUserId: String,
    memberLogged: Member,
    logout: () -> Unit
) {
    Scaffold(
        topBar = {
            val topBarParameter = TopBarValue(
                title = "Profile",
                vmTask = vmTask,
            )
            TopBar(navController, topBarParameter, memberLogged)
        },
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier.padding(innerPadding)
        ) {
            if (this.maxHeight > this.maxWidth)
                ColumnLayout(navController, vmMember, currentUserId, memberLogged, logout)
            else
                RowLayout(navController, vmMember, currentUserId, memberLogged, logout)
        }
    }
}

@Composable
fun ColumnLayout(
    navController: NavController,
    vmMember: MemberViewModel,
    currentUserId: String,
    memberLogged: Member,
    logout: () -> Unit
) {
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Row(
            modifier = Modifier
                .fillMaxHeight(1 / 3f)
                .padding(top = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center
        ) {
            RenderProfile(
                imageProfile = memberLogged.userImage,
                initialsName = memberLogged.initialsName,
                backgroundColor = Color.White,
                backgroundBrush = memberLogged.colorBrush,
                sizeTextPerson = 40.sp,
                sizeImage = 150.dp,
                typeProfile = TypeProfileIcon.PERSON,
            )
        }

        Text(
            text = "Hello, ${memberLogged.fullname}!",
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 32.dp),
            textAlign = TextAlign.Center,
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold
        )
        ProfileMenu(navController, vmMember, currentUserId, logout)
    }
}

@Composable
fun RowLayout(
    navController: NavController,
    vmMember: MemberViewModel,
    currentUserId: String,
    memberLogged: Member,
    logout: () -> Unit
) {
    val scrollState = rememberScrollState()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 16.dp, end = 16.dp)
            .verticalScroll(scrollState)
    ) {
        Column(
            modifier = Modifier
                .weight(1f) //Set column to 1/3
                .padding(top = 16.dp, bottom = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            RenderProfile(
                imageProfile = memberLogged.userImage,
                initialsName = memberLogged.initialsName,
                backgroundColor = Color.White,
                backgroundBrush = memberLogged.colorBrush,
                sizeTextPerson = 40.sp,
                sizeImage = 150.dp,
                typeProfile = TypeProfileIcon.PERSON,
            )
            Text(
                text = "Hello, ${memberLogged.fullname}!",
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 16.dp, bottom = 32.dp),
                textAlign = TextAlign.Center,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
        Column(
            modifier = Modifier
                .weight(2f) // Set column to 2/3
                .padding(top = 16.dp),
        ) {
            ProfileMenu(navController, vmMember, currentUserId, logout)
        }
    }
}

@Composable
fun ProfileMenu(
    navController: NavController,
    vmMember: MemberViewModel,
    currentUserId: String,
    logout: () -> Unit
) {
    val member by vmMember.memberLogged.collectAsState()
    val confirmDeleteAccount = remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .clickable { navController.navigate("profile/personalInfo/${member.id}") }
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
    ) {
        Icon(
            painterResource(id = R.drawable.person_circle),
            contentDescription = "Personal Information",
            modifier = Modifier.padding(end = 16.dp),
            tint = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Personal Information",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge,
        )
    }

    HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface)

    Row(
        modifier = Modifier
            .clickable { navController.navigate("profile/${member.id}/personalStats") }
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
    ) {
        Icon(
            painterResource(id = R.drawable.statistics),
            contentDescription = "Personal Statistics",
            modifier = Modifier.padding(end = 16.dp),
            tint = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Personal Statistics",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge
        )
    }

    HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface)

    Row(
        modifier = Modifier
            .clickable {
                logout()
            }
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
    ) {
        Icon(
            painterResource(id = R.drawable.logout),
            contentDescription = "Logout",
            modifier = Modifier.padding(end = 16.dp),
            tint = MaterialTheme.colorScheme.onBackground
        )
        Text(
            text = "Logout",
            color = MaterialTheme.colorScheme.onBackground,
            style = MaterialTheme.typography.bodyLarge
        )
    }

    HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface)

    Row(
        modifier = Modifier
            .clickable {
                confirmDeleteAccount.value = true
            }
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 16.dp),
    ) {
        Icon(
            painterResource(id = R.drawable.delete),
            contentDescription = "Delete Account",
            modifier = Modifier.padding(end = 16.dp),
            tint = MaterialTheme.colorScheme.error
        )
        Text(
            text = "Delete Account",
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge
        )
    }
    if (confirmDeleteAccount.value) {
        //Show dialog
        DialogDeleteAccount(confirmDeleteAccount = confirmDeleteAccount, onConfirm = {
            vmMember.deleteMember(currentUserId, member.id, logout)
        })
    }
}

@Composable
fun DialogDeleteAccount(
    confirmDeleteAccount: MutableState<Boolean>,
    onConfirm: () -> Unit,
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
                    text = "Are you sure you want to delete your account?",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            confirmDeleteAccount.value = false
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
                            confirmDeleteAccount.value = false
                            onConfirm()
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.error,
                        )
                    ) {
                        Text(text = "Delete")
                    }
                }
            }
        }
    }
}

