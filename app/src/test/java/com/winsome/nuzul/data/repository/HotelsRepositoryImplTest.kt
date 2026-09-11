package com.winsome.nuzul.data.repository

import com.winsome.nuzul.data.local.dao.HotelDao
import com.winsome.nuzul.data.local.entity.HotelEntity
import com.winsome.nuzul.data.local.entity.HotelWithFavorite
import com.winsome.nuzul.data.remote.HotelDto
import com.winsome.nuzul.data.remote.HotelsDataSource
import com.winsome.nuzul.data.remote.LocationDto
import com.winsome.nuzul.data.remote.MoneyDto
import com.winsome.nuzul.domain.model.HotelFilter
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.math.BigDecimal

class HotelsRepositoryImplTest {

    private val hotelDao: HotelDao = mockk(relaxed = true)
    private val remoteDataSource: HotelsDataSource = mockk(relaxed = true)

    private val repository = HotelsRepositoryImpl(hotelDao, remoteDataSource)

    private val sampleHotelDto = HotelDto(
        id = "1",
        name = "Grand Hotel",
        city = "Dubai",
        rating = 4.8,
        pricePerNight = MoneyDto(150.0, "USD"),
        images = listOf("url1"),
        amenities = listOf("WiFi"),
        description = "Luxury hotel",
        location = LocationDto(25.2048, 55.2708, "Dubai")
    )

    private val sampleHotelEntity = HotelEntity(
        id = "1",
        name = "Grand Hotel",
        city = "Dubai",
        rating = 4.8,
        priceAmount = BigDecimal("150.0"),
        currency = "USD",
        images = listOf("url1"),
        amenities = listOf("WiFi"),
        description = "Luxury hotel",
        latitude = 25.2048,
        longitude = 55.2708,
        address = "Dubai",
        isFeatured = false
    )

    @Test
    fun getCities_returnsLocalCitiesWhenNotEmpty() = runTest {
        coEvery { hotelDao.getCities() } returns listOf("Dubai", "Abu Dhabi")

        val result = repository.getCities()

        assertTrue(result.isSuccess)
        assertEquals(listOf("Dubai", "Abu Dhabi"), result.getOrNull())
        coVerify(exactly = 0) { remoteDataSource.getCities() }
    }

    @Test
    fun getCities_fetchesRemoteWhenLocalIsEmpty() = runTest {
        coEvery { hotelDao.getCities() } returns emptyList()
        coEvery { remoteDataSource.getCities() } returns listOf("Sharjah", "Ajman")

        val result = repository.getCities()

        assertTrue(result.isSuccess)
        assertEquals(listOf("Sharjah", "Ajman"), result.getOrNull())
        coVerify(exactly = 1) { remoteDataSource.getCities() }
    }

    @Test
    fun getCities_returnsFailureWhenExceptionThrown() = runTest {
        val exception = RuntimeException("Database error")
        coEvery { hotelDao.getCities() } throws exception

        val result = repository.getCities()

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }

    @Test
    fun getFeaturedHotels_emitsMappedDomainModels() = runTest {
        val hotelWithFav = HotelWithFavorite(hotel = sampleHotelEntity, isFavorite = true)
        every { hotelDao.observeFeaturedHotels() } returns flowOf(listOf(hotelWithFav))

        val flow = repository.getFeaturedHotels()
        val emittedList = flow.first()

        assertEquals(1, emittedList.size)
        assertEquals("1", emittedList[0].id)
        assertEquals("Grand Hotel", emittedList[0].name)
        assertTrue(emittedList[0].isFavorite)
    }

    @Test
    fun getHotels_seedsDatabaseWhenEmptyAndReturnsFilteredResults() = runTest {
        coEvery { hotelDao.countHotels() } returns 0
        coEvery { remoteDataSource.getHotels() } returns listOf(sampleHotelDto)
        coEvery { remoteDataSource.getFeaturedHotelIds() } returns listOf(sampleHotelDto)

        val hotelWithFav = HotelWithFavorite(hotel = sampleHotelEntity, isFavorite = false)
        coEvery {
            hotelDao.getFilteredHotels(
                query = any(),
                city = any(),
                minRating = any(),
                minPrice = any(),
                maxPrice = any(),
                pageSize = any(),
                offset = any()
            )
        } returns listOf(hotelWithFav)

        val filter = HotelFilter(query = "Grand")
        val result = repository.getHotels(filter, page = 1, pageSize = 10)

        assertTrue(result.isSuccess)
        assertEquals(1, result.getOrNull()?.size)
        assertEquals("Grand Hotel", result.getOrNull()?.get(0)?.name)
        coVerify(exactly = 1) { remoteDataSource.getHotels() }
        coVerify(exactly = 1) { hotelDao.updateFeaturedHotels(any(), any()) }
    }

    @Test
    fun getHotels_doesNotSeedWhenNotEmpty() = runTest {
        coEvery { hotelDao.countHotels() } returns 5
        val hotelWithFav = HotelWithFavorite(hotel = sampleHotelEntity, isFavorite = false)
        coEvery {
            hotelDao.getFilteredHotels(
                query = any(),
                city = any(),
                minRating = any(),
                minPrice = any(),
                maxPrice = any(),
                pageSize = any(),
                offset = any()
            )
        } returns listOf(hotelWithFav)

        val filter = HotelFilter()
        val result = repository.getHotels(filter, page = 1, pageSize = 10)

        assertTrue(result.isSuccess)
        coVerify(exactly = 0) { remoteDataSource.getHotels() }
    }

    @Test
    fun getHotelById_emitsHotelOrNull() = runTest {
        val hotelWithFav = HotelWithFavorite(hotel = sampleHotelEntity, isFavorite = true)
        every { hotelDao.observeHotelById("1") } returns flowOf(hotelWithFav)
        every { hotelDao.observeHotelById("99") } returns flowOf(null)

        val hotel = repository.getHotelById("1").first()
        val missingHotel = repository.getHotelById("99").first()

        assertEquals("Grand Hotel", hotel?.name)
        assertTrue(hotel?.isFavorite == true)
        assertEquals(null, missingHotel)
    }

    @Test
    fun refreshHotels_fetchesRemoteAndUpdatesDao() = runTest {
        coEvery { remoteDataSource.getHotels() } returns listOf(sampleHotelDto)
        coEvery { remoteDataSource.getFeaturedHotelIds() } returns listOf(sampleHotelDto)

        val result = repository.refreshHotels()

        assertTrue(result.isSuccess)
        coVerify(exactly = 1) { remoteDataSource.getHotels() }
        coVerify(exactly = 1) { remoteDataSource.getFeaturedHotelIds() }
        coVerify(exactly = 1) { hotelDao.updateFeaturedHotels(any(), any()) }
    }

    @Test
    fun refreshHotels_returnsFailureWhenRemoteFails() = runTest {
        val exception = RuntimeException("Network error")
        coEvery { remoteDataSource.getHotels() } throws exception

        val result = repository.refreshHotels()

        assertTrue(result.isFailure)
        assertEquals(exception, result.exceptionOrNull())
    }
}
