package com.example.pocdb

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.pocdb.data.room.RoomChannelEntity
import com.example.pocdb.data.room.RoomChannelWithPrograms
import com.example.pocdb.data.room.RoomProgramEntity
import com.example.pocdb.domain.DomainChannel
import com.example.pocdb.domain.DomainProgram
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import kotlin.random.Random

class MainViewModel(
    private val sqlDelightDb: AppDatabase
) : ViewModel() {

    init {
        Log.d("MainViewModel", "init")
    }

    private fun generateRandomString(): String {
        val chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ "
        val stringLength = Random.nextInt(from = 1, until = 15)
        return (1..stringLength).map { chars.random() }.joinToString("")
    }

    private suspend fun generateRoomChannelEntities(): List<RoomChannelWithPrograms> = withContext(Dispatchers.Default) {
        val numberOfChannels = 1000
        val result = mutableListOf<RoomChannelWithPrograms>()
        var counterOfHundreds = 0

        for (i in 1..numberOfChannels) {
            launch {
                val channelEntity = RoomChannelEntity(
                    channelUid = i,
                    name = generateRandomString()
                )

                val programEntities = generateRoomProgramListForThreeDays(channelId = channelEntity.channelUid)

                result.add(RoomChannelWithPrograms(channel = channelEntity, programs = programEntities))
                if (i % 100 == 0) {
                    Log.d("MainViewModel", "generateRoomChannelEntities() Generated ${++counterOfHundreds}th hundred channel")
                }
            }
        }

        return@withContext result
    }

    var programCounter = 0

    @SuppressLint("NewApi")
    private suspend fun generateRoomProgramListForThreeDays(channelId: Int): List<RoomProgramEntity> = withContext(Dispatchers.Default) {
        val now = LocalDate.now()
        val startOfYesterday = now.minusDays(1).atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
        val endOfTomorrow = now.plusDays(1).atTime(LocalTime.MAX).atZone(ZoneOffset.UTC).toInstant().toEpochMilli()

        val result = mutableListOf<RoomProgramEntity>()
        var cursor = startOfYesterday

        while (cursor < endOfTomorrow) {
            // random duration in millis
            val durationMinutes = Random.nextInt(5, 121)
            val durationMillis = durationMinutes * 60 * 1000L

            val end = (cursor + durationMillis).coerceAtMost(endOfTomorrow)

            val programEntity = RoomProgramEntity(
                programUid = programCounter++,
                name = generateRandomString(),
                description = generateRandomString(),
                channelId = channelId,
                startTime = cursor,
                endTime = end
            )

            result.add(programEntity)

            cursor = end
        }

//        Log.d("MainViewModel", "generateRoomProgramListForThreeDays(channelId: $channelId) Returning ${result.size} programs for channel")
        return@withContext result
    }

    //region SqlDelight

    fun upsertSqlDelight() {
        viewModelScope.launch(Dispatchers.Default) {
            Log.d("MainViewModel", "upsertSqlDelight() Will generate channel entities.")
            var channelsWithPrograms: List<RoomChannelWithPrograms>? = generateRoomChannelEntities()

            val channels = channelsWithPrograms!!.map { it.channel }
            val programs = channelsWithPrograms.flatMap { it.programs }
            channelsWithPrograms = null

            Log.d("MainViewModel", "upsertSqlDelight() Generated channel entities. Will now start the transaction")
            withContext(Dispatchers.IO) {
                sqlDelightDb.transaction {
                    channels.forEach { channel ->
                        sqlDelightDb.channelQueries.upsert(
                            channel.channelUid.toLong(), channel.name
                        )
                    }

                    programs.forEach { program ->
                        sqlDelightDb.programQueries.upsert(
                            id = program.programUid.toLong(),
                            name = program.name,
                            description = program.description,
                            startTime = program.startTime,
                            endTime = program.endTime,
                            channelId = program.channelId.toLong()
                        )
                    }
                }
            }
            Log.d("MainViewModel", "upsertSqlDelight() function end")
        }
    }

    fun collectAllSqlDelightChannels() {
        viewModelScope.launch(Dispatchers.Default) {
            Log.d("MainViewModel", "collectAllSqlDelightChannels() Will now start collecting channels")
            val size = withContext(Dispatchers.IO) {
                sqlDelightDb.channelQueries.getAllChannels().executeAsList()
            }.let { rows ->
                Log.d("MainViewModel", "collectAllSqlDelightChannels() SQLDelight Channel Entities changed.")
                Log.d("MainViewModel", "collectAllSqlDelightChannels() Will now start mapping to domain models")
                rows.groupBy { it.channelId }
            }.map { (channelId, rowsForChannel) ->
                DomainChannel(
                    id = channelId,
                    name = rowsForChannel.firstOrNull()?.channelName,
                    programs = rowsForChannel.map { row ->
                        DomainProgram(
                            id = row.programId!!,
                            name = row.programName!!,
                            description = row.description,
                            startTime = row.start_time!!,
                            endTime = row.end_time!!
                        )
                    }
                )
            }.size
            Log.d("MainViewModel", "collectAllSqlDelightChannels() Domain models mapped. Number of channels in DB: ${size}")
        }
    }

    @SuppressLint("NewApi")
    fun collectSqlDelightChannelsForToday() {
        viewModelScope.launch(Dispatchers.Default) {
            val todayStart = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
            val todayEnd = LocalDate.now().atTime(LocalTime.MAX).atZone(ZoneOffset.UTC).toInstant().toEpochMilli()

            Log.d("MainViewModel", "collectSqlDelightChannelsForToday() Will now start collecting channels")
            val rows = withContext(Dispatchers.IO) {
                sqlDelightDb.channelQueries.getAllChannelsForTimeline(
                    start = todayStart,
                    end = todayEnd,
                    limit = 100,
                    offset = 0
                ).executeAsList()
            }
            Log.d("MainViewModel", "collectSqlDelightChannelsForToday() SQLDelight Channel Entities changed.")

            Log.d("MainViewModel", "collectSqlDelightChannelsForToday() Will now start mapping to domain models")
            val domainObjects = rows.groupBy { it.channelId }.map { (channelId, rowsForChannel) ->
                DomainChannel(
                    id = channelId,
                    name = rowsForChannel.firstOrNull()?.channelName,
                    programs = rows.map { row ->
                        DomainProgram(
                            id = row.programId!!,
                            name = row.programName!!,
                            description = row.description,
                            startTime = row.start_time!!,
                            endTime = row.end_time!!
                        )
                    }
                )
            }
            Log.d("MainViewModel", "collectSqlDelightChannelsForToday() Domain models mapped. Number of channels in DB: ${domainObjects.size}")
        }
    }

    fun collectAllSqlDelightPrograms() {
        viewModelScope.launch(Dispatchers.Default) {
            Log.d("MainViewModel", "collectAllSqlDelightPrograms() Will now start collecting programs")
            val rows = withContext(Dispatchers.IO) {
                sqlDelightDb.programQueries.getAllPrograms().executeAsList()
            }
            Log.d("MainViewModel", "collectAllSqlDelightPrograms() SQLDelight Program Entities changed.")

            Log.d("MainViewModel", "collectAllSqlDelightPrograms() Will now start mapping to domain models")
            val domainObjects = rows.map { row ->
                DomainProgram(
                    id = row.id,
                    name = row.name!!,
                    description = row.description,
                    startTime = row.start_time!!,
                    endTime = row.end_time!!
                )
            }
            Log.d("MainViewModel", "collectAllSqlDelightPrograms() Domain models mapped. Number of channels in DB: ${domainObjects.size}")
        }
    }

    fun deleteAllSqlDelightEntities() {
        viewModelScope.launch(Dispatchers.IO) {
            Log.d("MainViewModel", "deleteAllSqlDelightEntities() Will now start deleting channels and programs")
            sqlDelightDb.channelQueries.deleteAll()
            Log.d("MainViewModel", "deleteAllSqlDelightEntities() Function end")
        }
    }

    //endregion
}