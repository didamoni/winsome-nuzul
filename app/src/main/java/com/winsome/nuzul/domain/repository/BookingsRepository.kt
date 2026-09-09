package com.winsome.nuzul.domain.repository

import com.winsome.nuzul.domain.model.Booking
import kotlinx.coroutines.flow.Flow

interface BookingsRepository {
    /** Persists a new booking and returns the created booking with generated reference. */
    suspend fun createBooking(booking: Booking): Result<Booking>

    /** Emits all bookings, ordered by check-in date (upcoming first). */
    fun getBookings(): Flow<List<Booking>>

    /** Fetches a specific booking by reference. */
    suspend fun getBookingByReference(reference: String): Result<Booking>
}