package com.winsome.nuzul.domain.util

import com.winsome.nuzul.domain.model.Money
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDate
import java.time.temporal.ChronoUnit

object BookingCalculationEngine {
    val VAT_RATE: BigDecimal = BigDecimal("0.15") // 15% VAT per specification

    data class PricingBreakdown(
        val nights: Int,
        val basePrice: Money,
        val vat: Money,
        val totalPrice: Money
    )

    fun calculate(
        checkIn: LocalDate,
        checkOut: LocalDate,
        rooms: Int,
        pricePerNight: Money
    ): PricingBreakdown {
        require(rooms > 0) { "Number of rooms must be greater than zero" }
        require(checkOut.isAfter(checkIn)) { "Check-out must be strictly after check-in" }

        val nights = ChronoUnit.DAYS.between(checkIn, checkOut).toInt()
        val nightsBd = BigDecimal.valueOf(nights.toLong())
        val roomsBd = BigDecimal.valueOf(rooms.toLong())

        // basePrice = pricePerNight * nights * rooms
        val baseAmount = pricePerNight.amount
            .multiply(nightsBd)
            .multiply(roomsBd)
            .setScale(2, RoundingMode.HALF_UP)

        // vat = basePrice * 0.15
        val vatAmount = baseAmount
            .multiply(VAT_RATE)
            .setScale(2, RoundingMode.HALF_UP)

        // totalPrice = basePrice + vat
        val totalAmount = baseAmount
            .add(vatAmount)
            .setScale(2, RoundingMode.HALF_UP)

        val currency = pricePerNight.currency

        return PricingBreakdown(
            nights = nights,
            basePrice = Money(baseAmount, currency),
            vat = Money(vatAmount, currency),
            totalPrice = Money(totalAmount, currency)
        )
    }
}