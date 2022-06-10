package ru.hivislav.simpleweather.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.hivislav.simpleweather.model.repository.RepositoryDetailsImpl
import ru.hivislav.simpleweather.model.repository.RepositoryLocalImpl
import java.lang.Thread.sleep

class MainViewModel(private val liveData: MutableLiveData<AppStateMain> = MutableLiveData(),
                    private val repositoryLocalImpl: RepositoryLocalImpl = RepositoryLocalImpl()
) : ViewModel() {

    fun getLiveData(): MutableLiveData<AppStateMain> {
        return liveData
    }

    fun getWeatherFromLocalSourceRus() = getWeatherFromServer(true)

    fun getWeatherFromLocalSourceWorld() = getWeatherFromServer(false)

    private fun getWeatherFromServer(isRussian: Boolean) {
        liveData.postValue(AppStateMain.Loading(0))
        Thread {
            sleep(1000)
            if (isRussian) {
                liveData.postValue(AppStateMain.Success(repositoryLocalImpl.getWeatherFromLocalStorageRus()))
            } else {
                liveData.postValue(AppStateMain.Success(repositoryLocalImpl.getWeatherFromLocalStorageWorld()))
            }
        }.start()
    }
}