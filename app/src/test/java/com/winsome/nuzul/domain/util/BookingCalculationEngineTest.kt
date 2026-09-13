package com.winsome.nuzul.domain.util

import com.winsome.nuzul.domain.model.Money
import org.junit.Assert.assertEquals
import org.junit.Assert.assertThrows
import org.junit.Test
import java.math.BigDecimal
import java.time.LocalDate

class BookingCalculationEngineTest {

    private val samplePrice = Money(BigDecimal("100.00"), "USD")

    @Test
    fun calculate_singleNightSingleRoom_correctlyCalculatesBreakdown() {
        val checkIn = LocalDate.of(2023, 6, 1)
        val checkOut = LocalDate.of(2023, 6, 2) // 1 night

        val breakdown = BookingCalculationEngine.calculate(
            checkIn = checkIn,
            checkOut = checkOut,
            rooms = 1,
            pricePerNight = samplePrice
        )

        assertEquals(1, breakdown.nights)
        assertEquals(BigDecimal("100.00"), breakdown.basePrice.amount)
        assertEquals("USD", breakdown.basePrice.currency)
        assertEquals(BigDecimal("15.00"), breakdown.vat.amount)
        assertEquals("USD", breakdown.vat.currency)
        assertEquals(BigDecimal("115.00"), breakdown.totalPrice.amount)
        assertEquals("USD", breakdown.totalPrice.currency)
    }

    @Test
    fun calculate_multipleNightsMultipleRooms_correctlyCalculatesBreakdown() {
        val checkIn = LocalDate.of(2023, 6, 1)
        val checkOut = LocalDate.of(2023, 6, 4) // 3 nights
        val rooms = 2

        // Base price = 100 * 3 nights * 2 rooms = 600.00
        // VAT = 600.00 * 0.15 = 90.00
        // Total = 690.00

        val breakdown = BookingCalculationEngine.calculate(
            checkIn = checkIn,
            checkOut = checkOut,
            rooms = rooms,
            pricePerNight = samplePrice
        )

        assertEquals(3, breakdown.nights)
        assertEquals(BigDecimal("600.00"), breakdown.basePrice.amount)
        assertEquals(BigDecimal("90.00"), breakdown.vat.amount)
        assertEquals(BigDecimal("690.00"), breakdown.totalPrice.amount)
    }

    @Test
    fun calculate_zeroRooms_throwsIllegalArgumentException() {
        val checkIn = LocalDate.of(2023, 6, 1)
        val checkOut = LocalDate.of(2023, 6, 2)

        val exception = assertThrows(IllegalArgumentException::class.java) {
            BookingCalculationEngine.calculate(
                checkIn = checkIn,
                checkOut = checkOut,
                rooms = 0,
                pricePerNight = samplePrice
            )
        }
        assertEquals("Number of rooms must be greater than zero", exception.message)
    }

    @Test
    fun calculate_negativeRooms_throwsIllegalArgumentException() {
        val checkIn = LocalDate.of(2023, 6, 1)
        val checkOut = LocalDate.of(2023, 6, 2)

        val exception = assertThrows(IllegalArgumentException::class.java) {
            BookingCalculationEngine.calculate(
                checkIn = checkIn,
                checkOut = checkOut,
                rooms = -1,
                pricePerNight = samplePrice
            )
        }
        assertEquals("Number of rooms must be greater than zero", exception.message)
    }

    @Test
    fun calculate_checkOutSameAsCheckIn_throwsIllegalArgumentException() {
        val date = LocalDate.of(2023, 6, 1)

        val exception = assertThrows(IllegalArgumentException::class.java) {
            BookingCalculationEngine.calculate(
                checkIn = date,
                checkOut = date,
                rooms = 1,
                pricePerNight = samplePrice
            )
        }
        assertEquals("Check-out must be strictly after check-in", exception.message)
    }

    @Test
    fun calculate_checkOutBeforeCheckIn_throwsIllegalArgumentException() {
        val checkIn = LocalDate.of(2023, 6, 2)
        val checkOut = LocalDate.of(2023, 6, 1)

        val exception = assertThrows(IllegalArgumentException::class.java) {
            BookingCalculationEngine.calculate(
                checkIn = checkIn,
                checkOut = checkOut,
                rooms = 1,
                pricePerNight = samplePrice
            )
        }
        assertEquals("Check-out must be strictly after check-in", exception.message)
    }

    @Test
    fun calculate_roundingCheck_roundsHalfUpCorrectly() {
        // Price per night with fractional cents or decimals requiring rounding
        val oddPrice = Money(BigDecimal("33.333"), "EUR")
        val checkIn = LocalDate.of(2023, 6, 1)
        val checkOut = LocalDate.of(2023, 6, 4) // 3 nights
        val rooms = 1

        // Base amount = 33.333 * 3 * 1 = 99.999 -> rounds to 100.00
        // VAT = 100.00 * 0.15 = 15.00
        // Total = 115.00

        val breakdown = BookingCalculationEngine.calculate(
            checkIn = checkIn,
            checkOut = checkOut,
            rooms = rooms,
            pricePerNight = oddPrice
        )

        assertEquals(BigDecimal("100.00"), breakdown.basePrice.amount)
        assertEquals(BigDecimal("15.00"), breakdown.vat.amount)
        assertEquals(BigDecimal("115.00"), breakdown.totalPrice.amount)
    }
}
