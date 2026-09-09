package com.winsome.nuzul.data.repository

import com.winsome.nuzul.data.local.dao.HotelDao
import com.winsome.nuzul.data.local.entity.FeaturedHotelEntity
import com.winsome.nuzul.data.mapper.toDomain
import com.winsome.nuzul.data.mapper.toEntity
import com.winsome.nuzul.data.remote.HotelsDataSource
import com.winsome.nuzul.domain.model.Hotel
import com.winsome.nuzul.domain.model.HotelFilter
import com.winsome.nuzul.domain.repository.HotelsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class HotelsRepositoryImpl @Inject constructor(
    private val hotelDao: HotelDao,
    private val remoteDataSource: HotelsDataSource
) : HotelsRepository {

    override suspend fun getCities(): Result<List<String>> = withContext(Dispatchers.IO) {
        runCatching {
            val localCities = hotelDao.getCities()
            if (localCities.isNotEmpty()) {
                localCities
            } else {
                remoteDataSource.getCities()
            }
        }
    }

    override fun getFeaturedHotels(): Flow<List<Hotel>> {
        return hotelDao.observeFeaturedHotels()
            .map { list -> list.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun getHotels(
        filter: HotelFilter,
        page: Int,
        pageSize: Int
    ): Result<List<Hotel>> = withContext(Dispatchers.IO) {
        runCatching {
            // Seed DB on first run if empty
            if (hotelDao.countHotels() == 0) {
                refreshHotels().getOrThrow()
            }

            val offset = (page.coerceAtLeast(1) - 1) * pageSize
            val entities = hotelDao.getFilteredHotels(
                query = filter.query.trim(),
                city = filter.city,
                minRating = filter.minRating,
                minPrice = filter.minPrice,
                maxPrice = filter.maxPrice,
                pageSize = pageSize,
                offset = offset
            )
            entities.map { it.toDomain() }
        }
    }

    override fun getHotelById(id: String): Flow<Hotel?> {
        return hotelDao.observeHotelById(id)
            .map { it?.toDomain() }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun refreshHotels(): Result<Unit> = withContext(Dispatchers.IO) {
        runCatching {
            val allHotels = remoteDataSource.getHotels()
            val featuredHotels = remoteDataSource.getFeaturedHotelIds()
            val featuredIds = featuredHotels.map { it.id }.toSet()

            val allEntities = allHotels.map { dto ->
                dto.toEntity(isFeatured = featuredIds.contains(dto.id))
            }
            val featuredEntities = featuredHotels.map { dto ->
                dto.toEntity(isFeatured = true)
            }
            val featuredJunctions = featuredHotels.mapIndexed { index, dto ->
                FeaturedHotelEntity(hotelId = dto.id, displayOrder = index)
            }

            hotelDao.updateFeaturedHotels(allEntities + featuredEntities, featuredJunctions)
        }
    }
}