package com.example.donotbotherme.model.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class RoomCondition(
    @PrimaryKey(autoGenerate = true) val id: Int?,
    @ColumnInfo(name = "contactName") val contactName : String?,
    @ColumnInfo(name = "contactNumber") val contactNumber : String?,
    @ColumnInfo(name = "timeStart") val timeStart: String?,
    @ColumnInfo(name = "timeEnd") val timeEnd: String?,
    @ColumnInfo(name = "isMondayBlocked") val isMondayBlocked : Int,
    @ColumnInfo(name = "isTuesdayBlocked") val isTuesdayBlocked : Int,
    @ColumnInfo(name = "isWednesdayBlocked") val isWednesdayBlocked : Int,
    @ColumnInfo(name = "isThursdayBlocked") val isThursdayBlocked : Int,
    @ColumnInfo(name = "isFridayBlocked") val isFridayBlocked : Int,
    @ColumnInfo(name = "isSaturdayBlocked") val isSaturdayBlocked : Int,
    @ColumnInfo(name = "isSundayBlocked") val isSundayBlocked : Int,
) {
}