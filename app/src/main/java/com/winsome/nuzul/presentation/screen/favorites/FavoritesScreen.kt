package com.winsome.nuzul.presentation.screen.favorites

import android.content.res.Configuration
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.winsome.nuzul.R
import com.winsome.nuzul.domain.model.Hotel
import com.winsome.nuzul.domain.model.Location
import com.winsome.nuzul.domain.model.Money
import com.winsome.nuzul.presentation.ui.component.EmptyStateView
import com.winsome.nuzul.presentation.ui.component.HotelCard
import com.winsome.nuzul.presentation.ui.theme.NuzulTheme
import java.math.BigDecimal

@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel,
    onHotelClick: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    FavoritesScreenContent(
        state = state,
        onIntent = viewModel::onIntent,
        onHotelClick = onHotelClick
    )
}

@Composable
private fun FavoritesScreenContent(
    state: FavoritesUiState,
    onIntent: (FavoritesIntent) -> Unit,
    onHotelClick: (String) -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(bottom = 32.dp)
        ) {
            // 1. Editorial Header
            item(key = "favorites_header") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.favorites_brand_eyebrow),
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = colorScheme.tertiary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.favorites_title),
                            style = MaterialTheme.typography.displayMedium,
                            color = colorScheme.onBackground
                        )

                        // Count Badge (shown if favorites exist)
                        if (state.favorites.isNotEmpty()) {
                            Surface(
                                shape = MaterialTheme.shapes.small,
                                color = colorScheme.surfaceVariant
                            ) {
                                Text(
                                    text = stringResource(R.string.favorites_count_format, state.favorites.size),
                                    style = MaterialTheme.typography.labelMedium,
                                    color = colorScheme.primary,
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp)
                                )
                            }
                        }
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.favorites_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }

            // 2. Loading State
            if (state.isLoading) {
                item(key = "loading_state") {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(260.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(
                            color = colorScheme.primary,
                            strokeWidth = 2.5.dp
                        )
                    }
                }
            }

            // 3. Empty State
            if (!state.isLoading && state.favorites.isEmpty()) {
                item(key = "empty_state") {
                    EmptyStateView(
                        icon = Icons.Outlined.FavoriteBorder,
                        title = stringResource(R.string.favorites_empty_title),
                        subtitle = stringResource(R.string.favorites_empty_subtitle),
                        modifier = Modifier.padding(top = 40.dp)
                    )
                }
            }

            // 4. Saved Hotels List
            items(state.favorites, key = { it.id }) { hotel ->
                AnimatedVisibility(
                    visible = true,
                    enter = fadeIn(),
                    exit = fadeOut()
                ) {
                    HotelCard(
                        hotel = hotel,
                        onClick = { onHotelClick(hotel.id) },
                        onFavoriteClick = { onIntent(FavoritesIntent.OnRemoveFavorite(hotel.id)) },
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 10.dp)
                    )
                }
            }
        }
    }
}

// ─────────────────────────────────────────────────────────────────────────────
// Screen Previews (Light & Dark Theme)
// ─────────────────────────────────────────────────────────────────────────────

private val previewFavoritesSample = listOf(
    Hotel(
        id = "1",
        name = "The Oberoi Beach Resort",
        city = "Hurghada",
        rating = 4.9,
        pricePerNight = Money(BigDecimal("1150.00"), "SAR"),
        images = listOf("https://images.unsplash.com/photo-1540555700478-4be289fbecef?auto=format&fit=crop&w=1200&q=80"),
        amenities = listOf("WiFi", "Beach", "Spa"),
        description = "Tranquil beachfront resort.",
        location = Location(27.04, 33.87, "Sahl Hasheesh, Hurghada"),
        isFavorite = true,
        isFeatured = true
    ),
    Hotel(
        id = "2",
        name = "Mövenpick Resort Aswan",
        city = "Aswan",
        rating = 4.8,
        pricePerNight = Money(BigDecimal("650.00"), "SAR"),
        images = listOf("https://images.unsplash.com/photo-1572252821143-024227318f78?auto=format&fit=crop&w=1200&q=80"),
        amenities = listOf("Pool", "Nile View", "Restaurant"),
        description = "Island sanctuary surrounded by lush gardens.",
        location = Location(24.08, 32.89, "Elephantine Island, Aswan"),
        isFavorite = true,
        isFeatured = false
    )
)

private val previewFavoritesState = FavoritesUiState(
    isLoading = false,
    favorites = previewFavoritesSample,
    errorMessage = null
)

@Preview(
    name = "Favorites Screen • Light Theme",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
private fun FavoritesScreenLightPreview() = NuzulTheme(darkTheme = false) {
    Box(Modifier.background(colorScheme.background)) {
        FavoritesScreenContent(
            state = previewFavoritesState,
            onIntent = {},
            onHotelClick = {}
        )
    }
}

@Preview(
    name = "Favorites Screen • Dark Theme",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun FavoritesScreenDarkPreview() = NuzulTheme(darkTheme = true) {
    Box(Modifier.background(colorScheme.background)) {
        FavoritesScreenContent(
            state = previewFavoritesState,
            onIntent = {},
            onHotelClick = {}
        )
    }
}