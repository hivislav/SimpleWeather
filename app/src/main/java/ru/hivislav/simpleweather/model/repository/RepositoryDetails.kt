package ru.hivislav.simpleweather.model.repository

import retrofit2.Callback
import ru.hivislav.simpleweather.model.entities.rest_entities.WeatherDTO

interface RepositoryDetails {
    fun getWeatherFromServer(lat: Double, lon: Double, callback: Callback<WeatherDTO>)

}