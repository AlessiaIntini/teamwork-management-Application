package it.polito.teamhub.ui.view.taskView.createTask

import android.net.Uri
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import it.polito.teamhub.R
import it.polito.teamhub.dataClass.task.Attachment
import it.polito.teamhub.ui.theme.Gray3
import it.polito.teamhub.ui.theme.linearGradient
import it.polito.teamhub.ui.view.component.attachment.AttachmentSelection
import it.polito.teamhub.ui.view.component.simpleVerticalScrollbar
import it.polito.teamhub.ui.view.taskView.taskDetails.ShowAttachment
import it.polito.teamhub.utils.attachment.calculateAttachment
import it.polito.teamhub.viewmodel.TaskViewModel
import kotlinx.coroutines.launch


@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetAttachments(vmTask: TaskViewModel) {
    val newfiles = remember { mutableStateOf<List<Uri?>>(listOf()) }
    val newAttachment = mutableListOf<Attachment>()
    val showAttachmentSelection = remember { mutableStateOf(false) }
    val context = LocalContext.current

    var showAttachmentBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(showAttachmentBottomSheet)
    val scope = rememberCoroutineScope()

    if (showAttachmentSelection.value) {
        AttachmentSelection(newfiles) {
            showAttachmentSelection.value = false
            for (file in newfiles.value) {
                if (file != null) {
                    newAttachment.add(calculateAttachment(context, file))
                }
            }
            vmTask.addAttachment(newAttachment)
        }
    }
    LaunchedEffect(key1 = showAttachmentBottomSheet) {
        if (showAttachmentBottomSheet) {
            scope.launch {
                sheetState.show()
            }
        } else {
            scope.launch {
                sheetState.hide()
            }
        }
    }
    if (showAttachmentBottomSheet) {
        AttachmentBottomSheet(
            vmTask = vmTask,
            sheetState = sheetState,
            onDismissRequest = {
                showAttachmentBottomSheet = false
            }
        )
    }

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
            modifier = Modifier
                .weight(.6f)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.attach),
                contentDescription = "Tags icon",
                modifier = Modifier
                    .size(44.dp)
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
                text = "Attachments",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(.4f)
        ) {
            if (vmTask.attachments.size == 0) {
                Button(
                    onClick = {
                        showAttachmentSelection.value = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.onBackground,
                    ),
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(50.dp)
                        )
                        .clip(RoundedCornerShape(50.dp))
                ) {
                    Text(
                        text = "Select File",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else {
                vmTask.attachments.forEachIndexed { index, it ->
                    Box(
                        if (index != vmTask.url.size - 1) {
                            Modifier
                                .padding(end = 8.dp)
                                .clickable {
                                    showAttachmentBottomSheet = true
                                }
                        } else {
                            Modifier
                                .clickable {
                                    showAttachmentBottomSheet = true
                                }
                        }
                    ) {
                        Text(
                            text = it.name,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.End,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.small)
                                .border(
                                    1.dp,
                                    color = Gray3,
                                    shape = MaterialTheme.shapes.small
                                )
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                                .clickable { showAttachmentBottomSheet = true }
                        )

                    }
                }
            }
        }
    }

    HorizontalDivider(
        color = MaterialTheme.colorScheme.onSurface,
        thickness = 1.dp,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetUrl(vmTask: TaskViewModel) {
    var showUrlBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(showUrlBottomSheet)
    val scope = rememberCoroutineScope()

    LaunchedEffect(key1 = showUrlBottomSheet) {
        if (showUrlBottomSheet) {
            scope.launch {
                sheetState.show()
            }
        } else {
            scope.launch {
                sheetState.hide()
            }
        }
    }
    if (showUrlBottomSheet) {
        WriteUrlBottomSheet(
            vmTask = vmTask,
            sheetState = sheetState,
            onDismissRequest = {
                showUrlBottomSheet = false
            }
        )
    }
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
            modifier = Modifier
                .weight(.6f)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.url),
                contentDescription = "Tags icon",
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
                text = "Url",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .weight(.4f)
        ) {
            if (vmTask.url.size == 0) {
                Button(
                    onClick = {
                        showUrlBottomSheet = true
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.background,
                        contentColor = MaterialTheme.colorScheme.onBackground,
                    ),
                    shape = RoundedCornerShape(50.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.primary,
                            shape = RoundedCornerShape(50.dp)
                        )
                        .clip(RoundedCornerShape(50.dp))
                ) {
                    Text(
                        text = "Add Url",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else {
                vmTask.url.forEachIndexed { index, it ->
                    Box(
                        if (index != vmTask.url.size - 1) {
                            Modifier
                                .padding(end = 8.dp)
                                .clickable {
                                    showUrlBottomSheet = true
                                }
                        } else {
                            Modifier
                                .clickable {
                                    showUrlBottomSheet = true
                                }
                        }
                    ) {
                        Text(
                            text = it,
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            textAlign = TextAlign.End,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier
                                .clip(MaterialTheme.shapes.small)
                                .border(
                                    1.dp,
                                    color = Gray3,
                                    shape = MaterialTheme.shapes.small
                                )
                                .padding(horizontal = 6.dp, vertical = 4.dp)
                                .clickable { showUrlBottomSheet = true }
                        )

                    }
                }
            }
        }
    }

    HorizontalDivider(
        color = MaterialTheme.colorScheme.onSurface,
        thickness = 1.dp,
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WriteUrlBottomSheet(
    vmTask: TaskViewModel,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
) {
    val listState = rememberLazyListState()
    val textState = remember { mutableStateOf(TextFieldValue()) }

    ModalBottomSheet(
        modifier = Modifier.height(600.dp),
        onDismissRequest = {
            onDismissRequest()
        },
        sheetState = sheetState,
        tonalElevation = 0.dp,
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Add url",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold,
                )
            }


            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface
            )

            TextField(
                value = textState.value,
                onValueChange = { textState.value = it },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                leadingIcon = {
                    Icon(
                        painter = painterResource(id = R.drawable.url),
                        contentDescription = "Icona del link",
                        tint = MaterialTheme.colorScheme.onBackground,
                        modifier = Modifier.size(24.dp)
                    )
                },
                placeholder = { Text("Add link") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    errorTextColor = MaterialTheme.colorScheme.onBackground,
                )

            )

            Box {
                LazyColumn(
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .padding(bottom = 89.dp, start = 8.dp, end = 8.dp)
                        .simpleVerticalScrollbar(state = listState),
                    state = listState,
                ) {

                    itemsIndexed(vmTask.url) { index, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(
                                modifier = Modifier.weight(4f)
                            ) {
                                val uriHandler = LocalUriHandler.current
                                val annotatedText = buildAnnotatedString {
                                    withStyle(
                                        style = SpanStyle(
                                            color = MaterialTheme.colorScheme.primary,
                                            fontSize = MaterialTheme.typography.titleMedium.fontSize,
                                            textDecoration = TextDecoration.Underline,

                                            )
                                    ) {
                                        append(item)
                                        addStringAnnotation(
                                            tag = "URL",
                                            annotation = item,
                                            start = 0,
                                            end = "Clicca qui".length
                                        )
                                    }
                                }

                                ClickableText(
                                    text = annotatedText,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis,
                                    onClick = { offset ->
                                        annotatedText.getStringAnnotations(
                                            tag = "URL",
                                            start = offset,
                                            end = offset
                                        )
                                            .firstOrNull()?.let { annotation ->
                                                val url = annotation.item
                                                val httpUrl =
                                                    if (url.startsWith("http://") || url.startsWith(
                                                            "https://"
                                                        )
                                                    ) {
                                                        url
                                                    } else {
                                                        "http://$url"
                                                    }
                                                uriHandler.openUri(httpUrl)
                                            }
                                    }
                                )
                            }
                            Column(
                                modifier = Modifier.weight(0.5f)
                            ) {
                                IconButton(onClick = {
                                    vmTask.deleteUrl(index)
                                }) {
                                    Icon(
                                        Icons.Default.Clear,
                                        contentDescription = "Delete url"
                                    )
                                }
                            }

                        }
                    }
                }
                Column(
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 25.dp, start = 16.dp, end = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Button(
                            onClick = {
                                if (textState.value.text.isNotEmpty() && textState.value.text.isNotBlank()) {
                                    vmTask.addUrl(textState.value.text)
                                    textState.value = TextFieldValue()
                                }
                            }
                        ) {
                            Text("Add")
                        }
                    }
                }
            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AttachmentBottomSheet(
    vmTask: TaskViewModel,
    sheetState: SheetState,
    onDismissRequest: () -> Unit,
) {
    val listState = rememberLazyListState()

    val showAttachmentSelection = remember { mutableStateOf(false) }
    val newfiles = remember { mutableStateOf<List<Uri?>>(listOf()) }
    val newAttachment = mutableListOf<Attachment>()
    val context = LocalContext.current

    if (showAttachmentSelection.value) {
        AttachmentSelection(newfiles) {
            showAttachmentSelection.value = false
            for (file in newfiles.value) {
                if (file != null) {
                    newAttachment.add(calculateAttachment(context, file))
                }
            }
            vmTask.addAttachment(newAttachment)
        }
    }

    ModalBottomSheet(
        modifier = Modifier
            .fillMaxHeight(0.8f),
        onDismissRequest = {
            onDismissRequest()
        },
        sheetState = sheetState,
        tonalElevation = 0.dp,
    ) {
        Column {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp, start = 16.dp, end = 16.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    text = "Add Attachments",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                )
            }
            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface
            )

            Box {
                LazyColumn(
                    Modifier
                        .fillMaxHeight()
                        .fillMaxWidth()
                        .padding(bottom = 89.dp)
                        .simpleVerticalScrollbar(state = listState),
                    state = listState,
                ) {
                    itemsIndexed(vmTask.attachments) { index, item ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(start = 16.dp, end = 16.dp, top = 16.dp, bottom = 8.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Column(
                                modifier = Modifier.weight(4f)
                            ) {
                                ShowAttachment(
                                    context = context,
                                    attachment = item,
                                    deleted = true,
                                    onDelete = { vmTask.deleteAttachment(index) })
                            }
                        }
                    }
                }
                Column(
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 25.dp, start = 16.dp, end = 16.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        Button(
                            onClick = { showAttachmentSelection.value = true }
                        ) {
                            Text("Add")
                        }
                    }
                }
            }
        }
    }
}