package com.example.donotbotherme.model

sealed class AppState {
    data class Success(val data: List<DisturbCondition>?): AppState()
    data class Error(val error: Throwable): AppState()
    data class Loading(val progress: Int?): AppState()
}
