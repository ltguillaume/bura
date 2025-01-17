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

package com.davidtakac.bura.graphs.pop

import com.davidtakac.bura.forecast.ForecastResult
import com.davidtakac.bura.graphs.common.GraphTime
import com.davidtakac.bura.pop.Pop
import com.davidtakac.bura.pop.PopMoment
import com.davidtakac.bura.pop.PopPeriod
import java.time.LocalDate
import java.time.LocalDateTime

fun getPopGraphs(
    now: LocalDateTime,
    popPeriod: PopPeriod
): ForecastResult<List<PopGraph>> {
    val days = popPeriod.daysFrom(now.toLocalDate()) ?: return ForecastResult.Outdated
    return ForecastResult.Success(
        data = days.mapIndexed { idx, currDay ->
            PopGraph(
                day = currDay.first().hour.toLocalDate(),
                points = buildList {
                    val firstPopTomorrow = days.getOrNull(idx + 1)?.first()
                    val currDayWithFirstMomentOfTomorrow =
                        if (firstPopTomorrow != null) currDay + firstPopTomorrow else currDay
                    val max = currDayWithFirstMomentOfTomorrow.maxBy { it.pop }
                    for (i in currDay.indices) {
                        val moment = currDay[i]
                        add(getPoint(now, moment, max))
                    }
                    if (firstPopTomorrow != null) {
                        add(getPoint(now, firstPopTomorrow, max))
                    }
                }
            )
        }
    )
}

private fun getPoint(
    now: LocalDateTime,
    moment: PopMoment,
    maxPopMoment: PopMoment
): PopGraphPoint = PopGraphPoint(
    time = GraphTime(moment.hour, now),
    pop = GraphPop(
        value = moment.pop,
        meta = if (moment == maxPopMoment) GraphPop.Meta.Maximum else GraphPop.Meta.Regular
    )
)

data class PopGraph(
    val day: LocalDate,
    val points: List<PopGraphPoint>
)

data class PopGraphPoint(
    val time: GraphTime,
    val pop: GraphPop
)

data class GraphPop(
    val value: Pop,
    val meta: Meta
) {
    enum class Meta {
        Regular, Maximum
    }
}