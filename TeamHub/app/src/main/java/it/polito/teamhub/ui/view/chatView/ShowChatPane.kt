package it.polito.teamhub.ui.view.chatView

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.teamhub.R
import it.polito.teamhub.dataClass.chat.Chat
import it.polito.teamhub.dataClass.chat.Message
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.ui.theme.Gray2
import it.polito.teamhub.ui.theme.Gray4
import it.polito.teamhub.ui.theme.Green
import it.polito.teamhub.ui.theme.linearGradient
import it.polito.teamhub.ui.view.component.profileIcon.RenderProfile
import it.polito.teamhub.ui.view.component.profileIcon.TypeProfileIcon
import it.polito.teamhub.ui.view.component.topBar.TopBar
import it.polito.teamhub.ui.view.component.topBar.TopBarValue
import it.polito.teamhub.utils.getDate
import it.polito.teamhub.utils.getTime
import it.polito.teamhub.viewmodel.ChatViewModel
import it.polito.teamhub.viewmodel.MemberViewModel
import it.polito.teamhub.viewmodel.TeamViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale


@Composable
fun ShowChatPane(
    navController: NavController,
    vmChat: ChatViewModel,
    vmMember: MemberViewModel,
    vmTeam: TeamViewModel,
    chatId: Long,
    memberList: List<Member>,
    memberLogged: Member
) {
    val chatList by vmChat.getChats().collectAsState(initial = emptyList())
    if (chatList.isNotEmpty()) {
        val chat = chatList.find { it.id == chatId }
        val teamCommon by vmTeam.teamList.collectAsState(initial = emptyList())
        val teamList by vmTeam.getAllTeams().collectAsState(initial = emptyList())
        LaunchedEffect(chat?.messages?.size) {
            vmChat.readMessage(chatId, memberLogged.id)
        }

        if (teamList.isNotEmpty() && memberList.isNotEmpty()) {
            Scaffold(
                topBar = {
                    val topBarParameter = TopBarValue(
                        title = if (chat?.type == TypeProfileIcon.PERSON) memberList.find { it.id == chat.members.find { it != memberLogged.id } }!!.fullname else teamList.find { it.id == chat?.members?.first() }!!.name,
                        backArrow = true,
                        chatMode = true,
                        typeChat = chat?.type,
                        teamChat = if (chat?.type == TypeProfileIcon.TEAM) chat.members.first()
                            .let { idTeam ->
                                teamList.find { it.id == idTeam }
                            } else null,
                        memberChat = if (chat?.type == TypeProfileIcon.PERSON) chat.members.find { it != memberLogged.id }
                            .let { idMember ->
                                memberList.find { it.id == idMember }
                            } else null,
                        vmChat = vmChat,
                        clickable = teamCommon.flatMap { it.members }
                            .any { it.idMember != memberLogged.id && it.isMember }

                    )
                    TopBar(navController, topBarParameter, memberLogged)
                },

                ) { innerPadding ->
                BoxWithConstraints {
                    if (this.maxHeight > this.maxWidth) {
                        Box(
                            modifier = Modifier
                                .padding(innerPadding)
                                .background(MaterialTheme.colorScheme.background)
                        ) {
                            ShowChatPaneContent(
                                memberList = memberList,
                                vmTeam = vmTeam,
                                vmMember = vmMember,
                                vmChat = vmChat,
                                chatId = chatId,
                                memberLogged = memberLogged
                            )
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .padding(innerPadding)
                                .background(MaterialTheme.colorScheme.background)
                                .fillMaxWidth()
                                .align(Alignment.Center),
                            contentAlignment = Alignment.Center
                        ) {
                            Box(
                                modifier = Modifier.fillMaxWidth(.8f),
                                contentAlignment = Alignment.Center
                            ) {
                                ShowChatPaneContent(
                                    memberList = memberList,
                                    vmTeam = vmTeam,
                                    vmMember = vmMember,
                                    vmChat = vmChat,
                                    chatId = chatId,
                                    memberLogged = memberLogged
                                )
                            }
                        }
                    }
                }
            }
        }
    }


}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShowChatPaneContent(
    memberList: List<Member>,
    vmTeam: TeamViewModel,
    vmMember: MemberViewModel,
    vmChat: ChatViewModel,
    chatId: Long,
    memberLogged: Member
) {
    val listState = rememberLazyListState()
    val chatList by vmChat.getChats().collectAsState(initial = emptyList())
    if (chatList.isEmpty()) return
    val chat = chatList.find { it.id == chatId }
    val groupedMessages = chat?.messages?.groupBy { message ->
        SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(message.date)
    }
    LaunchedEffect(key1 = chat) {
        if (chat!!.messages.isNotEmpty()) {
            val lastIndex = chat.messages.size - 1
            listState.animateScrollToItem(lastIndex)
        }
    }
    LazyColumn(
        Modifier
            .fillMaxSize()
            .padding(bottom = 97.dp, start = 16.dp, end = 16.dp),
        state = listState,
    ) {
        groupedMessages?.mapValues { (_, messages) ->
            messages.filter { message ->
                message.deleted?.get(memberLogged.id) != -1
            }
        }?.filterValues { it.isNotEmpty() }?.forEach { (date, messages) ->
            stickyHeader {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center,
                ) {
                    AnimatedVisibility(
                        visible = true, //listState.layoutInfo.visibleItemsInfo.size > messages.size,
                        enter = fadeIn(),
                        exit = fadeOut()
                    ) {
                        Box(
                            modifier = Modifier
                                .background(
                                    MaterialTheme.colorScheme.onBackground.copy(
                                        alpha = 0.5f
                                    ),
                                    RoundedCornerShape(50)
                                )
                                .align(Alignment.CenterHorizontally)
                        ) {
                            Text(
                                text = getDate(date),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.surface,
                                modifier = Modifier.padding(8.dp)
                            )
                        }
                    }
                }
            }

            itemsIndexed(messages) { _, message ->
                val member = memberList.find { it.id == message.author }!!
                Column {
                    DisplayMessage(
                        member = member,
                        message = message,
                        position = member.id == memberLogged.id,
                        typeChat = chat.type,
                        vmTeam = vmTeam,
                        teamId = chat.members.first(),
                    )

                }

            }
        }
    }
    if (chat?.type == TypeProfileIcon.TEAM) {
        WriteMessageTeamChat(
            member = memberLogged,
            vmTeam = vmTeam,
            chat = chat,
            vmChat = vmChat,
            chatId = chatId,
        )
    } else {
        WriteMessagePersonalChat(
            member = chat?.members?.find { it != memberLogged.id }?.let { idMember ->
                memberList.find { it.id == idMember }
            }!!,
            chat = chat,
            listState = listState,
            vmMember = vmMember,
            vmChat = vmChat,
            chatId = chatId,
            listMember = memberList,
            memberLogged = memberLogged
        )

    }
}


@Composable
fun DisplayMessage(
    member: Member,
    message: Message,
    position: Boolean,
    typeChat: TypeProfileIcon,
    vmTeam: TeamViewModel,
    teamId: Long
) {
    val teamList = vmTeam.teamList.collectAsState()
    val team = teamList.value.find { it.id == teamId }
    val fullname = team?.members?.find { it.idMember == member.id }?.fullname

    if (position) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, bottom = 5.dp),
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier
                    .weight(7f),
                horizontalArrangement = Arrangement.End,

                ) {
                ElevatedCard(
                    elevation = CardDefaults.cardElevation(
                        defaultElevation = 6.dp
                    ),
                    modifier = Modifier
                        .fillMaxHeight()
                        .padding(top = 12.dp, bottom = 12.dp, start = 12.dp, end = 12.dp),
                    colors = CardDefaults.elevatedCardColors(
                        contentColor = MaterialTheme.colorScheme.onBackground,
                        containerColor = MaterialTheme.colorScheme.surface,
                    ),
                ) {
                    Column(
                        modifier = Modifier
                            .padding(top = 8.dp, start = 10.dp, end = 10.dp, bottom = 5.dp)
                    ) {
                        if (typeChat == TypeProfileIcon.TEAM) {
                            Text(
                                text = fullname!!,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier
                                    .padding(bottom = 8.dp)
                            )
                        }
                        Text(
                            text = message.text,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                        )

                        Row(
                            verticalAlignment = Alignment.Bottom,
                            modifier = Modifier
                                .align(Alignment.End)
                        ) {
                            Text(
                                text = getTime(message.date),
                                style = MaterialTheme.typography.labelSmall,
                                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                                modifier = Modifier
                                    .align(Alignment.CenterVertically)
                            )
                            val countVisualize = message.receiver.count { it.value == 1 }
                            val countTotal = message.receiver.size
                            if (countVisualize == countTotal) {
                                Icon(
                                    painter = painterResource(id = R.drawable.checkdouble),
                                    contentDescription = "Check icon",
                                    modifier = Modifier
                                        .size(20.dp),
                                    tint = Green.copy(alpha = 0.8f)

                                )
                            } else {
                                Icon(
                                    painter = painterResource(id = R.drawable.check),
                                    contentDescription = "Check icon",
                                    modifier = Modifier
                                        .size(20.dp),
                                    tint = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f)

                                )
                            }

                        }
                    }
                }
            }
            Row(
                modifier = Modifier
                    .weight(1f)
            ) {
                RenderProfile(
                    imageProfile = member.userImage,
                    initialsName = member.initialsName,
                    backgroundColor = Color.White,
                    backgroundBrush = member.colorBrush,
                    sizeTextPerson = 16.sp,
                    sizeImage = 40.dp,
                    typeProfile = TypeProfileIcon.PERSON,
                )
            }
        }
    } else {
        Row(
            Modifier
                .padding(top = 16.dp, bottom = 5.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            var isMember = true
            if (typeChat == TypeProfileIcon.TEAM) {
                isMember = team?.members?.find { it.idMember == member.id }?.isMember!!
            }
            if (!isMember) {
                RenderProfile(
                    imageProfile = "",
                    initialsName = "-",
                    backgroundColor = Color.White,
                    backgroundBrush = Brush.linearGradient(
                        colors = listOf(Gray2, Gray4),
                    ),
                    sizeTextPerson = 16.sp,
                    sizeImage = 40.dp,
                    typeProfile = TypeProfileIcon.PERSON,
                )
            } else {
                RenderProfile(
                    imageProfile = member.userImage,
                    initialsName = member.initialsName,
                    backgroundColor = Color.White,
                    backgroundBrush = member.colorBrush,
                    sizeTextPerson = 16.sp,
                    sizeImage = 40.dp,
                    typeProfile = TypeProfileIcon.PERSON,
                )
            }

            ElevatedCard(
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 6.dp
                ),
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(top = 12.dp, bottom = 12.dp, start = 12.dp, end = 12.dp),
                colors = CardDefaults.elevatedCardColors(
                    contentColor = MaterialTheme.colorScheme.onBackground,
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            ) {
                Column(
                    modifier = Modifier
                        .padding(top = 8.dp, start = 10.dp, end = 10.dp, bottom = 5.dp)
                ) {
                    if (typeChat == TypeProfileIcon.TEAM) {
                        Text(
                            text = fullname!!,
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                    Text(
                        text = getTime(message.date),
                        style = MaterialTheme.typography.labelSmall,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.8f),
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 5.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun WriteMessagePersonalChat(
    member: Member,
    chat: Chat,
    listState: LazyListState,
    vmMember: MemberViewModel,
    vmChat: ChatViewModel,
    chatId: Long,
    listMember: List<Member>,
    memberLogged: Member
) {
    val newMessageText = remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    /* val memberLoggedId = vmMember.memberLogged.collectAsState().value.id
     val memberLogged by vmMember.getMemberById(memberLoggedId).collectAsState(initial = null)
     if (memberLogged == null) return*/
    if (listMember.isEmpty()) return

    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 80.dp)
        ) {
            HorizontalDivider(
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 16.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 20.dp, top = 12.dp, start = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            RenderProfile(
                imageProfile = memberLogged.userImage,
                initialsName = memberLogged.initialsName,
                backgroundColor = Color.White,
                backgroundBrush = memberLogged.colorBrush,
                sizeTextPerson = 16.sp,
                sizeImage = 40.dp,
                typeProfile = TypeProfileIcon.PERSON,
            )

            Box(
                modifier = Modifier
                    .heightIn(max = 60.dp)
                    .verticalScroll(rememberScrollState())
            ) {
                OutlinedTextField(
                    value = newMessageText.value,
                    onValueChange = { newMessageText.value = it },
                    placeholder = { Text("Write a message") },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .padding(start = 12.dp, end = 4.dp)
                        .height(60.dp)
                        .width(250.dp),
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                        cursorColor = MaterialTheme.colorScheme.onBackground
                    )
                )
            }

            val currentDateTime = Calendar.getInstance().time


            IconButton(
                onClick = {
                    if (newMessageText.value.isNotEmpty()) {
                        val newMessage = Message(
                            author = memberLogged.id,
                            text = newMessageText.value,
                            date = currentDateTime,
                            receiver = listMember.find { it.id == chat.members.find { it != memberLogged.id } }
                                ?.let { member ->
                                    mapOf(member.id to 0)
                                }?.toMutableMap()!!,
                            deleted = chat.members.associateWith { _ -> 0 }
                                .toMutableMap()
                        )


                        vmChat.addMessage(chatId, newMessage)
                        if (listMember.find { it.id == member.id }?.chats?.contains(chatId) == false) {
                            vmMember.addChat(member.id, chatId)
                        }
                        newMessageText.value = ""
                        scope.launch {
                            if (chat.messages.isNotEmpty()) {
                                listState.animateScrollToItem(chat.messages.size - 1)
                            }
                        }
                    }
                },
                modifier = Modifier
                    .align(Alignment.CenterVertically)
            ) {
                Icon(
                    painter = painterResource(id = R.drawable.send),
                    contentDescription = "Send comment",
                    modifier = Modifier
                        .size(32.dp)
                        .graphicsLayer(alpha = 0.99f)
                        .drawWithCache {
                            onDrawWithContent {
                                drawContent()
                                drawRect(
                                    linearGradient,
                                    blendMode = BlendMode.SrcAtop
                                )
                            }
                        },
                )
            }
        }
    }
}


@Composable
fun WriteMessageTeamChat(
    member: Member,
    vmTeam: TeamViewModel,
    chat: Chat,
    vmChat: ChatViewModel,
    chatId: Long,
) {
    val newMessageText = remember { mutableStateOf("") }
    val teams by vmTeam.teamList.collectAsState()
    if (teams.isEmpty()) return

    Box(modifier = Modifier.fillMaxSize()) {
        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 80.dp)
        ) {
            HorizontalDivider(
                modifier = Modifier.padding(start = 8.dp, end = 8.dp, bottom = 16.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface
            )
        }

        Row(
            Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .padding(bottom = 20.dp, top = 20.dp, start = 8.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier
                    .weight(.15f)
                    .padding(start = 4.dp),
                horizontalAlignment = Alignment.Start
            ) {
                RenderProfile(
                    imageProfile = member.userImage,
                    initialsName = member.initialsName,
                    backgroundColor = Color.White,
                    backgroundBrush = member.colorBrush,
                    sizeTextPerson = 16.sp,
                    sizeImage = 40.dp,
                    typeProfile = TypeProfileIcon.PERSON,
                )
            }
            Column(
                modifier = Modifier.weight(.7f)
            ) {
                Box(
                    modifier = Modifier
                        .heightIn(max = 60.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    OutlinedTextField(
                        value = newMessageText.value,
                        onValueChange = { newMessageText.value = it },
                        placeholder = { Text("Write a message") },
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier
                            .height(60.dp)
                            .fillMaxWidth(),
                        colors = TextFieldDefaults.colors(
                            focusedTextColor = MaterialTheme.colorScheme.onBackground,
                            unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                            cursorColor = MaterialTheme.colorScheme.onBackground
                        )
                    )
                }
            }

            Column(
                modifier = Modifier
                    .weight(.15f)
                    .padding(end = 4.dp),
                horizontalAlignment = Alignment.End
            ) {
                val currentDateTime = Calendar.getInstance().time
                IconButton(
                    onClick = {
                        if (newMessageText.value.isNotEmpty()) {
                            val newMessage = Message(
                                author = member.id,
                                text = newMessageText.value,
                                date = currentDateTime,
                                receiver = teams.find { it.id == chat.members.first() }?.members?.filter { it.idMember != member.id && it.isMember }
                                    ?.associate { it.idMember to 0 }
                                    ?.toMutableMap()!!,
                                deleted = teams.find { it.id == chat.members.first() }?.members?.filter { it.isMember }!!
                                    .associate { it.idMember to 0 }
                                    .toMutableMap(),
                            )

                            vmChat.addMessage(chatId, newMessage)
                            newMessageText.value = ""
                            /*  scope.launch {
                              if (chat.messages.isNotEmpty()) {
                                  listState.animateScrollToItem(chat.messages.size - 1)
                              }
                          }*/
                        }
                    },
                ) {
                    Icon(
                        painter = painterResource(id = R.drawable.send),
                        contentDescription = "Send comment",
                        modifier = Modifier
                            .size(32.dp)
                            .graphicsLayer(alpha = 0.99f)
                            .drawWithCache {
                                onDrawWithContent {
                                    drawContent()
                                    drawRect(
                                        linearGradient,
                                        blendMode = BlendMode.SrcAtop
                                    )
                                }
                            },
                    )
                }
            }
        }
    }
}