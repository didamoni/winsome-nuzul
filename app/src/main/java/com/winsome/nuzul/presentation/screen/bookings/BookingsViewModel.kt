package com.winsome.nuzul.presentation.screen.bookings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.winsome.nuzul.domain.usecase.bookings.GetBookingsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import java.time.LocalDate
import javax.inject.Inject

@HiltViewModel
class BookingsViewModel @Inject constructor(
    private val getBookingsUseCase: GetBookingsUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(BookingsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeBookings()
    }

    private fun observeBookings() {
        getBookingsUseCase()
            .onEach { bookings ->
                val today = LocalDate.now()
                val (upcoming, past) = bookings.partition { booking ->
                    !booking.checkOut.isBefore(today)
                }

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        upcomingBookings = upcoming,
                        pastBookings = past,
                        errorMessage = null
                    )
                }
            }
            .catch { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to load bookings"
                    )
                }
            }
            .launchIn(viewModelScope)
    }
}