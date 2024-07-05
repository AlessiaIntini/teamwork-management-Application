package it.polito.teamhub.ui.view.profile.edit_personal_info

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.navigation.NavController
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.ui.view.profile.add_personal_info.CreateProfilePane
import it.polito.teamhub.viewmodel.MemberViewModel
import it.polito.teamhub.viewmodel.TaskViewModel
import it.polito.teamhub.viewmodel.TeamViewModel


@Composable
fun EditProfilePane(
    navController: NavController,
    vmMember: MemberViewModel,
    vmTask: TaskViewModel,
    vmTeam: TeamViewModel,
    memberLogged: Member
) {

    LaunchedEffect(key1 = true) {
        vmMember.setFullName(memberLogged.fullname)
        vmMember.setNameInitials(memberLogged.initialsName)
        vmMember.setDescription(memberLogged.description)
        vmMember.setJobTitle(memberLogged.jobTitle)
        vmMember.setDescription(memberLogged.description)
        vmMember.setNickname(memberLogged.nickname)
        vmMember.setEmail(memberLogged.email)
        vmMember.setLocation(memberLogged.location)
        vmMember.setPhoneNumber(memberLogged.phoneNumber)
        vmMember.setBirthDate(memberLogged.birthDate)
        vmMember.setGender(memberLogged.gender)
        vmMember.updateImageProfile(memberLogged.userImage)
        vmMember.setMemberColor(memberLogged.color)
    }
    CreateProfilePane(navController = navController, vmMember = vmMember, vmTask = vmTask, vmTeam = vmTeam)
}
