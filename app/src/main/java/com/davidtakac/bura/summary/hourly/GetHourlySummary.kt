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

package com.davidtakac.bura.summary.hourly

import com.davidtakac.bura.condition.Condition
import com.davidtakac.bura.condition.ConditionPeriod
import com.davidtakac.bura.forecast.ForecastResult
import com.davidtakac.bura.pop.Pop
import com.davidtakac.bura.pop.PopPeriod
import com.davidtakac.bura.sun.SunEvent
import com.davidtakac.bura.sun.SunPeriod
import com.davidtakac.bura.temperature.Temperature
import com.davidtakac.bura.temperature.TemperaturePeriod
import java.time.LocalDateTime

fun getHourlySummary(
    now: LocalDateTime,
    tempPeriod: TemperaturePeriod,
    popPeriod: PopPeriod,
    condPeriod: ConditionPeriod,
    sunPeriod: SunPeriod?
): ForecastResult<List<HourSummary>> {
    val futureTemps = tempPeriod.momentsFrom(now, takeMoments = 24) ?: return ForecastResult.Outdated
    val futurePops = popPeriod.momentsFrom(now, takeMoments = 24) ?: return ForecastResult.Outdated
    val futureDesc = condPeriod.momentsFrom(now, takeMoments = 24) ?: return ForecastResult.Outdated
    val combinedWeatherData = buildList {
        for (i in futureTemps.indices) {
            add(
                HourSummary.Weather(
                    time = futureTemps[i].hour,
                    isNow = i == 0,
                    temp = futureTemps[i].temperature,
                    pop = futurePops[i].pop.takeIf { it.value > 0 },
                    desc = futureDesc[i].condition
                )
            )
        }
    }
    val combinedSunData = sunPeriod
        ?.momentsFrom(now, takeMomentsUpToHoursInFuture = 24)
        ?.map {
            HourSummary.Sun(
                time = it.time,
                event = it.event
            )
        }
        ?: listOf()

    return ForecastResult.Success((combinedWeatherData + combinedSunData).sortedBy { it.time })
}

sealed interface HourSummary {
    val time: LocalDateTime

    data class Weather(
        override val time: LocalDateTime,
        val isNow: Boolean,
        val temp: Temperature,
        val pop: Pop?,
        val desc: Condition
    ) : HourSummary

    data class Sun(
        override val time: LocalDateTime,
        val event: SunEvent
    ) : HourSummary
}