package com.example.pocdb.data.room

import androidx.room.Embedded
import androidx.room.Relation

data class RoomChannelWithPrograms(
    @Embedded val channel: RoomChannelEntity,
    @Relation(
        parentColumn = "channelUid",
        entityColumn = "channelId"
    )
    val programs: List<RoomProgramEntity>
)