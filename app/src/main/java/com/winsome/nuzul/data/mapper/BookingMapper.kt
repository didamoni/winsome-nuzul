package com.winsome.nuzul.data.mapper

import com.winsome.nuzul.data.local.entity.BookingEntity
import com.winsome.nuzul.domain.model.Booking
import com.winsome.nuzul.domain.model.Money

fun Booking.toEntity(): BookingEntity {
    return BookingEntity(
        reference = reference,
        hotelId = hotelId,
        hotelName = hotelName,
        hotelCity = hotelCity,
        hotelImages = hotelImages,
        pricePerNightAmount = pricePerNight.amount,
        currency = pricePerNight.currency,
        checkIn = checkIn,
        checkOut = checkOut,
        rooms = rooms,
        nights = nights,
        basePriceAmount = basePrice.amount,
        vatAmount = vat.amount,
        totalPriceAmount = totalPrice.amount,
        createdAt = createdAt
    )
}

fun BookingEntity.toDomain(): Booking {
    return Booking(
        reference = reference,
        hotelId = hotelId,
        hotelName = hotelName,
        hotelCity = hotelCity,
        hotelImages = hotelImages,
        pricePerNight = Money(pricePerNightAmount, currency),
        checkIn = checkIn,
        checkOut = checkOut,
        rooms = rooms,
        nights = nights,
        basePrice = Money(basePriceAmount, currency),
        vat = Money(vatAmount, currency),
        totalPrice = Money(totalPriceAmount, currency),
        createdAt = createdAt
    )
}