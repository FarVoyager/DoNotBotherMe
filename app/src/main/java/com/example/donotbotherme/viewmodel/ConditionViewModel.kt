package com.example.donotbotherme.viewmodel

import androidx.lifecycle.ViewModel
import com.example.donotbotherme.interactors.IMainInteractor
import com.example.donotbotherme.model.DisturbCondition

class ConditionViewModel(private val interactor: IMainInteractor): ViewModel() {

    fun insertData(data: DisturbCondition) {
        interactor.insertData(data)
    }

}