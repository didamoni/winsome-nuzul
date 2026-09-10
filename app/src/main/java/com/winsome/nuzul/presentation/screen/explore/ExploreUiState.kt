package com.winsome.nuzul.presentation.screen.explore

import com.winsome.nuzul.domain.model.Hotel
import com.winsome.nuzul.domain.model.HotelFilter
import java.math.BigDecimal

data class ExploreUiState(
    val searchQuery: String = "",
    val hotels: List<Hotel> = emptyList(),
    val featuredHotels: List<Hotel> = emptyList(),
    val availableCities: List<String> = emptyList(),
    val filter: HotelFilter = HotelFilter(),
    val isLoadingInitial: Boolean = true,
    val isLoadingNextPage: Boolean = false,
    val isRefreshing: Boolean = false,
    val isEndOfPagination: Boolean = false,
    val errorMessage: String? = null
)

sealed interface ExploreIntent {
    data class OnSearchQueryChanged(val query: String) : ExploreIntent
    data class OnCityFilterSelected(val city: String?) : ExploreIntent
    data class OnRatingFilterSelected(val rating: Double?) : ExploreIntent
    data class OnPriceRangeFilterChanged(val minPrice: BigDecimal?, val maxPrice: BigDecimal?) : ExploreIntent
    data object OnResetFilters : ExploreIntent
    data object OnLoadNextPage : ExploreIntent
    data object OnRefresh : ExploreIntent
    data object OnRetry : ExploreIntent
    data class OnToggleFavorite(val hotelId: String, val isFavorite: Boolean) : ExploreIntent
}

sealed interface ExploreEvent {
    data class ShowSnackbar(val message: String) : ExploreEvent
}