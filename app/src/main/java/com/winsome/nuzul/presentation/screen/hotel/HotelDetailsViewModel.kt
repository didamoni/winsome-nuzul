package com.winsome.nuzul.presentation.screen.hotel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.winsome.nuzul.domain.usecase.favorites.ToggleFavoriteUseCase
import com.winsome.nuzul.domain.usecase.hotels.GetHotelDetailsUseCase
import dagger.assisted.Assisted
import dagger.assisted.AssistedFactory
import dagger.assisted.AssistedInject
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

@HiltViewModel(assistedFactory = HotelDetailsViewModel.Factory::class)
class HotelDetailsViewModel @AssistedInject constructor(
    @Assisted private val hotelId: String,
    private val getHotelDetailsUseCase: GetHotelDetailsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    @AssistedFactory
    interface Factory {
        fun create(hotelId: String): HotelDetailsViewModel
    }

    private val _uiState = MutableStateFlow(HotelDetailsUiState())
    val uiState = _uiState.asStateFlow()

    init {
        observeHotelDetails()
    }

    fun onIntent(intent: HotelDetailsIntent) {
        when (intent) {
            is HotelDetailsIntent.OnToggleFavorite -> toggleFavorite()
        }
    }

    private fun observeHotelDetails() {
        getHotelDetailsUseCase(hotelId)
            .onEach { hotel ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        hotel = hotel,
                        errorMessage = if (hotel == null) "Hotel not found" else null
                    )
                }
            }
            .catch { error ->
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = error.message ?: "Failed to load hotel details"
                    )
                }
            }
            .launchIn(viewModelScope)
    }

    private fun toggleFavorite() {
        val currentHotel = _uiState.value.hotel ?: return
        viewModelScope.launch {
            toggleFavoriteUseCase(currentHotel.id, !currentHotel.isFavorite)
        }
    }
}