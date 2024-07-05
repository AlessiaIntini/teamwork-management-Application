package it.polito.teamhub.ui.view.component.profileIcon

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import coil.compose.AsyncImage
import com.yalantis.ucrop.UCrop
import it.polito.teamhub.R
import it.polito.teamhub.ui.theme.RoyalBlue
import it.polito.teamhub.utils.camera.Camera
import it.polito.teamhub.viewmodel.MemberViewModel
import it.polito.teamhub.viewmodel.TeamViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.File


@Composable
fun SetProfileIcon(
    vmMember: MemberViewModel,
    expanded: MutableState<Boolean>,
    context: Context,
    cropImageLauncher: MutableState<ActivityResultLauncher<Intent>?>,
    camera: Camera,
    lifecycleOwner: LifecycleOwner,
    vmTeam: TeamViewModel? = null,
) {
    val newViewFinder = remember { mutableStateOf<PreviewView?>(null) }
    val lifecycleState = lifecycleOwner.lifecycle.currentState
    val permissionRequest =
        rememberLauncherForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                CoroutineScope(Dispatchers.Main).launch {
                    if (lifecycleState.isAtLeast(Lifecycle.State.STARTED)) {
                        camera.startCamera(newViewFinder.value!!)
                    } else {
                        Log.e("cameraX", "lifecycle not started")
                    }
                }
            } else {
                Log.e("cameraX", "Permission not granted")
            }
        }
    /* val pickImageLauncher =
         rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
             uri?.let {
                 try {
                     val newImage = it.toString()
                     if (vmTeam != null) vmTeam.updateImageTeam(newImage) else vmMember.updateImageProfile(
                         newImage
                     )
                 } catch (e: Exception) {
                     Log.e("ImagePicker", "Failed to pick image", e)
                 }
             }
         }*/


    cropImageLauncher.value =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK && result.data != null) {
                val newImage = UCrop.getOutput(result.data!!).toString()
                if (vmTeam != null) vmTeam.updateImageTeam(newImage) else vmMember.updateImageProfile(
                    newImage
                )
            }
        }

    val pickImageLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.GetContent()) { uri: Uri? ->
            uri?.let {

                val destinationFile = File(context.cacheDir, "${System.currentTimeMillis()}.jpg")
                val destinationUri = Uri.fromFile(destinationFile)
                try {
                    val uCropIntent = UCrop.of(it, destinationUri)
                        .withAspectRatio(1f, 1f)
                        .getIntent(context as Activity)
                    cropImageLauncher.value?.launch(uCropIntent)

                    Log.println(Log.DEBUG, "UCrop", "UCrop started")
                } catch (e: Exception) {
                    Log.e("UCrop", "Failed to start UCrop", e)
                }
            }
        }
    Box(
        modifier = Modifier
            .fillMaxSize()
    ) {
        AndroidView(
            factory = { c ->
                PreviewView(c).also { newViewFinder.value = it }
            },
            modifier = Modifier.fillMaxSize()
        )
        if (vmMember.userImage == "" && vmTeam == null) {
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .background(
                        Brush.linearGradient(
                            colors = listOf(
                                Color(vmMember.color[0]),
                                Color(vmMember.color[1])
                            )
                        ), CircleShape
                    )
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            )
            {
                Text(
                    text = vmMember.initialsName,
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Medium,
                    fontSize = 40.sp,
                    color = MaterialTheme.colorScheme.onPrimary
                )
            }
        } else if (vmTeam == null) {
            AsyncImage(
                model = Uri.parse(vmMember.userImage),
                contentDescription = "User image",
                placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .align(Alignment.Center)
            )
        } else if (vmTeam.teamImage == "") {
            Box(
                modifier = Modifier
                    .size(150.dp)
                    .background(Color(vmTeam.defaultColor), CircleShape)
                    .align(Alignment.Center),
                contentAlignment = Alignment.Center
            )
            {
                Icon(
                    painter = painterResource(id = vmTeam.defaultImage),
                    contentDescription = "Default Image",
                    modifier = Modifier.size(100.dp),
                    tint = Color.White
                )
            }
        } else {
            AsyncImage(
                model = Uri.parse(vmTeam.teamImage),
                contentDescription = "User image",
                placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(150.dp)
                    .clip(CircleShape)
                    .align(Alignment.Center)
            )
        }

        IconButton(
            onClick = { expanded.value = true },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .offset(x = 32.dp, y = 5.dp)
                .clip(CircleShape)
                .size(45.dp)
                .background(RoyalBlue, shape = CircleShape)
        ) {
            Icon(
                painterResource(R.drawable.camera_plus),
                contentDescription = "Localized description",
                tint = Color.White
            )
        }
        if (expanded.value) {
            Dialog(
                onDismissRequest = { expanded.value = false }
            ) {
                Box(
                    contentAlignment = Alignment.Center,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(MaterialTheme.colorScheme.surface)
                ) {
                    Column(
                        horizontalAlignment = Alignment.Start,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = "New profile picture",
                            style = MaterialTheme.typography.bodyLarge,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp, top = 16.dp)
                        )
                        Button(
                            onClick = {
                                if (vmTeam != null) vmTeam.changePhoto(true) else vmMember.changePhoto(
                                    true
                                )
                                permissionRequest.launch(Manifest.permission.CAMERA)
                                expanded.value = false
                            },
                            shape = RoundedCornerShape(0.dp),
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.surface),
                        ) {
                            Icon(
                                painterResource(id = R.drawable.photo_camera),
                                contentDescription = "Take photo",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = "Take photo",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth(.9f)
                                .align(Alignment.CenterHorizontally),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f)
                        )
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_PICK)

                                intent.type = "image/*"
                                pickImageLauncher.launch(intent.type)
                                expanded.value = false
                            },
                            shape = RoundedCornerShape(0.dp),
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.surface),
                        ) {
                            Icon(
                                painterResource(id = R.drawable.image),
                                contentDescription = "Choose from gallery",
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = "Choose from gallery",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                        HorizontalDivider(
                            modifier = Modifier
                                .fillMaxWidth(.9f)
                                .align(Alignment.CenterHorizontally),
                            thickness = 1.dp,
                            color = MaterialTheme.colorScheme.tertiary.copy(alpha = 0.12f)
                        )
                        Button(
                            onClick = {
                                if (vmTeam != null) vmTeam.deleteImageTeam() else
                                    vmMember.deleteImageProfile()
                                expanded.value = false
                            },
                            shape = RoundedCornerShape(0.dp),
                            colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.surface),
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Delete,
                                contentDescription = "Delete photo",
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                            Text(
                                text = "Delete photo",
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onBackground,
                                textAlign = TextAlign.Start,
                                modifier = Modifier.fillMaxWidth()
                            )
                        }
                    }
                }
            }
        }
    }
}