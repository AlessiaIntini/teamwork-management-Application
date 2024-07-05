package it.polito.teamhub.ui.view.component.attachment

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState

@Composable
fun AttachmentSelection(
    newfiles: MutableState<List<Uri?>>,
    onSelectionComplete: () -> Unit
) {
    val launcher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.OpenMultipleDocuments()) { uri ->
            uri.let {
                newfiles.value = it
                onSelectionComplete()
            }
        }

    LaunchedEffect(Unit) {
        launcher.launch(arrayOf("*/*"))
    }

}