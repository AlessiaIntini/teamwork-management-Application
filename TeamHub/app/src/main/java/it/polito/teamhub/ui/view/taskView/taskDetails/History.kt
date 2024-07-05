package it.polito.teamhub.ui.view.taskView.taskDetails


import androidx.compose.foundation.Canvas
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.dataClass.task.Action
import it.polito.teamhub.dataClass.task.Task
import it.polito.teamhub.ui.theme.Gray2
import it.polito.teamhub.ui.theme.Gray4
import it.polito.teamhub.ui.theme.PurpleBlue
import it.polito.teamhub.ui.view.component.topBar.TopBar
import it.polito.teamhub.ui.view.component.topBar.TopBarValue
import it.polito.teamhub.utils.RotatingScallopedProfilePic
import it.polito.teamhub.utils.getDate
import it.polito.teamhub.viewmodel.TaskViewModel
import it.polito.teamhub.viewmodel.TeamViewModel
import java.util.Locale

@Composable
fun History(
    navController: NavController,
    vmTask: TaskViewModel,
    vmTeam: TeamViewModel,
    memberList: List<Member>,
    memberLogged: Member
) {
    val task by vmTask.task.collectAsState()
    Scaffold(
        topBar = {
            val topBarParameter = TopBarValue(
                title = "Task History",
                backArrow = true,
                vmTask = vmTask,
            )
            TopBar(navController, topBarParameter, memberLogged)
        },
    ) { innerPadding ->
        BoxWithConstraints {
            if (this.maxHeight > this.maxWidth) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(innerPadding)
                ) {
                    HistoryContent(vmTeam, task!!, memberList)
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
                        HistoryContent( vmTeam, task!!, memberList)
                    }
                }
            }
        }
    }
}

@Composable
fun HistoryContent(
    vmTeam: TeamViewModel,
    task: Task,
    memberList: List<Member>
) {
    if (memberList.isEmpty()) return
    val teamId = task.idTeam
    val teamList by vmTeam.teamList.collectAsState()
    if (teamList.isEmpty()) return
    val membersTeam = teamList.find { it.id == teamId }?.members
    val reverseH = task.histories.reversed()

    LazyColumn(
        modifier = Modifier
            .padding(start = 20.dp, top = 30.dp, end = 10.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        val formatter = android.icu.text.SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        if (reverseH.isNotEmpty()) {
            itemsIndexed(reverseH) { _, item ->
                val author = memberList.find { it.id == item.author }!!
                val fullname = membersTeam?.find { it.idMember == item.author }!!.fullname
                val isMember = membersTeam.find { it.idMember == item.author }!!.isMember
                Row(
                    modifier = Modifier
                        .padding(end = 20.dp)
                        .fillMaxWidth(1f)
                ) {
                    if (isMember) {

                        RotatingScallopedProfilePic(
                            author.initialsName,
                            author.colorBrush,
                            author.userImage,
                        )
                    } else {
                        RotatingScallopedProfilePic(
                            "-",
                            Brush.linearGradient(
                                colors = listOf(Gray2, Gray4),
                            ),
                            "",
                        )
                    }
                    Canvas(
                        modifier = Modifier
                            .padding(start = 20.dp, top = 10.dp)
                            .fillMaxHeight()
                    ) {
                        drawLine(
                            color = PurpleBlue,
                            start = Offset(x = size.width / 2, y = 110f),
                            end = Offset(x = size.width / 2, y = size.height),
                            strokeWidth = 5f
                        )
                    }
                    Column(
                        modifier = Modifier
                            .padding(start = 10.dp)
                            .weight(1f)
                    )
                    {
                        Text(
                            text = fullname,
                            style = MaterialTheme.typography.bodyLarge,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(end = 10.dp),
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        if (item.action == Action.UPDATE_MEMBERS) {
                            val removedFullnames = mutableListOf<String>()
                            val addedFullnames = mutableListOf<String>()
                            item.removedMembers.forEach { id -> removedFullnames.add(membersTeam.find { it.idMember == id }!!.fullname) }
                            item.addedMembers.forEach { id -> addedFullnames.add(membersTeam.find { it.idMember == id }!!.fullname) }
                            val description =
                                item.action.getUpdatedMembers(removedFullnames, addedFullnames)
                            Text(
                                text = description,
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        } else {
                            Text(
                                text = item.description,
                                color = MaterialTheme.colorScheme.onBackground,
                                style = MaterialTheme.typography.bodyMedium
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                    }
                    val date = getDate(formatter.format(item.date))
                    Text(
                        text = date,
                        modifier = Modifier
                            .padding(start = 8.dp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }
            }
        }
    }
}
