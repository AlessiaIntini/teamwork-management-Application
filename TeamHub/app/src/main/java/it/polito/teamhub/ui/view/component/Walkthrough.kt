package it.polito.teamhub.ui.view.component

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.PagerState
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import it.polito.teamhub.R
import it.polito.teamhub.ui.theme.CustomFontFamily
import it.polito.teamhub.ui.theme.Gray4
import it.polito.teamhub.ui.theme.linearGradient
import kotlinx.coroutines.launch

data class WalkThroughData(
    val icon: Int,
    val title: String,
    val description: String
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun Walkthrough(signIn: () -> Unit) {
    val items = ArrayList<WalkThroughData>()

    items.add(
        WalkThroughData(
            icon = R.drawable.collaboration,
            "Join your teams",
            "Easily connect with your teams for seamless collaboration"
        )
    )
    items.add(
        WalkThroughData(
            R.drawable.list,
            "Manage your tasks",
            " Stay organized and on top of your tasks effortlessly"
        )
    )
    items.add(
        WalkThroughData(
            R.drawable.discussion,
            "Chat with your colleagues",
            "Communicate instantly with your team members for quick exchanges."
        )
    )
    items.add(
        WalkThroughData(
            R.drawable.statistic,
            "Look at your statistics",
            " Gain insights into your performance and progress at a glance."
        )
    )
    val pagerState = rememberPagerState(
        pageCount = { items.size },
        initialPageOffsetFraction = 0f,
        initialPage = 0,
    )
    OnBoardingPager(
        item = items,
        pagerState = pagerState,
        signIn = signIn
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnBoardingPager(
    item: List<WalkThroughData>,
    pagerState: PagerState,
    modifier: Modifier = Modifier,
    signIn: () -> Unit
) {
    val scrollState = rememberScrollState()
    var isColumnLayout by remember { mutableStateOf(false) }
    BoxWithConstraints(
        modifier = modifier
            .verticalScroll(scrollState)
            .background(color = MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        if (this.maxHeight > this.maxWidth)
            isColumnLayout = true
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 30.dp, start = 16.dp, end = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text(
                text = "TeamHUB",
                style = TextStyle(
                    brush = linearGradient,
                    fontFamily = CustomFontFamily,
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 36.sp,
                    lineHeight = 36.sp,
                    letterSpacing = 0.sp
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 10.dp),
                textAlign = TextAlign.Center
            )
            HorizontalPager(state = pagerState) { page ->
                Column(
                    modifier = Modifier
                        .padding(top = 20.dp)
                        .fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Image(
                        painter = painterResource(id = item[page].icon),
                        contentDescription = item[page].title,
                        modifier = Modifier
                            .height(if (isColumnLayout) 250.dp else 50.dp)
                            .fillMaxWidth()
                    )
                    Text(
                        text = item[page].title,
                        modifier = Modifier.padding(top = 40.dp),
                        color = MaterialTheme.colorScheme.onBackground,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center
                    )
                    Text(
                        text = item[page].description,
                        modifier = Modifier
                            .height(110.dp)
                            .padding(top = 30.dp, start = 20.dp, end = 20.dp),
                        color = Gray4,
                        style = MaterialTheme.typography.bodyLarge,
                        fontSize = 18.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }
            PagerIndicator(item.size, pagerState.currentPage)
            BottomSection(pagerState, signIn)
        }
    }
}

@Composable
fun PagerIndicator(size: Int, currentPage: Int) {
    Row(
        horizontalArrangement = Arrangement.SpaceBetween,
        modifier = Modifier.padding(vertical = 30.dp)
    ) {
        repeat(size) {
            Indicator(isSelected = it == currentPage)
        }
    }
}

@Composable
fun Indicator(isSelected: Boolean) {
    val width = animateDpAsState(targetValue = if (isSelected) 25.dp else 10.dp, label = "")

    Box(
        modifier = Modifier
            .padding(1.dp)
            .height(10.dp)
            .width(width.value)
            .clip(CircleShape)
            .background(
                if (isSelected) MaterialTheme.colorScheme.primary else Gray4
            )
    )
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun BottomSection(
    pagerState: PagerState,
    signIn: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (pagerState.currentPage == pagerState.pageCount - 1) {
            Button(
                onClick = { signIn() },
                modifier = Modifier
                    .padding(start = 20.dp, end = 20.dp, bottom = 50.dp, top = 10.dp)
                    .fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.background,
                    contentColor = MaterialTheme.colorScheme.onBackground
                ),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.onBackground),
                contentPadding = PaddingValues(16.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.google),
                    contentDescription = "Google Icon",
                    modifier = Modifier.padding(end = 16.dp)
                )
                Text(
                    text = "Sign in with Google",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }
        } else {
            Row {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .padding(horizontal = 20.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp))
                        .background(brush = linearGradient),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                ) {
                    Text(
                        text = "Next",
                        color = Color.White,
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            Row {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            pagerState.animateScrollToPage(pagerState.pageCount - 1)
                        }
                    },
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .padding(horizontal = 16.dp, vertical = 10.dp)
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(16.dp)),
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Transparent),
                ) {
                    Text(text = "Skip", color = Gray4, style = MaterialTheme.typography.bodyLarge)
                }
            }
        }
    }
}



