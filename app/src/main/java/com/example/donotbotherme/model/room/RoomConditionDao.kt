package com.example.donotbotherme.model.room

import androidx.room.*

@Dao
interface RoomConditionDao {

    @Query("SELECT * FROM RoomCondition")
    fun getAll(): List<RoomCondition>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(vararg conditions: RoomCondition)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insert(condition: RoomCondition)

    @Update
    fun update(condition: RoomCondition)

    @Update
    fun update(vararg conditions: RoomCondition)

    @Delete
    fun delete(vararg conditions: RoomCondition)

    @Query("DELETE FROM RoomCondition")
    fun deleteAll()
}