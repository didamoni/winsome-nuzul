package com.winsome.nuzul.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.winsome.nuzul.data.local.converter.Converters
import com.winsome.nuzul.data.local.dao.BookingDao
import com.winsome.nuzul.data.local.dao.FavoriteDao
import com.winsome.nuzul.data.local.dao.HotelDao
import com.winsome.nuzul.data.local.entity.BookingEntity
import com.winsome.nuzul.data.local.entity.FavoriteEntity
import com.winsome.nuzul.data.local.entity.FeaturedHotelEntity
import com.winsome.nuzul.data.local.entity.HotelEntity

@Database(
    entities = [
        HotelEntity::class,
        FeaturedHotelEntity::class,
        FavoriteEntity::class,
        BookingEntity::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class NuzulDatabase : RoomDatabase() {
    abstract fun hotelDao(): HotelDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun bookingDao(): BookingDao
}