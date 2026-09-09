package com.winsome.nuzul.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.winsome.nuzul.data.local.entity.FavoriteEntity
import com.winsome.nuzul.data.local.entity.HotelWithFavorite
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteDao {

    @Query("""
        SELECT h.*, 1 AS isFavorite
        FROM favorites f
        INNER JOIN hotels h ON f.hotelId = h.id
        ORDER BY f.savedAt DESC
    """)
    fun observeFavoriteHotels(): Flow<List<HotelWithFavorite>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFavorite(favorite: FavoriteEntity)

    @Query("DELETE FROM favorites WHERE hotelId = :hotelId")
    suspend fun deleteFavorite(hotelId: String)
}