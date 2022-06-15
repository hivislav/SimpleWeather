package ru.hivislav.simpleweather.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import ru.hivislav.simpleweather.model.repository.RepositoryLocalImpl


class HistoryViewModel(private val liveData: MutableLiveData<AppStateMain> = MutableLiveData(),
                       private val repositoryLocalImpl: RepositoryLocalImpl = RepositoryLocalImpl()
) : ViewModel() {

    fun getLiveData() = liveData


    fun getAllHistory() {
        liveData.value = AppStateMain.Loading(0)
        viewModelScope.launch { (Dispatchers.Main)
            val task = async(Dispatchers.IO){ repositoryLocalImpl.getAllHistoryWeather() }
            val listWeather = task.await()
            if (isActive) {
                liveData.postValue(AppStateMain.Success(listWeather))
            }
        }
    }
}