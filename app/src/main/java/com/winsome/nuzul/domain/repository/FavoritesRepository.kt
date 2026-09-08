package com.winsome.nuzul.domain.repository

import com.winsome.nuzul.domain.model.Hotel
import kotlinx.coroutines.flow.Flow

interface FavoritesRepository {
    /** Toggles favorite status and persists it locally. */
    suspend fun toggleFavorite(hotelId: String, isFavorite: Boolean): Result<Unit>
    
    /** Emits the list of favorite hotels. */
    fun getFavoriteHotels(): Flow<List<Hotel>>
}