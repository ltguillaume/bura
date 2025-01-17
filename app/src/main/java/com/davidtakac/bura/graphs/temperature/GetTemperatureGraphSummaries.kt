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

package com.davidtakac.bura.graphs.temperature

import com.davidtakac.bura.forecast.ForecastResult
import com.davidtakac.bura.temperature.Temperature
import com.davidtakac.bura.condition.Condition
import com.davidtakac.bura.condition.ConditionPeriod
import com.davidtakac.bura.temperature.TemperaturePeriod
import java.time.LocalDate
import java.time.LocalDateTime

fun getTemperatureGraphSummaries(
    now: LocalDateTime,
    tempPeriod: TemperaturePeriod,
    feelsPeriod: TemperaturePeriod,
    condPeriod: ConditionPeriod
): ForecastResult<List<TemperatureGraphSummary>> {
    val tempDays = tempPeriod.daysFrom(now.toLocalDate()) ?: return ForecastResult.Outdated
    val conditionDays = condPeriod.momentsFrom(now)?.daysFrom(now.toLocalDate()) ?: return ForecastResult.Outdated
    val feelsLikeNow = feelsPeriod[now]?.temperature ?: return ForecastResult.Outdated

    return ForecastResult.Success(
        data = tempDays.mapIndexed { idx, tempDay ->
            val day = tempDay.first().hour.toLocalDate()
            val minTemp = tempDay.minimum
            val maxTemp = tempDay.maximum
            val conditionDay = conditionDays[idx]
            val condition = conditionDay[now]?.condition ?: conditionDay.day ?: conditionDay.night!!
            val nowTemp = tempDay[now]?.temperature

            TemperatureGraphSummary(
                day = day,
                minTemp = minTemp,
                maxTemp = maxTemp,
                condition = condition,
                now = nowTemp?.let {
                    TemperatureGraphNowSummary(
                        temp = nowTemp,
                        feelsLike = feelsLikeNow
                    )
                }
            )
        }
    )
}

data class TemperatureGraphSummary(
    val day: LocalDate,
    val minTemp: Temperature,
    val maxTemp: Temperature,
    val condition: Condition,
    val now: TemperatureGraphNowSummary?
)

data class TemperatureGraphNowSummary(
    val temp: Temperature,
    val feelsLike: Temperature
)