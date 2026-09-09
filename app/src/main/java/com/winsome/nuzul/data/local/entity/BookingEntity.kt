package com.winsome.nuzul.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

@Entity(
    tableName = "bookings",
    indices = [
        Index(value = ["checkIn"]),
        Index(value = ["createdAt"])
    ]
)
data class BookingEntity(
    @PrimaryKey
    val reference: String,
    val hotelId: String,
    val hotelName: String,
    val hotelCity: String,
    val hotelImages: List<String>,
    val pricePerNightAmount: BigDecimal,
    val currency: String,
    val checkIn: LocalDate,
    val checkOut: LocalDate,
    val rooms: Int,
    val nights: Int,
    val basePriceAmount: BigDecimal,
    val vatAmount: BigDecimal,
    val totalPriceAmount: BigDecimal,
    val createdAt: LocalDateTime
)