package com.example.donotbotherme.interactors

import com.example.donotbotherme.model.AppState
import com.example.donotbotherme.model.DisturbCondition
import com.example.donotbotherme.model.IRepository
import com.example.donotbotherme.model.room.RoomRepository

class MainInteractor(private val roomRepository: IRepository): IMainInteractor {
    override fun getData(): AppState {
        return AppState.Success(roomRepository.getData())
    }

    override fun insertData(data: DisturbCondition){
        roomRepository.insertData(data)
    }
}