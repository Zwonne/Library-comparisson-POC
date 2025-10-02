package com.example.pocdb.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "channel")
data class RoomChannelEntity(
    @PrimaryKey val channelUid: Int,
    val name: String?
)