package com.winsome.nuzul.domain.util

import org.junit.Assert.assertEquals
import org.junit.Test
import java.time.LocalDate

class BookingDateValidatorTest {

    private val today = LocalDate.of(2023, 6, 1)

    @Test
    fun validate_validDatesAndRooms_returnsValid() {
        val checkIn = LocalDate.of(2023, 6, 5)
        val checkOut = LocalDate.of(2023, 6, 8)
        val rooms = 1

        val result = BookingDateValidator.validate(
            checkIn = checkIn,
            checkOut = checkOut,
            rooms = rooms,
            today = today
        )

        assertEquals(BookingDateValidator.ValidationResult.Valid, result)
    }

    @Test
    fun validate_zeroRooms_returnsInvalidRoomCount() {
        val checkIn = LocalDate.of(2023, 6, 5)
        val checkOut = LocalDate.of(2023, 6, 8)
        val rooms = 0

        val result = BookingDateValidator.validate(
            checkIn = checkIn,
            checkOut = checkOut,
            rooms = rooms,
            today = today
        )

        assertEquals(BookingDateValidator.ValidationResult.Invalid.InvalidRoomCount, result)
    }

    @Test
    fun validate_negativeRooms_returnsInvalidRoomCount() {
        val checkIn = LocalDate.of(2023, 6, 5)
        val checkOut = LocalDate.of(2023, 6, 8)
        val rooms = -1

        val result = BookingDateValidator.validate(
            checkIn = checkIn,
            checkOut = checkOut,
            rooms = rooms,
            today = today
        )

        assertEquals(BookingDateValidator.ValidationResult.Invalid.InvalidRoomCount, result)
    }

    @Test
    fun validate_checkInInPast_returnsCheckInInPast() {
        val checkIn = LocalDate.of(2023, 5, 31) // Before today (June 1)
        val checkOut = LocalDate.of(2023, 6, 3)
        val rooms = 1

        val result = BookingDateValidator.validate(
            checkIn = checkIn,
            checkOut = checkOut,
            rooms = rooms,
            today = today
        )

        assertEquals(BookingDateValidator.ValidationResult.Invalid.CheckInInPast, result)
    }

    @Test
    fun validate_checkOutSameAsCheckIn_returnsCheckOutNotAfterCheckIn() {
        val checkIn = LocalDate.of(2023, 6, 5)
        val checkOut = LocalDate.of(2023, 6, 5)
        val rooms = 1

        val result = BookingDateValidator.validate(
            checkIn = checkIn,
            checkOut = checkOut,
            rooms = rooms,
            today = today
        )

        assertEquals(BookingDateValidator.ValidationResult.Invalid.CheckOutNotAfterCheckIn, result)
    }

    @Test
    fun validate_checkOutBeforeCheckIn_returnsCheckOutNotAfterCheckIn() {
        val checkIn = LocalDate.of(2023, 6, 5)
        val checkOut = LocalDate.of(2023, 6, 4)
        val rooms = 1

        val result = BookingDateValidator.validate(
            checkIn = checkIn,
            checkOut = checkOut,
            rooms = rooms,
            today = today
        )

        assertEquals(BookingDateValidator.ValidationResult.Invalid.CheckOutNotAfterCheckIn, result)
    }

    @Test
    fun validate_checkInEqualToToday_returnsValid() {
        val checkIn = LocalDate.of(2023, 6, 1) // Equal to today
        val checkOut = LocalDate.of(2023, 6, 2)
        val rooms = 1

        val result = BookingDateValidator.validate(
            checkIn = checkIn,
            checkOut = checkOut,
            rooms = rooms,
            today = today
        )

        assertEquals(BookingDateValidator.ValidationResult.Valid, result)
    }
}
