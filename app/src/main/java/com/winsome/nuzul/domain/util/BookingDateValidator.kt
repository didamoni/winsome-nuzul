package com.winsome.nuzul.domain.util

import java.time.LocalDate

object BookingDateValidator {

    sealed interface ValidationResult {
        data object Valid : ValidationResult
        sealed class Invalid : ValidationResult {
            data object CheckInInPast : Invalid()
            data object CheckOutNotAfterCheckIn : Invalid()
            data object InvalidRoomCount : Invalid()
        }
    }

    fun validate(
        checkIn: LocalDate,
        checkOut: LocalDate,
        rooms: Int,
        today: LocalDate = LocalDate.now()
    ): ValidationResult {
        if (rooms <= 0) {
            return ValidationResult.Invalid.InvalidRoomCount
        }
        if (checkIn.isBefore(today)) {
            return ValidationResult.Invalid.CheckInInPast
        }
        if (!checkOut.isAfter(checkIn)) {
            return ValidationResult.Invalid.CheckOutNotAfterCheckIn
        }
        return ValidationResult.Valid
    }
}