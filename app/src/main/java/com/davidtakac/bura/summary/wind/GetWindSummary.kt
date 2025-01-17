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

package com.davidtakac.bura.summary.wind

import com.davidtakac.bura.forecast.ForecastResult
import com.davidtakac.bura.gust.GustPeriod
import com.davidtakac.bura.wind.Wind
import com.davidtakac.bura.wind.WindPeriod
import com.davidtakac.bura.wind.WindSpeed
import java.time.LocalDateTime

fun getWindSummary(
    now: LocalDateTime,
    windPeriod: WindPeriod,
    gustPeriod: GustPeriod
): ForecastResult<WindSummary> {
    return ForecastResult.Success(
        WindSummary(
            windNow = windPeriod[now]?.wind ?: return ForecastResult.Outdated,
            gustNow = gustPeriod[now]?.speed ?: return ForecastResult.Outdated
        )
    )
}

data class WindSummary(
    val windNow: Wind,
    val gustNow: WindSpeed
)