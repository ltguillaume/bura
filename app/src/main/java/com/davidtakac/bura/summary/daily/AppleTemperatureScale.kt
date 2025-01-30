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

package com.davidtakac.bura.summary.daily

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.geometry.RoundRect
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.unit.dp
import com.davidtakac.bura.common.AppTheme

@Composable
fun AppleTemperatureScale(
    minCelsius: Double,
    nowCelsius: Double?,
    maxCelsius: Double,
    absMinCelsius: Double,
    absMaxCelsius: Double,
    modifier: Modifier = Modifier
) {
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant
    val nowColor = MaterialTheme.colorScheme.onSurface
    val nowOutlineThickness = with(LocalDensity.current) { 4.dp.toPx() }
    val gradient = AppTheme.colors.temperatureColors(absMinCelsius, absMaxCelsius)
    val layoutDirection = LocalLayoutDirection.current
    Canvas(
        modifier = Modifier
            .height(6.dp)
            .then(modifier)
            .clip(RoundedCornerShape(percent = 100))
            .background(Brush.horizontalGradient(
                colors = gradient,
                startX = if (layoutDirection == LayoutDirection.Ltr) 0f else Float.POSITIVE_INFINITY,
                endX = if (layoutDirection == LayoutDirection.Ltr) Float.POSITIVE_INFINITY else 0f
            ))
    ) {
        val range = absMaxCelsius - absMinCelsius
        val pillMinPerc = (minCelsius - absMinCelsius) / range
        val pillMaxPerc = 1 - (absMaxCelsius - maxCelsius) / range
        val pillLeft =
            if (this.layoutDirection == LayoutDirection.Ltr) (pillMinPerc * size.width).toFloat()
            else ((1 - pillMaxPerc) * size.width).toFloat()
        val pillRight =
            if (this.layoutDirection == LayoutDirection.Ltr) (pillMaxPerc * size.width).toFloat()
            else ((1 - pillMinPerc) * size.width).toFloat()
        // Coerce makes the pill at least a circle shape when the day's temperature range
        // is very small, like 0-2C difference between min and max temps
        val pillWidth = (pillRight - pillLeft).coerceAtLeast(size.height)
        nowCelsius?.let {
            val nowPerc = (it - absMinCelsius) / range
            val nowLeft =
                if (this.layoutDirection == LayoutDirection.Ltr) (nowPerc * size.width).toFloat()
                else ((1 - nowPerc) * size.width).toFloat()
            val nowRadius = size.height / 2
            val nowOffset =
                if (this.layoutDirection == LayoutDirection.Ltr) -nowRadius
                else nowRadius
            val nowCenter = Offset(
                x = (nowLeft + nowOffset).coerceIn(
                    minimumValue = pillLeft + nowRadius,
                    maximumValue = pillRight - nowRadius
                ),
                y = nowRadius
            )
            drawCircle(
                color = backgroundColor,
                radius = nowRadius,
                center = nowCenter,
                style = Stroke(width = nowOutlineThickness)
            )
            drawCircle(
                color = nowColor,
                radius = nowRadius,
                center = nowCenter
            )
        }
        clipPath(
            path = Path().apply {
                addRoundRect(
                    RoundRect(
                        rect = Rect(
                            offset = Offset(x = pillLeft, y = 0f),
                            size = Size(width = pillWidth, height = size.height)
                        ),
                        cornerRadius = CornerRadius(x = size.height, y = size.height)
                    )
                )
            },
            clipOp = ClipOp.Difference
        ) {
            drawRect(
                color = backgroundColor,
                size = size,
            )
        }
    }
}

@Preview
@Composable
private fun AppleTemperatureScalePreview() {
    AppTheme {
        AppleTemperatureScale(
            minCelsius = 0.0,
            maxCelsius = 20.0,
            nowCelsius = 2.0,
            absMinCelsius = -5.0,
            absMaxCelsius = 22.0,
            modifier = Modifier.width(200.dp)
        )
    }
}

@Preview
@Composable
private fun AppleTemperatureScaleRtlPreview() {
    AppTheme {
        CompositionLocalProvider(LocalLayoutDirection provides LayoutDirection.Rtl) {
            AppleTemperatureScale(
                minCelsius = 0.0,
                maxCelsius = 20.0,
                nowCelsius = 2.0,
                absMinCelsius = -5.0,
                absMaxCelsius = 22.0,
                modifier = Modifier.width(200.dp)
            )
        }
    }
}