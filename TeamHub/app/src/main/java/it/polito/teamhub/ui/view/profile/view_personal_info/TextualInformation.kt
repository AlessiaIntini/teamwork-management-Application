package it.polito.teamhub.ui.view.profile.view_personal_info

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.polito.teamhub.R
import it.polito.teamhub.dataClass.member.Gender
import it.polito.teamhub.dataClass.task.State
import it.polito.teamhub.ui.theme.linearGradient
import it.polito.teamhub.viewmodel.TaskViewModel
import it.polito.teamhub.viewmodel.TeamViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MainInfo(
    fullName: String,
    jobTitle: String,
    description: String,
) {
    Text(
        text = fullName,
        style = MaterialTheme.typography.headlineLarge,
        modifier = Modifier.padding(bottom = 2.dp, top = 6.dp)
    )

    Text(
        text = jobTitle,
        style = MaterialTheme.typography.headlineSmall,
        color = MaterialTheme.colorScheme.tertiary,
        modifier = Modifier.padding(bottom = 12.dp)
    )

    Text(
        text = description,
        style = MaterialTheme.typography.bodyLarge,
        fontStyle = FontStyle.Italic,
        textAlign = TextAlign.Center,
        modifier = Modifier.fillMaxWidth(.9f)
    )
}

@Composable
fun PersonalStats(
    navController: NavController,
    userId: Long,
    vmTask: TaskViewModel,
    vmTeam: TeamViewModel,
) {
    val taskList by vmTask.getTasks().collectAsState(initial = listOf())
    val teamList by vmTeam.teamList.collectAsState()
    val nTeams = teamList.count { it -> it.members.any { it.idMember == userId } }.toString()
    val nTasksCompleted =
        taskList.count { it.members.contains(userId) && it.state == State.COMPLETED }.toString()
    val completedOnTime = taskList.count {
        it.members.contains(userId) && it.state == State.COMPLETED && it.dueDate >= it.histories.last().date
    }.toString()

    Card(
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primary
        ),
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp, 10.dp, 6.dp, 8.dp)
        ) {

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(.3f)
            ) {
                Text(
                    "Tasks completed",
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = nTasksCompleted,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(.2f)
            ) {
                Text(
                    "Teams",
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = nTeams,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )

            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(.3f)
            ) {
                Text(
                    "Completed on time",
                    style = MaterialTheme.typography.labelMedium,
                    textAlign = TextAlign.Center
                )
                Text(
                    text = completedOnTime,
                    style = MaterialTheme.typography.headlineMedium,
                    textAlign = TextAlign.Center
                )
            }
        }

        HorizontalDivider(thickness = 0.5.dp, color = Color.White)

        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(4.dp, 10.dp, 6.dp, 8.dp)
        ) {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier
                    .weight(.3f)
                    .fillMaxSize()
                    .clickable {
                        navController.navigate("profile/${userId}/personalStats")
                    }
            ) {
                Text(
                    text = AnnotatedString("View more statistics"),
                    style = MaterialTheme.typography.bodyMedium.copy(color = Color.White),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
fun AdditionalPersonalInfo(
    nickname: String,
    email: String,
    location: String,
    phoneNumber: String?,
    birthDate: Date?,
    gender: Gender
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                painterResource(id = R.drawable.person),
                contentDescription = "Nickname",
                modifier = Modifier
                    .padding(end = 8.dp)
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
                text = "Nickname",
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Text(
            text = nickname,
            textAlign = TextAlign.End,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyLarge
        )
    }

    HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp, top = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                painterResource(id = R.drawable.mail),
                contentDescription = "Email",
                modifier = Modifier
                    .padding(end = 8.dp)
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
                text = "Email",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Text(
            text = email,
            textAlign = TextAlign.End,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyLarge
        )
    }

    HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp, top = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                painterResource(id = R.drawable.location_on),
                contentDescription = "Location",
                modifier = Modifier
                    .padding(end = 8.dp)
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
                text = "Location",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        Text(
            text = location,
            textAlign = TextAlign.End,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyLarge
        )
    }

    HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp, top = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                painterResource(id = R.drawable.call),
                contentDescription = "Phone Number",
                modifier = Modifier
                    .padding(end = 8.dp)
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
                text = "Phone Number",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        if (!phoneNumber.isNullOrBlank()) {
            Text(
                text = phoneNumber,
                textAlign = TextAlign.End,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }

    HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 20.dp, top = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                painterResource(id = R.drawable.cake),
                contentDescription = "Birth Date",
                modifier = Modifier
                    .padding(end = 8.dp)
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
                text = "Birth Date",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyLarge
            )
        }
        val format = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
        if (birthDate != null) {
            Text(
                text = format.format(birthDate),
                textAlign = TextAlign.End,
                fontWeight = FontWeight.SemiBold,
                style = MaterialTheme.typography.bodyLarge
            )
        }
    }

    HorizontalDivider(thickness = 1.dp, color = MaterialTheme.colorScheme.onSurface)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 20.dp, bottom = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            horizontalArrangement = Arrangement.Start
        ) {
            Icon(
                painterResource(id = R.drawable.male),
                contentDescription = "Gender",
                modifier = Modifier
                    .padding(end = 8.dp)
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
                text = "Gender",
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.bodyLarge
            )
        }

        Text(
            text = if (gender == Gender.PREFER_NOT_TO_SAY) "" else gender.getGenderString(),
            textAlign = TextAlign.End,
            fontWeight = FontWeight.SemiBold,
            style = MaterialTheme.typography.bodyLarge
        )

    }
}