package com.winsome.nuzul.presentation.screen.hotel

import com.winsome.nuzul.domain.model.Hotel

data class HotelDetailsUiState(
    val isLoading: Boolean = true,
    val hotel: Hotel? = null,
    val errorMessage: String? = null
)

sealed interface HotelDetailsIntent {
    data object OnToggleFavorite : HotelDetailsIntent
}