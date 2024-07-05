package it.polito.teamhub.ui.view.taskView.taskDetails

import android.app.DownloadManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.content.Intent.FLAG_GRANT_READ_URI_PERMISSION
import android.content.res.Configuration
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.ktx.storage
import it.polito.teamhub.R
import it.polito.teamhub.dataClass.task.Attachment
import it.polito.teamhub.dataClass.task.Task
import it.polito.teamhub.ui.theme.Gray3
import java.io.File

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun RenderAttachment(
    attachments: MutableList<Attachment>,
    task: Task
) {
    HorizontalDivider(
        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
        thickness = 1.dp,
        color = MaterialTheme.colorScheme.onSurface
    )

    Text(
        text = if (attachments.size == 0) "No attachments" else "Click on an attachment to download it",
        style = MaterialTheme.typography.labelMedium,
        color = MaterialTheme.colorScheme.onBackground,
        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)

    )
    for (attachment in attachments) {
        ShowAttachment(
            context = LocalContext.current,
            attachment = attachment,
            clickable = true
        )
    }

}

@RequiresApi(Build.VERSION_CODES.O)
fun openFile(context: Context, attachment: Attachment) {
    val storage = Firebase.storage
    val httpsReference = storage.getReferenceFromUrl(attachment.url)
    httpsReference.downloadUrl.addOnSuccessListener { downloadUrl ->
        val request = DownloadManager.Request(downloadUrl).apply {
            setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED)

            val customPath = "/Download/${attachment.name}"
            val customUri =
                Uri.fromFile(File(Environment.getExternalStorageDirectory(), customPath))
            setDestinationUri(customUri)
        }
        val downloadManager = context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager
        val downloadId = downloadManager.enqueue(request)

        Toast.makeText(
            context,
            "Attachment downloaded",
            Toast.LENGTH_LONG
        ).show()
    }.addOnFailureListener {
        Toast.makeText(
            context,
            "An error occurred, the attachment was not downloaded",
            Toast.LENGTH_LONG
        ).show()
    }
}


class MyBroadcastReceiver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        val downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1)
        if (downloadId.toInt() != -1) {
            val extras = intent.extras
            val attachment = extras?.getParcelable<Attachment>("attachment")
            val downloadManager =
                context.getSystemService(Context.DOWNLOAD_SERVICE) as DownloadManager

            val customPath = "/Download/prova1.pdf"
            val fileUri = Uri.fromFile(File(Environment.getExternalStorageDirectory(), customPath))
            /*val openFileIntent = Intent(ACTION_VIEW).apply {
                setDataAndType(fileUri, "application/pdf")
                addFlags(FLAG_GRANT_READ_URI_PERMISSION)
            }*/
            val openFileIntent = Intent(ACTION_VIEW).apply {
                setDataAndType(fileUri, "application/pdf")
                addFlags(FLAG_GRANT_READ_URI_PERMISSION)
                setPackage("com.adobe.reader")
            }
            if (openFileIntent.resolveActivity(context.packageManager) != null) {
                context.startActivity(openFileIntent)
            } else {


            }
        }
    }
}

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun ShowAttachment(
    context: Context,
    attachment: Attachment,
    onDelete: () -> Unit = { },
    deleted: Boolean = false,
    clickable: Boolean = false
) {
    val configuration = LocalConfiguration.current
    val orientation = when (configuration.orientation) {
        Configuration.ORIENTATION_LANDSCAPE -> "Landscape"
        Configuration.ORIENTATION_PORTRAIT -> "Portrait"
        else -> "Unknown"
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        horizontalArrangement = if (orientation == "Landscape") Arrangement.Center else Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            Modifier
                .width(400.dp)
                .height(100.dp)
                .clickable {
                    if (clickable)
                        openFile(
                            context = context,
                            attachment = attachment
                        )
                },
        ) {
            Box(
                Modifier
                    .fillMaxWidth()
                    .padding(
                        top = if (deleted) 10.dp else 0.dp,
                        end = if (deleted) 10.dp else 0.dp
                    )
                    .border(
                        1.dp,
                        color = Gray3,
                        shape = MaterialTheme.shapes.small
                    )
            ) {
                when (attachment.extension) {
                    "jpg", "png" -> {
                        Row(
                            modifier = Modifier.padding(
                                top = 4.dp,
                                bottom = 20.dp,
                                start = 8.dp,
                                end = 8.dp
                            )
                        ) {
                            Text(
                                text = "Image",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                        }
                        Row(
                            modifier = Modifier
                                .padding(top = 25.dp)
                        ) {
                            AsyncImage(
                                model = Uri.parse(attachment.url),
                                contentDescription = "File preview",
                                modifier = Modifier
                                    .width(60.dp)
                                    .padding(top = 8.dp, start = 8.dp, end = 8.dp, bottom = 8.dp)

                            )
                            Text(
                                text = AnnotatedString(attachment.name),
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(6.dp),
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                        }
                    }

                    "pdf" -> {
                        Row(
                            modifier = Modifier.padding(
                                top = 4.dp,
                                bottom = 20.dp,
                                start = 8.dp,
                                end = 8.dp
                            )
                        ) {
                            Text(
                                text = "PDF",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                        }
                        Row(
                            modifier = Modifier
                                .padding(top = 25.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.pdf),
                                contentDescription = "PDF icon",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier
                                    .size(60.dp)
                                    .padding(8.dp)
                            )
                            Text(
                                text = AnnotatedString(attachment.name),
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(6.dp),
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                        }
                    }

                    "csv" -> {
                        Row(
                            modifier = Modifier.padding(
                                top = 4.dp,
                                bottom = 20.dp,
                                start = 8.dp,
                                end = 8.dp
                            )
                        ) {
                            Text(
                                text = "PDF",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                        }
                        Row(
                            modifier = Modifier
                                .padding(top = 25.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.csv),
                                contentDescription = "PDF icon",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier
                                    .size(60.dp)
                                    .padding(8.dp)
                            )
                            Text(
                                text = AnnotatedString(attachment.name),
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(6.dp),
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                        }
                    }

                    else -> {
                        Row(
                            modifier = Modifier.padding(
                                top = 4.dp,
                                bottom = 20.dp,
                                start = 8.dp,
                                end = 8.dp
                            )
                        ) {
                            Text(
                                text = "Unknown file",
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                        }
                        Row(
                            modifier = Modifier
                                .padding(top = 25.dp)
                        ) {
                            Icon(
                                painter = painterResource(id = R.drawable.unknown_document),
                                contentDescription = "PDF icon",
                                tint = MaterialTheme.colorScheme.onBackground,
                                modifier = Modifier
                                    .size(60.dp)
                                    .padding(8.dp)
                            )
                            Text(
                                text = AnnotatedString(attachment.name),
                                style = MaterialTheme.typography.labelMedium,
                                modifier = Modifier.padding(6.dp),
                                color = MaterialTheme.colorScheme.onBackground,
                            )
                        }
                    }
                }
            }
            if (deleted) {
                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .clip(CircleShape)
                        .size(24.dp)
                        .background(MaterialTheme.colorScheme.onBackground.copy(0.2f))  // Scegli il colore di sfondo del cerchio
                ) {
                    Icon(
                        Icons.Default.Clear, contentDescription = "Delete attachment",
                        tint = MaterialTheme.colorScheme.onBackground.copy(0.7f),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
