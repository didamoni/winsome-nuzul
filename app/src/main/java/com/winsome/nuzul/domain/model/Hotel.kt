package com.winsome.nuzul.domain.model

data class Hotel(
    val id: String,
    val name: String,
    val city: String,
    val rating: Double,
    val pricePerNight: Money,
    val images: List<String>,
    val amenities: List<String>,
    val description: String,
    val location: Location,
    val isFavorite: Boolean = false,
    val isFeatured: Boolean = false
)

