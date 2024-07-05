package it.polito.teamhub.ui.view.component.profileIcon

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import it.polito.teamhub.R

@Composable
fun RenderProfile(
    imageProfile: String,
    initialsName: String? = null,
    backgroundColor: Color,
    backgroundBrush: Brush? = null,
    sizeTextPerson: TextUnit,
    sizeImage: Dp,
    typeProfile: TypeProfileIcon
) {
    Box {
        if (imageProfile == "") {
            when (typeProfile) {
                TypeProfileIcon.TEAM -> {
                    Box(
                        modifier = Modifier
                            .background(backgroundColor, CircleShape)
                            .align(Alignment.Center)
                            .size(sizeImage),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            painterResource(R.drawable.group_2),
                            contentDescription = "Team Icon",
                            modifier = Modifier
                                .background(backgroundColor, CircleShape)
                                .padding(start = 5.dp, end = 5.dp, bottom = 3.dp)
                                .size(45.dp)
                                .align(Alignment.Center),
                            tint = Color.White
                        )
                    }
                }

                TypeProfileIcon.PERSON -> {
                    Box(
                        modifier = Modifier
                            .size(sizeImage)
                            .background(brush = backgroundBrush!!, shape = CircleShape)
                            .align(Alignment.Center),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = initialsName!!,
                            style = MaterialTheme.typography.headlineLarge,
                            fontWeight = FontWeight.Medium,
                            fontSize = sizeTextPerson,
                            color = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            }
        } else {
            AsyncImage(
                model = Uri.parse(imageProfile),
                contentDescription = "User image",
                placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .clip(CircleShape)
                    .size(sizeImage)
                    .align(Alignment.Center)
            )
        }
    }
}