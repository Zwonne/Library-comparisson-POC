package com.example.pocdb.data.room

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "program",
    indices = [
        Index(value = ["channelId"]),
        Index(value = ["start_time", "end_time"])
    ],
    foreignKeys = [
        ForeignKey(
            entity = RoomChannelEntity::class,
            parentColumns = ["channelUid"],
            childColumns = ["channelId"],
            onDelete = ForeignKey.CASCADE
        )
    ]
)
data class RoomProgramEntity(
    @PrimaryKey val programUid: Int,
    val name: String?,
    val description: String?,
    @ColumnInfo(name = "start_time") val startTime: Long?,
    @ColumnInfo(name = "end_time") val endTime: Long?,
    val channelId: Int
)