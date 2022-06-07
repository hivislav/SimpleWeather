package ru.hivislav.simpleweather.viewmodel

import ru.hivislav.simpleweather.model.entities.rest_entities.WeatherDTO

sealed class AppStateDetails {
    data class Loading(val progress: Int): AppStateDetails()
    data class Success(val weatherDTO: WeatherDTO): AppStateDetails()
    data class Error(val error: Throwable): AppStateDetails()
}
