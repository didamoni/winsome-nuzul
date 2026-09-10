package com.winsome.nuzul.presentation.screen.booking

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.winsome.nuzul.domain.usecase.bookings.CalculateBookingPricingUseCase
import com.winsome.nuzul.domain.usecase.bookings.CreateBookingUseCase
import com.winsome.nuzul.domain.usecase.bookings.ValidateBookingDatesUseCase
import com.winsome.nuzul.domain.usecase.hotels.GetHotelDetailsUseCase
import com.winsome.nuzul.domain.util.BookingDateValidator
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate

@HiltViewModel(assistedFactory = BookingViewModel.Factory::class)
class BookingViewModel @AssistedInject constructor(
    @Assisted private val hotelId: String,
    private val getHotelDetailsUseCase: GetHotelDetailsUseCase,
    private val calculateBookingPricingUseCase: CalculateBookingPricingUseCase,
    private val validateBookingDatesUseCase: ValidateBookingDatesUseCase,
    private val createBookingUseCase: CreateBookingUseCase
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(hotelId: String): BookingViewModel
    }

    private val _uiState = MutableStateFlow(BookingUiState())
    val uiState = _uiState.asStateFlow()

    private val _events = Channel<BookingEvent>(Channel.BUFFERED)
    val events = _events.receiveAsFlow()

    init {
        loadHotel()
    }

    fun onIntent(intent: BookingIntent) {
        when (intent) {
            is BookingIntent.OnCheckInDateSelected -> updateDates(checkIn = intent.date)
            is BookingIntent.OnCheckOutDateSelected -> updateDates(checkOut = intent.date)
            is BookingIntent.OnIncrementRooms -> updateRooms(_uiState.value.rooms + 1)
            is BookingIntent.OnDecrementRooms -> {
                if (_uiState.value.rooms > 1) updateRooms(_uiState.value.rooms - 1)
            }
            is BookingIntent.OnConfirmBooking -> confirmBooking()
        }
    }

    private fun loadHotel() {
        viewModelScope.launch {
            val hotel = getHotelDetailsUseCase(hotelId).firstOrNull()
            if (hotel != null) {
                _uiState.update { it.copy(hotel = hotel, isLoadingHotel = false) }
                recalculate()
            } else {
                _uiState.update {
                    it.copy(isLoadingHotel = false, errorMessage = "Hotel information unavailable")
                }
            }
        }
    }

    private fun updateDates(
        checkIn: LocalDate = _uiState.value.checkInDate,
        checkOut: LocalDate = _uiState.value.checkOutDate
    ) {
        _uiState.update { it.copy(checkInDate = checkIn, checkOutDate = checkOut) }
        recalculate()
    }

    private fun updateRooms(rooms: Int) {
        _uiState.update { it.copy(rooms = rooms) }
        recalculate()
    }

    private fun recalculate() {
        val state = _uiState.value
        val hotel = state.hotel ?: return

        val validation = validateBookingDatesUseCase(
            checkIn = state.checkInDate,
            checkOut = state.checkOutDate,
            rooms = state.rooms
        )

        if (validation is BookingDateValidator.ValidationResult.Valid) {
            val pricing = calculateBookingPricingUseCase(
                checkIn = state.checkInDate,
                checkOut = state.checkOutDate,
                rooms = state.rooms,
                pricePerNight = hotel.pricePerNight
            )
            _uiState.update {
                it.copy(pricing = pricing, dateValidationError = null)
            }
        } else {
            _uiState.update {
                it.copy(
                    pricing = null,
                    dateValidationError = validation as BookingDateValidator.ValidationResult.Invalid
                )
            }
        }
    }

    private fun confirmBooking() {
        val state = _uiState.value
        val hotel = state.hotel ?: return
        if (!state.canConfirm) return

        viewModelScope.launch {
            _uiState.update { it.copy(isSubmitting = true) }

            createBookingUseCase(
                hotel = hotel,
                checkIn = state.checkInDate,
                checkOut = state.checkOutDate,
                rooms = state.rooms
            ).fold(
                onSuccess = { booking ->
                    _uiState.update { it.copy(isSubmitting = false) }
                    _events.send(BookingEvent.BookingSuccess(booking.reference))
                },
                onFailure = { error ->
                    _uiState.update { it.copy(isSubmitting = false) }
                    _events.send(BookingEvent.ShowError(error.message ?: "Booking failed"))
                }
            )
        }
    }
}