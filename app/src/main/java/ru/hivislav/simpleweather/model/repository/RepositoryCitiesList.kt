package ru.hivislav.simpleweather.model.repository

import ru.hivislav.simpleweather.model.entities.Weather

interface RepositoryCitiesList {
    fun getWeatherFromLocalStorageRus(): List<Weather>
    fun getWeatherFromLocalStorageWorld(): List<Weather>
}