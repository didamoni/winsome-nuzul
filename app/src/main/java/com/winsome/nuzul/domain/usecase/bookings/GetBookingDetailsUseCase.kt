package com.winsome.nuzul.domain.usecase.bookings

import com.winsome.nuzul.domain.model.Booking
import com.winsome.nuzul.domain.repository.BookingsRepository
import javax.inject.Inject

class GetBookingDetailsUseCase @Inject constructor(
    private val bookingsRepository: BookingsRepository
) {
    suspend operator fun invoke(reference: String): Result<Booking> {
        return bookingsRepository.getBookingByReference(reference)
    }
}