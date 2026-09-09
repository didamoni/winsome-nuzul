package com.winsome.nuzul.data.remote

import kotlinx.serialization.Serializable

@Serializable
data class HotelsResponse(
    val cities: List<String>,
    val featured: List<String>,
    val hotels: List<HotelDto>
)

@Serializable
data class HotelDto(
    val id: String,
    val name: String,
    val city: String,
    val rating: Double,
    val pricePerNight: MoneyDto,
    val images: List<String>,
    val amenities: List<String>,
    val description: String,
    val location: LocationDto
)

@Serializable
data class MoneyDto(
    val amount: Double,
    val currency: String
)

@Serializable
data class LocationDto(
    val latitude: Double,
    val longitude: Double,
    val address: String
)