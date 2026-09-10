package com.winsome.nuzul.presentation.screen.explore

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.winsome.nuzul.domain.model.HotelFilter
import com.winsome.nuzul.domain.usecase.favorites.GetFavoriteHotelsUseCase
import com.winsome.nuzul.domain.usecase.favorites.ToggleFavoriteUseCase
import com.winsome.nuzul.domain.usecase.hotels.GetCitiesUseCase
import com.winsome.nuzul.domain.usecase.hotels.GetFeaturedHotelsUseCase
import com.winsome.nuzul.domain.usecase.hotels.GetHotelsUseCase
import com.winsome.nuzul.domain.usecase.hotels.RefreshHotelsUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import kotlin.time.Duration.Companion.milliseconds

@OptIn(FlowPreview::class)
@HiltViewModel
class ExploreViewModel @Inject constructor(
    private val getHotelsUseCase: GetHotelsUseCase,
    private val getFeaturedHotelsUseCase: GetFeaturedHotelsUseCase,
    private val getFavoriteHotelsUseCase: GetFavoriteHotelsUseCase, // Injected for live sync
    private val getCitiesUseCase: GetCitiesUseCase,
    private val refreshHotelsUseCase: RefreshHotelsUseCase,
    private val toggleFavoriteUseCase: ToggleFavoriteUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ExploreUiState())
    val uiState = _uiState.asStateFlow()

    private val searchQueryFlow = MutableStateFlow("")
    private var currentPage = 1
    private val pageSize = 6

    init {
        loadCities()
        observeFeaturedHotels()
        observeFavoritesSync()
        observeSearchQuery()
        loadHotels(page = 1, isInitial = true)
    }

    fun onIntent(intent: ExploreIntent) {
        when (intent) {
            is ExploreIntent.OnSearchQueryChanged -> {
                _uiState.update { it.copy(searchQuery = intent.query) }
                searchQueryFlow.value = intent.query
            }
            is ExploreIntent.OnCityFilterSelected -> {
                applyFilterUpdate { copy(city = intent.city) }
            }
            is ExploreIntent.OnRatingFilterSelected -> {
                applyFilterUpdate { copy(minRating = intent.rating) }
            }
            is ExploreIntent.OnPriceRangeFilterChanged -> {
                applyFilterUpdate { copy(minPrice = intent.minPrice, maxPrice = intent.maxPrice) }
            }
            is ExploreIntent.OnResetFilters -> {
                _uiState.update { it.copy(searchQuery = "") }
                searchQueryFlow.value = ""
                applyFilterUpdate { HotelFilter() }
            }
            is ExploreIntent.OnLoadNextPage -> {
                loadNextPage()
            }
            is ExploreIntent.OnRefresh -> {
                refresh()
            }
            is ExploreIntent.OnRetry -> {
                loadHotels(page = 1, isInitial = true)
            }
            is ExploreIntent.OnToggleFavorite -> {
                toggleFavorite(intent.hotelId, intent.isFavorite)
            }
        }
    }

    private fun observeFavoritesSync() {
        getFavoriteHotelsUseCase()
            .onEach { favoriteHotels ->
                val favoriteIds = favoriteHotels.map { it.id }.toSet()
                _uiState.update { state ->
                    state.copy(
                        hotels = state.hotels.map { hotel ->
                            hotel.copy(isFavorite = favoriteIds.contains(hotel.id))
                        },
                        featuredHotels = state.featuredHotels.map { hotel ->
                            hotel.copy(isFavorite = favoriteIds.contains(hotel.id))
                        }
                    )
                }
            }
            .catch { /* ignore or log */ }
            .launchIn(viewModelScope)
    }

    private fun toggleFavorite(hotelId: String, isCurrentlyFavorite: Boolean) {
        val nextFavoriteState = !isCurrentlyFavorite

        _uiState.update { state ->
            state.copy(
                hotels = state.hotels.map { hotel ->
                    if (hotel.id == hotelId) hotel.copy(isFavorite = nextFavoriteState) else hotel
                },
                featuredHotels = state.featuredHotels.map { hotel ->
                    if (hotel.id == hotelId) hotel.copy(isFavorite = nextFavoriteState) else hotel
                }
            )
        }

        viewModelScope.launch {
            toggleFavoriteUseCase(hotelId, nextFavoriteState).onFailure {
                _uiState.update { state ->
                    state.copy(
                        hotels = state.hotels.map { hotel ->
                            if (hotel.id != hotelId) hotel
                            else hotel.copy(isFavorite = isCurrentlyFavorite)
                        },
                        featuredHotels = state.featuredHotels.map { hotel ->
                            if (hotel.id != hotelId) hotel
                            else hotel.copy(isFavorite = isCurrentlyFavorite)
                        }
                    )
                }
            }
        }
    }

    private fun observeSearchQuery() {
        searchQueryFlow
            .debounce(350.milliseconds)
            .distinctUntilChanged()
            .onEach { query ->
                applyFilterUpdate { copy(query = query) }
            }
            .launchIn(viewModelScope)
    }

    private fun applyFilterUpdate(update: HotelFilter.() -> HotelFilter) {
        val newFilter = _uiState.value.filter.update()
        _uiState.update { it.copy(filter = newFilter) }
        currentPage = 1
        loadHotels(page = 1, isInitial = false)
    }

    private fun observeFeaturedHotels() {
        getFeaturedHotelsUseCase()
            .onEach { featured ->
                _uiState.update { it.copy(featuredHotels = featured) }
            }
            .catch { error ->
                _uiState.update { it.copy(errorMessage = error.message) }
            }
            .launchIn(viewModelScope)
    }

    private fun loadCities() {
        viewModelScope.launch {
            getCitiesUseCase().onSuccess { cities ->
                _uiState.update { it.copy(availableCities = cities) }
            }
        }
    }

    private fun loadHotels(page: Int, isInitial: Boolean) {
        viewModelScope.launch {
            if (isInitial) {
                _uiState.update { it.copy(isLoadingInitial = true, errorMessage = null) }
            }

            getHotelsUseCase(filter = _uiState.value.filter, page = page, pageSize = pageSize)
                .fold(
                    onSuccess = { newHotels ->
                        _uiState.update { state ->
                            state.copy(
                                hotels = if (page == 1) newHotels else state.hotels + newHotels,
                                isLoadingInitial = false,
                                isLoadingNextPage = false,
                                isRefreshing = false,
                                isEndOfPagination = newHotels.size < pageSize,
                                errorMessage = null
                            )
                        }
                        currentPage = page
                    },
                    onFailure = { error ->
                        _uiState.update { state ->
                            state.copy(
                                isLoadingInitial = false,
                                isLoadingNextPage = false,
                                isRefreshing = false,
                                errorMessage = error.message ?: "Failed to load hotels"
                            )
                        }
                    }
                )
        }
    }

    private fun loadNextPage() {
        val currentState = _uiState.value
        if (currentState.isLoadingNextPage || currentState.isLoadingInitial || currentState.isEndOfPagination) {
            return
        }

        _uiState.update { it.copy(isLoadingNextPage = true) }
        loadHotels(page = currentPage + 1, isInitial = false)
    }

    private fun refresh() {
        viewModelScope.launch {
            _uiState.update { it.copy(isRefreshing = true) }
            refreshHotelsUseCase()
            currentPage = 1
            loadHotels(page = 1, isInitial = false)
        }
    }
}