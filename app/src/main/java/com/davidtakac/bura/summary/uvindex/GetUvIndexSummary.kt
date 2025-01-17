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

package com.davidtakac.bura.summary.uvindex

import com.davidtakac.bura.forecast.ForecastResult
import com.davidtakac.bura.uvindex.UvIndex
import com.davidtakac.bura.uvindex.UvIndexPeriod
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.temporal.ChronoUnit

fun getUvIndexSummary(
    now: LocalDateTime,
    uvIndexPeriod: UvIndexPeriod
): ForecastResult<UvIndexSummary> {
    val futureUv = uvIndexPeriod.getDay(now.toLocalDate())?.momentsFrom(now) ?: return ForecastResult.Outdated
    val protection = futureUv.protectionWindows.firstOrNull()?.let {
        if (it.startInclusive == now.truncatedTo(ChronoUnit.HOURS)) {
            if (it.endExclusive == null) {
                UseProtection.UntilEndOfDay
            } else {
                UseProtection.Until(
                    endExclusive = it.endExclusive.toLocalTime()
                )
            }
        } else {
            if (it.endExclusive == null) {
                UseProtection.FromUntilEndOfDay(
                    startInclusive = it.startInclusive.toLocalTime()
                )
            } else {
                UseProtection.FromUntil(
                    startInclusive = it.startInclusive.toLocalTime(),
                    endExclusive = it.endExclusive.toLocalTime()
                )
            }
        }
    } ?: UseProtection.None
    return ForecastResult.Success(
        UvIndexSummary(
            now = uvIndexPeriod[now]?.uvIndex ?: return ForecastResult.Outdated,
            useProtection = protection
        )
    )
}

data class UvIndexSummary(
    val now: UvIndex,
    val useProtection: UseProtection
)

sealed interface UseProtection {
    data class FromUntil(
        val startInclusive: LocalTime,
        val endExclusive: LocalTime
    ) : UseProtection

    data class Until(val endExclusive: LocalTime) : UseProtection

    data object UntilEndOfDay : UseProtection

    data class FromUntilEndOfDay(val startInclusive: LocalTime) : UseProtection

    data object None : UseProtection
}