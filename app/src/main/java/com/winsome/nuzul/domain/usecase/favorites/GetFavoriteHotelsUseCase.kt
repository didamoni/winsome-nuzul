package com.winsome.nuzul.domain.usecase.favorites

import com.winsome.nuzul.domain.model.Hotel
import com.winsome.nuzul.domain.repository.FavoritesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetFavoriteHotelsUseCase @Inject constructor(
    private val favoritesRepository: FavoritesRepository
) {
    operator fun invoke(): Flow<List<Hotel>> {
        return favoritesRepository.getFavoriteHotels()
    }
}