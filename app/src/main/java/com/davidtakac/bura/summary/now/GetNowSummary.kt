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

package com.davidtakac.bura.summary.now

import com.davidtakac.bura.forecast.ForecastResult
import com.davidtakac.bura.temperature.Temperature
import com.davidtakac.bura.condition.Condition
import com.davidtakac.bura.condition.ConditionPeriod
import com.davidtakac.bura.temperature.TemperaturePeriod
import java.time.LocalDateTime

fun getNowSummary(
    now: LocalDateTime,
    tempPeriod: TemperaturePeriod,
    feelsPeriod: TemperaturePeriod,
    condPeriod: ConditionPeriod
): ForecastResult<NowSummary> {
    val tempToday = tempPeriod.getDay(now.toLocalDate()) ?: return ForecastResult.Outdated
    return ForecastResult.Success(
        NowSummary(
            temp = tempPeriod[now]?.temperature ?: return ForecastResult.Outdated,
            feelsLike = feelsPeriod[now]?.temperature ?: return ForecastResult.Outdated,
            minTemp = tempToday.minimum,
            maxTemp = tempToday.maximum,
            cond = condPeriod[now]?.condition ?: return ForecastResult.Outdated
        ),
    )
}

data class NowSummary(
    val temp: Temperature,
    val feelsLike: Temperature,
    val minTemp: Temperature,
    val maxTemp: Temperature,
    val cond: Condition
)