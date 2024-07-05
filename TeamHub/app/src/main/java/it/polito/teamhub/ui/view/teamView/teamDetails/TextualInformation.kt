package it.polito.teamhub.ui.view.teamView.teamDetails

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

@Composable
fun TextInfo(
    name: String,
    description: String,
    nMembers: Int,
) {
    Text(
        text = name,
        style = MaterialTheme.typography.headlineLarge,
        modifier = Modifier.padding(bottom = 2.dp, top = 6.dp),
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
    )
    Text(
        text = "$nMembers members",
        style = MaterialTheme.typography.bodyLarge,
        color = MaterialTheme.colorScheme.tertiary,
        modifier = Modifier.padding(bottom = 12.dp),
        overflow = TextOverflow.Ellipsis,
        maxLines = 1,
    )
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .padding(bottom = 5.dp)
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface,
                shape = RoundedCornerShape(16.dp)
            )
    ) {
        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            overflow = TextOverflow.Ellipsis,
            maxLines = 5,
        )
    }


}