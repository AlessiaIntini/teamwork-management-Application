package it.polito.teamhub.ui.view.component.card

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.teamhub.R
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.dataClass.task.State
import it.polito.teamhub.dataClass.task.Task
import it.polito.teamhub.dataClass.team.TeamMember
import it.polito.teamhub.ui.theme.Gray2
import it.polito.teamhub.ui.theme.Gray4
import it.polito.teamhub.ui.view.component.profileIcon.RenderProfile
import it.polito.teamhub.ui.view.component.profileIcon.TypeProfileIcon

@Composable
fun RenderTitle(
    title: String
) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = Color.White,
            modifier = Modifier
                .weight(1f)
                .padding(top = 4.dp)
        )
    }
}


@Composable
fun RenderDescription(
    description: String
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 8.dp, vertical = 8.dp)
    ) {
        Text(
            text = description,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            color = Color.White,
            style = MaterialTheme.typography.bodyMedium
        )
    }
}

@Composable
fun ExpiredDate(
    day: String,
    month: String
) {
    Box(
        modifier = Modifier
            .clip(shape = RoundedCornerShape(10.dp))
            .background(Color(0x60FB413A))
            .padding(vertical = 4.dp, horizontal = 5.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Image(
                painterResource(R.drawable.expiration_date_coloured),
                contentDescription = "Due date Icon",
                modifier = Modifier
                    .size(30.dp)
                    .padding(end = 6.dp)
            )
            Text(
                text = "$day $month",
                style = MaterialTheme.typography.bodySmall,
                color = Color.White,
            )
        }
    }
}

@Composable
fun NotExpiredDate(
    day: String,
    month: String
) {
    Icon(
        painterResource(R.drawable.expiration_date),
        contentDescription = "Due date Icon",
        tint = Color.White,
        modifier = Modifier
            .size(30.dp)
            .padding(end = 6.dp)
    )
    Text(
        text = "$day $month",
        style = MaterialTheme.typography.bodySmall,
        color = Color.White,
    )
}

@Composable
fun RenderMember(
    task: Task,
    membersOfTeam: MutableList<TeamMember>,
    memberList: List<Member>
) {
    //val memberList by vmMember.getMembers().collectAsState(initial = emptyList())
    if (memberList.isEmpty()) return

    task.members.take(2).forEachIndexed { index, item ->
        val member = memberList.find { it.id == item } ?: return

        val isInTeam = membersOfTeam.find { it.idMember == item }?.isMember!!

        Box(
            modifier = Modifier
                .size(30.dp)
                .offset(x = (-index * 3).dp)
                .background(
                    if (task.state == State.COMPLETED && !isInTeam)
                        Brush.linearGradient(
                            colors = listOf(Gray2, Gray4),
                        )
                    else member.colorBrush,
                    CircleShape
                )
                .border(
                    .5.dp,
                    Color.White,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            RenderProfile(
                imageProfile = if (task.state == State.COMPLETED && !isInTeam) "" else member.userImage,
                initialsName = if (task.state == State.COMPLETED && !isInTeam) "-" else member.initialsName,
                backgroundColor = Color.White,
                backgroundBrush =
                if (task.state == State.COMPLETED && !isInTeam)
                    Brush.linearGradient(
                        colors = listOf(Gray2, Gray4),
                    )
                else member.colorBrush,
                sizeTextPerson = 10.sp,
                sizeImage = 30.dp,
                typeProfile = TypeProfileIcon.PERSON
            )
        }
    }
    if (task.members.size > 2) {
        Box(
            modifier = Modifier
                .size(30.dp)
                .offset(-(3 * 2).dp)
                .background(
                    color = Color.LightGray,
                    CircleShape
                )
                .border(
                    .5.dp,
                    Color.White,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        )
        {
            Text(
                text = "+${task.members.size - 2}",
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.onPrimary,
            )
        }
    }
}