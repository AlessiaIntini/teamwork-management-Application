package it.polito.teamhub.ui.view.teamView.teamDetails

import android.graphics.Bitmap
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.toSize
import androidx.compose.ui.window.Dialog
import androidx.navigation.NavController
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import it.polito.teamhub.R
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.dataClass.team.listOfRolePrintable
import it.polito.teamhub.ui.view.component.MembersList
import it.polito.teamhub.viewmodel.TeamViewModel
import kotlinx.coroutines.delay

@Composable
fun InviteNewMember(
    teamId: Long
) {
    val showInvitationPanel = remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurface,
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { showInvitationPanel.value = true }
    ) {
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Invite new member",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            )
        }

        Icon(
            painterResource(id = R.drawable.add_members),
            contentDescription = "Add members",
            tint = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)

        )
    }
    Spacer(modifier = Modifier.padding(8.dp))
    if (showInvitationPanel.value) {
        InvitationPanel(showInvitationPanel, teamId)
    }
}

@Composable
fun MembersListTeam(
    navController: NavController,
    vmTeam: TeamViewModel,
    teamId: Long,
    memberList: List<Member>,
    memberLogged: Member
) {
    val teamList by vmTeam.teamList.collectAsState()
    val teamMembers = teamList.find { it.id == teamId }?.members
    if (teamMembers != null && memberList.isNotEmpty()) {
        val teamMembersIds = teamMembers.map { it.idMember }
        val listMembersPersonalInfo = memberList.filter { teamMembersIds.contains(it.id) }
        val currentRoute = navController.currentBackStackEntry?.destination?.route
        MembersList(
            navController = navController,
            listMembersOfTeam = listMembersPersonalInfo,
            teamMembers = teamMembers,
            menu = currentRoute?.startsWith("team") == true,
            vmTeam = vmTeam,
            teamId = teamId,
            memberLogged = memberLogged
        )
    }
}


@Composable
fun InvitationPanel(
    showInvitationPanel: MutableState<Boolean>,
    teamId: Long
) {
    val roles = listOfRolePrintable()
    val selectedRole = remember {
        mutableStateOf("")
    }
    val showMenu = remember { mutableStateOf(false) }
    val showInviteMember = remember { mutableStateOf(false) }
    val icon = if (showMenu.value)
        Icons.Filled.KeyboardArrowUp
    else
        Icons.Filled.KeyboardArrowDown
    var textFieldSize by remember { mutableStateOf(Size.Zero) }
    val scrollState = rememberScrollState()

    Dialog(
        onDismissRequest = { showInvitationPanel.value = false },
    ) {
        Box(
            contentAlignment = Alignment.TopCenter,
            modifier = Modifier
                .verticalScroll(scrollState)
                .fillMaxWidth()
                .height(500.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Top
            ) {
                Box {
                    IconButton(
                        onClick = {
                            showInvitationPanel.value = false
                        },
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(top = 5.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Rounded.Close,
                            contentDescription = "Close Icon",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                    Row(
                        modifier = Modifier
                            .padding(top = 10.dp)
                            .fillMaxWidth()
                            .align(Alignment.Center),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Invite new member",
                            color = MaterialTheme.colorScheme.onBackground,
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Box(modifier = Modifier.padding(top = 16.dp)) {
                    Row(
                        modifier = Modifier
                            .onGloballyPositioned { coordinates ->
                                //This value is used to assign to the DropDown the same width
                                textFieldSize = coordinates.size.toSize()
                            }
                            .background(
                                MaterialTheme.colorScheme.background,
                                RoundedCornerShape(10.dp)
                            )
                            .border(
                                1.dp,
                                MaterialTheme.colorScheme.onBackground,
                                RoundedCornerShape(10.dp)
                            )
                            .clip(RoundedCornerShape(10.dp))
                            .clickable { showMenu.value = !showMenu.value }
                            .padding(horizontal = 16.dp, vertical = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = selectedRole.value.ifEmpty { "Select a role" },
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier
                                .weight(1f)
                                .padding(start = 8.dp)
                        )
                        Icon(
                            icon, "contentDescription",
                            tint = MaterialTheme.colorScheme.onBackground,
                        )
                    }
                    DropdownMenu(
                        expanded = showMenu.value,
                        onDismissRequest = { showMenu.value = false },
                        modifier = Modifier
                            .width(with(LocalDensity.current) { textFieldSize.width.toDp() })
                    ) {
                        roles.forEach { role ->
                            DropdownMenuItem(
                                onClick = {
                                    selectedRole.value = role
                                    showMenu.value = false
                                    showInviteMember.value = true
                                },
                                text = {
                                    Text(
                                        text = role,
                                        color = MaterialTheme.colorScheme.onBackground,
                                        style = MaterialTheme.typography.bodyMedium
                                    )
                                }
                            )
                        }
                    }
                }
                if (showInviteMember.value) {
                    InviteMember(showInviteMember, teamId, selectedRole.value)
                }
            }
        }
    }
}

enum class UiState {
    Loading, Loaded
}

@Composable
fun InviteMember(showInviteMember: MutableState<Boolean>, teamId: Long, role: String) {
    val deeplink = "https://www.teamhub.com/invite/$role/$teamId"
    val qrCodeBitmap = generateQRCode(deeplink, 300, 300)
    var state by remember { mutableStateOf(UiState.Loading) }
    // Remember the current role
    var currentRole by remember { mutableStateOf(role) }

    // Check if the role has changed
    val roleChanged = remember { mutableStateOf(false) }
    if (currentRole != role) {
        roleChanged.value = !roleChanged.value
        currentRole = role
        state = UiState.Loading
    }

    LaunchedEffect(key1 = showInviteMember.value, key2 = roleChanged.value) {
        if (showInviteMember.value) {
            delay(1000) // delay for 1 second
            state = UiState.Loaded
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(top = 10.dp, bottom = 30.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        AnimatedContent(
            targetState = state,
            transitionSpec = {
                fadeIn(
                    animationSpec = tween(3000)
                ) togetherWith fadeOut(animationSpec = tween(100))
            },
            modifier = Modifier.clickable {
                state = when (state) {
                    UiState.Loading -> UiState.Loaded
                    UiState.Loaded -> UiState.Loaded
                }
            },
            label = "Animated Content"
        ) { targetState ->
            when (targetState) {
                UiState.Loading -> {
                    CircularProgressIndicator()
                }

                UiState.Loaded -> {
                    Image(
                        bitmap = qrCodeBitmap.asImageBitmap(),
                        contentDescription = "QR Code",
                        modifier = Modifier.size(200.dp)
                    )
                }
            }
        }

        if (state == UiState.Loaded) {
            val clipboardManager = LocalClipboardManager.current
            OutlinedTextField(
                value = deeplink,
                textStyle = TextStyle(
                    color = MaterialTheme.colorScheme.onBackground,
                ),
                onValueChange = {},
                readOnly = true,
                label = {
                    Text(
                        text = "Invite Link",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                },
                trailingIcon = {
                    IconButton(
                        onClick = {
                            // Copy to clipboard
                            clipboardManager.setText(AnnotatedString(deeplink))
                        }
                    ) {
                        Icon(
                            painter = painterResource(id = R.drawable.copy),
                            contentDescription = "Copy Icon",
                            tint = MaterialTheme.colorScheme.onBackground
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 20.dp),
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    unfocusedIndicatorColor = MaterialTheme.colorScheme.primary,
                    focusedLabelColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedLabelColor = MaterialTheme.colorScheme.onBackground,
                    focusedTextColor = MaterialTheme.colorScheme.onBackground,
                    unfocusedTextColor = MaterialTheme.colorScheme.onBackground,
                )
            )
        }
    }
}

fun generateQRCode(text: String, width: Int, height: Int): Bitmap {
    val bitMatrix: BitMatrix
    try {
        bitMatrix = MultiFormatWriter().encode(
            text,
            BarcodeFormat.QR_CODE,
            width, height, null
        )
    } catch (illegalArgumentException: IllegalArgumentException) {
        return Bitmap.createBitmap(1, 1, Bitmap.Config.RGB_565)
    }

    val pixels = IntArray(width * height)
    for (y in 0 until height) {
        val offset = y * width
        for (x in 0 until width) {
            pixels[offset + x] = if (bitMatrix[x, y]) Color.Black.toArgb() else Color.White.toArgb()
        }
    }
    val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
    bitmap.setPixels(pixels, 0, width, 0, 0, width, height)
    return bitmap
}