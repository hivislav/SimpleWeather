package ru.hivislav.simpleweather.model.repository

import retrofit2.Callback
import ru.hivislav.simpleweather.model.entities.Weather
import ru.hivislav.simpleweather.model.entities.rest_entities.WeatherDTO

interface Repository {
    fun getWeatherFromServer(lat: Double, lon: Double, callback: Callback<WeatherDTO>)
    fun getWeatherFromLocalStorageRus(): List<Weather>
    fun getWeatherFromLocalStorageWorld(): List<Weather>
}