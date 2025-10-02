package com.example.pocdb.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface ChannelDao {

    @Transaction
    @Query("SELECT * FROM channel")
    fun getAllChannelsFlow(): Flow<List<RoomChannelWithPrograms>>

    @Transaction
    @Query("SELECT * FROM channel")
    suspend fun getAllChannels(): List<RoomChannelWithPrograms>

    @Transaction
    @Query(
        """
            SELECT c.*, p.*
            FROM channel c
            LEFT JOIN program p
                ON p.channelId = c.channelUid
                AND p.start_time <= :end
                AND p.end_time >= :start
                LIMIT :limit OFFSET :offset
        """
    )
    suspend fun getAllChannelsForTimeline(
        start: Long,
        end: Long,
        limit: Int,
        offset: Int
    ): List<RoomChannelWithPrograms>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(channel: RoomChannelEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(channels: List<RoomChannelEntity>)

    @Query("DELETE FROM channel")
    suspend fun deleteAll()
}