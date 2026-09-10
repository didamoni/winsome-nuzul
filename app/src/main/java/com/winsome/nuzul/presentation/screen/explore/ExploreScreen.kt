package com.winsome.nuzul.presentation.screen.explore

import android.content.res.Configuration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CloudOff
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.winsome.nuzul.R
import com.winsome.nuzul.domain.model.HotelFilter
import com.winsome.nuzul.presentation.ui.component.CityFilterRow
import com.winsome.nuzul.presentation.ui.component.EmptyStateView
import com.winsome.nuzul.presentation.ui.component.ErrorStateView
import com.winsome.nuzul.presentation.ui.component.FeaturedHotelCard
import com.winsome.nuzul.presentation.ui.component.FilterBottomSheet
import com.winsome.nuzul.presentation.ui.component.HotelCard
import com.winsome.nuzul.presentation.ui.component.NuzulSearchBar
import com.winsome.nuzul.presentation.ui.theme.NuzulTheme
import com.winsome.nuzul.util.Samples
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter

@Composable
fun ExploreScreen(
    viewModel: ExploreViewModel,
    onHotelClick: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    ExploreScreenContent(
        state = state,
        onIntent = viewModel::onIntent,
        onHotelClick = onHotelClick
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun ExploreScreenContent(
    state: ExploreUiState,
    onIntent: (ExploreIntent) -> Unit,
    onHotelClick: (String) -> Unit
) {
    var showFilterSheet by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    // Pagination trigger when approaching list end
    val shouldLoadMore by remember {
        derivedStateOf {
            val totalItems = listState.layoutInfo.totalItemsCount
            val lastVisibleIndex = listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index ?: 0
            totalItems > 0 && lastVisibleIndex >= totalItems - 2
        }
    }

    LaunchedEffect(shouldLoadMore) {
        snapshotFlow { shouldLoadMore }
            .distinctUntilChanged()
            .filter { it }
            .collect { onIntent(ExploreIntent.OnLoadNextPage) }
    }

    PullToRefreshBox(
        isRefreshing = state.isRefreshing,
        onRefresh = { onIntent(ExploreIntent.OnRefresh) },
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        LazyColumn(
            state = listState,
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // Header & Search
            item(key = "header_search") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Image(
                            painter = painterResource(
                                if (isSystemInDarkTheme()) R.drawable.ic_launcher_foreground_dark
                                else R.drawable.ic_launcher_foreground
                            ),
                            contentDescription = stringResource(R.string.explore_brand_eyebrow),
                            modifier = Modifier.size(52.dp)
                        )

                        Spacer(modifier = Modifier.width(14.dp))

                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.explore_brand_eyebrow),
                                style = typography.labelSmall.copy(
                                    letterSpacing = 2.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = colorScheme.tertiary
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = stringResource(R.string.explore_title),
                                style = typography.displayMedium.copy(
                                    fontSize = 28.sp,
                                    lineHeight = 34.sp
                                ),
                                color = colorScheme.onBackground
                            )
                        }
                    }
                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = stringResource(R.string.explore_subtitle),
                        style = typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(18.dp))

                    NuzulSearchBar(
                        query = state.searchQuery,
                        hasActiveFilters = state.filter.minRating != null || state.filter.minPrice != null || state.filter.maxPrice != null,
                        onQueryChange = { onIntent(ExploreIntent.OnSearchQueryChanged(it)) },
                        onFilterClick = { showFilterSheet = true }
                    )
                }
            }

            // City Filter Chips
            if (state.availableCities.isNotEmpty()) {
                item(key = "city_chips") {
                    CityFilterRow(
                        cities = state.availableCities,
                        selectedCity = state.filter.city,
                        onCitySelected = { onIntent(ExploreIntent.OnCityFilterSelected(it)) }
                    )
                }
            }

            // Featured Carousel
            if (state.featuredHotels.isNotEmpty() && state.filter.query.isBlank()) {
                item(key = "featured_section") {
                    Column(modifier = Modifier.padding(top = 18.dp)) {
                        Text(
                            text = stringResource(R.string.explore_featured_section_title),
                            style = typography.headlineSmall,
                            color = colorScheme.onBackground,
                            modifier = Modifier.padding(horizontal = 20.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        LazyRow(
                            contentPadding = PaddingValues(horizontal = 20.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            items(state.featuredHotels, key = { "featured_${it.id}" }) { hotel ->
                                FeaturedHotelCard(
                                    hotel = hotel,
                                    onClick = { onHotelClick(hotel.id) },
                                    onFavoriteClick = {
                                        onIntent(ExploreIntent.OnToggleFavorite(hotel.id, hotel.isFavorite))
                                    }
                                )
                            }
                        }
                    }
                }
            }

            // All Hotels Section Title
            item(key = "list_header") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 28.dp, bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val sectionTitle = if (state.filter.city != null) {
                        stringResource(R.string.explore_hotels_in_city_title, state.filter.city)
                    } else {
                        stringResource(R.string.explore_all_hotels_section_title)
                    }

                    Text(
                        text = sectionTitle,
                        style = typography.headlineSmall,
                        color = colorScheme.onBackground
                    )

                    if (state.filter.minRating != null || state.filter.minPrice != null || state.filter.maxPrice != null || state.filter.city != null) {
                        TextButton(onClick = { onIntent(ExploreIntent.OnResetFilters) }) {
                            Text(
                                text = stringResource(R.string.explore_clear_filters_action),
                                style = typography.labelLarge,
                                color = colorScheme.tertiary
                            )
                        }
                    }
                }
            }

            // Initial Loading
            if (state.isLoadingInitial) {
                item(key = "initial_loading") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = colorScheme.primary,
                            strokeWidth = 2.5.dp
                        )
                    }
                }
            }

            // Error View
            if (state.errorMessage != null && state.hotels.isEmpty()) {
                item(key = "error_view") {
                    ErrorStateView(
                        errorMessage = state.errorMessage,
                        onRetry = { onIntent(ExploreIntent.OnRetry) }
                    )
                }
            }

            // Empty View
            if (!state.isLoadingInitial && state.hotels.isEmpty() && state.errorMessage == null) {
                item(key = "empty_view") {
                    EmptyStateView(
                        icon = Icons.Outlined.CloudOff,
                        title = stringResource(R.string.explore_empty_title),
                        subtitle = stringResource(R.string.explore_empty_subtitle),
                        actionButtonText = stringResource(R.string.explore_empty_action),
                        onActionClick = { onIntent(ExploreIntent.OnResetFilters) }
                    )
                }
            }

            // Hotels List
            items(state.hotels, key = { it.id }) { hotel ->
                HotelCard(
                    hotel = hotel,
                    onClick = { onHotelClick(hotel.id) },
                    onFavoriteClick = {
                        onIntent(ExploreIntent.OnToggleFavorite(hotel.id, hotel.isFavorite))
                    },
                    modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                )
            }

            // Pagination Spinner
            if (state.isLoadingNextPage) {
                item(key = "pagination_loading") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 20.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            modifier = Modifier.size(24.dp),
                            strokeWidth = 2.dp,
                            color = colorScheme.primary
                        )
                    }
                }
            }
        }
    }

    if (showFilterSheet) {
        FilterBottomSheet(
            currentFilter = state.filter,
            onDismiss = { showFilterSheet = false },
            onApply = { rating, minPrice, maxPrice ->
                onIntent(ExploreIntent.OnRatingFilterSelected(rating))
                onIntent(ExploreIntent.OnPriceRangeFilterChanged(minPrice, maxPrice))
                showFilterSheet = false
            },
            onReset = {
                onIntent(ExploreIntent.OnResetFilters)
                showFilterSheet = false
            }
        )
    }
}

private val previewSampleState = ExploreUiState(
    searchQuery = "",
    hotels = Samples.hotels,
    featuredHotels = Samples.hotels,
    availableCities = listOf("Hurghada", "Riyadh", "Aswan", "Jeddah"),
    filter = HotelFilter(),
    isLoadingInitial = false,
    isLoadingNextPage = false,
    isRefreshing = false
)

@Preview(
    name = "Explore Screen • Light Theme",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
private fun ExploreScreenLightPreview() = NuzulTheme(darkTheme = false) {
    Box(Modifier.background(colorScheme.background)) {
        ExploreScreenContent(
            state = previewSampleState,
            onIntent = {},
            onHotelClick = {}
        )
    }
}

@Preview(
    name = "Explore Screen • Dark Theme",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun ExploreScreenDarkPreview() = NuzulTheme(darkTheme = true) {
    Box(Modifier.background(colorScheme.background)) {
        ExploreScreenContent(
            state = previewSampleState,
            onIntent = {},
            onHotelClick = {}
        )
    }
}