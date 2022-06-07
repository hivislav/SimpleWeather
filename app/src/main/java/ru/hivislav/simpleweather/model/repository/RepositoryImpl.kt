package ru.hivislav.simpleweather.model.repository

import com.google.gson.GsonBuilder
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.hivislav.simpleweather.model.WeatherApi
import ru.hivislav.simpleweather.model.entities.getRussianCities
import ru.hivislav.simpleweather.model.entities.getWorldCities
import ru.hivislav.simpleweather.model.entities.rest_entities.WeatherDTO
import ru.hivislav.simpleweather.utils.YANDEX_API_KEY_VALUE
import ru.hivislav.simpleweather.utils.YANDEX_API_URL

class RepositoryImpl: Repository {
    private val retrofit = Retrofit.Builder()
        .baseUrl(YANDEX_API_URL)
        .addConverterFactory(
            GsonConverterFactory.create(
                GsonBuilder().setLenient().create()
            )
        )
        .build().create(WeatherApi::class.java)

    override fun getWeatherFromServer(lat: Double, lon: Double, callback: Callback<WeatherDTO>) {
        //отправляем ассинхронный запрос
        retrofit.getWeather(YANDEX_API_KEY_VALUE, lat, lon).enqueue(callback)
    }

    override fun getWeatherFromLocalStorageRus() = getRussianCities()

    override fun getWeatherFromLocalStorageWorld() = getWorldCities()
}