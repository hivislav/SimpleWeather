package ru.hivislav.simpleweather.model

import android.os.Build
import androidx.annotation.RequiresApi
import com.google.gson.Gson
import ru.hivislav.simpleweather.model.entities.rest_entities.WeatherDTO
import java.io.BufferedReader
import java.io.InputStreamReader
import java.lang.Exception
import java.net.URL
import java.util.stream.Collectors
import javax.net.ssl.HttpsURLConnection

object WeatherLoader {
    //функция для загрузки данных
    fun loadWeather(lat: Double, lon: Double): WeatherDTO? {
        val uri = URL("https://api.weather.yandex.ru/v2/informers?lat=$lat&lon=$lon")
        //создаем соединение
        lateinit var urlConnection: HttpsURLConnection

        return try {
            //открываем соединение
            urlConnection = uri.openConnection() as HttpsURLConnection
            //создаем запрос на сервер
            urlConnection.requestMethod = "GET"
            //параметры доступа для запроса
            urlConnection.addRequestProperty("X-Yandex-API-Key", "9028ef0f-648d-4863-a5e3-cc4f28c11d86")
            urlConnection.readTimeout = 10000
            //считываем данные из входящего потока
            val bufferedReader = BufferedReader(InputStreamReader(urlConnection.inputStream))
            //преобразовываем ответ от сервера (JSON) в модель данных (WeatherDTO)
            val lines = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.N) {
                getLinesForOld(bufferedReader)
            } else {
                getLines(bufferedReader)
            }

            Gson().fromJson(lines, WeatherDTO::class.java)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        } finally {
            urlConnection.disconnect()
        }
    }

    //метод для старых версий
    private fun getLinesForOld(bufferedReader: BufferedReader): String {
        val rawData = StringBuilder(1024)
        var tempVariable: String?

        while (bufferedReader.readLine().also { tempVariable = it } != null) {
            rawData.append(tempVariable).append("\n")
        }
        bufferedReader.close()
        return rawData.toString()
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun getLines(bufferedReader: BufferedReader): String {
        //читаем строки и ставим разделитель
        return bufferedReader.lines().collect(Collectors.joining("\n"))
    }
}