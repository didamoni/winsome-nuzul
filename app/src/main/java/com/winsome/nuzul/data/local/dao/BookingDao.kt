package com.winsome.nuzul.data.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.winsome.nuzul.data.local.entity.BookingEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookingDao {

    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insertBooking(booking: BookingEntity)

    @Query("SELECT * FROM bookings ORDER BY checkIn ASC, createdAt DESC")
    fun observeBookings(): Flow<List<BookingEntity>>

    @Query("SELECT * FROM bookings WHERE reference = :reference LIMIT 1")
    suspend fun getBookingByReference(reference: String): BookingEntity?
}