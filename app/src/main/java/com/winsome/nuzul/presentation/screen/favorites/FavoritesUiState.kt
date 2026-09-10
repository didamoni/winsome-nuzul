package com.winsome.nuzul.presentation.screen.favorites

import com.winsome.nuzul.domain.model.Hotel

data class FavoritesUiState(
    val isLoading: Boolean = true,
    val favorites: List<Hotel> = emptyList(),
    val errorMessage: String? = null
)

sealed interface FavoritesIntent {
    data class OnRemoveFavorite(val hotelId: String) : FavoritesIntent
}