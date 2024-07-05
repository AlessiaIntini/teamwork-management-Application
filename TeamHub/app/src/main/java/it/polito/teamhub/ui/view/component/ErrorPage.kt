package it.polito.teamhub.ui.view.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import it.polito.teamhub.R
import it.polito.teamhub.dataClass.member.Member
import it.polito.teamhub.ui.theme.CustomFontFamily
import it.polito.teamhub.ui.theme.linearGradient
import it.polito.teamhub.ui.view.component.topBar.TopBar
import it.polito.teamhub.ui.view.component.topBar.TopBarValue

@Composable
fun ErrorPage(navController: NavController, memberLogged: Member) {
    val scrollState = rememberScrollState()
    var isColumnLayout = false
    Scaffold(
        topBar = {
            val topBarParameter = TopBarValue(
                title = "TeamHUB",
                home = true,
            )
            TopBar(navController, topBarParameter, memberLogged)
        },
    ) { innerPadding ->
        BoxWithConstraints {
            if (this.maxHeight > this.maxWidth) {
                isColumnLayout = true
                Column(
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.Start,
                    modifier = Modifier
                        .padding(innerPadding)
                        .fillMaxWidth()
                        .verticalScroll(scrollState)
                ) {
                    ErrorPageContent(isColumnLayout)
                }
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(innerPadding)
                        .verticalScroll(scrollState),
                ) {
                    ErrorPageContent(isColumnLayout)
                }
            }
        }
    }
}

@Composable
fun ErrorPageContent(isColumnLayout: Boolean) {
    val configuration = LocalConfiguration.current
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        ElevatedCard(
            modifier = if (isColumnLayout)
                Modifier
                    .padding(vertical = 30.dp, horizontal = 16.dp)
                    .fillMaxWidth(1f)
                    .height((configuration.screenHeightDp * 0.85).dp)
            else
                Modifier
                    .padding(vertical = 30.dp, horizontal = 16.dp)
                    .fillMaxWidth(0.8f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = R.drawable.error_404),
                    contentDescription = "404 Error - Page not found",
                    modifier = Modifier
                        .padding(bottom = 20.dp)
                        .size(100.dp)
                        .graphicsLayer(alpha = 0.99f)
                        .drawWithCache {
                            onDrawWithContent {
                                drawContent()
                                drawRect(
                                    linearGradient,
                                    blendMode = BlendMode.SrcAtop
                                )
                            }
                        }
                )

                Text(
                    text = "PAGE NOT FOUND",
                    style = TextStyle(
                        brush = linearGradient,
                        shadow = Shadow(
                            offset = Offset(4.0f, 2.0f),
                            blurRadius = 1f
                        ),
                        fontFamily = CustomFontFamily,
                        fontSize = 22.sp,
                        lineHeight = 22.sp,
                        letterSpacing = 0.sp
                    ),
                    modifier = Modifier.fillMaxWidth(),
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}