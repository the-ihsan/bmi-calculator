package com.rabeya.bmiapp.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlin.math.ceil
import kotlin.math.roundToInt


@Suppress("COMPOSE_APPLIER_CALL_MISMATCH")
@Composable
fun VerticalRuler(
    value: Double,
    tickSpacing: Double,
    min: Double,
    max: Double,
    modifier: Modifier = Modifier,
    onValueChange: (Double) -> Unit,
    labelFormatter: (Double) -> String,
    majorTickFrequency: Int = 2,
) {
    require(tickSpacing > 0 && min < max) { "Invalid tick spacing or value range" }

    val trackWidth = 80.dp
    val tickHeightDp = 24.dp
    val tickHeightPx = with(LocalDensity.current) { tickHeightDp.toPx() }

    val totalTicks = ceil((max - min) / tickSpacing).roundToInt() + 1
    val listState = rememberLazyListState()
    val isDragged by listState.interactionSource.collectIsDraggedAsState()
    val colorFg = MaterialTheme.colorScheme.onSurface

    var isProgrammaticScroll by remember { mutableStateOf(false) }

    val scrollValue by remember(min, max, tickSpacing, tickHeightPx) {
        derivedStateOf {
            val index = listState.firstVisibleItemIndex
            val offset = listState.firstVisibleItemScrollOffset
            val exactFractionalIndex = index + (offset / tickHeightPx)
            val calculatedValue = max - (exactFractionalIndex * tickSpacing)
            calculatedValue.coerceIn(min, max)
        }
    }

    LaunchedEffect(scrollValue) {
        if (listState.isScrollInProgress && !isProgrammaticScroll) {
            onValueChange(scrollValue)
        }
    }

    LaunchedEffect(value, max, tickSpacing) {
        if (!isDragged && !listState.isScrollInProgress) {
            isProgrammaticScroll = true
            try {
                val offsetIndex = ((max - value) / tickSpacing).coerceAtLeast(0.0)
                val targetIndex = offsetIndex.toInt()
                val targetOffsetPx = ((offsetIndex - targetIndex) * tickHeightPx).roundToInt()
                listState.animateScrollToItem(targetIndex, targetOffsetPx)
            } finally {
                isProgrammaticScroll = false
            }
        }
    }

    BoxWithConstraints(
        modifier = modifier.height(400.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        val halfHeight = this.maxHeight / 2
        val verticalPadding = halfHeight - (tickHeightDp / 2)

        Box(
            modifier = Modifier
                .width(trackWidth)
                .fillMaxHeight()
                .clip(CircleShape.copy(CornerSize(20.dp)))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )

        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxHeight()
                .width(trackWidth)
                .padding(end = 20.dp),
            contentPadding = PaddingValues(vertical = verticalPadding),
            horizontalAlignment = Alignment.End
        ) {
            items(totalTicks) { index ->
                val tickValue = max - index * tickSpacing
                val isMajor = index % majorTickFrequency == 0 // 🔺 Uses dynamic frequency

                Row(
                    modifier = Modifier
                        .height(tickHeightDp)
                        .fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.End
                ) {
                    if (isMajor) {
                        Text(
                            text = labelFormatter(tickValue),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(end = 8.dp)
                        )
                    } else {
                        Spacer(Modifier.width(28.dp))
                    }

                    Box(
                        modifier = Modifier
                            .height(2.dp)
                            .width(if (isMajor) 20.dp else 10.dp)
                            .background(if (isMajor) colorFg else colorFg.copy(alpha = 0.6f))
                    )
                }
            }
        }

        // Center Indicator (Canvas) remains exactly the same...
        Box(modifier = Modifier.align(Alignment.CenterStart)) {
            Canvas(modifier = Modifier.size(20.dp).offset(x = 65.dp)) {
                val path = Path().apply {
                    moveTo(size.width, 0f)
                    lineTo(size.width, size.height)
                    lineTo(0f, size.height / 2f)
                    close()
                }
                drawIntoCanvas { canvas ->
                    val paint = Paint().apply {
                        color = colorFg
                        pathEffect = PathEffect.cornerPathEffect(10f)
                    }
                    paint.asFrameworkPaint().apply {
                        setShadowLayer(12f, 0f, 4f, android.graphics.Color.argb(60, 0, 0, 0))
                    }
                    canvas.drawPath(path, paint)
                }
            }
        }
    }
}
