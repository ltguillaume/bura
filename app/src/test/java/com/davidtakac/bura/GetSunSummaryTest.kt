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

package com.davidtakac.bura

import com.davidtakac.bura.condition.Condition
import com.davidtakac.bura.condition.ConditionMoment
import com.davidtakac.bura.condition.ConditionPeriod
import com.davidtakac.bura.forecast.ForecastResult
import com.davidtakac.bura.summary.sun.Sunrise
import com.davidtakac.bura.summary.sun.Sunset
import com.davidtakac.bura.summary.sun.getSunSummary
import com.davidtakac.bura.sun.SunEvent
import com.davidtakac.bura.sun.SunMoment
import com.davidtakac.bura.sun.SunPeriod
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.Duration
import java.time.temporal.ChronoUnit

class GetSunSummaryTest {
    @Test
    fun `sunrise and sunset soon`() = runTest {
        val now = unixEpochStart
        val firstMoment = now
        val secondMoment = now.plus(2, ChronoUnit.HOURS)
        val sunPeriod = SunPeriod(
            listOf(
                SunMoment(firstMoment, event = SunEvent.Sunrise),
                SunMoment(secondMoment, event = SunEvent.Sunset)
            )
        )
        val condPeriod = ConditionPeriod(
            listOf(
                ConditionMoment(firstMoment, condition = Condition(1, true))
            )
        )

        val summary = getSunSummary(now, sunPeriod, condPeriod)
        assertEquals(
            Sunrise.WithSunsetSoon(
                time = firstMoment.toLocalTime(),
                sunset = secondMoment.toLocalTime()
            ),
            (summary as ForecastResult.Success).data
        )
    }

    @Test
    fun `sunset and sunrise soon`() = runTest {
        val now = unixEpochStart
        val firstMoment = now
        val secondMoment = now.plus(2, ChronoUnit.HOURS)
        val sunPeriod = SunPeriod(
            listOf(
                SunMoment(firstMoment, event = SunEvent.Sunset),
                SunMoment(secondMoment, event = SunEvent.Sunrise)
            )
        )
        val condPeriod = ConditionPeriod(
            listOf(
                ConditionMoment(
                    firstMoment,
                    condition = Condition(1, false)
                )
            )
        )
        assertEquals(
            Sunset.WithSunriseSoon(
                time = firstMoment.toLocalTime(),
                sunrise = secondMoment.toLocalTime()
            ),
            (getSunSummary(now, sunPeriod, condPeriod) as ForecastResult.Success).data
        )
    }

    @Test
    fun `sunrise soon but sunset in two days`() = runTest {
        val now = unixEpochStart
        val firstMoment = now
        val secondMoment = now.plus(2, ChronoUnit.DAYS)
        val sunPeriod = SunPeriod(
            listOf(
                SunMoment(firstMoment, event = SunEvent.Sunrise),
                SunMoment(secondMoment, event = SunEvent.Sunset)
            )
        )

        val condPeriod = ConditionPeriod(
            listOf(
                ConditionMoment(firstMoment, condition = Condition(1, true))
            )
        )

        assertEquals(
            Sunrise.WithSunsetLater(
                time = firstMoment.toLocalTime(),
                sunset = secondMoment
            ),
            (getSunSummary(now, sunPeriod, condPeriod) as ForecastResult.Success).data
        )
    }

    @Test
    fun `sunset soon but sunrise in two days`() = runTest {
        val now = unixEpochStart
        val firstMoment = now
        val secondMoment = now.plus(2, ChronoUnit.DAYS)
        val sunPeriod = SunPeriod(
            listOf(
                SunMoment(firstMoment, event = SunEvent.Sunset),
                SunMoment(secondMoment, event = SunEvent.Sunrise)
            )
        )
        val condPeriod = ConditionPeriod(
            listOf(
                ConditionMoment(firstMoment, condition = Condition(1, true))
            )
        )
        assertEquals(
            Sunset.WithSunriseLater(
                time = firstMoment.toLocalTime(),
                sunrise = secondMoment
            ),
            (getSunSummary(now, sunPeriod, condPeriod) as ForecastResult.Success).data
        )
    }

    @Test
    fun `sunrise later`() = runTest {
        val now = unixEpochStart
        val firstMoment = now.plus(2, ChronoUnit.DAYS)
        val sunPeriod = SunPeriod(
            listOf(
                SunMoment(firstMoment, event = SunEvent.Sunrise),
            )
        )
        val condPeriod = ConditionPeriod(
            listOf(
                ConditionMoment(firstMoment, condition = Condition(1, true))
            )
        )
        assertEquals(
            Sunrise.Later(firstMoment),
            (getSunSummary(now, sunPeriod, condPeriod) as ForecastResult.Success).data
        )
    }

    @Test
    fun `sunset later`() = runTest {
        val now = unixEpochStart
        val firstMoment = now.plus(2, ChronoUnit.DAYS)
        val sunPeriod = SunPeriod(
            listOf(
                SunMoment(firstMoment, event = SunEvent.Sunset),
            )
        )
        val condPeriod = ConditionPeriod(
            listOf(
                ConditionMoment(firstMoment, condition = Condition(1, true))
            )
        )
        assertEquals(
            Sunset.Later(firstMoment),
            (getSunSummary(now, sunPeriod, condPeriod) as ForecastResult.Success).data
        )
    }

    @Test
    fun `night currently but no sunrise in sight`() = runTest {
        val now = unixEpochStart
        val sunPeriod = null
        val condPeriod = ConditionPeriod(List(48) {
            ConditionMoment(
                now.plus(it.toLong(), ChronoUnit.HOURS),
                condition = Condition(1, false)
            )
        })
        assertEquals(
            Sunrise.OutOfSight(Duration.ofHours(48)),
            (getSunSummary(now, sunPeriod, condPeriod) as ForecastResult.Success).data
        )
    }

    @Test
    fun `day currently but no sunset in sight`() = runTest {
        val now = unixEpochStart
        val sunPeriod = null
        val condPeriod = ConditionPeriod(List(48) {
            ConditionMoment(
                now.plus(it.toLong(), ChronoUnit.HOURS),
                condition = Condition(1, true)
            )
        })
        assertEquals(
            Sunset.OutOfSight(Duration.ofHours(48)),
            (getSunSummary(now, sunPeriod, condPeriod) as ForecastResult.Success).data
        )
    }

    @Test
    fun `when no current desc returns outdated`() = runTest {
        val start = unixEpochStart
        val sunPeriod = null
        val condPeriod = ConditionPeriod(List(48) {
            ConditionMoment(
                start.plus(it.toLong(), ChronoUnit.HOURS),
                condition = Condition(1, false)
            )
        })
        val now = start.plus(48.toLong(), ChronoUnit.HOURS)
        assertEquals(ForecastResult.Outdated, getSunSummary(now, sunPeriod, condPeriod))
    }
}