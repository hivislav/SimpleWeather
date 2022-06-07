package ru.hivislav.simpleweather.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import ru.hivislav.simpleweather.model.entities.rest_entities.WeatherDTO
import ru.hivislav.simpleweather.model.repository.RepositoryImpl

class DetailsViewModel(private val liveData: MutableLiveData<AppStateDetails> = MutableLiveData(),
                       private val repositoryImpl: RepositoryImpl = RepositoryImpl()
) : ViewModel() {

    fun getLiveData(): MutableLiveData<AppStateDetails> {
        return liveData
    }

    fun getWeatherFromRemoteServer(lat: Double, lon: Double) {
        liveData.postValue(AppStateDetails.Loading(0))
        repositoryImpl.getWeatherFromServer(lat, lon, callback)
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
