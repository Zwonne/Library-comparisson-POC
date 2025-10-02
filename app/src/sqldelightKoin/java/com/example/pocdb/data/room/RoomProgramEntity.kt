package com.example.pocdb.data.room

data class RoomProgramEntity(
    val programUid: Int,
    val name: String?,
    val description: String?,
    val startTime: Long?,
    val endTime: Long?,
    val channelId: Int
)