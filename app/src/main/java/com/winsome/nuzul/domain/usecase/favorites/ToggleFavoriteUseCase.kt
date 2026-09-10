package com.winsome.nuzul.domain.usecase.favorites

import com.winsome.nuzul.domain.repository.FavoritesRepository
import javax.inject.Inject

class ToggleFavoriteUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {
    suspend operator fun invoke(hotelId: String, isFavorite: Boolean): Result<Unit> {
        return favoritesRepository.toggleFavorite(hotelId, isFavorite)
    }
}