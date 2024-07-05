package it.polito.teamhub.ui.view.component


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import it.polito.teamhub.ui.theme.linearGradient

@Composable
fun FloatingActionButtonWithoutBottomBar(navController: NavController, teamId: Long=-1, addTeam: Boolean = false) {
    val isDuplicate = false
    FloatingActionButton(
        onClick = { if(addTeam)navController.navigate("team/create") else navController.navigate("team/$teamId/tasks/create/${isDuplicate}") },
        elevation = FloatingActionButtonDefaults.elevation(0.dp),
        containerColor = Color.Transparent,
        shape = CircleShape,
        modifier = Modifier
            .background(brush = linearGradient, CircleShape)
    ) {
        Icon(
            imageVector = Icons.Default.Add,
            contentDescription = "Add",
            tint = Color.White,
            modifier = Modifier.size(32.dp)
        )
    }
}