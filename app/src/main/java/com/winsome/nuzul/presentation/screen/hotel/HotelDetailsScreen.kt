package com.winsome.nuzul.presentation.screen.hotel

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.LocationOn
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.winsome.nuzul.R
import com.winsome.nuzul.presentation.ui.component.EmptyStateView
import com.winsome.nuzul.presentation.ui.component.HotelBookingBottomBar
import com.winsome.nuzul.presentation.ui.component.HotelImagePager
import com.winsome.nuzul.presentation.ui.theme.NuzulTheme
import com.winsome.nuzul.util.Samples

@Composable
fun HotelDetailsScreen(
    viewModel: HotelDetailsViewModel,
    onBookClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    HotelDetailsScreenContent(
        state = state,
        onIntent = viewModel::onIntent,
        onBookClick = onBookClick,
        onBackClick = onBackClick
    )
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun HotelDetailsScreenContent(
    state: HotelDetailsUiState,
    onIntent: (HotelDetailsIntent) -> Unit,
    onBookClick: (String) -> Unit,
    onBackClick: () -> Unit
) {
    val density = LocalDensity.current
    var bottomBarHeightDp by remember { mutableStateOf(0.dp) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        // 1. Loading State
        if (state.isLoading) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(
                    color = colorScheme.primary,
                    strokeWidth = 2.5.dp
                )
            }
        }

        // 2. Error State
        if (!state.isLoading && state.hotel == null) {
            EmptyStateView(
                icon = Icons.Outlined.ErrorOutline,
                title = stringResource(R.string.hotel_details_error_title),
                subtitle = stringResource(R.string.hotel_details_error_subtitle),
                actionButtonText = stringResource(R.string.hotel_details_error_back),
                onActionClick = onBackClick,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // 3. Scrollable Hotel Content
        state.hotel?.let { hotel ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Hero Image Gallery
                HotelImagePager(
                    images = hotel.images,
                    isFavorite = hotel.isFavorite,
                    onBackClick = onBackClick,
                    onFavoriteClick = { onIntent(HotelDetailsIntent.OnToggleFavorite) }
                )

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 20.dp)
                ) {
                    // Rating & City Badge Row
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = colorScheme.surfaceVariant
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Star,
                                    contentDescription = null,
                                    tint = colorScheme.tertiary,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = hotel.rating.toString(),
                                    style = MaterialTheme.typography.labelMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = colorScheme.onSurface
                                )
                            }
                        }

                        Text(
                            text = hotel.city,
                            style = MaterialTheme.typography.labelMedium,
                            color = colorScheme.tertiary,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Hotel Name
                    Text(
                        text = hotel.name,
                        style = MaterialTheme.typography.displayMedium,
                        color = colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Address
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Outlined.LocationOn,
                            contentDescription = null,
                            tint = colorScheme.secondary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = hotel.location.address ?: "${hotel.city}, Saudi Arabia",
                            style = MaterialTheme.typography.bodyMedium,
                            color = colorScheme.onSurfaceVariant
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(20.dp))

                    // Description Section
                    Text(
                        text = stringResource(R.string.hotel_details_about_section),
                        style = MaterialTheme.typography.headlineSmall,
                        color = colorScheme.onBackground
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = hotel.description,
                        style = MaterialTheme.typography.bodyLarge,
                        color = colorScheme.onSurfaceVariant,
                        lineHeight = 24.sp
                    )

                    Spacer(modifier = Modifier.height(24.dp))
                    HorizontalDivider(color = colorScheme.outlineVariant)
                    Spacer(modifier = Modifier.height(20.dp))

                    // Amenities Section
                    if (hotel.amenities.isNotEmpty()) {
                        Text(
                            text = stringResource(R.string.hotel_details_amenities_section),
                            style = MaterialTheme.typography.headlineSmall,
                            color = colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            hotel.amenities.forEach { amenity ->
                                Surface(
                                    shape = RoundedCornerShape(8.dp),
                                    color = colorScheme.surfaceVariant
                                ) {
                                    Text(
                                        text = amenity,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = colorScheme.onSurface,
                                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                    )
                                }
                            }
                        }
                    }

                    // Dynamic clearance spacer: matches measured bottom bar height + 16dp
                    Spacer(modifier = Modifier.height(bottomBarHeightDp + 16.dp))
                }
            }

            // 4. Sticky Bottom Action Bar
            HotelBookingBottomBar(
                pricePerNight = hotel.pricePerNight,
                onBookClick = { onBookClick(hotel.id) },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .onGloballyPositioned { coordinates ->
                        bottomBarHeightDp = with(density) { coordinates.size.height.toDp() }
                    }
            )
        }
    }
}

private val previewHotelDetailsState = HotelDetailsUiState(
    isLoading = false,
    hotel = Samples.hotels.firstOrNull(),
    errorMessage = null
)

@Preview(
    name = "Hotel Details • Light Theme",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
private fun HotelDetailsScreenLightPreview() = NuzulTheme(darkTheme = false) {
    HotelDetailsScreenContent(
        state = previewHotelDetailsState,
        onIntent = {},
        onBookClick = {},
        onBackClick = {}
    )
}

@Preview(
    name = "Hotel Details • Dark Theme",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun HotelDetailsScreenDarkPreview() = NuzulTheme(darkTheme = true) {
    HotelDetailsScreenContent(
        state = previewHotelDetailsState,
        onIntent = {},
        onBookClick = {},
        onBackClick = {}
    )
}