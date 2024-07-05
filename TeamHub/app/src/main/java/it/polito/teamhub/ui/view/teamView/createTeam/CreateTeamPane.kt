package it.polito.teamhub.ui.view.teamView.createTeam

import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.view.PreviewView
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.lifecycle.LifecycleOwner
import androidx.navigation.NavController
import it.polito.teamhub.R
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.dataClass.team.Role
import it.polito.teamhub.dataClass.team.TeamMember
import it.polito.teamhub.dataClass.team.TimeParticipation
import it.polito.teamhub.ui.theme.PurpleBlue
import it.polito.teamhub.ui.theme.teamColorList
import it.polito.teamhub.ui.view.component.CameraRendering
import it.polito.teamhub.ui.view.component.profileIcon.RenderProfile
import it.polito.teamhub.ui.view.component.profileIcon.SetProfileIcon
import it.polito.teamhub.ui.view.component.profileIcon.TypeProfileIcon
import it.polito.teamhub.ui.view.component.simpleVerticalScrollbar
import it.polito.teamhub.ui.view.component.topBar.TopBar
import it.polito.teamhub.ui.view.component.topBar.TopBarValue
import it.polito.teamhub.utils.camera.Camera
import it.polito.teamhub.viewmodel.MemberViewModel
import it.polito.teamhub.viewmodel.TeamViewModel
import java.util.concurrent.Executors

@Composable
fun CreateTeamPane(
    navController: NavController,
    vmTeam: TeamViewModel,
    vmMember: MemberViewModel,
    teamId: Long? = null,
    memberList: List<Member>,
    memberLogged: Member
) {

    val newMemberTeam = remember { mutableStateListOf<Long>() }
    LaunchedEffect(vmTeam.listMember) {
        newMemberTeam.apply {
            if (teamId != null) {
                val listNewMembers = vmTeam.getIdsMemberTeam(vmTeam.listMember)
                listNewMembers.forEach { id ->
                    if (!newMemberTeam.contains(id)) {
                        newMemberTeam.add(id)
                    }
                }
            } else {
                vmTeam.addNewMember(
                    vmTeam.listMember,
                )
            }
        }
    }
    if (teamId == null) {
        val teamList by vmTeam.teamList.collectAsState()
        vmTeam.defaultColor =
            teamColorList[teamList.size % teamColorList.size].toArgb().toLong()
    }
    val scrollState = rememberScrollState()
    val expanded = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val cropImageLauncher = remember { mutableStateOf<ActivityResultLauncher<Intent>?>(null) }

    val activityResultLauncher =
        rememberLauncherForActivityResult(contract = ActivityResultContracts.RequestMultiplePermissions()) { _ ->
        }
    val lifecycleOwner = LocalLifecycleOwner.current
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

    if (vmTeam.photo) {
        CameraRendering(
            viewFinder = PreviewView(context),
            camera = camera,
            setPhoto = vmTeam::changePhoto,
            setImageProfile = vmTeam::updateImageTeam
        )
    } else {
        Scaffold(
            topBar = {
                val topBarParameter = TopBarValue(
                    title = if (teamId != null) "Edit Team" else "Create Team",
                    backArrow = true,
                    vmTeam = vmTeam,
                )
                TopBar(
                    navController = navController,
                    topBarParameter = topBarParameter,
                    memberLogged = memberLogged
                )
            },

            ) { innerPadding ->

            Box(
                modifier = Modifier
                    .padding(innerPadding)
            ) {
                CreateLayout(
                    scrollState = scrollState,
                    vmTeam = vmTeam,
                    vmMember = vmMember,
                    newMemberTeam = newMemberTeam,
                    expanded = expanded,
                    context = context,
                    cropImageLauncher = cropImageLauncher,
                    camera = camera,
                    lifecycleOwner = lifecycleOwner,
                    memberLogged = memberLogged,
                    memberList = memberList
                )
            }

        }
    }

}

@Composable
fun CreateLayout(
    scrollState: ScrollState,
    vmTeam: TeamViewModel,
    vmMember: MemberViewModel,
    newMemberTeam: SnapshotStateList<Long>,
    expanded: MutableState<Boolean>,
    context: Context,
    cropImageLauncher: MutableState<ActivityResultLauncher<Intent>?>,
    camera: Camera,
    lifecycleOwner: LifecycleOwner,
    memberLogged: Member,
    memberList: List<Member>
) {
    val memberPanel = remember { mutableStateOf(false) }
    //val memberList by vmMember.getMembers().collectAsState(emptyList())
    if (memberList.isEmpty()) return
    val showDialog = remember { mutableStateOf(false) }
    val listState = rememberLazyListState()
    val configuration = LocalConfiguration.current
    val screenHeight =
        configuration.orientation == Configuration.ORIENTATION_PORTRAIT
    val currentTeamMember = remember {
        mutableStateOf(
            TeamMember(
                idMember = -1,
                fullname = "",
                role = Role.MEMBER,
                timeParticipation = TimeParticipation.FULL_TIME
            )
        )
    }

    if (showDialog.value) {
        ShowTimeParticipationDialog(
            showDialog = showDialog,
            teamMember = currentTeamMember.value,
            onDoneClicked = { selectedOption ->
                showDialog.value = false
                vmTeam.updateTimeParticipation(selectedOption, currentTeamMember.value.idMember)
            })
    }

    Column(
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .fillMaxSize()
    ) {
        val modifier = if (!screenHeight) {
            Modifier
                .fillMaxHeight(0.7f)
                .fillMaxWidth(0.8f)
                .padding(start = 16.dp, end = 16.dp)
                .verticalScroll(scrollState)
                .navigationBarsPadding()
                .imePadding()
        } else {
            Modifier
                .padding(start = 16.dp, end = 16.dp)
                .verticalScroll(scrollState)
                .navigationBarsPadding()
                .imePadding()
        }
        Column(
            modifier = modifier,
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
                    lifecycleOwner = lifecycleOwner,
                    vmTeam = vmTeam,
                )
            }
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 16.dp, bottom = 16.dp),
            ) {
                Column {
                    SetInformationTeam(vmTeam)
                }
            }
            Spacer(modifier = Modifier.weight(1f))
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally,

                ) {
                Button(
                    onClick = {
                        memberPanel.value = true
                    },
                ) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = ImageVector.vectorResource(id = R.drawable.add_members),
                            contentDescription = "add new member",
                            modifier = Modifier.padding(end = 8.dp)
                        )
                        Text("Add new Member", style = MaterialTheme.typography.titleSmall)
                    }
                }
            }
        }

        if (vmTeam.memberError.isNotBlank()) {
            Row(
                modifier = Modifier
                    .padding(15.dp)
            ) {
                Column {
                    Text(
                        vmTeam.memberError,
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        if (memberPanel.value) {
            MemberPanel(
                memberPanel,
                vmTeam,
                newMemberTeam,
                memberLogged,
                memberList
            )
        }

        LazyColumn(
            modifier = Modifier
                .fillMaxWidth(if (!screenHeight) 0.8f else 1f)
                .fillMaxHeight()
                .padding(5.dp)
                .simpleVerticalScrollbar(listState)
                .background(MaterialTheme.colorScheme.background),
            state = listState
        ) {
            itemsIndexed(vmTeam.listMember.reversed()) { _, teamMember ->
                val member = memberList.find { it.id == teamMember.idMember }!!
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(bottom = 8.dp)
                        .align(Alignment.CenterHorizontally)
                        .background(
                            MaterialTheme.colorScheme.background,
                            MaterialTheme.shapes.medium
                        )
                ) {
                    DisplayMemberRow(
                        member = member,
                        teamMember = teamMember,
                        showDialog = showDialog,
                        currentTeamMember = currentTeamMember,
                        vmTeam = vmTeam,
                    )
                }
            }
        }
    }
}

@Composable
fun DisplayMemberRow(
    member: Member,
    teamMember: TeamMember,
    showDialog: MutableState<Boolean>,
    currentTeamMember: MutableState<TeamMember>,
    vmTeam: TeamViewModel
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp, vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier
                .weight(.5f)
        ) {
            Row(
                horizontalArrangement = Arrangement.Start,
                verticalAlignment = Alignment.CenterVertically
            ) {

                RenderProfile(
                    imageProfile = member.userImage,
                    initialsName = member.initialsName,
                    backgroundColor = Color.White,
                    backgroundBrush = member.colorBrush,
                    sizeTextPerson = 16.sp,
                    sizeImage = 40.dp,
                    typeProfile = TypeProfileIcon.PERSON,
                )
                /* if (member.userImage == "") {
                     Box(
                         modifier = Modifier
                             .size(40.dp)
                             .background(member.colorBrush, CircleShape),
                         contentAlignment = Alignment.Center
                     ) {
                         Text(
                             text = member.initialsName,
                             style = MaterialTheme.typography.headlineLarge,
                             fontWeight = FontWeight.Medium,
                             fontSize = 16.sp,
                             color = Color.White
                         )
                     }
                 } else {
                     AsyncImage(
                         model = Uri.parse(member.userImage),
                         contentDescription = "User image",
                         placeholder = painterResource(id = R.drawable.ic_launcher_foreground),
                         contentScale = ContentScale.Crop,
                         modifier = Modifier
                             .clip(CircleShape)
                             .size(40.dp)
                     )
                 }*/
                Text(
                    text = member.fullname,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onBackground,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(start = 6.dp, end = 6.dp)
                )
            }
        }
        Column(
            modifier = Modifier
                .weight(.2f)
                .align(Alignment.CenterVertically)
        ) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .border(1.dp, PurpleBlue, CircleShape)
                    .background(Color.Transparent, CircleShape)
            ) {
                IconButton(
                    onClick = {
                        showDialog.value = true
                        currentTeamMember.value = teamMember
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .align(Alignment.Center)
                ) {
                    Icon(
                        imageVector = ImageVector.vectorResource(id = R.drawable.time),
                        contentDescription = "time",
                        tint = PurpleBlue
                    )
                }
            }
        }
        Column(
            modifier = Modifier
                .weight(.4f)
                .align(Alignment.CenterVertically)
        ) {
            RoleDropdownMenu(teamMember, vmTeam)
        }
    }
}