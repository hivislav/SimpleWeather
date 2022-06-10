package ru.hivislav.simpleweather.model.repository

import ru.hivislav.simpleweather.model.MyApp
import ru.hivislav.simpleweather.model.entities.City
import ru.hivislav.simpleweather.model.entities.Weather
import ru.hivislav.simpleweather.model.entities.getRussianCities
import ru.hivislav.simpleweather.model.entities.getWorldCities
import ru.hivislav.simpleweather.model.entities.room.HistoryWeatherEntity

class RepositoryLocalImpl:RepositoryCitiesList, RepositoryHistoryWeather {

    override fun getWeatherFromLocalStorageRus() = getRussianCities()

    override fun getWeatherFromLocalStorageWorld() = getWorldCities()

    override fun getAllHistoryWeather(): List<Weather> {
        return convertHistoryWeatherEntityToWeather(MyApp.getHistoryWeatherDao().getAllHistoryWeather())
    }

    override fun saveWeather(weather: Weather) {
        MyApp.getHistoryWeatherDao().insert(convertWeatherToHistoryWeatherEntity(weather))
    }

    private fun convertHistoryWeatherEntityToWeather(entityList: List<HistoryWeatherEntity>): List<Weather> {
        return entityList.map {
            Weather(
                City(it.city, 0.0, 0.0), it.temperature, it.feelsLike, it.icon
            )
        }
    }

    private fun convertWeatherToHistoryWeatherEntity(weather: Weather) =
        HistoryWeatherEntity(
            0,
            weather.city.name,
            weather.temperature,
            weather.feelsLike,
            weather.icon
        )
}