package com.example.pocdb.di

import android.content.Context
import androidx.room.Room
import com.example.pocdb.data.room.ChannelDao
import com.example.pocdb.data.room.Database
import com.example.pocdb.data.room.ProgramDao
import org.koin.core.annotation.ComponentScan
import org.koin.core.annotation.Factory
import org.koin.core.annotation.Module
import org.koin.core.annotation.Single

@Module
@ComponentScan("com.example.pocdb")
class AppKoinModule {

    @Single
    fun provideRoomDb(context: Context): Database = Room.databaseBuilder<Database>(context, Database::class.java, "room-db").build()

    @Factory
    fun provideChannelsRoomDao(db: Database): ChannelDao = db.channelDao()

    @Factory
    fun provideProgramsRoomDao(db: Database): ProgramDao = db.programDao()
}