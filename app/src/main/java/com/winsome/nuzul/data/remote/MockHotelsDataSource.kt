package com.winsome.nuzul.data.remote

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.Json
import javax.inject.Inject
import kotlin.time.Duration.Companion.seconds

class MockHotelsDataSource @Inject constructor(
    @ApplicationContext private val context: Context,
    private val json: Json
) : HotelsDataSource {

    private suspend fun loadMockData(): HotelsResponse = withContext(Dispatchers.IO) {
        delay(1.seconds) // Simulate network latency

        val inputStream = context.assets.open("hotels_mock.json")
        val size = inputStream.available()
        val buffer = ByteArray(size)
        inputStream.read(buffer)
        inputStream.close()

        val jsonString = String(buffer, Charsets.UTF_8)
        json.decodeFromString<HotelsResponse>(jsonString)
    }

    override suspend fun getCities(): List<String> = loadMockData().cities

    override suspend fun getHotels(): List<HotelDto> = loadMockData().hotels

    override suspend fun getFeaturedHotelIds(): List<HotelDto> = loadMockData().let {
        val featuredIds = it.featured
        it.hotels.filter { hotel -> featuredIds.contains(hotel.id) }
    }
}