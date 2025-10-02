package com.example.pocdb.di

import android.content.Context
import androidx.room.Room
import com.example.pocdb.data.room.ChannelDao
import com.example.pocdb.data.room.Database
import com.example.pocdb.data.room.ProgramDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
class DiModule {

    @Singleton
    @Provides
    fun provideRoomDb(@ApplicationContext context: Context): Database = Room.databaseBuilder(context, Database::class.java, "room-db").build()

    @Provides
    fun provideChannelsRoomDao(db: Database): ChannelDao = db.channelDao()

    @Provides
    fun provideProgramsRoomDao(db: Database): ProgramDao = db.programDao()
}