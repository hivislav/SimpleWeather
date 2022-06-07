package ru.hivislav.simpleweather.model

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import ru.hivislav.simpleweather.model.entities.rest_entities.WeatherDTO
import ru.hivislav.simpleweather.utils.YANDEX_API_KEY
import ru.hivislav.simpleweather.utils.YANDEX_API_URL_END_POINT

interface WeatherApi {
    @GET(YANDEX_API_URL_END_POINT)
    fun getWeather(@Header(YANDEX_API_KEY) apiKey: String,
                   @Query("lat") lat: Double,
                   @Query("lon") lon: Double
    ): Call<WeatherDTO>
}