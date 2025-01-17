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

import android.content.Context
import android.content.SharedPreferences
import com.davidtakac.bura.common.UserAgentProvider
import com.davidtakac.bura.condition.ConditionRepository
import com.davidtakac.bura.condition.StaticConditionRepository
import com.davidtakac.bura.forecast.ForecastConverter
import com.davidtakac.bura.forecast.ForecastDataCacher
import com.davidtakac.bura.forecast.ForecastDataDownloader
import com.davidtakac.bura.forecast.ForecastRepository
import com.davidtakac.bura.place.saved.DeletePlace
import com.davidtakac.bura.place.saved.FileSavedPlacesRepository
import com.davidtakac.bura.place.saved.GetSavedPlaces
import com.davidtakac.bura.place.saved.SavedPlacesRepository
import com.davidtakac.bura.place.search.SearchPlaces
import com.davidtakac.bura.place.selected.PrefsSelectedPlaceRepository
import com.davidtakac.bura.place.selected.SelectPlace
import com.davidtakac.bura.place.selected.SelectedPlaceRepository
import com.davidtakac.bura.temperature.StaticTemperatureRepository
import com.davidtakac.bura.temperature.TemperatureRepository
import com.davidtakac.bura.units.PrefsSelectedUnitsRepository
import com.davidtakac.bura.units.SelectedUnitsRepository

class AppContainer(private val appContext: Context) {
    val prefs: SharedPreferences get() = appContext.getSharedPreferences("prefs", Context.MODE_PRIVATE)
    private val root get() = appContext.filesDir
    private val userAgentProvider get() = UserAgentProvider(appContext)

    private val forecastCacher by lazy { ForecastDataCacher(root) }
    val forecastRepo by lazy {
        ForecastRepository(
            cacher = forecastCacher,
            downloader = ForecastDataDownloader(userAgentProvider),
            converter = ForecastConverter()
        )
    }

    private val staticTempRepo: TemperatureRepository get() = StaticTemperatureRepository(forecastRepo)
    private val staticConditionRepo: ConditionRepository get() = StaticConditionRepository(forecastRepo)

    val selectedPlaceRepo: SelectedPlaceRepository by lazy { PrefsSelectedPlaceRepository(prefs, savedPlacesRepo) }
    val selectedUnitsRepo: SelectedUnitsRepository by lazy { PrefsSelectedUnitsRepository(prefs) }

    private val savedPlacesRepo: SavedPlacesRepository by lazy { FileSavedPlacesRepository(root) }
    val getSavedPlaces get() = GetSavedPlaces(savedPlacesRepo, staticTempRepo, staticConditionRepo)
    val searchPlaces get() = SearchPlaces(userAgentProvider)
    val selectPlace get() = SelectPlace(selectedPlaceRepo, savedPlacesRepo)
    val deletePlace get() = DeletePlace(savedPlacesRepo, forecastCacher)
}