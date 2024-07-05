package it.polito.teamhub.ui.view.taskView.createTask

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import it.polito.teamhub.R
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.dataClass.task.Tag
import it.polito.teamhub.ui.theme.Gray3
import it.polito.teamhub.ui.theme.linearGradient
import it.polito.teamhub.ui.view.component.simpleVerticalScrollbar
import it.polito.teamhub.viewmodel.TagViewModel
import it.polito.teamhub.viewmodel.TaskViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetTags(
    vmTask: TaskViewModel,
    vmTag: TagViewModel,
    teamId: Long,
    memberLogged: Member
) {
    var showTagsBottomSheet by remember { mutableStateOf(false) }
    var showAddTagDialog by remember { mutableStateOf(false) }
    var showEditTagBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(showTagsBottomSheet)
    val scope = rememberCoroutineScope()
    val tags = vmTag.tags.collectAsState()

    if (showAddTagDialog) {
        DialogTag(
            onDismiss = { showAddTagDialog = false },
            onEditTag = { null },
            onAddTag = { newTag ->
                vmTag.addTag(newTag)
            },
            teamId = teamId,
            tag = null
        )
    }

    if (showEditTagBottomSheet) {
        EditTags(
            onDismissRequest = { showEditTagBottomSheet = false },
            vmTask = vmTask,
            vmTag = vmTag,
            teamId = teamId,
            tags = tags.value,
            memberLogged = memberLogged
        )
    }

    LaunchedEffect(key1 = showTagsBottomSheet) {
        if (showTagsBottomSheet) {
            scope.launch {
                sheetState.show()
            }
        } else {
            scope.launch {
                sheetState.hide()
            }
        }
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
                painter = painterResource(id = R.drawable.tag),
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
                text = "Tags",
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
            if (vmTask.tag.size == 0) {
                Button(
                    onClick = { showTagsBottomSheet = true },
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
                        text = "Select Tags",
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.bodyMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            } else {
                vmTask.tag.forEachIndexed { index, it ->
                    Box(
                        if (index != vmTask.tag.size - 1) {
                            Modifier
                                .padding(end = 8.dp)
                                .clickable {
                                    showTagsBottomSheet = true
                                }
                        } else {
                            Modifier
                                .clickable {
                                    showTagsBottomSheet = true
                                }
                        }
                    ) {
                        val tag = tags.value.find { tag -> tag.id == it.id }
                        if (tag != null) {
                            Text(
                                text = tag.name,
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
                                    .clickable { showTagsBottomSheet = true }
                            )
                        }
                    }
                }
            }
        }
    }

    HorizontalDivider(
        color = MaterialTheme.colorScheme.onSurface,
        thickness = 1.dp,
    )

    if (showTagsBottomSheet) {
        TagsBottomSheet(
            sheetState = sheetState,
            scope = scope,
            vmTask = vmTask,
            vmTag = vmTag,
            tags = tags.value,
            onDismissRequest = {
                showTagsBottomSheet = false
            },
            openEditTag = {
                showEditTagBottomSheet = true
            },
            openAddTag = {
                showAddTagDialog = true
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TagsBottomSheet(
    sheetState: SheetState,
    scope: CoroutineScope,
    vmTask: TaskViewModel,
    vmTag: TagViewModel,
    tags: List<Tag>,
    onDismissRequest: () -> Unit,
    openEditTag: () -> Unit,
    openAddTag: () -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    val tagIds by remember { mutableStateOf(vmTask.tag.map { it.id }) }
    var selectedTags by remember { mutableStateOf(tagIds) }
    var completeTags by remember { mutableStateOf(listOf(Tag("", -2))) }
    val shownList = tags
    var filteredList by remember { mutableStateOf(listOf<Tag>()) }
    var useSearch by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    LaunchedEffect(selectedTags) {
        if (selectedTags.isNotEmpty()) {
            vmTag.getListTagById(selectedTags).collect { tags ->
                completeTags = tags
            }
        } else {
            completeTags = listOf(Tag("", -1))
        }
    }

    LaunchedEffect(searchText) {
        scope.launch {
            filteredList = shownList.filter { it.name.contains(searchText, ignoreCase = true) }
        }
    }

    ModalBottomSheet(
        modifier = Modifier
            .height(600.dp),
        onDismissRequest = {
            onDismissRequest()
            selectedTags = vmTask.tag.map { it.id }
        },
        sheetState = sheetState,
        tonalElevation = 0.dp
    ) {
        Column {
            // Sheet content
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Choose tag",
                    color = MaterialTheme.colorScheme.onBackground,
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
            }

            HorizontalDivider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                thickness = 1.dp,
                color = MaterialTheme.colorScheme.onSurface
            )


            TextField(
                value = searchText,
                onValueChange = { newText ->
                    searchText = newText
                    useSearch = true
                },
                placeholder = {
                    Text(
                        "Search",
                        color = if (isSystemInDarkTheme()) Color.White else Color.Gray
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 24.dp, end = 24.dp, bottom = 8.dp, top = 8.dp)
                    .clip(MaterialTheme.shapes.small),
                colors = TextFieldDefaults.colors(
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                    focusedContainerColor = MaterialTheme.colorScheme.onSurface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.onSurface,
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                ),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search Icon",
                        tint = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onBackground else Color.Gray
                    )
                },
                trailingIcon = {
                    if (searchText.isNotEmpty()) {
                        IconButton(
                            onClick = {
                                searchText = ""
                                useSearch = false
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Close Icon",
                                tint = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onBackground else Color.Gray
                            )
                        }
                    }
                }
            )

            Box {
                LazyColumn(
                    Modifier
                        .fillMaxHeight()
                        .padding(bottom = 150.dp, start = 8.dp, end = 8.dp)
                        .simpleVerticalScrollbar(state = listState),
                    state = listState,
                ) {
                    val tagsSearched = if (useSearch) {
                        shownList.filter { it.name.contains(searchText, ignoreCase = true) }
                    } else {
                        shownList
                    }
                    items(tagsSearched) { tag ->
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 0.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(4f)
                            ) {
                                Text(
                                    text = tag.name,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Checkbox(
                                    checked = selectedTags.contains(tag.id),
                                    onCheckedChange = { isChecked ->
                                        if (isChecked) {
                                            selectedTags = selectedTags.toMutableList()
                                                .also { it.add(tag.id) }

                                        } else {
                                            selectedTags = selectedTags.toMutableList()
                                                .also { it.remove(tag.id) }
                                        }
                                    }
                                )
                            }
                        }

                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }

                Column(
                    modifier = Modifier.align(Alignment.BottomCenter)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, bottom = 10.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Button(
                                onClick = { openEditTag() },
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .padding(end = 10.dp)
                                    .width(150.dp),
                                border = BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.primary
                                ),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = MaterialTheme.colorScheme.surface
                                ),
                            )
                            {
                                Icon(
                                    painter = painterResource(id = R.drawable.edit),
                                    contentDescription = "Edit Icon",
                                    modifier = Modifier
                                        .padding(end = 1.dp)
                                        .size(24.dp),
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                                Text(
                                    color = MaterialTheme.colorScheme.primary,
                                    text = "Edit tags"

                                )
                            }
                        }

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Button(
                                onClick = { openAddTag() },
                                modifier = Modifier
                                    .align(Alignment.Start)
                                    .padding(end = 10.dp)
                                    .width(150.dp),
                                border = BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.primary
                                ),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = MaterialTheme.colorScheme.surface
                                ),
                            )
                            {
                                Text(
                                    color = MaterialTheme.colorScheme.primary,
                                    text = "Add new tag"

                                )
                            }
                        }
                    }
                    HorizontalDivider(
                        modifier = Modifier
                            .padding(horizontal = 16.dp, vertical = 0.dp),
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp, bottom = 30.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Button(
                                onClick = {
                                    selectedTags = tagIds
                                    scope.launch { sheetState.hide() }.invokeOnCompletion {
                                        if (!sheetState.isVisible) {
                                            onDismissRequest()
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .padding(end = 10.dp),
                                border = BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.onBackground
                                ),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.surface,
                                    contentColor = MaterialTheme.colorScheme.surface
                                ),
                            ) {
                                Text(
                                    text = "Cancel",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onBackground
                                )
                            }
                        }

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {

                            Button(
                                onClick = {
                                    if (completeTags.isEmpty())
                                        completeTags = listOf(Tag("", -1))
                                    if (completeTags[0].teamId != -2L) {
                                        vmTask.updateTag(completeTags.toMutableList())
                                        scope.launch { sheetState.hide() }.invokeOnCompletion {
                                            if (!sheetState.isVisible) {
                                                onDismissRequest()
                                            }
                                        }
                                    }
                                },
                                modifier = Modifier
                                    .align(Alignment.Start)
                                    .padding(start = 10.dp),
                                border = BorderStroke(1.dp, MaterialTheme.colorScheme.primary),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = MaterialTheme.colorScheme.primary,
                                    contentColor = MaterialTheme.colorScheme.primary
                                ),
                            ) {
                                Text(
                                    text = "Save",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onPrimary
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

private fun checkText(text: String): Boolean {
    return text.isNotBlank()
}

@Composable
fun DialogTag(
    onDismiss: () -> Unit,
    onEditTag: (Tag) -> Unit?,
    onAddTag: (Tag) -> Unit?,
    teamId: Long,
    tag: Tag?
) {
    var text by remember { mutableStateOf(tag?.name ?: "") }
    var error by remember { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = (if (tag != null) "Edit tag" else "Add new tag"),
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,
                    modifier = Modifier.padding(top = 20.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))
                TextField(
                    value = text,
                    onValueChange = { newText -> text = newText },
                    placeholder = {
                        Text(
                            "Tag name",
                            color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onBackground else Color.Gray
                        )
                    },
                    colors = TextFieldDefaults.colors(
                        focusedTextColor = MaterialTheme.colorScheme.onBackground,
                        unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                        focusedContainerColor = MaterialTheme.colorScheme.onSurface,
                        unfocusedContainerColor = MaterialTheme.colorScheme.onSurface,
                        focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    ),
                    trailingIcon = {
                        if (text.isNotEmpty()) {
                            IconButton(
                                onClick = { text = "" }
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Clear,
                                    contentDescription = "Clear Icon",
                                    tint = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onBackground else Color.Gray
                                )
                            }
                        }
                    },
                )
                Spacer(modifier = Modifier.height(16.dp))
                if (error.isNotBlank()) {
                    Text(
                        error,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
                Spacer(modifier = Modifier.height(16.dp))
                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(bottom = 20.dp)
                ) {
                    Button(
                        onClick = {
                            onDismiss()
                        },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.background),
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.onBackground
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Text("Cancel", color = MaterialTheme.colorScheme.onBackground)
                    }

                    Button(
                        onClick = {
                            if (checkText(text)) {
                                if (tag != null)
                                    onEditTag(Tag(name = text, teamId = teamId))
                                else
                                    onAddTag(Tag(name = text, teamId = teamId))
                                onDismiss()
                            } else {
                                error = "Tag name cannot be empty"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Text(if (tag != null) "Edit" else "Add")
                    }
                }
            }
        }
    }
}


@Composable
fun EditTags(
    vmTask: TaskViewModel,
    vmTag: TagViewModel,
    onDismissRequest: () -> Unit,
    teamId: Long,
    tags: List<Tag>,
    memberLogged: Member
) {
    val listState = rememberLazyListState()
    var showDialogEdit by remember { mutableStateOf(false) }
    var selectedTag by remember { mutableStateOf(Tag("", -1)) }
    val showDeleteDialog = remember { mutableStateOf(false) }

    Dialog(
        onDismissRequest = onDismissRequest,
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier.padding(8.dp, 8.dp)
            ) {
                Box {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .align(Alignment.Center),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Edit tags",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 20.dp),
                            textAlign = TextAlign.Center
                        )
                    }
                }

                Box {
                    LazyColumn(
                        Modifier
                            .fillMaxHeight(0.8f)
                            .padding(bottom = 100.dp, start = 8.dp, end = 8.dp)
                            .simpleVerticalScrollbar(state = listState),
                        state = listState,
                    ) {
                        itemsIndexed(tags) { _, tag ->
                            Row(
                                modifier = Modifier.padding(
                                    horizontal = 14.dp,
                                    vertical = 0.dp
                                ),
                                horizontalArrangement = Arrangement.Start,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(4f)
                                ) {
                                    Text(
                                        text = tag.name,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        style = MaterialTheme.typography.bodyMedium,
                                        modifier = Modifier.padding(start = 8.dp)
                                    )
                                }
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    IconButton(onClick = {
                                        showDialogEdit = true
                                        selectedTag = tag
                                    }) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "Edit tag"
                                        )
                                    }
                                }
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    IconButton(onClick = {
                                        selectedTag = tag
                                        showDeleteDialog.value = true
                                    }) {
                                        Icon(
                                            Icons.Default.Clear,
                                            contentDescription = "Delete tag"
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(
                                    horizontal = 16.dp,
                                    vertical = 8.dp
                                ),
                                thickness = 1.dp,
                                color = MaterialTheme.colorScheme.onSurface
                            )

                        }
                    }
                    Row(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Button(onClick = { onDismissRequest() }) {
                            Text("Done")

                        }
                    }

                    if (showDeleteDialog.value)
                        ConfirmDeleteDialog(
                            showDeleteDialog = showDeleteDialog,
                            vmTask = vmTask,
                            selectedTag = selectedTag,
                            memberLogged = memberLogged
                        )

                    if (showDialogEdit)
                        DialogTag(
                            onDismiss = { showDialogEdit = false },
                            onEditTag = { newTag ->
                                vmTag.updateTagList(selectedTag.id, newTag)
                            },
                            onAddTag = { null },
                            teamId = teamId,
                            tag = selectedTag
                        )
                }
            }
        }
    }
}


@Composable
fun ConfirmDeleteDialog(
    showDeleteDialog: MutableState<Boolean>,
    vmTask: TaskViewModel,
    selectedTag: Tag,
    memberLogged: Member
) {
    Dialog(
        onDismissRequest = { showDeleteDialog.value = false },
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.surface)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    "Confirm Deletion",
                    style = MaterialTheme.typography.headlineSmall,
                    color = MaterialTheme.colorScheme.onBackground,

                    )
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Are you sure you want to delete this tag?",
                    style = MaterialTheme.typography.bodyMedium,
                    modifier = Modifier.padding(horizontal = 16.dp),
                    textAlign = TextAlign.Center
                )
                Spacer(modifier = Modifier.height(16.dp))
                Row(horizontalArrangement = Arrangement.Center) {
                    Button(
                        onClick = {
                            showDeleteDialog.value = false
                        },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.surface),
                        border = BorderStroke(
                            1.dp,
                            MaterialTheme.colorScheme.onBackground
                        ),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Text("Cancel", color = MaterialTheme.colorScheme.onBackground)
                    }

                    Button(
                        onClick = {
                            if (vmTask.tag.contains(selectedTag)) {
                                val tags = vmTask.tag
                                tags.remove(selectedTag)
                                vmTask.deletedTags.add(selectedTag)
                                vmTask.updateTag(tags)
                            }
                            vmTask.deleteTag(selectedTag.id, selectedTag.name, memberLogged.id)
                            showDeleteDialog.value = false
                        },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.error),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Text("Confirm")
                    }
                }
            }
        }

    }
}