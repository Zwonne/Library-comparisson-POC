package com.example.pocdb.data.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ProgramDao {

    @Query("SELECT * FROM program")
    fun getAllProgramsFlow(): Flow<List<RoomProgramEntity>>

    @Query("SELECT * FROM program")
    suspend fun getAllPrograms(): List<RoomProgramEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(programs: List<RoomProgramEntity>)
}