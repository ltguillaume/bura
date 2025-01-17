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

package com.davidtakac.bura.summary.feelslike

import com.davidtakac.bura.forecast.ForecastResult
import com.davidtakac.bura.temperature.Temperature
import com.davidtakac.bura.temperature.TemperaturePeriod
import java.time.LocalDateTime
import kotlin.math.absoluteValue

fun getFeelsLikeSummary(
    now: LocalDateTime,
    tempPeriod: TemperaturePeriod,
    feelsPeriod: TemperaturePeriod
): ForecastResult<FeelsLikeSummary> {
    val feelsNow = feelsPeriod[now]?.temperature ?: return ForecastResult.Outdated
    val actualNow = tempPeriod[now]?.temperature ?: return ForecastResult.Outdated
    return ForecastResult.Success(
        FeelsLikeSummary(
            feelsLikeNow = feelsNow,
            actualNow = actualNow,
            vsActual = calculateComparedToActual(
                actualTemp = actualNow,
                feelsLikeTemp = feelsNow
            )
        ),
    )
}

private fun calculateComparedToActual(
    actualTemp: Temperature,
    feelsLikeTemp: Temperature
): FeelsVsActual {
    val actualCelsius = actualTemp.convertTo(Temperature.Unit.DegreesCelsius).value
    val feelsCelsius = feelsLikeTemp.convertTo(Temperature.Unit.DegreesCelsius).value
    return when {
        (feelsCelsius - actualCelsius).absoluteValue < 1 -> FeelsVsActual.Similar
        feelsCelsius <= 10 -> if (feelsCelsius < actualCelsius) FeelsVsActual.Colder else FeelsVsActual.Warmer
        feelsCelsius <= 25 -> if (feelsCelsius < actualCelsius) FeelsVsActual.Cooler else FeelsVsActual.Warmer
        else -> if (feelsCelsius < actualCelsius) FeelsVsActual.Cooler else FeelsVsActual.Hotter
    }
}

data class FeelsLikeSummary(
    val feelsLikeNow: Temperature,
    val actualNow: Temperature,
    val vsActual: FeelsVsActual
)

enum class FeelsVsActual {
    Colder,
    Cooler,
    Similar,
    Warmer,
    Hotter
}