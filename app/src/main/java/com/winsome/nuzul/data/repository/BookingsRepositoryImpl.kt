package com.winsome.nuzul.data.repository

import com.winsome.nuzul.data.local.dao.BookingDao
import com.winsome.nuzul.data.mapper.toDomain
import com.winsome.nuzul.data.mapper.toEntity
import com.winsome.nuzul.domain.model.Booking
import com.winsome.nuzul.domain.repository.BookingsRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookingsRepositoryImpl @Inject constructor(
    private val bookingDao: BookingDao
) : BookingsRepository {

    override suspend fun createBooking(booking: Booking): Result<Booking> = withContext(Dispatchers.IO) {
        runCatching {
            bookingDao.insertBooking(booking.toEntity())
            booking
        }
    }

    override fun getBookings(): Flow<List<Booking>> {
        return bookingDao.observeBookings()
            .map { list -> list.map { it.toDomain() } }
            .flowOn(Dispatchers.IO)
    }

    override suspend fun getBookingByReference(reference: String): Result<Booking> = withContext(Dispatchers.IO) {
        runCatching {
            bookingDao.getBookingByReference(reference)?.toDomain()
                ?: throw NoSuchElementException("No booking found with reference '$reference'")
        }
    }
}