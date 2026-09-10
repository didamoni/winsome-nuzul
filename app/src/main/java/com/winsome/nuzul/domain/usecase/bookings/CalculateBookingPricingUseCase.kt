package com.winsome.nuzul.domain.usecase.bookings

import com.winsome.nuzul.domain.model.Money
import com.winsome.nuzul.domain.util.BookingCalculationEngine
import java.time.LocalDate
import javax.inject.Inject

class CalculateBookingPricingUseCase @Inject constructor() {
    operator fun invoke(
        checkIn: LocalDate,
        checkOut: LocalDate,
        rooms: Int,
        pricePerNight: Money
    ): BookingCalculationEngine.PricingBreakdown {
        return BookingCalculationEngine.calculate(checkIn, checkOut, rooms, pricePerNight)
    }
}