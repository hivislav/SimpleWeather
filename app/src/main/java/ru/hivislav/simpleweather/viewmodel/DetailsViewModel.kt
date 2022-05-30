package ru.hivislav.simpleweather.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import ru.hivislav.simpleweather.model.repository.RepositoryImpl

class DetailsViewModel(private val liveData: MutableLiveData<AppState> = MutableLiveData(),
                       private val repositoryImpl: RepositoryImpl = RepositoryImpl()
) : ViewModel() {

    fun getLiveData(): MutableLiveData<AppState> {
        return liveData
    }

    fun loadData(lat: Double, lon: Double) {
        liveData.postValue(AppState.Loading(0))
        Thread {
                val data = repositoryImpl.getWeatherFromServer(lat, lon)
                liveData.postValue(AppState.Success(listOf(data)))
        }.start()
    }
}
