package com.winsome.nuzul.data.remote

interface HotelsDataSource {
    suspend fun getCities(): List<String>
    suspend fun getHotels(): List<HotelDto>
    suspend fun getFeaturedHotelIds(): List<HotelDto>
}