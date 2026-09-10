package com.winsome.nuzul.presentation.screen.booking

import com.winsome.nuzul.domain.model.Hotel
import com.winsome.nuzul.domain.util.BookingCalculationEngine
import com.winsome.nuzul.domain.util.BookingDateValidator
import java.time.LocalDate

data class BookingUiState(
    val hotel: Hotel? = null,
    val isLoadingHotel: Boolean = true,
    val checkInDate: LocalDate = LocalDate.now().plusDays(1),
    val checkOutDate: LocalDate = LocalDate.now().plusDays(2),
    val rooms: Int = 1,
    val pricing: BookingCalculationEngine.PricingBreakdown? = null,
    val dateValidationError: BookingDateValidator.ValidationResult.Invalid? = null,
    val isSubmitting: Boolean = false,
    val errorMessage: String? = null
) {
    val canConfirm: Boolean
        get() = hotel != null && dateValidationError == null && !isSubmitting
}

sealed interface BookingIntent {
    data class OnCheckInDateSelected(val date: LocalDate) : BookingIntent
    data class OnCheckOutDateSelected(val date: LocalDate) : BookingIntent
    data object OnIncrementRooms : BookingIntent
    data object OnDecrementRooms : BookingIntent
    data object OnConfirmBooking : BookingIntent
}

sealed interface BookingEvent {
    data class BookingSuccess(val reference: String) : BookingEvent
    data class ShowError(val message: String) : BookingEvent
}