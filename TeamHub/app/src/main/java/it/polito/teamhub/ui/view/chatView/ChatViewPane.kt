package it.polito.teamhub.ui.view.chatView


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import it.polito.teamhub.R
import it.polito.teamhub.dataClass.chat.Chat
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.ui.theme.linearGradient
import it.polito.teamhub.ui.view.component.profileIcon.RenderProfile
import it.polito.teamhub.ui.view.component.profileIcon.TypeProfileIcon
import it.polito.teamhub.ui.view.component.topBar.TopBar
import it.polito.teamhub.ui.view.component.topBar.TopBarValue
import it.polito.teamhub.utils.getDateD
import it.polito.teamhub.utils.getTime
import it.polito.teamhub.utils.isTodayD
import it.polito.teamhub.viewmodel.ChatViewModel
import it.polito.teamhub.viewmodel.MemberViewModel
import it.polito.teamhub.viewmodel.TeamViewModel

@Composable
fun ChatViewPane(
    navController: NavController,
    vmChat: ChatViewModel,
    vmMember: MemberViewModel,
    vmTeam: TeamViewModel,
    memberList: List<Member>,
    memberLogged: Member
) {
    Scaffold(
        topBar = {
            val topBarParameter = TopBarValue(
                title = "Chat",
                plus = true,
                vmChat = vmChat,
                vmMember = vmMember,
                vmTeam = vmTeam,
            )
            TopBar(navController, topBarParameter, memberLogged)
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
                    AllChat(vmChat, navController, vmMember, vmTeam, memberList, memberLogged)
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
                        AllChat(vmChat, navController, vmMember, vmTeam, memberList, memberLogged)
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AllChat(
    vmChat: ChatViewModel,
    navController: NavController,
    vmMember: MemberViewModel,
    vmTeam: TeamViewModel,
    memberList: List<Member>,
    memberLogged: Member
) {
    val chatList by vmChat.getChats().collectAsState(initial = emptyList())
    //val teamList by vmTeam.teamList.collectAsState()
    val teamList by vmTeam.getAllTeams().collectAsState(initial = emptyList())
    if ((teamList.isNotEmpty() && teamList.any { team -> team.members.any { it.idMember == memberLogged.id && it.isMember } }) ||
        (chatList.isNotEmpty() && chatList.filter { it.type == TypeProfileIcon.PERSON }
            .any { chat -> chat.members.contains(memberLogged.id) })
    ) {
        // memberLogged is a member of at least one team in teamList
        val showDeleteDialog = remember { mutableStateOf(false) }
        val clearChat = remember {
            mutableStateOf(false)
        }
        val showMenu = remember { mutableStateOf(false) }
        var selectedChat by remember {
            mutableStateOf(
                Chat(
                    TypeProfileIcon.PERSON,
                    mutableListOf(0),
                    mutableListOf()
                )
            )
        }
        if (showMenu.value) ChatMenu(
            chat = selectedChat,
            navController = navController,
            showMenu = showMenu,
            showDeleteDialog = showDeleteDialog,
            clearChat = clearChat

        )
        if (showDeleteDialog.value) ConfirmDeleteDialog(
            showDeleteDialog,
            vmChat,
            selectedChat,
            clearChat,
            vmMember,
            memberLogged
        )
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
        ) {
            itemsIndexed(
                chatList.filter { chat ->
                    memberLogged.chats?.contains(chat.id) ?: false
                }.sortedByDescending { chat ->
                    chat.messages.filter { message -> message.deleted?.get(memberLogged.id) != -1 }
                        .maxByOrNull { message -> message.date }?.date
                }
            ) { _, chat ->
                Row(
                    modifier = Modifier
                        .combinedClickable(
                            onClick = {
                                navController.navigate("chat/${chat.id}")
                            },
                            onLongClick = {
                                showMenu.value = true
                                selectedChat = chat
                            }
                        )
                        .padding(horizontal = 20.dp, vertical = 15.dp)
                        .fillMaxWidth()
                        .fillMaxHeight(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(1.1f)
                    ) {
                        if (chat.type == TypeProfileIcon.TEAM) {
                            val team = teamList.find { it.id == chat.members.first() }
                            if (team != null) {
                                RenderProfile(
                                    imageProfile = team.imageTeam,
                                    backgroundColor = team.color,
                                    sizeTextPerson = 16.sp,
                                    sizeImage = 50.dp,
                                    typeProfile = TypeProfileIcon.TEAM,
                                )
                            }
                        } else {
                            val member =
                                memberList.find { it.id == chat.members.find { it != memberLogged.id } }
                            if (member != null) {
                                RenderProfile(
                                    imageProfile = member.userImage,
                                    initialsName = member.initialsName,
                                    backgroundColor = Color.White,
                                    backgroundBrush = member.colorBrush,
                                    sizeTextPerson = 16.sp,
                                    sizeImage = 50.dp,
                                    typeProfile = TypeProfileIcon.PERSON,
                                )
                            }
                        }
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(4f)
                            .align(Alignment.Top)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight()
                        ) {
                            var name = ""
                            if (chat.type == TypeProfileIcon.TEAM)
                                name = teamList.find { it.id == chat.members.first() }?.name ?: ""
                            else
                                name =
                                    memberList.find { it.id == chat.members.find { it != memberLogged.id } }?.fullname
                                        ?: ""

                            Text(
                                text = name,
                                style = MaterialTheme.typography.titleSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                overflow = TextOverflow.Ellipsis,
                                maxLines = 1,
                            )
                            if (chat.messages.isNotEmpty()) {
                                Text(
                                    text =
                                    if (chat.messages.last().deleted?.get(memberLogged.id) == -1)
                                        ""
                                    else {
                                        if (chat.type == TypeProfileIcon.TEAM) {
                                            val memberName =
                                                memberList.find { it.id == chat.messages.last().author }?.fullname
                                            "$memberName: ${chat.messages.last().text}"
                                        } else {
                                            chat.messages.last().text
                                        }
                                    },
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1,
                                    modifier = Modifier
                                        .padding(top = 4.dp)
                                )
                            }
                        }
                    }
                    val weight = 0.5f
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .weight(weight)
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxHeight(),
                            horizontalAlignment = Alignment.End,
                            verticalArrangement = Arrangement.Center
                        ) {
                            if (chat.messages.isNotEmpty()) {
                                var count = chat.messages.count {
                                    it.receiver[memberLogged.id] == 0 && it.deleted?.get(
                                        memberLogged.id
                                    ) != -1
                                }
                                Text(
                                    text = if (chat.messages.last().deleted?.get(memberLogged.id) == -1) {
                                        ""
                                    } else {
                                        if (isTodayD(chat.messages.last().date)) getTime(
                                            chat.messages.last().date
                                        ) else getDateD(chat.messages.last().date)
                                    },
                                    color = if (count > 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onBackground,
                                    style = MaterialTheme.typography.labelSmall,
                                    overflow = TextOverflow.Ellipsis,
                                    maxLines = 1,
                                )

                                if (count > 0) {
                                    Box(
                                        modifier = Modifier
                                            .size(23.dp)
                                            .background(
                                                MaterialTheme.colorScheme.primary,
                                                CircleShape
                                            )
                                            .align(Alignment.End),
                                        contentAlignment = Alignment.Center
                                    ) {

                                        Text(
                                            text = "$count",
                                            color = MaterialTheme.colorScheme.onPrimary,
                                            style = MaterialTheme.typography.labelMedium
                                        )
                                    }
                                    count = 0
                                } else {
                                    Box(
                                        modifier = Modifier
                                            .size(25.dp)
                                    )
                                }
                            }
                        }
                    }
                }

                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onSurface,
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }
    } else {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = R.drawable.chat),
                    contentDescription = "chat icon",
                    modifier = Modifier
                        .size(80.dp)
                        .padding(bottom = 8.dp)
                        .graphicsLayer(alpha = 0.99f)
                        .drawWithCache {
                            onDrawWithContent {
                                drawContent()
                                drawRect(
                                    linearGradient,
                                    blendMode = BlendMode.SrcAtop
                                )
                            }
                        }
                )

                Text(
                    text = "No chats found",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleSmall
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = "Join a team to chat with other members",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}

@Composable
fun ChatMenu(
    chat: Chat,
    navController: NavController,
    showMenu: MutableState<Boolean>,
    showDeleteDialog: MutableState<Boolean>,
    clearChat: MutableState<Boolean>
) {
    val shape = RoundedCornerShape(20.dp)
    Dialog(
        onDismissRequest = {
            showMenu.value = false
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(0.8f)
                .clip(shape)
                .background(MaterialTheme.colorScheme.background),
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (chat.type == TypeProfileIcon.TEAM)
                                navController.navigate("chat/${chat.id}/chatInfo")
                            else
                                navController.navigate("profile/personalInfo/${chat.members}")
                            showMenu.value = false
                        }
                        .padding(horizontal = 15.dp, vertical = 15.dp)
                ) {
                    Text(
                        "Show information",
                        Modifier
                            .align(Alignment.CenterVertically)
                            .weight(1f),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Icon(
                        painter = if (chat.type == TypeProfileIcon.TEAM) painterResource(R.drawable.group) else painterResource(
                            R.drawable.profile
                        ),
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.CenterVertically)
                    )
                }
                HorizontalDivider(
                    color = MaterialTheme.colorScheme.onSurface,
                    thickness = 0.5.dp,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            showDeleteDialog.value = true
                            showMenu.value = false
                            clearChat.value = true
                        }
                        .padding(horizontal = 15.dp, vertical = 15.dp)
                ) {
                    Text(
                        "Clear chat", Modifier
                            .align(Alignment.CenterVertically)
                            .weight(1f),
                        style = MaterialTheme.typography.titleSmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                    Icon(
                        painter = painterResource(R.drawable.clear),
                        contentDescription = null,
                        modifier = Modifier.align(Alignment.CenterVertically),
                        tint = MaterialTheme.colorScheme.onBackground
                    )
                }
                if (chat.type == TypeProfileIcon.PERSON) {
                    HorizontalDivider(
                        color = MaterialTheme.colorScheme.onSurface,
                        thickness = 0.5.dp,
                        modifier = Modifier.padding(horizontal = 4.dp)
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                showDeleteDialog.value = true
                                showMenu.value = false
                            }
                            .padding(horizontal = 15.dp, vertical = 15.dp)
                    ) {
                        Text(
                            "Delete chat", Modifier
                                .align(Alignment.CenterVertically)
                                .weight(1f),
                            style = MaterialTheme.typography.titleSmall,
                            color = MaterialTheme.colorScheme.error
                        )
                        Icon(
                            painter = painterResource(R.drawable.delete),
                            contentDescription = null,
                            modifier = Modifier.align(Alignment.CenterVertically),
                            tint = MaterialTheme.colorScheme.error
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun ConfirmDeleteDialog(
    showDeleteDialog: MutableState<Boolean>,
    vmChat: ChatViewModel,
    chat: Chat,
    clearChat: MutableState<Boolean>,
    vmMember: MemberViewModel,
    memberLogged: Member
) {
    Dialog(
        onDismissRequest = { showDeleteDialog.value = false },
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
                    if (clearChat.value) "Are you sure you want to clear this chat?" else "Are you sure you want to delete this chat?",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.Center) {
                    Button(
                        onClick = {
                            showDeleteDialog.value = false
                            clearChat.value = false
                        },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.surface),
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.onBackground
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Text("Cancel", color = MaterialTheme.colorScheme.onBackground)
                    }

                    Button(
                        onClick = {
                            if (!clearChat.value)
                                vmMember.deleteChat(memberLogged.id, chat.id)
                            vmChat.deleteMessage(chat.id, memberLogged.id)
                            showDeleteDialog.value = false
                            clearChat.value = false
                        },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }
    }
}