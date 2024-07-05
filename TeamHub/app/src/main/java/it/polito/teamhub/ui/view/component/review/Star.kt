package it.polito.teamhub.ui.view.component.review

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableIntState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import it.polito.teamhub.R

@Composable
fun RenderRate(
    rating: Float,
    modifier: Modifier = Modifier,
    numStars: Int = 5,
    size: Dp = 30.dp,
    spacing: Dp = 8.dp,
    activeColor: Color = MaterialTheme.colorScheme.primary,
    inactiveColor: Color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f),
    brush: Brush? = null
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing)
    ) {
        for (i in 1..numStars) {
            if (rating >= i)
                FullStar(activeColor, size, brush)
            else if (rating > i - 1)
                HalfStar(activeColor, size, brush)
            else
                EmptyStar(inactiveColor, size, brush)
        }
    }
}

@Composable
fun RenderClickableStar(
    clicked: MutableIntState,
    modifier: Modifier = Modifier,
    numStars: Int = 5,
    size: Dp = 30.dp,
    spacing: Dp = 8.dp,
    activeColor: Color = MaterialTheme.colorScheme.primary,
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(spacing),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 1..numStars) {
            IconButton(
                modifier = Modifier
                    .size(size),
                onClick = { clicked.intValue = i }
            ) {
                if (i > clicked.intValue)
                    EmptyStar(activeColor, size)
                else
                    FullStar(activeColor, size)
            }
        }
    }
}

@Composable
fun FullStar(
    color: Color,
    size: Dp,
    brush: Brush? = null
) {
    Icon(
        painter = painterResource(id = R.drawable.star_full),
        contentDescription = "Star",
        tint = color,
        modifier =
        if (brush != null) {
            Modifier
                .size(size)
                .graphicsLayer(alpha = 0.99f)
                .drawWithCache {
                    onDrawWithContent {
                        drawContent()
                        drawRect(
                            brush,
                            blendMode = BlendMode.SrcAtop
                        )
                    }
                }
        } else {
            Modifier.size(size)
        }
    )

}

@Composable
fun HalfStar(
    color: Color,
    size: Dp,
    brush: Brush? = null
) {
    Icon(
        painter = painterResource(id = R.drawable.star_half),
        contentDescription = "Half Star",
        tint = color,
        modifier =
        if (brush != null) {
            Modifier
                .size(size)
                .graphicsLayer(alpha = 0.99f)
                .drawWithCache {
                    onDrawWithContent {
                        drawContent()
                        drawRect(
                            brush,
                            blendMode = BlendMode.SrcAtop
                        )
                    }
                }
        } else {
            Modifier.size(size)
        }
    )
}

@Composable
fun EmptyStar(
    color: Color,
    size: Dp,
    brush: Brush? = null
) {
    Icon(
        painter = painterResource(id = R.drawable.star_empty),
        contentDescription = "Empty Star",
        tint = color,
        modifier =
        if (brush != null) {
            Modifier
                .size(size)
                .graphicsLayer(alpha = 0.99f)
                .drawWithCache {
                    onDrawWithContent {
                        drawContent()
                        drawRect(
                            brush,
                            blendMode = BlendMode.SrcAtop
                        )
                    }
                }
        } else {
            Modifier.size(size)
        }
    )
}