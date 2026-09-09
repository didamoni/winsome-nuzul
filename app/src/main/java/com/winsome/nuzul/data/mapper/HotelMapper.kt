package com.winsome.nuzul.data.mapper

import com.winsome.nuzul.data.local.entity.HotelEntity
import com.winsome.nuzul.data.local.entity.HotelWithFavorite
import com.winsome.nuzul.data.remote.HotelDto
import com.winsome.nuzul.domain.model.Hotel
import com.winsome.nuzul.domain.model.Location
import com.winsome.nuzul.domain.model.Money
import java.math.BigDecimal

fun HotelDto.toEntity(isFeatured: Boolean): HotelEntity {
    return HotelEntity(
        id = id,
        name = name,
        city = city,
        rating = rating,
        priceAmount = BigDecimal.valueOf(pricePerNight.amount),
        currency = pricePerNight.currency,
        images = images,
        amenities = amenities,
        description = description,
        latitude = location.latitude,
        longitude = location.longitude,
        address = location.address,
        isFeatured = isFeatured
    )
}

fun HotelWithFavorite.toDomain(): Hotel {
    return Hotel(
        id = hotel.id,
        name = hotel.name,
        city = hotel.city,
        rating = hotel.rating,
        pricePerNight = Money(hotel.priceAmount, hotel.currency),
        images = hotel.images,
        amenities = hotel.amenities,
        description = hotel.description,
        location = Location(
            latitude = hotel.latitude,
            longitude = hotel.longitude,
            address = hotel.address
        ),
        isFavorite = isFavorite,
        isFeatured = hotel.isFeatured
    )
}