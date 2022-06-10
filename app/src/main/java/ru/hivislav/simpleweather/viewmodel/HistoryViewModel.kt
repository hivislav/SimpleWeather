package ru.hivislav.simpleweather.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import ru.hivislav.simpleweather.model.repository.RepositoryLocalImpl
import kotlinx.coroutines.*
import java.util.logging.Handler


class HistoryViewModel(private val liveData: MutableLiveData<AppStateMain> = MutableLiveData(),
                       private val repositoryLocalImpl: RepositoryLocalImpl = RepositoryLocalImpl()
) : ViewModel() {

    fun getLiveData() = liveData


    fun getAllHistory() {
        liveData.value = AppStateMain.Loading(0)
        val listWeather = repositoryLocalImpl.getAllHistoryWeather()
            liveData.postValue(AppStateMain.Success(listWeather))
    }
}