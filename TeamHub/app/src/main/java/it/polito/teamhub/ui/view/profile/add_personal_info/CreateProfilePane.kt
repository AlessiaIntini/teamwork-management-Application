package it.polito.teamhub.ui.view.profile.add_personal_info

import android.content.Context
import android.content.Intent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.view.PreviewView
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import com.google.firebase.auth.FirebaseAuth
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.ui.theme.gradientPairList
import it.polito.teamhub.ui.view.component.CameraRendering
import it.polito.teamhub.ui.view.component.profileIcon.SetProfileIcon
import it.polito.teamhub.ui.view.component.topBar.TopBar
import it.polito.teamhub.ui.view.component.topBar.TopBarValue
import it.polito.teamhub.utils.camera.Camera
import it.polito.teamhub.viewmodel.MemberViewModel
import it.polito.teamhub.viewmodel.TaskViewModel
import it.polito.teamhub.viewmodel.TeamViewModel
import java.util.concurrent.Executors

@Composable
fun CreateProfilePane(
    navController: NavController,
    vmMember: MemberViewModel,
    vmTask: TaskViewModel,
    vmTeam: TeamViewModel
) {
    val memberLogged by vmMember.memberLogged.collectAsState()
    val members by vmMember.getMembers().collectAsState(initial = emptyList())
    //if (members.isEmpty()) return
    val scrollState = rememberScrollState()
    val expanded = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val cropImageLauncher = remember { mutableStateOf<ActivityResultLauncher<Intent>?>(null) }

    val activityResultLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions()) { _ ->
        }
    val lifecycleOwner = LocalLifecycleOwner.current

    val currentRoute = navController.currentBackStackEntry?.destination?.route
    if (currentRoute == "profile/create") {
        val loggedUser = FirebaseAuth.getInstance().currentUser
        val initials = loggedUser?.displayName?.split(" ").let {
            if (it != null) {
                if (it.size >= 2) {
                    it[0].first().toString() + it[1].first().toString()
                } else {
                    it[0].first().toString()
                }
            } else {
                ""
            }
        }
        val nickname = loggedUser?.email?.split("@")?.firstOrNull()
        vmMember.setNameInitials(initials)
        vmMember.setFullName(loggedUser?.displayName ?: "")
        vmMember.setNickname(nickname ?: "")
        vmMember.setEmail(loggedUser?.email ?: "")
        vmMember.setPhoneNumber(loggedUser?.phoneNumber)
        vmMember.setMemberColor(
            listOf(
                gradientPairList[(members.filter { !it.isDeleted }.size) % gradientPairList.size][0],
                gradientPairList[(members.filter { !it.isDeleted }.size) % gradientPairList.size][1]
            )
        )
    }

    val camera = remember {
        Camera(
            context = context,
            lifecycleOwner = lifecycleOwner,
            imageCapture = mutableStateOf(null),
            lensFacing = mutableIntStateOf(CameraSelector.LENS_FACING_BACK),
            contentResolver = context.contentResolver,
            mainExecutor = ContextCompat.getMainExecutor(context),
            cameraExecutor = Executors.newSingleThreadExecutor(),
            activityResultLauncher = activityResultLauncher
        )
    }
    Scaffold { innerPadding ->
        BoxWithConstraints(
            modifier = Modifier.padding(innerPadding)
        ) {
            if (this.maxHeight > this.maxWidth) {
                ColumnLayout(
                    navController = navController,
                    vmMember = vmMember,
                    vmTask = vmTask,
                    vmTeam = vmTeam,
                    scrollState = scrollState,
                    expanded = expanded,
                    context = context,
                    cropImageLauncher = cropImageLauncher,
                    viewFinder = PreviewView(context),
                    camera = camera,
                    lifecycleOwner = lifecycleOwner,
                    memberLogged = memberLogged
                )
            } else {
                RowLayout(
                    navController = navController,
                    vmMember = vmMember,
                    vmTask = vmTask,
                    vmTeam = vmTeam,
                    scrollState = scrollState,
                    expanded = expanded,
                    context = context,
                    cropImageLauncher = cropImageLauncher,
                    viewFinder = PreviewView(context),
                    camera = camera,
                    lifecycleOwner = lifecycleOwner,
                    memberLogged = memberLogged
                )
            }
        }
        if (vmMember.showPopup) {
            Dialog(
                onDismissRequest = { },
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
                            "Confirm Action",
                            style = MaterialTheme.typography.headlineSmall,
                            color = MaterialTheme.colorScheme.onBackground,

                            )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            "Are you sure you want to discard your changes and go back?",
                            style = MaterialTheme.typography.bodyMedium,
                            modifier = Modifier.padding(horizontal = 16.dp),
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Row(horizontalArrangement = Arrangement.Center) {
                            Button(
                                onClick = { vmMember.showPopup = false },
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
                                    vmMember.showPopup = false
                                    navController.navigateUp()
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
    }
}

@Composable
fun ColumnLayout(
    navController: NavController,
    vmMember: MemberViewModel,
    vmTask: TaskViewModel,
    vmTeam: TeamViewModel,
    scrollState: ScrollState,
    expanded: MutableState<Boolean>,
    context: Context,
    cropImageLauncher: MutableState<ActivityResultLauncher<Intent>?>,
    viewFinder: PreviewView,
    camera: Camera,
    lifecycleOwner: LifecycleOwner,
    memberLogged: Member
) {
    if (vmMember.photo) {
        CameraRendering(
            viewFinder = viewFinder,
            camera = camera,
            setPhoto = vmMember::changePhoto,
            setImageProfile = vmMember::updateImageProfile,
        )
    } else {
        Column {
            val topBarParameter = TopBarValue(
                title = if (navController.currentBackStackEntry?.destination?.route == "profile/create") "Add Personal Info" else
                    "Edit Personal Info",
                backArrow = navController.currentBackStackEntry?.destination?.route != "profile/create",
                vmMember = vmMember,
                vmTask = vmTask,
                vmTeam = vmTeam
            )
            TopBar(navController, topBarParameter, memberLogged)
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp)
                    .verticalScroll(scrollState)
                    .navigationBarsPadding()
                    .imePadding(),
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxHeight(1 / 3f)
                        .padding(top = 16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center
                ) {
                    SetProfileIcon(
                        vmMember = vmMember,
                        expanded = expanded,
                        context = context,
                        cropImageLauncher = cropImageLauncher,
                        camera = camera,
                        lifecycleOwner = lifecycleOwner
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(top = 16.dp, bottom = 16.dp),
                ) {
                    Column {
                        SetMainInfo(vmMember)
                        SetAdditionalPersonalInfo(vmMember)
                    }
                }
            }
        }
    }
}

@Composable
fun RowLayout(
    navController: NavController,
    vmMember: MemberViewModel,
    vmTask: TaskViewModel,
    vmTeam: TeamViewModel,
    scrollState: ScrollState,
    expanded: MutableState<Boolean>,
    context: Context,
    cropImageLauncher: MutableState<ActivityResultLauncher<Intent>?>,
    viewFinder: PreviewView,
    camera: Camera,
    lifecycleOwner: LifecycleOwner,
    memberLogged: Member
) {
    if (vmMember.photo) {
        CameraRendering(
            viewFinder = viewFinder,
            camera = camera,
            setPhoto = vmMember::changePhoto,
            setImageProfile = vmMember::updateImageProfile
        )
    } else {
        Column {
            val topBarParameter = TopBarValue(
                title = if (navController.currentBackStackEntry?.destination?.route == "profile/create") "Add Personal Info" else
                    "Edit Personal Info",
                backArrow = navController.currentBackStackEntry?.destination?.route != "profile/create",
                vmMember = vmMember,
                vmTask = vmTask,
                vmTeam = vmTeam
            )
            TopBar(
                navController = navController,
                topBarParameter = topBarParameter,
                memberLogged = memberLogged
            )
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(start = 16.dp, end = 16.dp)
                    .verticalScroll(scrollState)
                    .navigationBarsPadding()
                    .imePadding(),
            ) {
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(top = 16.dp), //Set column to 1/3
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.SpaceEvenly
                ) {
                    SetProfileIcon(
                        vmMember = vmMember,
                        expanded = expanded,
                        context = context,
                        cropImageLauncher = cropImageLauncher,
                        camera = camera,
                        lifecycleOwner = lifecycleOwner
                    )
                }
                Column(
                    modifier = Modifier
                        .weight(2f)
                        .padding(top = 16.dp) // Set column to 2/3
                        .padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                ) {
                    SetMainInfo(vmMember)
                    SetAdditionalPersonalInfo(vmMember)
                }
            }
        }
    }
}