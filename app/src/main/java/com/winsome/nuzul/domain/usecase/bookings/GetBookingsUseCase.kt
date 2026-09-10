package com.winsome.nuzul.domain.usecase.bookings

import com.winsome.nuzul.domain.model.Booking
import com.winsome.nuzul.domain.repository.BookingsRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetBookingsUseCase @Inject constructor(
    private val bookingsRepository: BookingsRepository
) {
    operator fun invoke(): Flow<List<Booking>> {
        return bookingsRepository.getBookings()
    }
}