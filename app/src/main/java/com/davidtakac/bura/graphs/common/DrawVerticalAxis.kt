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
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.unit.LayoutDirection

fun DrawScope.drawVerticalAxis(
    steps: Int,
    args: GraphArgs,
    layoutDirection: LayoutDirection = LayoutDirection.Ltr,
    onStepDrawn: (stepFraction: Float, lineX: Float, stepY: Float) -> Unit
) {
    val lineX =
        if (layoutDirection == LayoutDirection.Ltr) size.width - args.endGutter
        else args.endGutter
    for (i in 0..steps) {
        val stepFraction = i / steps.toFloat()
        val plotBottom = size.height - args.bottomGutter
        val plotHeight = size.height - args.topGutter - args.bottomGutter
        val stepY = plotBottom - plotHeight * stepFraction
        val horizontalLineStartX =
            if (layoutDirection == LayoutDirection.Ltr) args.startGutter
            else size.width - args.startGutter
        drawLine(
            color = args.axisColor,
            start = Offset(horizontalLineStartX, stepY),
            end = Offset(lineX, stepY)
        )
        onStepDrawn(stepFraction, lineX, stepY)
    }
}