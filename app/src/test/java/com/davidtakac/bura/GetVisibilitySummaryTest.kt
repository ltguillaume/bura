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

import com.davidtakac.bura.forecast.ForecastResult
import com.davidtakac.bura.summary.visibility.getVisibilitySummary
import com.davidtakac.bura.visibility.Visibility
import com.davidtakac.bura.visibility.VisibilityMoment
import com.davidtakac.bura.visibility.VisibilityPeriod
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.temporal.ChronoUnit

class GetVisibilitySummaryTest {
    private val period = VisibilityPeriod(
        listOf(
            VisibilityMoment(unixEpochStart, Visibility.fromMeters(1.0)),
            VisibilityMoment(
                unixEpochStart.plus(1, ChronoUnit.HOURS),
                Visibility.fromMeters(2.0)
            ),
            VisibilityMoment(
                unixEpochStart.plus(2, ChronoUnit.HOURS),
                Visibility.fromMeters(3.0)
            )
        )
    )

    @Test
    fun `gets distance and description of now`() = runTest {
        val now = unixEpochStart.plus(1, ChronoUnit.HOURS).plus(10, ChronoUnit.MINUTES)
        assertEquals(
            Visibility.fromMeters(2.0),
            (getVisibilitySummary(now, period) as ForecastResult.Success).data.now
        )
    }

    @Test
    fun `summary is outdated when no now`() = runTest {
        val now = unixEpochStart.plus(3, ChronoUnit.HOURS).plus(10, ChronoUnit.MINUTES)
        assertEquals(ForecastResult.Outdated, getVisibilitySummary(now, period))
    }
}