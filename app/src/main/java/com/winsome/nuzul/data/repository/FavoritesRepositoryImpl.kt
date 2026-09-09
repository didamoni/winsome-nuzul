package com.winsome.nuzul.data.repository

import com.winsome.nuzul.data.local.dao.FavoriteDao
import com.winsome.nuzul.data.local.entity.FavoriteEntity
import com.winsome.nuzul.data.mapper.toDomain
import com.winsome.nuzul.domain.model.Hotel
import com.winsome.nuzul.domain.repository.FavoritesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FavoritesRepositoryImpl @Inject constructor(
    private val favoriteDao: FavoriteDao
) : FavoritesRepository {

    override suspend fun toggleFavorite(hotelId: String, isFavorite: Boolean): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            if (isFavorite) {
                favoriteDao.insertFavorite(FavoriteEntity(hotelId = hotelId))
            } else {
                favoriteDao.deleteFavorite(hotelId = hotelId)
            }
        }
    }

    override fun getFavoriteHotels(): Flow<List<Hotel>> {
        return favoriteDao.observeFavoriteHotels()
            .map { list -> list.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)
    }
}