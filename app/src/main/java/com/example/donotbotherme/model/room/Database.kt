package com.example.donotbotherme.model.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RoomCondition::class], version = 1, exportSchema = true)
abstract class Database: RoomDatabase() {
    abstract val roomConditionDao: RoomConditionDao
}