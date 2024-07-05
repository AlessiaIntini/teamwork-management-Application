package it.polito.teamhub.ui.view.component


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.BadgedBox
import androidx.compose.material.BottomNavigation
import androidx.compose.material.BottomNavigationItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import it.polito.teamhub.R
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.ui.theme.PurpleBlue
import it.polito.teamhub.ui.theme.linearGradient
import it.polito.teamhub.viewmodel.ChatViewModel


@Composable
fun BottomBar(
    navController: NavController,
    vmChat: ChatViewModel,
    memberLogged: Member
) {
    val onSurfaceColor = MaterialTheme.colorScheme.onSurface
    val items = listOf(
        "home" to "Home",
        "tasks" to "Tasks",
        "calendar" to "Calendar",
        "chat" to "Chat",
        "profile" to "Profile"
    )
    BottomNavigation(
        backgroundColor = MaterialTheme.colorScheme.background,
        modifier = Modifier
            .drawWithContent {
                drawContent()
                drawLine(
                    color = onSurfaceColor,
                    start = Offset(x = 0f, y = 0f),
                    end = Offset(x = size.width, y = 0f),
                    strokeWidth = 1.dp.toPx()
                )
            }
            .shadow(elevation = 10.dp),
    ) {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route ?: "home"
        items.forEach { (route, title) ->
            val isSelected = currentRoute == route
            BottomNavigationItem(
                icon = {
                    when (route) {
                        "home" -> Icon(
                            painterResource(R.drawable.home),
                            contentDescription = "Home",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.iconLabelModifier(isSelected)
                        )

                        "tasks" -> Icon(
                            painterResource(R.drawable.tasks),
                            contentDescription = "Tasks",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.iconLabelModifier(isSelected)
                        )

                        "calendar" -> Icon(
                            painterResource(R.drawable.calendar),
                            contentDescription = "Calendar",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.iconLabelModifier(isSelected)
                        )

                        "chat" -> {

                            val count =
                                vmChat.getNotification(memberLogged.id).collectAsState(-1)
                            if (count.value.toInt() != -1) {
                                BadgedBox(
                                    badge = {
                                        if (count.value > 0) {

                                            Box(
                                                modifier = Modifier
                                                    .padding(end = 8.dp, top = 8.dp)
                                                    .size(22.dp)
                                                    .background(PurpleBlue, CircleShape),
                                                contentAlignment = Alignment.Center,
                                            ) {
                                                Text(
                                                    "${count.value}",
                                                    color = MaterialTheme.colorScheme.onPrimary,
                                                    style = MaterialTheme.typography.labelMedium
                                                )

                                            }

                                        }
                                    }


                                ) {

                                    Icon(
                                        painterResource(R.drawable.chat),
                                        contentDescription = "Chat",
                                        tint = MaterialTheme.colorScheme.onBackground,
                                        modifier = Modifier.iconLabelModifier(isSelected)
                                    )
                                }

                            }

                            Icon(
                                painterResource(R.drawable.chat),
                                contentDescription = "Chat",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier.iconLabelModifier(isSelected)
                            )

                        }

                        "profile" -> Icon(
                            painterResource(R.drawable.profile),
                            contentDescription = "Profile",
                            tint = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.iconLabelModifier(isSelected)
                        )
                    }
                },
                label = {
                    when (title) {
                        "Home" -> Text(
                            text = "Home",
                            fontSize = 9.5.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.iconLabelModifier(isSelected)
                        )

                        "Tasks" -> Text(
                            text = "My Tasks",
                            fontSize = 9.5.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.iconLabelModifier(isSelected)
                        )

                        "Calendar" -> Text(
                            text = "Calendar",
                            fontSize = 9.5.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.iconLabelModifier(isSelected)
                        )

                        "Chat" -> Text(
                            text = "Chat",
                            fontSize = 9.5.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.iconLabelModifier(isSelected)
                        )

                        "Profile" -> Text(
                            text = "Profile",
                            fontSize = 9.5.sp,
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.labelSmall,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.iconLabelModifier(isSelected)
                        )
                    }

                },
                alwaysShowLabel = true,
                selected = currentRoute == route,
                onClick = {
                    navController.navigate(route)
                },
            )
        }
    }
}

fun Modifier.iconLabelModifier(isSelected: Boolean): Modifier {
    return this.then(
        if (isSelected) {
            graphicsLayer(alpha = 0.99f)
                .drawWithCache {
                    onDrawWithContent {
                        drawContent()
                        drawRect(
                            linearGradient,
                            blendMode = BlendMode.SrcAtop
                        )
                    }
                }
        } else {
            Modifier
        }
    )
}
