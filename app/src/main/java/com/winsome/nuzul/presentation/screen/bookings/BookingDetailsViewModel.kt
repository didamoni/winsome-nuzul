package com.winsome.nuzul.presentation.screen.bookings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.winsome.nuzul.domain.usecase.bookings.GetBookingDetailsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = BookingDetailsViewModel.Factory::class)
class BookingDetailsViewModel @AssistedInject constructor(
    @Assisted private val reference: String,
    private val getBookingDetailsUseCase: GetBookingDetailsUseCase
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(reference: String): BookingDetailsViewModel
    }

    private val _uiState = MutableStateFlow(BookingDetailsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        loadBookingDetails()
    }

    private fun loadBookingDetails() {
        viewModelScope.launch {
            getBookingDetailsUseCase(reference).fold(
                onSuccess = { booking ->
                    _uiState.update {
                        it.copy(isLoading = false, booking = booking, errorMessage = null)
                    }
                },
                onFailure = { error ->
                    _uiState.update {
                        it.copy(isLoading = false, errorMessage = error.message ?: "Booking not found")
                    }
                }
            )
        }
    }
}