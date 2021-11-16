package com.example.donotbotherme.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.donotbotherme.app.App
import com.example.donotbotherme.interactors.IMainInteractor
import com.example.donotbotherme.model.AppState
import com.example.donotbotherme.model.DisturbCondition
import kotlinx.coroutines.*

class MainViewModel(private val interactor: IMainInteractor): ViewModel() {

    private val liveData: MutableLiveData<AppState> = MutableLiveData()
    private var job: Job? = null
    private val coroutineScope = CoroutineScope(
        Dispatchers.IO
    + SupervisorJob()
    + CoroutineExceptionHandler { _, throwable ->
            liveData.postValue(AppState.Error(throwable))
        }
    )

    fun subscribe() : LiveData<AppState> {
        return liveData
    }

    fun getConditionsList() {
        liveData.postValue(AppState.Loading(null))
        job?.cancel()
        job = coroutineScope.launch {
            val data = interactor.getData()
            liveData.postValue(data)
        }
    }

    override fun onCleared() {
        liveData.postValue(AppState.Success(null))
        super.onCleared()
        coroutineScope.cancel()
    }
}