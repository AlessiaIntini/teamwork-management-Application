package it.polito.teamhub.utils.attachment

import android.content.Context
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import it.polito.teamhub.dataClass.task.Attachment

fun calculateAttachment(context: Context, uri: Uri): Attachment {
    val mimeType =
        context.contentResolver.getType(uri)
    val extension = MimeTypeMap.getSingleton()
        .getExtensionFromMimeType(mimeType)!!
    val name = getFileNameFromUri(context, uri)
    return Attachment(name, uri.toString(), extension)
}

private fun getFileNameFromUri(context: Context, uri: Uri): String {
    var result: String? = null
    if (uri.scheme == "content") {
        val cursor = context.contentResolver.query(uri, null, null, null, null)
        cursor?.use {
            if (it.moveToFirst()) {
                val columnIndex = it.getColumnIndex(OpenableColumns.DISPLAY_NAME)
                result = cursor.getString(columnIndex)
            }
        }
    }
    if (result == null) {
        result = uri.path
        val cut = result?.lastIndexOf('/')
        if (cut != -1) {
            result = result?.substring(cut!!.plus(1))
        }
    }
    return result!!
}