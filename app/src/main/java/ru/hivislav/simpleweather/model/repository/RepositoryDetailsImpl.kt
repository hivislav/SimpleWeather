package ru.hivislav.simpleweather.model.repository

import com.google.gson.GsonBuilder
import retrofit2.Callback
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import ru.hivislav.simpleweather.model.WeatherApi
import ru.hivislav.simpleweather.model.entities.rest_entities.WeatherDTO
import ru.hivislav.simpleweather.utils.YANDEX_API_KEY_VALUE
import ru.hivislav.simpleweather.utils.YANDEX_API_URL

class RepositoryDetailsImpl: RepositoryDetails {
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
}