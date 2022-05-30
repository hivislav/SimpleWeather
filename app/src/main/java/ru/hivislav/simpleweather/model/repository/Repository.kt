package ru.hivislav.simpleweather.model.repository

import ru.hivislav.simpleweather.model.entities.Weather

interface Repository {
    fun getWeatherFromServer(lat: Double, lon: Double): Weather
    fun getWeatherFromLocalStorageRus(): List<Weather>
    fun getWeatherFromLocalStorageWorld(): List<Weather>
}