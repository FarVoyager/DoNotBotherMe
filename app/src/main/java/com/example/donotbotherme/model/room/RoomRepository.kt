package com.example.donotbotherme.model.room

import com.example.donotbotherme.model.DisturbCondition
import com.example.donotbotherme.model.IRepository

class RoomRepository(private val db: Database): IRepository {
    override fun getData(): List<DisturbCondition> {
        val roomData = db.roomConditionDao.getAll()
        val convertedData = mutableListOf<DisturbCondition>()
        for (i in roomData.indices) {
            convertedData.add(
                DisturbCondition(
                    roomData[i].contactName,
                    roomData[i].contactNumber,
                    roomData[i].timeStart,
                    roomData[i].timeEnd,
                    roomData[i].isMondayBlocked == 1,
                    roomData[i].isTuesdayBlocked == 1,
                    roomData[i].isWednesdayBlocked == 1,
                    roomData[i].isThursdayBlocked == 1,
                    roomData[i].isFridayBlocked == 1,
                    roomData[i].isSaturdayBlocked == 1,
                    roomData[i].isSundayBlocked == 1,
                )
            )
        }

        return convertedData
    }

    override fun insertData(data: DisturbCondition) {
        val roomCondition = RoomCondition(
            null, data.contactName, data.contactNumber, data.timeStart, data.timeEnd,
            if (data.isMondayBlocked) 1 else 0,
            if (data.isTuesdayBlocked) 1 else 0,
            if (data.isWednesdayBlocked) 1 else 0,
            if (data.isThursdayBlocked) 1 else 0,
            if (data.isFridayBlocked) 1 else 0,
            if (data.isSaturdayBlocked) 1 else 0,
            if (data.isSundayBlocked) 1 else 0
        )
        db.roomConditionDao.insert(roomCondition)
    }
}