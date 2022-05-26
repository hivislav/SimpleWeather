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

    fun getWeatherFromLocalSourceRus() = getWeatherFromServer(true)

    fun getWeatherFromLocalSourceWorld() = getWeatherFromServer(false)

    fun getWeatherFromRemoteSource() = getWeatherFromServer(true)//заглушка

    fun getWeatherFromServer(isRussian: Boolean) {
        liveData.postValue(AppState.Loading(0))
        Thread {
            sleep(2000)
            if (isRussian) {
                liveData.postValue(AppState.Success(repositoryImpl.getWeatherFromLocalStorageRus()))
            } else {
                liveData.postValue(AppState.Success(repositoryImpl.getWeatherFromLocalStorageWorld()))
            }
        }.start()
    }
}