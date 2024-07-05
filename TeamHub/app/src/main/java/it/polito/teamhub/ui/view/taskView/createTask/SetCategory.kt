package it.polito.teamhub.ui.view.taskView.createTask


import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.RadioButton
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
import androidx.compose.runtime.mutableLongStateOf
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import it.polito.teamhub.R
import it.polito.teamhub.dataClass.task.Category
import it.polito.teamhub.ui.theme.linearGradient
import it.polito.teamhub.ui.view.component.simpleVerticalScrollbar
import it.polito.teamhub.viewmodel.CategoryViewModel
import it.polito.teamhub.viewmodel.TaskViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SetCategory(
    vmTask: TaskViewModel,
    vmCategory: CategoryViewModel,
    teamId: Long
) {
    var showCategoryBottomSheet by remember { mutableStateOf(false) }
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var showEditCategoryBottomSheet by remember { mutableStateOf(false) }
    val sheetState = rememberModalBottomSheetState(showCategoryBottomSheet)
    val scope = rememberCoroutineScope()
    val categories = vmCategory.categories.collectAsState()

    if (showAddCategoryDialog) {
        DialogCategory(
            onDismiss = { showAddCategoryDialog = false },
            onEditCategory = { },
            onAddCategory = { category: Category -> vmCategory.addCategoryList(category) },
            teamId = teamId,
            category = null
        )
    }

    if (showEditCategoryBottomSheet) {
        EditCategory(
            vmTask = vmTask,
            vmCategory = vmCategory,
            onDismissRequest = { showEditCategoryBottomSheet = false },
            teamId = teamId,
            categories = categories.value
        )
    }

    LaunchedEffect(key1 = showCategoryBottomSheet) {
        if (showCategoryBottomSheet) {
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
            modifier = Modifier.weight(.6f)
        ) {
            Icon(
                painter = painterResource(id = R.drawable.category),
                contentDescription = "Category icon",
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
                text = "Category",
                color = MaterialTheme.colorScheme.onBackground,
                style = MaterialTheme.typography.bodyMedium
            )
        }
        Row(
            horizontalArrangement = Arrangement.End,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(.4f)
        ) {
            Button(
                onClick = { showCategoryBottomSheet = true },
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground,
                ),
                shape = RoundedCornerShape(50.dp),
                modifier = Modifier
                    .clip(RoundedCornerShape(50.dp))
                    .border(
                        width = 1.dp,
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(50.dp)
                    )
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 8.dp),
            ) {
                Text(
                    text = if (vmTask.category.name == "") "Select Category" else vmTask.category.name,
                    color = MaterialTheme.colorScheme.primary,
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
    HorizontalDivider(
        color = MaterialTheme.colorScheme.onSurface,
        thickness = 1.dp,
    )


    if (showCategoryBottomSheet) {
        CategoryBottomSheet(
            sheetState = sheetState,
            scope = scope,
            vmTask = vmTask,
            vmCategory = vmCategory,
            categories = categories.value,
            onDismissRequest = { showCategoryBottomSheet = false },
            openEditCategory = { showEditCategoryBottomSheet = true },
            openAddCategory = { showAddCategoryDialog = true }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryBottomSheet(
    sheetState: SheetState,
    scope: CoroutineScope,
    vmTask: TaskViewModel,
    vmCategory: CategoryViewModel,
    categories: List<Category>,
    onDismissRequest: () -> Unit,
    openEditCategory: () -> Unit,
    openAddCategory: () -> Unit
) {
    var searchText by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableLongStateOf(vmTask.category.id) }
    var completeCategory by remember { mutableStateOf(Category("", -2)) }
    val shownList = categories
    var filteredList by remember { mutableStateOf(listOf<Category>()) }
    val listState = rememberLazyListState()
    var useSearch by remember { mutableStateOf(false) }

    LaunchedEffect(selectedCategory) {
        if (selectedCategory != -1L) {
            vmCategory.getCategoryById(selectedCategory).collect { category ->
                completeCategory = category
            }
        } else {
            completeCategory = Category("", -1)
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
            selectedCategory = vmTask.category.id
        },
        sheetState = sheetState,
        tonalElevation = 0.dp,
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
                    text = "Choose category",
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
                        color = if (isSystemInDarkTheme()) MaterialTheme.colorScheme.onBackground else Color.Gray
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
                                contentDescription = "Clear Icon",
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

                    val categoriesSearched = if (useSearch) {
                        shownList.filter { it.name.contains(searchText, ignoreCase = true) }
                    } else {
                        shownList
                    }
                    items(categoriesSearched) { category ->
                        Row(
                            modifier = Modifier.padding(horizontal = 14.dp, vertical = 0.dp),
                            horizontalArrangement = Arrangement.Start,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(4f)
                            ) {
                                Text(
                                    text = category.name,
                                    color = MaterialTheme.colorScheme.onBackground,
                                    style = MaterialTheme.typography.bodyMedium,
                                    modifier = Modifier.padding(start = 8.dp)
                                )
                            }
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                RadioButton(
                                    selected = (selectedCategory == category.id),
                                    onClick = {
                                        selectedCategory = if (selectedCategory == category.id) {
                                            -1
                                        } else {
                                            category.id
                                        }
                                    },
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
                                onClick = { openEditCategory() },
                                modifier = Modifier
                                    .align(Alignment.End)
                                    .padding(end = 10.dp)
                                    .width(180.dp),
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
                                        .padding(start = 0.dp, end = 1.dp)
                                        .size(24.dp),
                                    tint = MaterialTheme.colorScheme.primary,
                                )
                                Text(
                                    color = MaterialTheme.colorScheme.primary,
                                    text = "Edit category"
                                )
                            }
                        }

                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Button(
                                onClick = {
                                    openAddCategory()
                                },
                                modifier = Modifier
                                    .align(Alignment.Start)
                                    .padding(start = 5.dp)
                                    .width(170.dp),
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
                                    text = "Add category"

                                )
                            }
                        }


                    }
                    HorizontalDivider(
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 0.dp),
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
                                    selectedCategory = vmTask.category.id
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
                                    if (completeCategory.teamId != -2L) {
                                        vmTask.updateCategory(completeCategory)
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
                                border = BorderStroke(
                                    1.dp,
                                    MaterialTheme.colorScheme.primary
                                ),
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

@Composable
fun EditCategory(
    vmTask: TaskViewModel,
    vmCategory: CategoryViewModel,
    onDismissRequest: () -> Unit,
    teamId: Long,
    categories: List<Category>
) {
    val listState = rememberLazyListState()
    var showDialogEdit by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf(Category("", -1)) }
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
                            text = "Edit category",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 30.dp, top = 5.dp),
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
                        itemsIndexed(categories) { _, category ->
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
                                        text = category.name,
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
                                        selectedCategory = category
                                    }) {
                                        Icon(
                                            Icons.Default.Edit,
                                            contentDescription = "Edit category"
                                        )
                                    }
                                }
                                Column(
                                    modifier = Modifier.weight(1f)
                                ) {
                                    IconButton(onClick = {
                                        selectedCategory = category
                                        showDeleteDialog.value = true
                                    }) {
                                        Icon(
                                            Icons.Default.Clear,
                                            contentDescription = "Delete category"
                                        )
                                    }
                                }
                            }

                            HorizontalDivider(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
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
                        ConfirmDeleteDialogCategory(
                            showDeleteDialog = showDeleteDialog,
                            vmTask = vmTask,
                            selectedCategory = selectedCategory
                        )
                    if (showDialogEdit)
                        DialogCategory(
                            onDismiss = { showDialogEdit = false },
                            onEditCategory = { category: Category ->
                                vmCategory.updateCategoryList(
                                    selectedCategory.id, category
                                )
                            },
                            onAddCategory = { },
                            teamId = teamId,
                            category = selectedCategory
                        )
                }
            }
        }
    }
}

private fun checkText(text: String): Boolean {
    return text.isNotBlank()
}

@Composable
fun DialogCategory(
    onDismiss: () -> Unit,
    onEditCategory: (Category) -> Unit,
    onAddCategory: (Category) -> Unit,
    teamId: Long,
    category: Category?
) {

    var text by remember { mutableStateOf(category?.name ?: "") }
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
                    text = if (category != null) "Edit category" else "Add new category",
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
                            "Category name",
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
                    }
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
                                if (category != null)
                                    onEditCategory(Category(name = text, teamId = teamId))
                                else
                                    onAddCategory(Category(name = text, teamId = teamId))
                                onDismiss()
                            } else {
                                error = "Category name cannot be empty"
                            }
                        },
                        colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Text(if (category != null) "Edit" else "Add")
                    }
                }
            }
        }
    }
}


@Composable
fun ConfirmDeleteDialogCategory(
    showDeleteDialog: MutableState<Boolean>,
    vmTask: TaskViewModel,
    selectedCategory: Category,
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
                    "Are you sure you want to delete this category?",
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
                            if (selectedCategory.id == vmTask.category.id) {
                                vmTask.updateCategory(Category("", -1))
                                vmTask.deletedCategories.add(selectedCategory)
                            }
                            vmTask.deleteCategory(
                                selectedCategory.id,
                                selectedCategory.name,
                                vmTask.memberLogged.value.id
                            )
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