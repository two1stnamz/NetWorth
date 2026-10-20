package com.maroondevelopment.networth.presentation.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.PathFillType
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.unit.dp
import com.maroondevelopment.networth.presentation.model.ChartPointUiModel

/**
 * A line chart of portfolio values over time, drawn directly on a [Canvas].
 *
 * Values are normalised against their own min and max rather than zero, so day-to-day movement
 * stays visible even when the amounts are large. A flat series is drawn down the middle.
 */
@Composable
fun ValueLineChart(
    points: List<ChartPointUiModel>,
    lineColor: Color,
    modifier: Modifier = Modifier
) {

    Canvas(modifier = modifier) {

        if (points.isEmpty()) return@Canvas

        // Keeps the stroke and its dots clear of the top and bottom edges.
        val verticalInset = 10.dp.toPx()
        val usableHeight = (size.height - verticalInset * 2).coerceAtLeast(1f)

        val lowest = points.minOf { it.value }
        val highest = points.maxOf { it.value }
        val span = (highest - lowest).takeIf { it > 0.0 }

        val stepX = if (points.size > 1) size.width / (points.size - 1) else 0f

        fun xAt(index: Int): Float =
            if (points.size > 1) stepX * index else size.width / 2f

        fun yAt(value: Double): Float {
            // A single value, or an unchanged one, has no span to scale against.
            val ratio = span?.let { (value - lowest) / it } ?: 0.5
            return verticalInset + (usableHeight - (ratio * usableHeight)).toFloat()
        }

        val line = Path()
        val fill = Path().apply { fillType = PathFillType.NonZero }

        points.forEachIndexed { index, point ->
            val x = xAt(index)
            val y = yAt(point.value)

            if (index == 0) {
                line.moveTo(x, y)
                fill.moveTo(x, size.height)
                fill.lineTo(x, y)
            } else {
                line.lineTo(x, y)
                fill.lineTo(x, y)
            }
        }

        fill.lineTo(xAt(points.lastIndex), size.height)
        fill.close()

        drawPath(
            path = fill,
            brush = Brush.verticalGradient(
                colors = listOf(lineColor.copy(alpha = 0.28f), Color.Transparent)
            )
        )

        drawPath(
            path = line,
            color = lineColor,
            style = Stroke(
                width = 2.5.dp.toPx(),
                cap = StrokeCap.Round,
                join = StrokeJoin.Round
            )
        )

        // Individual readings are worth marking while the series is still short.
        if (points.size <= 30) {

            points.forEachIndexed { index, point ->

                drawCircle(
                    color = lineColor,
                    radius = 3.dp.toPx(),
                    center = Offset(xAt(index), yAt(point.value))
                )

            }

        }

    }
}
