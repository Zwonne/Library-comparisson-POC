package com.example.pocdb.di

import android.content.Context
import androidx.room.Room
import com.example.pocdb.MainViewModel
import com.example.pocdb.data.room.ChannelDao
import com.example.pocdb.data.room.Database
import com.example.pocdb.data.room.ProgramDao
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.module

val appKoinModule = module {
    single<Database> {
        val context: Context = get()
        Room.databaseBuilder<Database>(context, Database::class.java, "room-db").build()
    }

    factory<ChannelDao> {
        val db: Database = get()
        db.channelDao()
    }

    factory<ProgramDao> {
        val db: Database = get()
        db.programDao()
    }

    viewModelOf(::MainViewModel)
}