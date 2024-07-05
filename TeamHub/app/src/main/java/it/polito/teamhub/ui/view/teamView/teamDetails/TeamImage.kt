package it.polito.teamhub.ui.view.teamView.teamDetails

import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import it.polito.teamhub.R

@Composable
fun TeamImage(imageTeam: String?, defaultImage: Int, color: Color) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        if (imageTeam.isNullOrBlank()) {
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .background(color = color, shape = CircleShape)
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            )
            {
                Icon(
                    painterResource(defaultImage),
                    contentDescription = "Team Icon",
                    modifier = Modifier
                        .padding(start = 5.dp, end = 5.dp, bottom = 3.dp)
                        .size(70.dp)
                        .align(Alignment.Center),
                    tint = Color.White
                )
            }
        } else {
            AsyncImage(
                model = Uri.parse(imageTeam),
                contentDescription = "User image",
                placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .align(Alignment.Center)
            )
        }

    }
}