package com.winsome.nuzul.domain.repository

import com.winsome.nuzul.domain.model.Hotel
import com.winsome.nuzul.domain.model.HotelFilter
import kotlinx.coroutines.flow.Flow

interface HotelsRepository {
    /** Fetches the list of available cities. */
    suspend fun getCities(): Result<List<String>>

    /** Emits featured hotels. */
    fun getFeaturedHotels(): Flow<List<Hotel>>

    /** Fetches a specific page of hotels based on filter. */
    suspend fun getHotels(filter: HotelFilter, page: Int, pageSize: Int = 10): Result<List<Hotel>>

    /** Fetches single hotel details, ensuring a hotel item status is synchronized. */
    fun getHotelById(id: String): Flow<Hotel?>

    /** Forces a refresh of the hotel cache. */
    suspend fun refreshHotels(): Result<Unit>
}
