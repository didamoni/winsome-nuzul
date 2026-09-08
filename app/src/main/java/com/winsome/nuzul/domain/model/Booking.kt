package com.winsome.nuzul.domain.model

import java.time.LocalDate
import java.time.LocalDateTime

data class Booking(
    val reference: String,
    val hotelId: String,
    val hotelName: String,
    val hotelCity: String,
    val hotelImages: List<String>,
    val pricePerNight: Money,
    val checkIn: LocalDate,
    val checkOut: LocalDate,
    val rooms: Int,
    val nights: Int,
    val basePrice: Money,
    val vat: Money,
    val totalPrice: Money,
    val createdAt: LocalDateTime
)