package com.winsome.nuzul.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.winsome.nuzul.data.local.entity.FeaturedHotelEntity
import com.winsome.nuzul.data.local.entity.HotelEntity
import com.winsome.nuzul.data.local.entity.HotelWithFavorite
import kotlinx.coroutines.flow.Flow
import java.math.BigDecimal

@Dao
interface HotelDao {

    @Query("""
        SELECT h.*, (f.hotelId IS NOT NULL) AS isFavorite
        FROM hotels h
        LEFT JOIN favorites f ON h.id = f.hotelId
        WHERE (:query = '' OR LOWER(h.name) LIKE '%' || LOWER(:query) || '%' OR LOWER(h.city) LIKE '%' || LOWER(:query) || '%')
          AND (:city IS NULL OR h.city = :city)
          AND (:minRating IS NULL OR h.rating >= :minRating)
          AND (:minPrice IS NULL OR h.priceAmount >= :minPrice)
          AND (:maxPrice IS NULL OR h.priceAmount <= :maxPrice)
        ORDER BY h.rating DESC, h.id ASC
        LIMIT :pageSize OFFSET :offset
    """)
    suspend fun getFilteredHotels(
        query: String,
        city: String?,
        minRating: Double?,
        minPrice: BigDecimal?,
        maxPrice: BigDecimal?,
        pageSize: Int,
        offset: Int
    ): List<HotelWithFavorite>

    @Query("""
        SELECT h.*, (f.hotelId IS NOT NULL) AS isFavorite
        FROM featured_hotels fh
        INNER JOIN hotels h ON fh.hotelId = h.id
        LEFT JOIN favorites f ON h.id = f.hotelId
        ORDER BY fh.displayOrder ASC
    """)
    fun observeFeaturedHotels(): Flow<List<HotelWithFavorite>>

    @Query("""
        SELECT h.*, (f.hotelId IS NOT NULL) AS isFavorite
        FROM hotels h
        LEFT JOIN favorites f ON h.id = f.hotelId
        WHERE h.id = :hotelId
        LIMIT 1
    """)
    fun observeHotelById(hotelId: String): Flow<HotelWithFavorite?>

    @Query("SELECT DISTINCT city FROM hotels ORDER BY city ASC")
    suspend fun getCities(): List<String>

    @Query("SELECT COUNT(*) FROM hotels")
    suspend fun countHotels(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertHotels(hotels: List<HotelEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFeaturedHotels(featured: List<FeaturedHotelEntity>)

    @Query("DELETE FROM featured_hotels")
    suspend fun clearFeaturedHotels()

    @Transaction
    suspend fun updateFeaturedHotels(
        hotels: List<HotelEntity>,
        featured: List<FeaturedHotelEntity>
    ) {
        upsertHotels(hotels)
        clearFeaturedHotels()
        insertFeaturedHotels(featured)
    }
}