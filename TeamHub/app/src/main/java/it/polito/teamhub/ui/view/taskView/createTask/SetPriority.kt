package it.polito.teamhub.ui.view.taskView.createTask

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.DropdownMenuItem
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import it.polito.teamhub.R
import it.polito.teamhub.dataClass.task.Priority
import it.polito.teamhub.dataClass.task.listOfPriority
import it.polito.teamhub.ui.theme.CustomFontFamily
import it.polito.teamhub.ui.theme.linearGradient
import it.polito.teamhub.viewmodel.TaskViewModel

@Composable
fun SetPriority(vmTask: TaskViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start,
            modifier = Modifier.weight(.5f)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.priority),
                contentDescription = "Priority icon",
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
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(.5f)
        ) {
            PriorityDropdownMenu(vmTask)
        }
    }

    Divider(
        color = MaterialTheme.colorScheme.onSurface,
        thickness = 1.dp,
    )
}

@Composable
fun PriorityDropdownMenu(vmTask: TaskViewModel) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOfPriority()
    var textFieldSize by remember { mutableStateOf(Size.Zero) }

    val icon = if (expanded)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown

    Column {
        OutlinedTextField(
            value = Priority.valueOf(vmTask.priority.name).getPriority(),
            textStyle = TextStyle(
                fontWeight = FontWeight.SemiBold,
                fontFamily = CustomFontFamily,
                fontSize = 13.sp,
                lineHeight = 20.sp,
                letterSpacing = 0.5.sp
            ),
            onValueChange = { newValue -> vmTask.updatePriority(Priority.valueOf(newValue)) },
            readOnly = true,
            modifier = Modifier
                .onGloballyPositioned { coordinates ->
                    //This value is used to assign to the DropDown the same width
                    textFieldSize = coordinates.size.toSize()
                },
            trailingIcon = {
                androidx.compose.material3.Icon(icon, "contentDescription",
                    Modifier.clickable { expanded = !expanded })
            },
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.background,
                unfocusedContainerColor = MaterialTheme.colorScheme.background,
                focusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedBorderColor = MaterialTheme.colorScheme.primary,
                unfocusedTextColor = MaterialTheme.colorScheme.primary,
                focusedTextColor = MaterialTheme.colorScheme.primary,
            ),
            shape = RoundedCornerShape(10.dp),
        )
        DropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false },
            modifier = Modifier
                .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
        ) {
            options.forEachIndexed { index, label ->
                DropdownMenuItem(onClick = {
                    vmTask.updatePriority(Priority.valueOf(label))
                    expanded = false
                }) {
                    Text(
                        text = Priority.valueOf(label).getPriority(),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.padding(start = 16.dp)
                    )
                }
            }
        }
    }
}



