package com.example.pocdb.data.room

data class RoomChannelWithPrograms(
    val channel: RoomChannelEntity,
    val programs: List<RoomProgramEntity>
)