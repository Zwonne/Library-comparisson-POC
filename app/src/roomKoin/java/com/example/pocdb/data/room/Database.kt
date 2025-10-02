package com.example.pocdb.data.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [RoomChannelEntity::class, RoomProgramEntity::class], version = 1)
abstract class Database : RoomDatabase() {
    abstract fun programDao(): ProgramDao
    abstract fun channelDao(): ChannelDao
}