/*
 * Copyright 2024 David Takač
 *
 * This file is part of Bura.
 *
 * Bura is free software: you can redistribute it and/or modify it under the terms of the GNU General Public License as published by the Free Software Foundation, either version 3 of the License, or (at your option) any later version.
 *
 * Bura is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License along with Bura. If not, see <https://www.gnu.org/licenses/>.
 */

package com.davidtakac.bura.graphs.common

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.text.TextMeasurer
import androidx.compose.ui.text.drawText
import androidx.compose.ui.unit.LayoutDirection
import java.time.LocalTime

fun DrawScope.drawTimeAxis(
    measurer: TextMeasurer,
    args: GraphArgs,
    onStepDrawn: (index: Int, x: Float) -> Unit
) {
    for (i in 0..24) {
        val xPercent = if (layoutDirection == LayoutDirection.Ltr) i / 24f else 1 - (i / 24f)
        val xOffset = if (layoutDirection == LayoutDirection.Ltr) args.startGutter else args.endGutter - args.startGutter
        val plotWidth = size.width - args.endGutter - args.startGutter
        val x = xPercent * plotWidth + xOffset
        fun drawTimeHelperLine(onEdge: Boolean) {
            drawLine(
                color = args.axisColor,
                start = Offset(x, y = if (onEdge) 0f else args.topGutter),
                end = Offset(x, y = size.height),
                strokeWidth = args.axisWidth,
                pathEffect = if (!onEdge) PathEffect.dashPathEffect(args.axisDashIntervals.toFloatArray()) else null
            )
        }
        if (i % 6 == 0) {
            val time = LocalTime.of(if (i == 24) 0 else i, 0)
            val label = measurer.measure(
                args.axisTimeFormatter.format(time),
                style = args.axisTextStyle
            )
            drawTimeHelperLine(onEdge = i == 0 || i == 24)
            if (i != 24) {
                val textTopLeftX =
                    if (layoutDirection == LayoutDirection.Ltr) x + args.bottomAxisTextPaddingStart
                    else x - label.size.width - args.bottomAxisTextPaddingStart
                val textTopLeftXMin =
                    if (layoutDirection == LayoutDirection.Ltr) args.startGutter + args.textPaddingMinHorizontal
                    else args.endGutter + args.textPaddingMinHorizontal
                val textTopLeftXMax =
                    if (layoutDirection == LayoutDirection.Ltr) size.width - args.endGutter - label.size.width - args.textPaddingMinHorizontal
                    else size.width - args.startGutter - label.size.width - args.textPaddingMinHorizontal
                drawText(
                    textLayoutResult = label,
                    color = args.axisColor,
                    topLeft = Offset(
                        x = textTopLeftX.coerceIn(
                            minimumValue = textTopLeftXMin,
                            maximumValue = textTopLeftXMax
                        ),
                        y = size.height - args.bottomGutter + args.bottomAxisTextPaddingTop
                    )
                )
            }
        }
        onStepDrawn(i, x)
    }
}