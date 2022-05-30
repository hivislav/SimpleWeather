package ru.hivislav.simpleweather.model.repository

import ru.hivislav.simpleweather.model.WeatherLoader
import ru.hivislav.simpleweather.model.entities.Weather
import ru.hivislav.simpleweather.model.entities.getRussianCities
import ru.hivislav.simpleweather.model.entities.getWorldCities

class RepositoryImpl: Repository {
    override fun getWeatherFromServer(lat: Double, lon: Double): Weather {
        val dto = WeatherLoader.loadWeather(lat, lon)
        return Weather(
            temperature = dto?.fact?.temp ?: 0,
            feelsLike = dto?.fact?.feelsLike ?: 0,
            condition = dto?.fact?.condition
        )
    }

    override fun getWeatherFromLocalStorageRus() = getRussianCities()

    override fun getWeatherFromLocalStorageWorld() = getWorldCities()
}