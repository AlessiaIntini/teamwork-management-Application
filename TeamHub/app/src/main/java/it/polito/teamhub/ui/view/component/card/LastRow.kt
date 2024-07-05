package it.polito.teamhub.ui.view.component.card

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import it.polito.teamhub.R
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.dataClass.task.Action
import it.polito.teamhub.dataClass.task.History
import it.polito.teamhub.dataClass.task.Task
import it.polito.teamhub.dataClass.team.TeamMember
import it.polito.teamhub.ui.theme.PurpleBlue
import it.polito.teamhub.ui.view.component.review.RenderPopUpReview
import it.polito.teamhub.ui.view.component.review.RenderRate
import it.polito.teamhub.viewmodel.TaskViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import okhttp3.internal.format
import java.util.Date

@Composable
fun RenderLastRowCompleted(
    item: Task,
    vmTask: TaskViewModel,
    membersOfTeam: MutableList<TeamMember>,
    memberList: List<Member>,
    memberLogged: Member
) {
    val popUpRender = remember { mutableStateOf(false) }
    val returnedPopUpValue = MutableStateFlow<Boolean?>(null)
    val review = remember {
        mutableFloatStateOf(
            0f
        )
    }
    val colorStar = Color.White
    val sizeStar = 30.dp
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Box(
                modifier = if (item.members.contains(memberLogged.id) && !item.mapReview.containsKey(
                        memberLogged.id
                    )
                )
                    Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            BorderStroke(1.dp, Color.White),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .fillMaxWidth(.6f)
                        .height(35.dp)
                        .background(Color.White)
                        .clickable { popUpRender.value = true }
                else Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .border(
                        BorderStroke(1.dp, Color.White),
                        shape = RoundedCornerShape(8.dp)
                    )
                    .fillMaxWidth(.6f)
                    .height(35.dp),
                contentAlignment = Alignment.Center
            ) {
                if (item.mapReview.isNotEmpty() && (item.mapReview.containsKey(memberLogged.id) || !item.members.contains(
                        memberLogged.id
                    ))
                ) {
                    RenderRate(
                        rating = item.review,
                        size = sizeStar,
                        activeColor = colorStar,
                        inactiveColor = colorStar
                    )
                } else {
                    if (item.members.contains(memberLogged.id) && !item.mapReview.containsKey(
                            memberLogged.id
                        )
                    ) {
                        // show button to leave a review
                        Row(
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.leave_review),
                                contentDescription = "Leave a review icon",
                                tint = PurpleBlue
                            )
                            Text(
                                text = "Leave a review",
                                color = PurpleBlue,
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier
                                    .padding(start = 6.dp)
                            )
                        }
                    } else {
                        // none has reviewed the task
                        Row(
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "No review found",
                                textAlign = TextAlign.Center,
                                color = Color.White,
                                style = MaterialTheme.typography.labelLarge,
                                modifier = Modifier
                                    .padding(start = 4.dp)
                                    .fillMaxWidth()
                            )
                        }
                    }
                }
            }
            Row(
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier
                    .offset((3 * if (item.members.size < 3) (item.members.size - 1) else 2).dp)
                    .padding(end = 6.dp)
                    .weight(.3f)
            ) {
                RenderMember(item, membersOfTeam, memberList)
            }
        }
    }
    if (popUpRender.value) {
        RenderPopUpReview(popUpRender, returnedPopUpValue, review)
        LaunchedEffect(returnedPopUpValue) {
            returnedPopUpValue.collect { value ->
                if (value == true) {
                    val task = item.copy(
                        mapReview = (item.mapReview + Pair(
                            memberLogged.id,
                            review.floatValue
                        )).toMutableMap(),
                        histories = (item.histories + History(
                            author = memberLogged.id,
                            action = Action.ADD_REVIEW,
                            date = Date(),
                            description = Action.ADD_REVIEW.getAction(
                                format(
                                    "%.2f",
                                    review.floatValue
                                )
                            )
                        )).toMutableList()
                    )
                    task.id = item.id
                    vmTask.currentTask.value = task
                    vmTask.updateTask(id = item.id)
                }
            }
        }
    }
}