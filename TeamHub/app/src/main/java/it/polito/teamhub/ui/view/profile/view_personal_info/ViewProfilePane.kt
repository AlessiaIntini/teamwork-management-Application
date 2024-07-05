package it.polito.teamhub.ui.view.profile.view_personal_info

import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.ui.view.component.profileIcon.RenderProfile
import it.polito.teamhub.ui.view.component.profileIcon.TypeProfileIcon
import it.polito.teamhub.ui.view.component.topBar.TopBar
import it.polito.teamhub.ui.view.component.topBar.TopBarValue
import it.polito.teamhub.viewmodel.MemberViewModel
import it.polito.teamhub.viewmodel.TaskViewModel
import it.polito.teamhub.viewmodel.TeamViewModel

@Composable
fun ViewProfilePane(
    navController: NavController,
    userId: Long,
    vmMember: MemberViewModel,
    vmTask: TaskViewModel,
    vmTeam: TeamViewModel,
    memberLogged: Member
) {
    val member by vmMember.getMemberById(userId).collectAsState(null)
    if (member == null) return
    Scaffold(
        topBar = {
            val topBarParameter = TopBarValue(
                title = "Personal Information",
                backArrow = true,
                vmMember = vmMember,
                vmTask = vmTask,
                vmTeam = vmTeam
            )
            TopBar(navController, topBarParameter, memberLogged)
        },
    ) { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier.padding(innerPadding)
        ) {
            val scrollState = rememberScrollState()
            BoxWithConstraints {
                if (this.maxHeight > this.maxWidth)
                    ColumnLayout(navController, member!!, vmTask, vmTeam, scrollState)
                else
                    RowLayout(navController, member!!, vmTask, vmTeam, scrollState)
            }
        }
    }
}

@Composable
fun ColumnLayout(
    navController: NavController,
    member: Member,
    vmTask: TaskViewModel,
    vmTeam: TeamViewModel,
    scrollState: ScrollState
) {
    Column {
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
                    imageProfile = member.userImage,
                    initialsName = member.initialsName,
                    backgroundColor = Color.White,
                    backgroundBrush = member.colorBrush,
                    sizeTextPerson = 40.sp,
                    sizeImage = 150.dp,
                    typeProfile = TypeProfileIcon.PERSON,
                )
            }
            MainInfo(
                fullName = member.fullname,
                jobTitle = member.jobTitle,
                description = member.description,
            )
            Spacer(modifier = Modifier.size(20.dp))
            RenderStatsAndAdditionalPersonalInfo(navController, member, vmTask, vmTeam)
        }
    }
}

@Composable
fun RowLayout(
    navController: NavController,
    member: Member,
    vmTask: TaskViewModel,
    vmTeam: TeamViewModel,
    scrollState: ScrollState
) {
    Column {
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
                RenderIconAndMainInfo(member)
            }
            Column(
                modifier = Modifier
                    .weight(2f) // Set column to 2/3
                    .padding(top = 16.dp),
            ) {
                RenderStatsAndAdditionalPersonalInfo(navController, member, vmTask, vmTeam)
            }
        }
    }
}

@Composable
fun RenderIconAndMainInfo(member: Member) {

    RenderProfile(
        imageProfile = member.userImage,
        initialsName = member.initialsName,
        backgroundColor = Color.White,
        backgroundBrush = member.colorBrush,
        sizeTextPerson = 40.sp,
        sizeImage = 150.dp,
        typeProfile = TypeProfileIcon.PERSON,
    )
    MainInfo(
        fullName = member.fullname,
        jobTitle = member.jobTitle,
        description = member.description,
    )
}

@Composable
fun RenderStatsAndAdditionalPersonalInfo(
    navController: NavController,
    member: Member,
    vmTask: TaskViewModel,
    vmTeam: TeamViewModel
) {

    PersonalStats(
        navController = navController,
        userId = member.id,
        vmTask = vmTask,
        vmTeam = vmTeam,
    )

    Spacer(modifier = Modifier.size(32.dp))

    AdditionalPersonalInfo(
        nickname = member.nickname,
        email = member.email,
        location = member.location,
        phoneNumber = member.phoneNumber,
        birthDate = member.birthDate,
        gender = member.gender,
    )
}





