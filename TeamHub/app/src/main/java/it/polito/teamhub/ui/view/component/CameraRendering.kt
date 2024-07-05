package it.polito.teamhub.ui.view.component

import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.view.PreviewView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import it.polito.teamhub.R
import it.polito.teamhub.ui.theme.Gray2
import it.polito.teamhub.utils.camera.Camera
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Composable
fun FlashButton(camera: Camera) {
    val currentAction = remember { mutableIntStateOf(0) }

    val icons = listOf(R.drawable.autoflash, R.drawable.flash, R.drawable.noflash)
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        IconButton(
            modifier = Modifier
                .align(Alignment.TopEnd)
                .padding(top = 7.dp, end = 10.dp)
                .clip(CircleShape)
                .background(Color.Transparent, shape = CircleShape),
            onClick = {
                currentAction.intValue = (currentAction.intValue + 1) % 3

                when (currentAction.intValue) {
                    0 -> {
                        camera.changeFlash(ImageCapture.FLASH_MODE_AUTO)
                    }

                    1 -> {
                        camera.changeFlash(ImageCapture.FLASH_MODE_ON)
                    }

                    2 -> {
                        camera.changeFlash(ImageCapture.FLASH_MODE_OFF)
                    }
                }
            }
        ) {
            Icon(
                painterResource(icons[currentAction.intValue]),
                contentDescription = "Flash camera",
                tint = Color.White
            )
        }
    }
}

@Composable
fun CameraRendering(
    viewFinder: PreviewView,
    camera: Camera,
    setPhoto: (Boolean) -> Unit,
    setImageProfile: (String) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        AndroidView(
            factory = { _ ->
                viewFinder.also { newViewFinder ->
                    CoroutineScope(Dispatchers.Main).launch {
                        camera.startCamera(newViewFinder)
                    }
                }
            },
            modifier = Modifier.fillMaxSize()
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(60.dp)
                .background(Color.Black.copy(alpha = 0.5f))
        ) {
            if (camera.getCamera() == CameraSelector.LENS_FACING_BACK) {
                FlashButton(camera = camera)
            }
            IconButton(
                onClick = {
                    setPhoto(false)
                },
                modifier = Modifier
                    .align(Alignment.TopStart)
                    .padding(top = 14.dp, start = 10.dp)
                    .clip(CircleShape)
                    .size(32.dp)
                    .background(Color.Transparent, shape = CircleShape)
            ) {
                Icon(
                    painterResource(R.drawable.action_cancel_close_delete_exit_remove_x_icon), // sostituisci con l'icona desiderata
                    contentDescription = "Exit camera",
                    tint = Color.White
                )
            }
        }
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(120.dp)
                .background(Color.Black.copy(alpha = 0.5f))
        )
        IconButton(
            onClick = {
                CoroutineScope(Dispatchers.Main).launch {
                    camera.takePhoto(viewFinder, setPhoto, setImageProfile)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 20.dp)
                .clip(CircleShape)
                .size(80.dp)
                .background(Color.White, shape = CircleShape)
        ) {}
        IconButton(
            onClick = {
                CoroutineScope(Dispatchers.Main).launch {
                    camera.flipCamera()
                }
                CoroutineScope(Dispatchers.Main).launch {
                    camera.startCamera(viewFinder)
                }
            },
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(end = 25.dp, bottom = 35.dp)
                .clip(CircleShape)
                .size(45.dp)
                .background(Gray2.copy(alpha = 0.6f), shape = CircleShape)
                .padding(5.dp)

        ) {
            Icon(
                painterResource(R.drawable.swap),
                contentDescription = "Swap camera",
                tint = Color.White
            )
        }
    }
}
