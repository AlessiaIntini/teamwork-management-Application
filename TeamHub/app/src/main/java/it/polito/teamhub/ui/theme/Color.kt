package it.polito.teamhub.ui.theme

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb

val DarkPurple = Color(0xFF6F2C91)
val PurpleBlue = Color(0xFF8A3FBE)
val LightPurple = Color(0xFFBB6BD9)
val RedOrange = Color(0xFFFB413A)
val Orange = Color(0xFFF2994A)
val Yellow = Color(0xFFF2C94C)
val LightGreen = Color(0xFFA2CD60)
val Green = Color(0xFF27AE60)
val LightBlue = Color(0xFF2D9CDB)
val RoyalBlue = Color(0xFF5B3DF6)
val GraySurface = Color(0xFF201C22)
val Gray2 = Color(0xFF4F4F4F)
val Gray3 = Color(0xFF828282)
val Gray4 = Color(0xFFBDBDBD)
val Gray5 = Color(0xFFE0E0E0)

val radialGradient = Brush.radialGradient(
    colors = listOf(RoyalBlue, RedOrange),
    center = Offset(50f, 50f),
    radius = 550f
)

val linearGradient = Brush.linearGradient(
    colors = listOf(RoyalBlue, RedOrange),
)

val gradientList = listOf(
    linearGradient,
    Brush.linearGradient(
        colors = listOf(RedOrange, Orange),
    ),
    Brush.linearGradient(
        colors = listOf(Orange, Yellow),
    ),
    Brush.linearGradient(
        colors = listOf(Green, Yellow),
    ),
)

val gradientPairList = listOf(
    mutableListOf(RoyalBlue.toArgb().toLong(), RedOrange.toArgb().toLong()),
    mutableListOf(RedOrange.toArgb().toLong(), Orange.toArgb().toLong()),
    mutableListOf(Orange.toArgb().toLong(), Yellow.toArgb().toLong()),
    mutableListOf(Green.toArgb().toLong(), Yellow.toArgb().toLong()),
)

val Team1 = Color(0xFF713EDC)
val Team2 = Color(0xFFB73F89)

val teamColorList = listOf(
    Team1,
    Team2,
    Orange,
    Yellow,
    LightGreen,
    Green
)