package com.winsome.nuzul.domain.usecase.bookings

import com.winsome.nuzul.domain.model.Booking
import com.winsome.nuzul.domain.model.Hotel
import com.winsome.nuzul.domain.repository.BookingsRepository
import com.winsome.nuzul.domain.util.BookingCalculationEngine
import com.winsome.nuzul.domain.util.BookingDateValidator
import com.winsome.nuzul.domain.util.BookingReferenceGenerator
import java.time.LocalDate
import java.time.LocalDateTime
import javax.inject.Inject

class CreateBookingUseCase @Inject constructor(
    private val bookingsRepository: BookingsRepository
) {
    suspend operator fun invoke(
        hotel: Hotel,
        checkIn: LocalDate,
        checkOut: LocalDate,
        rooms: Int
    ): Result<Booking> = runCatching {
        // 1. Validate inputs
        val validation = BookingDateValidator.validate(checkIn, checkOut, rooms)
        if (validation !is BookingDateValidator.ValidationResult.Valid) {
            throw IllegalArgumentException("Invalid booking parameters: $validation")
        }

        // 2. Perform financial calculation (15% VAT)
        val pricing = BookingCalculationEngine.calculate(
            checkIn = checkIn,
            checkOut = checkOut,
            rooms = rooms,
            pricePerNight = hotel.pricePerNight
        )

        // 3. Assemble domain entity with generated reference
        val booking = Booking(
            reference = BookingReferenceGenerator.generate(),
            hotelId = hotel.id,
            hotelName = hotel.name,
            hotelCity = hotel.city,
            hotelImages = hotel.images,
            pricePerNight = hotel.pricePerNight,
            checkIn = checkIn,
            checkOut = checkOut,
            rooms = rooms,
            nights = pricing.nights,
            basePrice = pricing.basePrice,
            vat = pricing.vat,
            totalPrice = pricing.totalPrice,
            createdAt = LocalDateTime.now()
        )

        // 4. Save to Room database
        bookingsRepository.createBooking(booking).getOrThrow()
    }
}