package com.winsome.nuzul.domain.usecase.bookings

import com.winsome.nuzul.domain.util.BookingDateValidator
import java.time.LocalDate
import javax.inject.Inject

class ValidateBookingDatesUseCase @Inject constructor() {
    operator fun invoke(
        checkIn: LocalDate,
        checkOut: LocalDate,
        rooms: Int,
        today: LocalDate = LocalDate.now()
    ): BookingDateValidator.ValidationResult {
        return BookingDateValidator.validate(checkIn, checkOut, rooms, today)
    }
}