package com.example.donotbotherme.model

interface IRepository {
    fun getData(): List<DisturbCondition>
    fun insertData(data: DisturbCondition)
}