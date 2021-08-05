package com.example.donotbotherme.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize


data class DisturbCondition(
    val contactName : String? = "default",
    val contactNumber : String? = "default",
    val timeStart: String? = "default",
    val timeEnd: String? = "default",
    val isMondayBlocked : Boolean = false,
    val isTuesdayBlocked : Boolean = false,
    val isWednesdayBlocked : Boolean = false,
    val isThursdayBlocked : Boolean = false,
    val isFridayBlocked : Boolean = false,
    val isSaturdayBlocked : Boolean = false,
    val isSundayBlocked : Boolean = false,
)