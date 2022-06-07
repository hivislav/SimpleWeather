package ru.hivislav.simpleweather.viewmodel

import ru.hivislav.simpleweather.model.entities.Weather
import ru.hivislav.simpleweather.model.entities.rest_entities.WeatherDTO

sealed class AppStateMain{
    data class Loading(val progress: Int): AppStateMain()
    data class Success(val weatherData: List<Weather>): AppStateMain()
    data class Error(val error: Throwable, val weatherData: List<Weather>): AppStateMain()
}
