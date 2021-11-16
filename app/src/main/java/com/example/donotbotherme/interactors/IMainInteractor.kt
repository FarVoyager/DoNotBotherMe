package com.example.donotbotherme.interactors

import com.example.donotbotherme.model.AppState
import com.example.donotbotherme.model.DisturbCondition

interface IMainInteractor {
    fun getData(): AppState
    fun insertData(data: DisturbCondition)
}