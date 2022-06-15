package ru.hivislav.simpleweather.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.hivislav.simpleweather.model.entities.Weather
import ru.hivislav.simpleweather.model.entities.rest_entities.WeatherDTO
import ru.hivislav.simpleweather.model.repository.RepositoryDetailsImpl
import ru.hivislav.simpleweather.model.repository.RepositoryLocalImpl

class DetailsViewModel(private val liveData: MutableLiveData<AppStateDetails> = MutableLiveData(),
                       private val repositoryDetailsImpl: RepositoryDetailsImpl = RepositoryDetailsImpl(),
                       private val repositoryLocalImpl: RepositoryLocalImpl = RepositoryLocalImpl()
) : ViewModel() {

    fun getLiveData(): MutableLiveData<AppStateDetails> {
        return liveData
    }

    fun getWeatherFromRemoteServer(lat: Double, lon: Double) {
        liveData.postValue(AppStateDetails.Loading(0))
        repositoryDetailsImpl.getWeatherFromServer(lat, lon, callback)
    }

    fun saveWeather(weather: Weather){
        viewModelScope.launch(Dispatchers.IO) {
            repositoryLocalImpl.saveWeather(weather)
        }
    }

    private val callback = object: Callback<WeatherDTO> {
        override fun onResponse(call: Call<WeatherDTO>, response: Response<WeatherDTO>) {
            if (response.isSuccessful) {
                response.body()?.let {
                    liveData.postValue(AppStateDetails.Success(it))
                }
            } else {
                val error = Throwable(response.code().toString())
                liveData.postValue(AppStateDetails.Error(error))
            }
        }

        override fun onFailure(call: Call<WeatherDTO>, t: Throwable) {
            liveData.postValue(AppStateDetails.Error(t))
        }
    }
}
