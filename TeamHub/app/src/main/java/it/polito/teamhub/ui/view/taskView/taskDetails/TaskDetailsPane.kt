package it.polito.teamhub.ui.view.taskView.taskDetails

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.rounded.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.teamhub.R
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.dataClass.task.Attachment
import it.polito.teamhub.dataClass.task.Category
import it.polito.teamhub.dataClass.task.Priority
import it.polito.teamhub.dataClass.task.State
import it.polito.teamhub.dataClass.task.Tag
import it.polito.teamhub.dataClass.task.Task
import it.polito.teamhub.dataClass.team.TeamMember
import it.polito.teamhub.ui.theme.Gray2
import it.polito.teamhub.ui.theme.Gray3
import it.polito.teamhub.ui.theme.Gray4
import it.polito.teamhub.ui.theme.linearGradient
import it.polito.teamhub.ui.view.component.attachment.AttachmentSelection
import it.polito.teamhub.ui.view.component.profileIcon.RenderProfile
import it.polito.teamhub.ui.view.component.profileIcon.TypeProfileIcon
import it.polito.teamhub.ui.view.component.review.RenderPopUpReview
import it.polito.teamhub.ui.view.component.review.RenderRate
import it.polito.teamhub.ui.view.component.simpleVerticalScrollbar
import it.polito.teamhub.ui.view.component.topBar.TopBar
import it.polito.teamhub.ui.view.component.topBar.TopBarValue
import it.polito.teamhub.utils.attachment.calculateAttachment
import it.polito.teamhub.utils.isBeforeCurrentDate
import it.polito.teamhub.viewmodel.CategoryViewModel
import it.polito.teamhub.viewmodel.MemberViewModel
import it.polito.teamhub.viewmodel.TagViewModel
import it.polito.teamhub.viewmodel.TaskViewModel
import it.polito.teamhub.viewmodel.TeamViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TaskDetailsPane(
    navController: NavController,
    task: Task,
    isColumnLayout: Boolean,
    vmTask: TaskViewModel,
    vmTag: TagViewModel,
    vmCategory: CategoryViewModel,
    vmTeam: TeamViewModel,
    showAttachmentBottomSheet: MutableState<Boolean>,
    memberList: List<Member>,
    memberLogged: Member
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    var showCommentBottomSheet by remember { mutableStateOf(false) }
    var showMembersBottomSheet by remember { mutableStateOf(false) }
    var showTagsBottomSheet by remember { mutableStateOf(false) }
    //val memberList by vmMember.getMembers().collectAsState(initial = emptyList())
    var showUrlBottomSheet by remember { mutableStateOf(false) }
    //val memberList by vmMember.getMembers().collectAsState(initial = emptyList())
    if (memberList.isEmpty()) return
    val team by vmTeam.teamList.collectAsState()
    if (team.isEmpty()) return
    val membersOfTeam = team.find { it.id == task.idTeam }?.members
    if (membersOfTeam.isNullOrEmpty()) return
    val category by vmCategory.getCategoryById(task.category!!).collectAsState(
        initial = Category("", -1)
    )

    val comments by vmTask.comments.collectAsState()

    if (showCommentBottomSheet) {
        Comment(
            task,
            comments,
            sheetState,
            vmTask,
            vmTeam,
            memberList,
            memberLogged
        ) {
            showCommentBottomSheet = false

        }
    }

    if (showMembersBottomSheet) {
        MembersBottomSheet(
            navController,
            sheetState,
            membersOfTeam,
            task.members,
            memberList,
        ) {
            showMembersBottomSheet = false
        }
    }

    if (showTagsBottomSheet) {
        TagsBottomSheet(
            sheetState,
            vmTag.getListTagById(task.tag).collectAsState(initial = listOf()).value,
        ) {
            showTagsBottomSheet = false
        }
    }

    if (showUrlBottomSheet) {
        UrlBottomSheet(
            sheetState,
            task.url,
        ) {
            showUrlBottomSheet = false
        }
    }

    Column(
        modifier = Modifier
            .fillMaxWidth()
    ) {
        ElevatedCard(
            modifier = Modifier
                .padding(horizontal = 16.dp, vertical = 16.dp)
                .shadow(elevation = 2.dp, shape = MaterialTheme.shapes.medium)
                .fillMaxWidth(if (isColumnLayout) 1f else 0.8f)
                .align(Alignment.CenterHorizontally),
            colors = CardDefaults.elevatedCardColors(
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.onBackground,
            ),
            elevation = CardDefaults.elevatedCardElevation(0.dp)
        ) {
            Column(
                modifier = Modifier.padding(vertical = 16.dp)
            ) {
                Text(
                    text = task.title,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )

                Text(
                    text = task.description,
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp)
                )

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painterResource(id = R.drawable.group_1),
                            contentDescription = "Members",
                            modifier = Modifier
                                .padding(end = 12.dp)
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
                        Text(
                            text = "Assigned to",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }

                    Row(
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.offset((5 * task.members.size).dp)
                    ) {
                        task.members.take(5).forEachIndexed { index, item ->
                            if (memberList.isNotEmpty()) {
                                val member = memberList.find { it.id == item }!!
                                val isInTeam =
                                    membersOfTeam.find { it.idMember == item }?.isMember!!
                                Box(
                                    modifier = Modifier
                                        .size(30.dp)
                                        .offset(x = (-index * 5).dp)
                                        .background(
                                            if (isInTeam) member.colorBrush
                                            else Brush.linearGradient(
                                                colors = listOf(Gray2, Gray4),
                                            ),
                                            CircleShape
                                        )
                                        .clickable { showMembersBottomSheet = true },
                                    contentAlignment = Alignment.Center
                                )
                                {
                                    RenderProfile(
                                        imageProfile = if (isInTeam) member.userImage else "",
                                        initialsName = if (isInTeam) member.initialsName else "-",
                                        backgroundColor = Color.White,
                                        backgroundBrush = if (isInTeam) member.colorBrush
                                        else Brush.linearGradient(
                                            colors = listOf(Gray2, Gray4),
                                        ),
                                        sizeTextPerson = 10.sp,
                                        sizeImage = 30.dp,
                                        typeProfile = TypeProfileIcon.PERSON
                                    )
                                }
                            }
                        }
                        if (task.members.size > 5) {
                            Box(
                                modifier = Modifier
                                    .size(30.dp)
                                    .offset(-(4 * task.members.size).dp)
                                    .background(
                                        color = Color.LightGray,
                                        CircleShape
                                    )
                                    .clickable { showMembersBottomSheet = true },
                                contentAlignment = Alignment.Center
                            )
                            {
                                Text(
                                    text = "+${task.members.size - 5}",
                                    style = MaterialTheme.typography.headlineLarge,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 10.sp,
                                    color = MaterialTheme.colorScheme.onPrimary,
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painterResource(id = R.drawable.creation_date),
                            contentDescription = "Creation Date",
                            modifier = Modifier
                                .padding(end = 12.dp)
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
                        Text(
                            text = "Creation Date",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                    Text(
                        text = format.format(task.creationDate),
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 15.dp, vertical = 8.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Icon(
                            painterResource(id = R.drawable.expiration_date),
                            contentDescription = "Expiration Date",
                            modifier = Modifier
                                .padding(end = 17.dp)
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
                        Text(
                            text = "Expiration Date",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                    Text(
                        text = format.format(task.dueDate),
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }

                if (isBeforeCurrentDate(task.dueDate) && task.state != State.COMPLETED) {
                    Text(
                        text = "Task is expired and not completed!",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.padding(start = 76.dp, top = 8.dp)
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painterResource(id = R.drawable.state),
                            contentDescription = "State",
                            modifier = Modifier
                                .padding(end = 12.dp)
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
                        Text(
                            text = "State",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                    Row {
                        Text(
                            text = State.valueOf(task.state.name).getStateString(),
                            textAlign = TextAlign.End,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(end = 8.dp),
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .size(20.dp)
                                .background(
                                    State
                                        .valueOf(task.state.name)
                                        .getState()
                                )
                                .border(
                                    1.dp,
                                    MaterialTheme.colorScheme.onSurface,
                                    CircleShape
                                )
                        )
                    }

                }

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painterResource(id = R.drawable.priority),
                            contentDescription = "Priority",
                            modifier = Modifier
                                .padding(end = 12.dp)
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
                        Text(
                            text = "Priority",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                    Text(
                        text = Priority.valueOf(task.priority.name).getPriority(),
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painterResource(id = R.drawable.tag),
                            contentDescription = "Tag",
                            modifier = Modifier
                                .padding(end = 12.dp)
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
                        Text(
                            text = "Tag",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }


                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 70.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        task.tag.forEachIndexed { index, it ->
                            val tag = vmTag.getTagById(it).collectAsState(Tag("", -1)).value
                            Box(
                                if (index != task.tag.size - 1) {
                                    Modifier
                                        .padding(end = 8.dp)
                                        .clickable {
                                            showTagsBottomSheet = true
                                        }
                                } else {
                                    Modifier
                                        .clickable {
                                            showTagsBottomSheet = true
                                        }
                                }
                            ) {
                                Text(
                                    text = tag.name,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.End,
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier
                                        .clip(MaterialTheme.shapes.small)
                                        .border(
                                            1.dp,
                                            color = Gray3,
                                            shape = MaterialTheme.shapes.small
                                        )
                                        .padding(horizontal = 6.dp, vertical = 4.dp)
                                        .clickable { showTagsBottomSheet = true }
                                )
                            }
                        }
                    }
                }

                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface
                )

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painterResource(id = R.drawable.category),
                            contentDescription = "Category",
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .clip(shape = MaterialTheme.shapes.small)
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
                        Text(
                            text = "Category",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                    Text(
                        text = category.name,
                        textAlign = TextAlign.End,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                    )
                }
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painterResource(id = R.drawable.url),
                            contentDescription = "Url",
                            modifier = Modifier
                                .padding(end = 12.dp)
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
                        Text(
                            text = "Url",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }


                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 70.dp),
                        horizontalArrangement = Arrangement.End
                    ) {
                        task.url.forEachIndexed { index, it ->

                            Box(
                                if (index != task.url.size - 1) {
                                    Modifier
                                        .padding(end = 8.dp)
                                        .clickable {
                                            showUrlBottomSheet = true
                                        }
                                } else {
                                    Modifier
                                        .clickable {
                                            showUrlBottomSheet = true
                                        }
                                }
                            ) {
                                Text(
                                    text = it,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    textAlign = TextAlign.End,
                                    fontWeight = FontWeight.SemiBold,
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    modifier = Modifier
                                        .clip(MaterialTheme.shapes.small)
                                        .border(
                                            1.dp,
                                            color = Gray3,
                                            shape = MaterialTheme.shapes.small
                                        )
                                        .padding(horizontal = 6.dp, vertical = 4.dp)
                                        .clickable { showUrlBottomSheet = true }
                                )
                            }
                        }
                    }

                }
                HorizontalDivider(
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                    thickness = 1.dp,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp)
                        .clickable {

                            showAttachmentBottomSheet.value = !showAttachmentBottomSheet.value

                        },
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Start,
                        modifier = Modifier.weight(2f),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            painterResource(id = R.drawable.attach),
                            contentDescription = "Url",
                            modifier = Modifier
                                .padding(end = 12.dp)
                                .size(40.dp)
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
                        Text(
                            text = "Attachments",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                        )
                    }


                    Row(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 70.dp, end = 4.dp),
                        horizontalArrangement = Arrangement.End,
                        verticalAlignment = Alignment.CenterVertically

                    ) {

                        Icon(
                            painter = painterResource(id = if (showAttachmentBottomSheet.value) R.drawable.arrow_up else R.drawable.arrow_down),
                            contentDescription = if (showAttachmentBottomSheet.value) "Arrow Down" else "Arrow Up"
                        )


                    }
                }
                if (showAttachmentBottomSheet.value) {
                    RenderAttachment(
                        attachments = task.attachment,
                        task = task,
                    )
                }


                RenderRating(
                    task = task,
                    memberLogged = memberLogged
                )
            }
        }

        ShowSingleComment(task, vmTeam, comments, isColumnLayout, memberList) {
            showCommentBottomSheet = it
        }

    }
}

@Composable
fun RenderRating(
    task: Task,
    memberLogged: Member
) {
    val colorStar = if (isSystemInDarkTheme()) Color.White else Color.Black
    val sizeStar = 24.dp
    val spacing = 1.dp
    if (task.state == State.COMPLETED) {
        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.onSurface
        )
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    painterResource(id = R.drawable.rating),
                    contentDescription = "Rating",
                    modifier = Modifier
                        .padding(end = 12.dp)
                        .clip(shape = MaterialTheme.shapes.small)
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
                Text(
                    text = "Rating",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                )
            }
            if (task.mapReview.isNotEmpty()) {
                RenderRate(
                    rating = task.review,
                    size = sizeStar,
                    activeColor = colorStar,
                    inactiveColor = colorStar,
                    spacing = spacing
                )
            } else {
                Row {
                    Text(
                        text = "No review found",
                        textAlign = TextAlign.End,
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
        if (task.members.contains(memberLogged.id) &&
            !task.mapReview.containsKey(memberLogged.id)
        ) {
            Text(
                text = "You should leave a review",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier.padding(start = 76.dp, top = 8.dp)
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MembersBottomSheet(
    navController: NavController,
    sheetState: SheetState,
    membersOfTeam: List<TeamMember>,
    members: MutableList<Long>,
    memberList: List<Member>,
    onDismissRequest: () -> Unit,
) {
    var searchText by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    //val memberList by vmMember.getMembers().collectAsState(initial = emptyList())
    if (memberList.isEmpty()) return

    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        tonalElevation = 0.dp,
    ) {
        // Sheet content
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Assigned Members",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                textAlign = TextAlign.Center
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface
            )

            TextField(
                value = searchText,
                onValueChange = { newText -> searchText = newText },
                placeholder = {
                    Text(
                        "Search",
                        color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onBackground else Color.Gray
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, bottom = 16.dp, top = 8.dp)
                    .clip(MaterialTheme.shapes.small),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.onSurface,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon",
                        tint = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onBackground else Color.Gray
                    )
                },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        Icon(
                            imageVector = Icons.Rounded.Clear,
                            contentDescription = "Clear Search",
                            tint = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onBackground else Color.Gray,
                            modifier = Modifier.clickable {
                                searchText = ""
                            }
                        )
                    }
                }
            )

            val filteredMembers = members.filter { item ->
                val member = memberList.find { it.id == item }!!

                member.fullname.contains(searchText, ignoreCase = true)
            }

            Box {
                LazyColumn(
                    modifier = Modifier
                        .padding(bottom = 100.dp, start = 8.dp, end = 8.dp)
                        .simpleVerticalScrollbar(state = listState),
                    state = listState
                ) {
                    items(filteredMembers) { item ->
                        val member = memberList.find { it.id == item }!!
                        val isInTeam = membersOfTeam.find { it.idMember == item }?.isMember!!
                        Row(
                            modifier = Modifier
                                .clickable {
                                    if (isInTeam)
                                        navController.navigate("profile/personalInfo/${member.id}")
                                }
                                .padding(horizontal = 24.dp, vertical = 16.dp)
                                .fillMaxWidth(),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {

                            RenderProfile(
                                imageProfile = if (isInTeam) member.userImage else "",
                                initialsName = if (isInTeam) member.initialsName else "-",
                                backgroundColor = Color.White,
                                backgroundBrush = if (isInTeam) member.colorBrush
                                else Brush.linearGradient(
                                    colors = listOf(Gray2, Gray4),
                                ),
                                sizeTextPerson = 14.sp,
                                sizeImage = 36.dp,
                                typeProfile = TypeProfileIcon.PERSON
                            )

                            Text(
                                text = membersOfTeam.find { it.idMember == item }?.fullname!!,
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.bodyMedium,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Column(
                    modifier = Modifier.align(Alignment.BottomCenter),
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 30.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        onDismissRequest()
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 24.dp, end = 24.dp)
                                .border(
                                    width = 1.dp,
                                    color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.tertiary,
                                    shape = MaterialTheme.shapes.large
                                ),
                            shape = MaterialTheme.shapes.small,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.background,
                                contentColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.tertiary
                            ),
                        ) {
                            Text(
                                text = "Close",
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UrlBottomSheet(
    sheetState: SheetState,
    url: List<String>,
    onDismissRequest: () -> Unit,
) {
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    ModalBottomSheet(
        onDismissRequest = {
            onDismissRequest()
        },
        sheetState = sheetState,
        tonalElevation = 0.dp,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Task Url",
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                textAlign = TextAlign.Center
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface
            )



            Box {
                LazyColumn(
                    modifier = Modifier
                        .padding(bottom = 100.dp, start = 8.dp, end = 8.dp)
                        .simpleVerticalScrollbar(state = listState),
                    state = listState,
                ) {

                    itemsIndexed(url) { index, item ->
                        Row(
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {

                            val uriHandler = LocalUriHandler.current
                            val annotatedText = buildAnnotatedString {
                                withStyle(
                                    style = SpanStyle(
                                        color = MaterialTheme.colorScheme.primary,
                                        fontSize = MaterialTheme.typography.bodyLarge.fontSize,
                                        textDecoration = TextDecoration.Underline,

                                        )
                                ) {
                                    append(item)
                                    addStringAnnotation(
                                        tag = "URL",
                                        annotation = item,
                                        start = 0,
                                        end = "Clicca qui".length
                                    )
                                }
                            }

                            ClickableText(
                                text = annotatedText,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                onClick = { offset ->
                                    annotatedText.getStringAnnotations(
                                        tag = "URL",
                                        start = offset,
                                        end = offset
                                    )
                                        .firstOrNull()?.let { annotation ->
                                            val url = annotation.item
                                            val httpUrl =
                                                if (url.startsWith("http://") || url.startsWith(
                                                        "https://"
                                                    )
                                                ) {
                                                    url
                                                } else {
                                                    "http://$url"
                                                }
                                            uriHandler.openUri(httpUrl)
                                        }
                                }
                            )
                        }
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
                Column(
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 30.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        onDismissRequest()
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 24.dp, end = 24.dp)
                                .border(
                                    width = 1.dp,
                                    color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.tertiary,
                                    shape = MaterialTheme.shapes.large
                                ),
                            shape = MaterialTheme.shapes.small,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.background,
                                contentColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.tertiary
                            ),
                        ) {
                            Text(
                                text = "Close",
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagsBottomSheet(
    sheetState: SheetState,
    tags: List<Tag>,
    onDismissRequest: () -> Unit
) {
    val scope = rememberCoroutineScope()
    val listState = rememberLazyListState()
    ModalBottomSheet(
        onDismissRequest = onDismissRequest,
        sheetState = sheetState,
        tonalElevation = 0.dp,
    ) {
        // Sheet content
        Column(
            modifier = Modifier
                .fillMaxWidth(),
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = "Task Tags",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                textAlign = TextAlign.Center
            )

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Box {
                LazyColumn(
                    modifier = Modifier
                        .padding(bottom = 100.dp, start = 8.dp, end = 8.dp)
                        .simpleVerticalScrollbar(state = listState),
                    state = listState,
                ) {
                    items(tags) { tag ->
                        Row(
                            modifier = Modifier.padding(horizontal = 24.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = tag.name,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis,
                                textAlign = TextAlign.End,
                                fontWeight = FontWeight.SemiBold,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier
                                    .clip(MaterialTheme.shapes.small)
                                    .border(
                                        1.dp,
                                        color = Gray3,
                                        shape = MaterialTheme.shapes.small
                                    )
                                    .padding(horizontal = 6.dp, vertical = 4.dp)
                            )
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Column(
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp, bottom = 30.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(
                            onClick = {
                                scope.launch { sheetState.hide() }.invokeOnCompletion {
                                    if (!sheetState.isVisible) {
                                        onDismissRequest()
                                    }
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 24.dp, end = 24.dp)
                                .border(
                                    width = 1.dp,
                                    color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.tertiary,
                                    shape = MaterialTheme.shapes.large
                                ),
                            shape = MaterialTheme.shapes.small,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.background,
                                contentColor = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.tertiary
                            ),
                        ) {
                            Text(
                                text = "Close",
                                style = MaterialTheme.typography.bodyLarge,
                                color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onBackground else MaterialTheme.colorScheme.tertiary
                            )
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ShowTaskDetails(
    navController: NavController,
    taskId: Long,
    vmTask: TaskViewModel,
    vmTag: TagViewModel,
    vmCategory: CategoryViewModel,
    vmMember: MemberViewModel,
    vmTeam: TeamViewModel,
    memberList: List<Member>,
    memberLogged: Member
) {

    val task by vmTask.task.collectAsState()
    val newAttachment = mutableListOf<Attachment>()
    val showAttachmentBottomSheet = remember { mutableStateOf(false) }
    val context = LocalContext.current
    if (task != null) {
        vmTask.currentTask.value = task as Task

        val scrollState = rememberScrollState()
        var isColumnLayout: Boolean

        val popUpRender = remember { mutableStateOf(false) }
        val popUpAttachment = remember { mutableStateOf(false) }
        val returnedPopUpValue = MutableStateFlow<Boolean?>(null)
        val review = remember {
            mutableFloatStateOf(
                0f
            )
        }
        val returnedPopUpValueAttachment = MutableStateFlow<Boolean?>(null)
        val newfiles = remember { mutableStateOf<List<Uri?>>(listOf()) }
        val files = task!!.attachment
        val fileToAdd = mutableListOf<Attachment>()
        Scaffold(
            topBar = {
                val topBarParameter = TopBarValue(
                    title = "Task Details",
                    backArrow = true,
                    vmTask = vmTask,
                    vmTeam = vmTeam,
                    vmMember = vmMember,
                    popUpRender = popUpRender,
                    popUpAttachment = popUpAttachment,
                )
                TopBar(navController, topBarParameter, memberLogged)
            },
        ) { innerPadding ->
            BoxWithConstraints {
                if (this.maxHeight > this.maxWidth) {
                    isColumnLayout = true
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                            .verticalScroll(scrollState)
                            .background(MaterialTheme.colorScheme.onSurface)
                    ) {
                        TaskDetailsPane(
                            navController,
                            task!!,
                            isColumnLayout,
                            vmTask,
                            vmTag,
                            vmCategory,
                            vmTeam,
                            showAttachmentBottomSheet,
                            memberList,
                            memberLogged
                        )
                    }
                } else {
                    isColumnLayout = false
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(innerPadding)
                            .verticalScroll(scrollState)
                            .background(MaterialTheme.colorScheme.onSurface)
                    ) {
                        TaskDetailsPane(
                            navController = navController,
                            task = task!!,
                            isColumnLayout = isColumnLayout,
                            vmTask = vmTask,
                            vmTag = vmTag,
                            vmCategory = vmCategory,
                            vmTeam = vmTeam,
                            memberList = memberList,
                            memberLogged = memberLogged,
                            showAttachmentBottomSheet = showAttachmentBottomSheet
                        )
                    }
                }
                if (popUpRender.value) {
                    RenderPopUpReview(popUpRender, returnedPopUpValue, review)
                    LaunchedEffect(returnedPopUpValue) {
                        returnedPopUpValue.collect { value ->
                            if (value == true) {
                                vmTask.updateReviewByTaskId(taskId, review.floatValue)
                            }
                        }
                    }
                }

                if (popUpAttachment.value) {
                    AttachmentSelection(newfiles) {
                        popUpAttachment.value = false
                        showAttachmentBottomSheet.value = false
                        for (file in newfiles.value) {
                            if (file != null) {
                                newAttachment.add(calculateAttachment(context, file))
                            }
                        }
                        vmTask.addAttachmentFromButton((task as Task), newAttachment, memberLogged.id)

                    }
                }
            }
        }
    }
}