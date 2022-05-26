package ru.hivislav.simpleweather.viewmodel

import ru.hivislav.simpleweather.model.Weather

sealed class AppState{
    data class Loading(val progress: Int): AppState()
    data class Success(val weatherData: List<Weather>): AppState()
    data class Error(val error: Throwable): AppState()
}
