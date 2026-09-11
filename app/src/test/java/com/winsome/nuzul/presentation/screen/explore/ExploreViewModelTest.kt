package com.winsome.nuzul.presentation.screen.explore

import com.winsome.nuzul.domain.model.Hotel
import com.winsome.nuzul.domain.model.Location
import com.winsome.nuzul.domain.model.Money
import com.winsome.nuzul.domain.usecase.favorites.GetFavoriteHotelsUseCase
import com.winsome.nuzul.domain.usecase.favorites.ToggleFavoriteUseCase
import com.winsome.nuzul.domain.usecase.hotels.GetCitiesUseCase
import com.winsome.nuzul.domain.usecase.hotels.GetFeaturedHotelsUseCase
import com.winsome.nuzul.domain.usecase.hotels.GetHotelsUseCase
import com.winsome.nuzul.domain.usecase.hotels.RefreshHotelsUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.math.BigDecimal

@OptIn(ExperimentalCoroutinesApi::class)
class ExploreViewModelTest {

    private val getHotelsUseCase: GetHotelsUseCase = mockk()
    private val getFeaturedHotelsUseCase: GetFeaturedHotelsUseCase = mockk()
    private val getFavoriteHotelsUseCase: GetFavoriteHotelsUseCase = mockk()
    private val getCitiesUseCase: GetCitiesUseCase = mockk()
    private val refreshHotelsUseCase: RefreshHotelsUseCase = mockk(relaxed = true)
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase = mockk(relaxed = true)

    private val testDispatcher = StandardTestDispatcher()

    private val mockHotel = Hotel(
        id = "h1",
        name = "Mövenpick Resort",
        city = "Aswan",
        rating = 4.8,
        pricePerNight = Money(BigDecimal("650"), "SAR"),
        images = emptyList(),
        amenities = emptyList(),
        description = "Island stay",
        location = Location(0.0, 0.0, ""),
        isFavorite = false,
        isFeatured = false
    )

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        coEvery { getCitiesUseCase() } returns Result.success(listOf("Aswan", "Riyadh"))
        coEvery { getFeaturedHotelsUseCase() } returns flowOf(listOf(mockHotel))
        coEvery { getFavoriteHotelsUseCase() } returns flowOf(emptyList())
        coEvery { getHotelsUseCase(any(), any(), any()) } returns Result.success(listOf(mockHotel))
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `initialization loads cities, featured hotels, and first page of hotels`() = runTest {
        val viewModel = ExploreViewModel(
            getHotelsUseCase = getHotelsUseCase,
            getFeaturedHotelsUseCase = getFeaturedHotelsUseCase,
            getFavoriteHotelsUseCase = getFavoriteHotelsUseCase,
            getCitiesUseCase = getCitiesUseCase,
            refreshHotelsUseCase = refreshHotelsUseCase,
            toggleFavoriteUseCase = toggleFavoriteUseCase
        )

        testDispatcher.scheduler.advanceUntilIdle()

        val state = viewModel.uiState.value
        assertTrue(state.availableCities.contains("Aswan"))
        assertEquals(1, state.hotels.size)
        assertEquals("Mövenpick Resort", state.hotels.first().name)
    }

    @Test
    fun `search query updates text immediately and debounces hotel query`() = runTest {
        val viewModel = ExploreViewModel(
            getHotelsUseCase = getHotelsUseCase,
            getFeaturedHotelsUseCase = getFeaturedHotelsUseCase,
            getFavoriteHotelsUseCase = getFavoriteHotelsUseCase,
            getCitiesUseCase = getCitiesUseCase,
            refreshHotelsUseCase = refreshHotelsUseCase,
            toggleFavoriteUseCase = toggleFavoriteUseCase
        )

        // Type query
        viewModel.onIntent(ExploreIntent.OnSearchQueryChanged("Oberoi"))

        // UI state updates query text immediately (no typing delay)
        assertEquals("Oberoi", viewModel.uiState.value.searchQuery)

        // Advance debounce time (350ms)
        testDispatcher.scheduler.advanceTimeBy(400)

        // Verify filter was applied
        assertEquals("Oberoi", viewModel.uiState.value.filter.query)
    }

    @Test
    fun `favorites sync automatically updates favorite flag on active hotels`() = runTest {
        // Prepare favorite flow to emit hotel "h1"
        val favoriteFlow = MutableSharedFlow<List<Hotel>>(replay = 1)
        favoriteFlow.tryEmit(emptyList())
        coEvery { getFavoriteHotelsUseCase() } returns favoriteFlow

        val viewModel = ExploreViewModel(
            getHotelsUseCase = getHotelsUseCase,
            getFeaturedHotelsUseCase = getFeaturedHotelsUseCase,
            getFavoriteHotelsUseCase = getFavoriteHotelsUseCase,
            getCitiesUseCase = getCitiesUseCase,
            refreshHotelsUseCase = refreshHotelsUseCase,
            toggleFavoriteUseCase = toggleFavoriteUseCase
        )

        testDispatcher.scheduler.advanceUntilIdle()

        // Update favorite flow to emit hotel "h1" as favorite
        favoriteFlow.emit(listOf(mockHotel.copy(isFavorite = true)))
        testDispatcher.scheduler.advanceUntilIdle()

        // Verify hotel favorite status synced reactively
        val updatedHotel = viewModel.uiState.value.hotels.first()
        assertTrue(updatedHotel.isFavorite)
    }
}