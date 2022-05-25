package ru.hivislav.simpleweather.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.hivislav.simpleweather.model.RepositoryImpl
import java.lang.Thread.sleep

class MainViewModel(private val liveData: MutableLiveData<AppState> = MutableLiveData(),
                    private val repositoryImpl: RepositoryImpl = RepositoryImpl()
) : ViewModel() {


    fun getLiveData(): LiveData<AppState> {
        return liveData
    }

    fun getWeatherFromServer() {
        liveData.postValue(AppState.Loading(0))
        Thread {
            sleep(4000)
            val rand = (1..40).random()
            if (rand > 20) {
                liveData.postValue(AppState.Success(repositoryImpl.getWeatherFromServer()))
            } else {
                liveData.postValue(AppState.Error(IllegalStateException("")))
            }
        }.start()
    }

    fun getWeather() {
        return getWeatherFromServer()
    }
}