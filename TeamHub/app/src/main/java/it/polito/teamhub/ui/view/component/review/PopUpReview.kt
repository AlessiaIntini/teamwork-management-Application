package it.polito.teamhub.ui.view.component.review

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableFloatState
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import kotlinx.coroutines.flow.MutableStateFlow


@Composable
fun RenderPopUpReview(
    dismissValue: MutableState<Boolean>,
    returnedPopUpVale: MutableStateFlow<Boolean?>,
    review: MutableFloatState
) {
    val question = listOf(
        "Did you get along well with your colleagues?",
        "Are you satisfied with the work?",
        "Was the time provided to complete the task proportional to its complexity?",
    )
    val finalRate: List<MutableState<Int>> = remember {
        List(question.size) { mutableIntStateOf(0) }
    }
    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = {
            dismissValue.value = false
        },
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Column(
                modifier = Modifier
                    .padding(8.dp, 8.dp)
                    .verticalScroll(scrollState)
            ) {
                RenderTitle(dismissValue)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier
                            .padding(bottom = 15.dp, start = 15.dp, end = 15.dp)
                    ) {
                        for (index in question.indices) {
                            finalRate[index].value = question(question[index])
                        }
                        review.floatValue = finalRate.sumOf { it.value }
                            .toFloat() / question.size.toFloat()
                    }
                }
                Row(
                    modifier = Modifier
                        .padding(bottom = 10.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Button(
                        onClick = {
                            dismissValue.value = false
                            returnedPopUpVale.value = false
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.surface,
                            contentColor = MaterialTheme.colorScheme.onBackground
                        ),
                        border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground)
                    ) {
                        Text("Do later")
                    }
                    Button(
                        onClick = {
                            dismissValue.value = false
                            returnedPopUpVale.value = true
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary,
                        )
                    ) {
                        Text("Done")
                    }
                }
            }
        }
    }
}


@Composable
fun RenderTitle(
    dismissValue: MutableState<Boolean>
) {
    Box(
        contentAlignment = Alignment.TopStart,
    ) {
        IconButton(
            onClick = { dismissValue.value = false },
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(top = 5.dp)
        ) {
            Icon(
                imageVector = Icons.Rounded.Close,
                contentDescription = "Close Icon",
                tint = MaterialTheme.colorScheme.onBackground
            )
        }
        Row(
            modifier = Modifier
                .padding(top = 10.dp)
                .fillMaxWidth()
                .align(Alignment.Center),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Task Completed",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.titleSmall,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun question(
    text: String
): Int {
    val clicked = remember { mutableIntStateOf(0) }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Justify,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onBackground,
            fontWeight = FontWeight.Normal
        )
    }
    Row(
        modifier = Modifier
            .padding(bottom = 8.dp)
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        RenderClickableStar(
            clicked = clicked,
            spacing = 10.dp,
            size = 35.dp
        )
    }
    return clicked.intValue
}
