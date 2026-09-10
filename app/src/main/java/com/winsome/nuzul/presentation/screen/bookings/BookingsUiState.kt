package com.winsome.nuzul.presentation.screen.bookings

import com.winsome.nuzul.domain.model.Booking

data class BookingsUiState(
    val isLoading: Boolean = true,
    val upcomingBookings: List<Booking> = emptyList(),
    val pastBookings: List<Booking> = emptyList(),
    val errorMessage: String? = null
)

data class BookingDetailsUiState(
    val isLoading: Boolean = true,
    val booking: Booking? = null,
    val errorMessage: String? = null
)