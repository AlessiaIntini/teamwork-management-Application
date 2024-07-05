package it.polito.teamhub.ui.view.chatView

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.teamhub.dataClass.chat.Chat
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.ui.view.component.profileIcon.RenderProfile
import it.polito.teamhub.ui.view.component.profileIcon.TypeProfileIcon
import it.polito.teamhub.ui.view.component.simpleVerticalScrollbar
import it.polito.teamhub.viewmodel.ChatViewModel
import it.polito.teamhub.viewmodel.MemberViewModel
import it.polito.teamhub.viewmodel.TeamViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewChatPane(
    vmMember: MemberViewModel,
    vmTeam: TeamViewModel,
    vmChat: ChatViewModel,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
    navController: NavController,
    memberLogged: Member
) {

    //from Top Bar, non spostabile
    val memberList by vmMember.getMembers().collectAsState(
        emptyList()
    )
    if (memberList.isEmpty()) {
        return
    }
    val searchText = rememberSaveable { mutableStateOf("") }
    val listState = rememberLazyListState()
    val teamList by vmTeam.teamList.collectAsState()
    val loggedMemberTeams = teamList.filter { team ->
        team.members.any { it.idMember == memberLogged.id && it.isMember }
    }
    val listNewMember = loggedMemberTeams.flatMap { it.members }
        .filter { it.idMember != memberLogged.id && it.isMember }
        .distinctBy { it.idMember }
        .toMutableList()

    ModalBottomSheet(
        modifier = Modifier
            .height(650.dp),
        onDismissRequest = {
            onDismissRequest()
        },
        sheetState = sheetState,
        tonalElevation = 0.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "New chat",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
        if (listNewMember.isNotEmpty())
            Box {
                LazyColumn(
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .padding(bottom = 70.dp, start = 8.dp, end = 8.dp)
                        .simpleVerticalScrollbar(state = listState),
                    state = listState,
                ) {
                    itemsIndexed(
                        listNewMember,
                    ) { _, teamMember ->
                        if (memberList.find { it.id == teamMember.idMember }?.fullname
                                ?.contains(
                                    searchText.value,
                                    ignoreCase = true
                                )!!
                        ) {
                            val member = memberList.find { it.id == teamMember.idMember }!!
                            RenderMember(
                                member,
                                navController,
                                vmChat,
                                vmMember,
                                memberLogged
                            )
                        }
                    }
                }
            }
        else {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .padding(bottom = 26.dp)
                    .fillMaxSize()
            ) {
                Text(
                    text = "You don't have any contacts to chat with.",
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier
                        .padding(horizontal = 16.dp)
                        .fillMaxWidth()
                )
            }
        }
    }
}

@Composable
fun RenderMember(
    member: Member,
    navController: NavController,
    vmChat: ChatViewModel,
    vmMember: MemberViewModel,
    memberLogged: Member
) {
    val chatList by vmChat.getChats().collectAsState(initial = emptyList())
    if (chatList.isEmpty()) return
    Row(
        modifier = Modifier
            .clickable {
                if (chatList.find { chat ->
                        chat.type == TypeProfileIcon.PERSON && chat.members.contains(member.id) &&
                                chat.members.contains(memberLogged.id)
                    } != null) {
                    val chatId = chatList.find { chat ->
                        chat.type == TypeProfileIcon.PERSON && chat.members.contains(member.id) &&
                                chat.members.contains(memberLogged.id)
                    }!!.id
                    navController.navigate("chat/${chatId}")
                    vmMember.addChat(memberLogged.id, chatId, member.id)

                } else {

                    val newChat = Chat(
                        members = mutableListOf(member.id, memberLogged.id),
                        type = TypeProfileIcon.PERSON,
                        messages = mutableListOf()
                    )
                    vmChat.addChat(newChat)
                    vmMember.addChat(memberLogged.id, newChat.id, member.id)
                    navController.navigate("chat/${newChat.id}")
                }

            }
            .padding(horizontal = 24.dp, vertical = 24.dp)
            .fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically

    ) {
        RenderProfile(
            imageProfile = member.userImage,
            initialsName = member.initialsName,
            backgroundColor = Color.White,
            backgroundBrush = member.colorBrush,
            sizeTextPerson = 14.sp,
            sizeImage = 36.dp,
            typeProfile = TypeProfileIcon.PERSON,
        )
        Text(
            member.fullname,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp),
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.onSurface
    )
}