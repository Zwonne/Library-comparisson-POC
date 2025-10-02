package com.example.pocdb

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.withTransaction
import com.example.pocdb.data.room.ChannelDao
import com.example.pocdb.data.room.Database
import com.example.pocdb.data.room.RoomChannelEntity
import com.example.pocdb.data.room.ProgramDao
import com.example.pocdb.data.room.RoomChannelWithPrograms
import com.example.pocdb.data.room.RoomProgramEntity
import com.example.pocdb.domain.DomainChannel
import com.example.pocdb.domain.DomainProgram
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneOffset
import javax.inject.Inject
import kotlin.random.Random

@HiltViewModel
class MainViewModel @Inject constructor(
    private val channelsRoomDao: ChannelDao,
    private val programsRoomDao: ProgramDao,
    private val roomDb: Database
) : ViewModel() {

    init {
        Log.d("MainViewModel", "init")
    }

    //region Room

    fun upsertRoom() {
        viewModelScope.launch {
            withContext(Dispatchers.Default) {
                Log.d("MainViewModel", "upsertRoom() Will generate channel entities.")
                var channelsWithPrograms: List<RoomChannelWithPrograms>? = generateRoomChannelEntities()
                var counterOfHundreds = 0

//                channelsWithPrograms.chunked(100).map { chunk ->
//                    async {
//                        val channels = chunk.map { it.channel }
//                        val programs = chunk.flatMap { it.programs }
//
//                        roomDb.withTransaction {
//                            channelsRoomDao.upsert(channels)
//                            programsRoomDao.upsert(programs)
//                        }
//                    }
//                }.awaitAll()

                val channels = channelsWithPrograms!!.map { it.channel }
                val programs = channelsWithPrograms.flatMap { it.programs }
                channelsWithPrograms = null

                Log.d("MainViewModel", "upsertRoom() Generated channel entities. Will now start the transaction")
                roomDb.withTransaction {
                    channelsRoomDao.upsert(channels)
                    programsRoomDao.upsert(programs)
                }
                Log.d("MainViewModel", "upsertRoom() function end")
            }
        }
    }

    fun collectAllRoomChannels() {
        viewModelScope.launch {
            Log.d("MainViewModel", "collectAllRoomChannels() Will now start collecting channels")
            val channelList = channelsRoomDao.getAllChannels()
            Log.d("MainViewModel", "collectAllRoomChannels() Room Channel Entities changed. Number of channels in DB: ${channelList.size}")
            Log.d("MainViewModel", "collectAllRoomChannels() Will now start mapping to domain models")
            withContext(Dispatchers.Default) {
                channelList.map {
                    DomainChannel(
                        id = it.channel.channelUid.toLong(),
                        name = it.channel.name,
                        programs = it.programs.map {
                            DomainProgram(
                                id = it.programUid.toLong(),
                                name = it.name!!,
                                description = it.description,
                                startTime = it.startTime!!,
                                endTime = it.endTime!!
                            )
                        }
                    )
                }
            }
            Log.d("MainViewModel", "collectAllRoomChannels() Domain models mapped.")
        }

//        channelsRoomDao.getAllChannelsFlow()
//            .onEach { channelList ->
//                Log.d("MainViewModel", "collectAllRoomChannels() Room Channel Entities changed. Number of channels in DB: ${channelList.size}")
//            }
//            .launchIn(viewModelScope)
    }

    @SuppressLint("NewApi")
    fun collectRoomChannelsForToday() {
        viewModelScope.launch {
            val todayStart = LocalDate.now().atStartOfDay(ZoneOffset.UTC).toInstant().toEpochMilli()
            val todayEnd = LocalDate.now().atTime(LocalTime.MAX).atZone(ZoneOffset.UTC).toInstant().toEpochMilli()

            Log.d("MainViewModel", "collectRoomChannelsForToday() Will now start collecting channels")
            val channelList = channelsRoomDao.getAllChannelsForTimeline(
                start = todayStart,
                end = todayEnd,
                limit = 100,
                offset = 0
            )
            Log.d("MainViewModel", "collectRoomChannelsForToday() Room Channel Entities changed. Number of channels in DB: ${channelList.size}")
            Log.d("MainViewModel", "collectRoomChannelsForToday() Will now start mapping to domain models")
            withContext(Dispatchers.Default) {
                channelList.map {
                    DomainChannel(
                        id = it.channel.channelUid.toLong(),
                        name = it.channel.name,
                        programs = it.programs.map {
                            DomainProgram(
                                id = it.programUid.toLong(),
                                name = it.name!!,
                                description = it.description,
                                startTime = it.startTime!!,
                                endTime = it.endTime!!
                            )
                        }
                    )
                }
            }
            Log.d("MainViewModel", "collectRoomChannelsForToday() Domain models mapped.")
        }
    }

    fun collectAllRoomPrograms() {
        viewModelScope.launch {
            Log.d("MainViewModel", "collectRoomChannelsForToday() Will now start collecting programs")
            val programList = programsRoomDao.getAllPrograms()
            Log.d("MainViewModel", "collectAllRoomPrograms() Room Program Entities changed. Number of programs in DB: ${programList.size}")
            Log.d("MainViewModel", "collectAllRoomPrograms() Will now start mapping to domain models")
            withContext(Dispatchers.Default) {
                val domainObjects = programList.map {
                    DomainProgram(
                        id = it.programUid.toLong(),
                        name = it.name!!,
                        description = it.description,
                        startTime = it.startTime!!,
                        endTime = it.endTime!!
                    )
                }
            }
            Log.d("MainViewModel", "collectAllRoomPrograms() Domain models mapped.")
        }

//        programsRoomDao.getAllProgramsFlow()
//            .onEach { programList ->
//                Log.d("MainViewModel", "collectAllRoomPrograms() Room Program Entities changed. Number of programs in DB: ${programList.size}")
//            }
//            .launchIn(viewModelScope)
    }

    fun deleteAllRoomEntities() {
        viewModelScope.launch {
            Log.d("MainViewModel", "deleteAllRoomEntities() Will now start deleting channels and programs")
            channelsRoomDao.deleteAll()
            Log.d("MainViewModel", "deleteAllRoomEntities() Function end")
        }
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

    //endregion
}