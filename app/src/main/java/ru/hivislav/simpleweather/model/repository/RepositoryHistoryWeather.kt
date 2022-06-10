package ru.hivislav.simpleweather.model.repository

import ru.hivislav.simpleweather.model.entities.Weather

interface RepositoryHistoryWeather {
    fun getAllHistoryWeather(): List<Weather>
    fun saveWeather(weather: Weather)
}