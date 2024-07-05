package it.polito.teamhub.ui.view.taskView.taskDetails

import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.teamhub.R
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.dataClass.task.Action
import it.polito.teamhub.dataClass.task.Comment
import it.polito.teamhub.dataClass.task.History
import it.polito.teamhub.dataClass.task.State
import it.polito.teamhub.dataClass.task.Task
import it.polito.teamhub.ui.theme.Gray2
import it.polito.teamhub.ui.theme.Gray4
import it.polito.teamhub.ui.theme.linearGradient
import it.polito.teamhub.ui.view.component.profileIcon.RenderProfile
import it.polito.teamhub.ui.view.component.profileIcon.TypeProfileIcon
import it.polito.teamhub.utils.getDate
import it.polito.teamhub.viewmodel.TaskViewModel
import it.polito.teamhub.viewmodel.TeamViewModel
import kotlinx.coroutines.launch
import java.util.Calendar
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Comment(
    task: Task,
    comments: List<Comment>,
    sheetState: SheetState,
    vmTask: TaskViewModel,
    vmTeam: TeamViewModel,
    memberList: List<Member>,
    memberLogged: Member,
    onDismissRequest: () -> Unit
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    val formatter = android.icu.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    val newCommentText = remember { mutableStateOf("") }
    if (memberList.isEmpty()) return
    val team by vmTeam.getTeamById(task.idTeam).collectAsState(initial = null)
    if (team == null) return
    val membersTeam = team!!.members
    val bottomPaddingLazyColumn = if (task.state != State.COMPLETED) 75.dp else 10.dp
    Column {
        ModalBottomSheet(
            onDismissRequest = onDismissRequest,
            sheetState = sheetState,
        ) {
            Text(
                text = "Comments",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp),
                textAlign = TextAlign.Center
            )

            HorizontalDivider(
                modifier = Modifier.padding(start = 16.dp, end = 16.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 8.dp, end = 8.dp)
            ) {
                LazyColumn(
                    Modifier
                        .align(Alignment.Center)
                        .padding(bottom = bottomPaddingLazyColumn, start = 8.dp, end = 8.dp),
                    state = listState,
                ) {
                    itemsIndexed(/*task.comments*/ comments) { index, item ->
                        val member = memberList.find { it.id == item.author }!!
                        val fullname = membersTeam.find { it.idMember == item.author }!!.fullname
                        val isMember = membersTeam.find { it.idMember == item.author }!!.isMember
                        Row(
                            if (index == 0) {
                                Modifier.padding(top = 16.dp, bottom = 5.dp)
                            } else {
                                Modifier.padding(bottom = 5.dp)
                            }
                        ) {
                            if (isMember) {
                                RenderProfile(
                                    imageProfile = member.userImage,
                                    initialsName = member.initialsName,
                                    backgroundColor = Color.White,
                                    backgroundBrush = member.colorBrush,
                                    sizeTextPerson = 16.sp,
                                    sizeImage = 40.dp,
                                    typeProfile = TypeProfileIcon.PERSON,
                                )
                            } else {
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
                            }


                            ElevatedCard(
                                elevation = CardDefaults.cardElevation(
                                    defaultElevation = 6.dp
                                ),
                                modifier = Modifier
                                    .fillMaxHeight()
                                    .width(200.dp)
                                    .padding(start = 12.dp)
                                    .weight(2f),
                                colors = CardDefaults.elevatedCardColors(
                                    contentColor = MaterialTheme.colorScheme.onBackground,
                                    containerColor = MaterialTheme.colorScheme.background,
                                ),
                            ) {
                                Column(modifier = Modifier.padding(top = 8.dp, start = 10.dp))
                                {
                                    Text(
                                        text = fullname,
                                        style = MaterialTheme.typography.bodyLarge,
                                        fontWeight = FontWeight.Bold, // Rende il nome in grassetto
                                        color = MaterialTheme.colorScheme.onBackground,
                                    )
                                    Spacer(modifier = Modifier.height(8.dp))
                                    Text(
                                        text = item.text,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onBackground,
                                    )
                                    Spacer(modifier = Modifier.height(10.dp))
                                }

                            }

                        }
                        Row(
                            if (index == /*task.comments.size*/ comments.size - 1) {
                                Modifier.padding(
                                    bottom = 32.dp,
                                    start = 60.dp,
                                    top = 4.dp
                                )
                            } else {
                                Modifier.padding(
                                    bottom = 20.dp,
                                    start = 60.dp,
                                    top = 4.dp
                                )
                            }
                        ) {
                            val date = getDate(formatter.format(item.date))
                            Text(
                                text = date,
                                modifier = Modifier
                                    .align(Alignment.CenterVertically),
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                        }
                    }
                }
                if (task.state != State.COMPLETED) {
                    Row(
                        Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(bottom = 75.dp)
                    ) {
                        HorizontalDivider(
                            modifier = Modifier.padding(
                                start = 8.dp,
                                end = 8.dp,
                                bottom = 16.dp,
                                top = 16.dp
                            ),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    Row(
                        Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(bottom = 20.dp, top = 20.dp, start = 8.dp, end = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(
                            modifier = Modifier.weight(.15f),
                            horizontalAlignment = Alignment.Start
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
                        }

                        Column(
                            modifier = Modifier
                                .weight(.7f),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Box(
                                modifier = Modifier
                                    .heightIn(max = 60.dp)
                                    .verticalScroll(rememberScrollState())
                            ) {
                                OutlinedTextField(
                                    value = newCommentText.value,
                                    onValueChange = { newCommentText.value = it },
                                    placeholder = { Text("Write a comment") },
                                    shape = RoundedCornerShape(16.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(60.dp),
                                    colors = TextFieldDefaults.colors(
                                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                                        cursorColor = MaterialTheme.colorScheme.onBackground
                                    )
                                )
                            }
                        }

                        Column(
                            modifier = Modifier.weight(.15f),
                            horizontalAlignment = Alignment.End
                        ) {
                            val currentDateTime = Calendar.getInstance().time
                            IconButton(
                                onClick = {
                                    val newComment = Comment(
                                        newCommentText.value,
                                        memberLogged.id,
                                        currentDateTime,
                                    )
                                    val newHistory = History(
                                        author = memberLogged.id,
                                        action = Action.COMMENT,
                                        date = currentDateTime,
                                        description = Action.COMMENT.getAction(newCommentText.value)

                                    )
                                    vmTask.addComment(task.id, newComment, newHistory)
                                    newCommentText.value = ""

                                    scope.launch {
                                        if (comments.isEmpty())
                                            listState.animateScrollToItem(0)
                                        else
                                            listState.animateScrollToItem(/*task.comments.size*/
                                                comments.size - 1
                                            )
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
        }
    }
}

@Composable
fun ShowSingleComment(
    task: Task,
    vmTeam: TeamViewModel,
    comments: List<Comment>,
    isColumnLayout: Boolean,
    memberList: List<Member>,
    setShowCommentBottomSheet: (Boolean) -> Unit,

    ) {
    if (memberList.isEmpty()) return
    val team by vmTeam.getTeamById(task.idTeam).collectAsState(initial = null)
    if (team == null) return
    val membersTeam = team!!.members
    val isDarkTheme = isSystemInDarkTheme()
    val formatter = android.icu.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())


    if (comments.isNotEmpty()) {
        val lastComment = comments.last()
        val author = memberList.find { it.id == lastComment.author }!!
        val fullname = membersTeam.find { it.idMember == author.id }!!.fullname
        val isMember = membersTeam.find { it.idMember == author.id }!!.isMember
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth(if (isColumnLayout) 1f else 0.8f),
            ) {

                Row(
                    modifier = Modifier
                        .padding(bottom = 5.dp)
                        .fillMaxWidth(),
                ) {
                    if(isMember) {
                        RenderProfile(
                            imageProfile = author.userImage,
                            initialsName = author.initialsName,
                            backgroundColor = Color.White,
                            backgroundBrush = author.colorBrush,
                            sizeTextPerson = 16.sp,
                            sizeImage = 40.dp,
                            typeProfile = TypeProfileIcon.PERSON,
                        )
                    }
                    else {
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
                    }
                    ElevatedCard(
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 6.dp
                        ),
                        modifier = Modifier
                            .fillMaxHeight()
                            .padding(start = 12.dp)
                            .weight(2f),
                        colors = CardDefaults.elevatedCardColors(
                            containerColor = MaterialTheme.colorScheme.background,
                            contentColor = MaterialTheme.colorScheme.onBackground,
                        ),
                    ) {
                        Column(modifier = Modifier.padding(top = 8.dp, start = 10.dp))
                        {
                            Text(
                                text = fullname,
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = /*task.comments.last().text,*/ comments.last().text,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                            Spacer(modifier = Modifier.height(10.dp))
                        }
                    }
                }

                Row(
                    modifier = Modifier.padding(
                        bottom = 20.dp,
                        start = 55.dp,
                        top = 4.dp
                    )
                ) {
                    val date =
                        getDate(formatter.format(/*task.comments.last().date*/ comments.last().date))
                    Text(
                        text = date,
                        modifier = Modifier
                            .align(Alignment.CenterVertically)
                            .padding(start = 8.dp),
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }
        }
    }
    if (task.state != State.COMPLETED || (task.state == State.COMPLETED && /*task.comments.isNotEmpty()*/ comments.isNotEmpty())) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 8.dp, start = 24.dp, end = 24.dp)
                .clickable { setShowCommentBottomSheet(true) },
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            /*IconButton(onClick = {
                showCommentBottomSheet = true
            }) {
                Icon(
                    painter = painterResource(id = R.drawable.comment),
                    contentDescription = "Comment Icon",
                    tint = Gray2
                )
            }*/
            Column(
                modifier = Modifier
                    .fillMaxWidth(if (isColumnLayout) 1f else 0.8f),
            ) {
                Text(
                    text = if (/*task.comments.isNotEmpty()*/ comments.isNotEmpty()) "View all ${/*task.comments.size*/ comments.size} comments" else "Add the first comment",
                    style = MaterialTheme.typography.bodyMedium,
                    color = if (isDarkTheme) Color.White else Gray2,
                )
            }
        }
    }
}

