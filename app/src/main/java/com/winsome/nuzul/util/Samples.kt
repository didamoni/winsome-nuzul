package com.winsome.nuzul.util

import com.winsome.nuzul.domain.model.Booking
import com.winsome.nuzul.domain.model.Hotel
import com.winsome.nuzul.domain.model.Location
import com.winsome.nuzul.domain.model.Money
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime

object Samples {

    val hotels = listOf(
        Hotel(
            id = "1",
            name = "The Oberoi Beach Resort",
            city = "Hurghada",
            rating = 4.9,
            pricePerNight = Money(BigDecimal("1150.00"), "SAR"),
            images = listOf("https://images.unsplash.com/photo-1540555700478-4be289fbecef?auto=format&fit=crop&w=1200&q=80"),
            amenities = listOf("WiFi", "Beach", "Pool", "Spa"),
            description = "Luxury Red Sea beachfront resort with serene architecture.",
            location = Location(27.04, 33.87, "Sahl Hasheesh, Hurghada"),
            isFavorite = true,
            isFeatured = true
        ),
        Hotel(
            id = "2",
            name = "The Ritz-Carlton, Riyadh",
            city = "Riyadh",
            rating = 4.8,
            pricePerNight = Money(BigDecimal("1800.00"), "SAR"),
            images = listOf("https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=1200&q=80"),
            amenities = listOf("Indoor Pool", "Spa", "Valet"),
            description = "Palatial hotel surrounded by olive gardens in Riyadh.",
            location = Location(24.66, 46.63, "Al Hada District, Riyadh"),
            isFavorite = false,
            isFeatured = true
        )
    )

    val bookings = listOf(
        Booking(
            reference = "NZL-7A8B2X",
            hotelId = "hrg-oberoi-02",
            hotelName = "The Oberoi Beach Resort",
            hotelCity = "Hurghada",
            hotelImages = listOf(
                "https://images.unsplash.com/photo-1540555700478-4be289fbecef?auto=format&fit=crop&w=1200&q=80"
            ),
            pricePerNight = Money(BigDecimal("1150.00"), "SAR"),
            checkIn = LocalDate.now().plusDays(3),
            checkOut = LocalDate.now().plusDays(6),
            rooms = 1,
            nights = 3,
            basePrice = Money(BigDecimal("3450.00"), "SAR"),
            vat = Money(BigDecimal("517.50"), "SAR"),
            totalPrice = Money(BigDecimal("3967.50"), "SAR"),
            createdAt = LocalDateTime.now()
        ),
        Booking(
            reference = "OML-3A8B0Y",
            hotelId = "ruh-ritz-01",
            hotelName = "The Ritz-Carlton, Riyadh",
            hotelCity = "Riyadh",
            hotelImages = listOf("https://images.unsplash.com/photo-1566073771259-6a8506099945?auto=format&fit=crop&w=1200&q=80"),
            pricePerNight = Money(BigDecimal("1800.00"), "SAR"),
            checkIn = LocalDate.now().minusDays(10),
            checkOut = LocalDate.now().minusDays(8),
            rooms = 2,
            nights = 2,
            basePrice = Money(BigDecimal("7200.00"), "SAR"),
            vat = Money(BigDecimal("1080.00"), "SAR"),
            totalPrice = Money(BigDecimal("8280.00"), "SAR"),
            createdAt = LocalDateTime.now()
        )
    )
}
