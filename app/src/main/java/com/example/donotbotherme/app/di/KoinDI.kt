package com.example.donotbotherme.app.di

import androidx.room.Room
import com.example.donotbotherme.interactors.IMainInteractor
import com.example.donotbotherme.interactors.MainInteractor
import com.example.donotbotherme.model.IRepository
import com.example.donotbotherme.model.room.Database
import com.example.donotbotherme.model.room.RoomRepository
import com.example.donotbotherme.viewmodel.MainViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

object KoinDI {
    fun getDatabaseModule() = module {
        single { Room.databaseBuilder(androidContext(), Database::class.java, "database").build() }
    }

    fun getCoreModule() = module {
        single<IRepository> { RoomRepository(db = get()) }
        single<IMainInteractor> { MainInteractor(roomRepository = get()) }

        viewModel { MainViewModel(interactor = get()) }
    }
}