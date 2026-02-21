package com.idk.bmicalculator.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.collectIsDraggedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
fun HorizontalRuler(
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

    val trackHeight = 80.dp
    val tickWidthDp = 24.dp
    val tickWidthPx = with(LocalDensity.current) { tickWidthDp.toPx() }

    val totalTicks = ceil((max - min) / tickSpacing).roundToInt() + 1
    val listState = rememberLazyListState()
    val isDragged by listState.interactionSource.collectIsDraggedAsState()
    val colorFg = MaterialTheme.colorScheme.onSurface

    val scrollValue by remember(min, max, tickSpacing, tickWidthPx) {
        derivedStateOf {
            val index = listState.firstVisibleItemIndex
            val offset = listState.firstVisibleItemScrollOffset
            val exactFractionalIndex = index + (offset / tickWidthPx)
            val calculatedValue = min + (exactFractionalIndex * tickSpacing)
            calculatedValue.coerceIn(min, max)
        }
    }

    LaunchedEffect(scrollValue) {
        if (listState.isScrollInProgress) {
            onValueChange(scrollValue)
        }
    }

    LaunchedEffect(value, min, tickSpacing) {
        if (!isDragged && !listState.isScrollInProgress) {
            val offsetIndex = ((value - min) / tickSpacing).coerceAtLeast(0.0)
            val targetIndex = offsetIndex.toInt()
            val targetOffsetPx = ((offsetIndex - targetIndex) * tickWidthPx).roundToInt()
            listState.animateScrollToItem(targetIndex, targetOffsetPx)
        }
    }

    BoxWithConstraints(
        modifier = modifier.fillMaxWidth().height(trackHeight),
        contentAlignment = Alignment.TopCenter
    ) {
        val halfWidth = this.maxWidth / 2
        val horizontalPadding = halfWidth - (tickWidthDp / 2)

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(trackHeight)
                .clip(CircleShape.copy(CornerSize(20.dp)))
                .background(MaterialTheme.colorScheme.surfaceVariant)
        )

        LazyRow(
            state = listState,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .padding(top = 20.dp),
            contentPadding = PaddingValues(horizontal = horizontalPadding),
            verticalAlignment = Alignment.Top
        ) {
            items(totalTicks) { index ->
                val tickValue = min + index * tickSpacing
                val isMajor = index % majorTickFrequency == 0

                Column(
                    modifier = Modifier
                        .width(tickWidthDp)
                        .fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Top
                ) {
                    Box(
                        modifier = Modifier
                            .width(2.dp)
                            .height(if (isMajor) 20.dp else 10.dp)
                            .background(if (isMajor) colorFg else colorFg.copy(alpha = 0.6f))
                    )

                    if (isMajor) {
                        Text(
                            text = labelFormatter(tickValue),
                            fontSize = 12.sp,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    } else {
                        Spacer(Modifier.height(28.dp))
                    }
                }
            }
        }

        Box(modifier = Modifier.align(Alignment.TopCenter)) {
            Canvas(modifier = Modifier.size(20.dp).offset(y = (-5).dp)) {
                val path = Path().apply {
                    moveTo(0f, 0f)
                    lineTo(size.width, 0f)
                    lineTo(size.width / 2f, size.height)
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
