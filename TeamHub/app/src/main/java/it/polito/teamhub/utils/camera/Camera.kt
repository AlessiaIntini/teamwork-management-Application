package it.polito.teamhub.utils.camera

import android.app.AlertDialog
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.annotation.RequiresApi
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import it.polito.teamhub.ui.theme.PurpleBlue
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.Executor
import java.util.concurrent.ExecutorService

data class Camera(
    private val context: Context,
    private val lifecycleOwner: LifecycleOwner,
    private var imageCapture: MutableState<ImageCapture?>,
    val lensFacing: MutableState<Int>,
    private val contentResolver: ContentResolver,
    private val mainExecutor: Executor,
    private val cameraExecutor: ExecutorService,
    private val activityResultLauncher: ActivityResultLauncher<Array<String>>

) {
    private val cameraProviderFuture = ProcessCameraProvider.getInstance(context)
    private var savedUri: Uri? = null
    private val flashMode = mutableIntStateOf(ImageCapture.FLASH_MODE_OFF)

    private suspend fun initializeCamera(viewFinder: PreviewView) {
        try {
            val cameraProvider = withContext(Dispatchers.IO) {
                cameraProviderFuture.get()
            }
            imageCapture.value = ImageCapture.Builder()
                .setFlashMode(flashMode.intValue)
                .build()

            val cameraSelector =
                CameraSelector.Builder().requireLensFacing(lensFacing.value).build()

            if (cameraProvider.hasCamera(cameraSelector)) {
                withContext(Dispatchers.Main) {
                    val preview = Preview.Builder().build().also {
                        it.setSurfaceProvider(viewFinder.surfaceProvider)
                    }
                    imageCapture.value = ImageCapture.Builder().build()


                    cameraProvider.unbindAll()
                    cameraProvider.bindToLifecycle(
                        lifecycleOwner, cameraSelector, preview, imageCapture.value
                    )
                }
                Log.println(Log.DEBUG, "CameraX", "Initialized camera.")
            } else {
                Log.println(Log.DEBUG, "CameraX", "No camera found with selected lens facing.")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Use case binding failed", e)

        }
    }

    suspend fun startCamera(viewFinder: PreviewView) {
        val cameraProvider = withContext(Dispatchers.IO) {
            cameraProviderFuture.get()
        }
        Log.println(Log.DEBUG, "CameraX", "Start camera.")
        withContext(Dispatchers.Main) {
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(viewFinder.surfaceProvider)
                }

            imageCapture.value = ImageCapture.Builder().build()


            val cameraSelector =
                CameraSelector.Builder().requireLensFacing(lensFacing.value).build()
            try {

                cameraProvider.unbindAll()

                if (lifecycleOwner.lifecycle.currentState.isAtLeast(Lifecycle.State.STARTED)) {

                    cameraProvider.bindToLifecycle(
                        lifecycleOwner, cameraSelector, preview, imageCapture.value
                    )
                } else {
                    Log.e(
                        TAG,
                        "Cannot bind use cases to camera because lifecycleOwner is not in an active state"
                    )
                }

            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }
    }

    fun flipCamera() {
        lensFacing.value = if (CameraSelector.LENS_FACING_FRONT == lensFacing.value) {
            CameraSelector.LENS_FACING_BACK
        } else {
            CameraSelector.LENS_FACING_FRONT
        }

    }

    suspend fun takePhoto(
        viewFinder: PreviewView,
        setPhoto: (Boolean) -> Unit,
        setImageProfile: (String) -> Unit
    ): Uri? {
        val imageCapture = imageCapture.value ?: return null

        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US)
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures")
            }
        }

        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
                contentResolver,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                contentValues
            )
            .build()

        var savedUri: Uri? = null
        withContext(Dispatchers.Main) {
            imageCapture.takePicture(
                outputOptions,
                mainExecutor,
                object : ImageCapture.OnImageSavedCallback {
                    override fun onError(exc: ImageCaptureException) {
                        Log.e(TAG, "Photo capture failed: ${exc.message}", exc)
                    }

                    override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                        val msg = "Photo capture succeeded: ${output.savedUri}"
                        Log.d(TAG, msg)
                        savedUri = output.savedUri
                        showDialog(
                            savedUri ?: throw IllegalStateException("Failed to create Uri"),
                            viewFinder,
                            setPhoto,
                            setImageProfile
                        )
                    }
                }
            )
        }
        return savedUri
    }

    fun showDialog(
        uri: Uri,
        viewFinder: PreviewView,
        setPhoto: (Boolean) -> Unit,
        setImageProfile: (String) -> Unit
    ) {
        // Create a new AlertDialog
        val builder = AlertDialog.Builder(context)
        builder.setTitle("Confirm Photo")

        // Create an ImageView and set the photo as its source
        val imageView = ImageView(context)
        imageView.setImageURI(uri)
        builder.setView(imageView)

        // Add the buttons
        builder.setPositiveButton("Confirm") { _, _ ->
            // User clicked Confirm button
            // Save the photo in the gallery and set it as icon
            setPhoto(false)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                savePhotoInGallery(uri) // viewFinder, setPhoto
            }
            setImageProfile(uri.toString())

        }
        builder.setNegativeButton("Discard") { _, _ ->
            deletePhotoFromGallery(uri)
            CoroutineScope(Dispatchers.Main).launch {
                initializeCamera(viewFinder)
                startCamera(viewFinder)
            }
        }

        // Create and show the AlertDialog
        val dialog = builder.create()
        dialog.setOnShowListener {
            // Customize the 'Confirm' button
            val positiveButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE)
            positiveButton.setTextColor(PurpleBlue.toArgb())


            // Customize the 'Discard' button
            val negativeButton = dialog.getButton(AlertDialog.BUTTON_NEGATIVE)
            negativeButton.setTextColor(Color.Black.toArgb())
        }
        dialog.show()
    }

    private fun deletePhotoFromGallery(uri: Uri) {
        contentResolver.delete(uri, null, null)
    }

    @RequiresApi(Build.VERSION_CODES.P)
    private fun savePhotoInGallery(
        uri: Uri?,
        /* viewFinder: PreviewView,
        setPhoto: (Boolean) -> Unit */
    ) {
        uri?.let {
            val contentValues = ContentValues().apply {
                put(MediaStore.MediaColumns.DISPLAY_NAME, "Image_${System.currentTimeMillis()}")
                put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/YourAppName")
                }
            }

            contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
                ?.let { uri ->
                    contentResolver.openOutputStream(uri)?.let { outputStream ->
                        val source = ImageDecoder.createSource(contentResolver, it)
                        val bitmap = ImageDecoder.decodeBitmap(source)
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                        outputStream.flush()
                        outputStream.close()


                        val msg = "Photo capture succeeded: $uri"
                        Log.d(TAG, msg)
                        savedUri = uri

                    }
                }
        }
    }

    companion object {
        private const val TAG = "TeamHub"
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
    }

    fun getCamera(): Int {
        return lensFacing.value
    }

    fun changeFlash(modeFlash: Int) {
        flashMode.intValue = modeFlash
        imageCapture.value?.flashMode = flashMode.intValue
    }
}