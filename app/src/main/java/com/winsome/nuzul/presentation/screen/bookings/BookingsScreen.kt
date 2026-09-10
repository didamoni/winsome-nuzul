package com.winsome.nuzul.presentation.screen.bookings

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.SecondaryTabRow
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.winsome.nuzul.R
import com.winsome.nuzul.presentation.ui.component.BookingCard
import com.winsome.nuzul.presentation.ui.component.EmptyStateView
import com.winsome.nuzul.presentation.ui.theme.NuzulTheme
import com.winsome.nuzul.util.Samples

private const val TAB_UPCOMING = 0
private const val TAB_PAST = 1

@Composable
fun BookingsScreen(
    viewModel: BookingsViewModel,
    onBookingClick: (String) -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    BookingsScreenContent(
        state = state,
        onBookingClick = onBookingClick
    )
}

@Composable
private fun BookingsScreenContent(
    state: BookingsUiState,
    onBookingClick: (String) -> Unit
) {
    var selectedTab by rememberSaveable { mutableIntStateOf(TAB_UPCOMING) }

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
            item(key = "header_section") {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 12.dp)
                ) {
                    Text(
                        text = stringResource(R.string.bookings_brand_eyebrow),
                        style = MaterialTheme.typography.labelSmall.copy(
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = colorScheme.tertiary
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = stringResource(R.string.bookings_title),
                        style = MaterialTheme.typography.displayMedium,
                        color = colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.bookings_subtitle),
                        style = MaterialTheme.typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                }
            }

            // 2. Elegant Tabs (Upcoming vs. Past)
            item(key = "tabs_section") {
                SecondaryTabRow(
                    selectedTabIndex = selectedTab,
                    containerColor = colorScheme.background,
                    contentColor = colorScheme.primary,
                    indicator = {
                        TabRowDefaults.SecondaryIndicator(
                            modifier = Modifier.tabIndicatorOffset(selectedTab),
                            color = colorScheme.primary,
                            height = 2.5.dp
                        )
                    },
                    divider = {},
                    modifier = Modifier.padding(horizontal = 20.dp)
                ) {
                    Tab(
                        selected = selectedTab == TAB_UPCOMING,
                        onClick = { selectedTab = TAB_UPCOMING },
                        text = {
                            Text(
                                text = stringResource(
                                    R.string.bookings_tab_upcoming,
                                    state.upcomingBookings.size
                                ),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (selectedTab == TAB_UPCOMING) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == TAB_UPCOMING) colorScheme.primary else colorScheme.onSurfaceVariant
                            )
                        }
                    )
                    Tab(
                        selected = selectedTab == TAB_PAST,
                        onClick = { selectedTab = TAB_PAST },
                        text = {
                            Text(
                                text = stringResource(
                                    R.string.bookings_tab_past,
                                    state.pastBookings.size
                                ),
                                style = MaterialTheme.typography.labelLarge,
                                fontWeight = if (selectedTab == TAB_PAST) FontWeight.Bold else FontWeight.Medium,
                                color = if (selectedTab == TAB_PAST) colorScheme.primary else colorScheme.onSurfaceVariant
                            )
                        }
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))
            }

            // 3. Loading Indicator
            if (state.isLoading) {
                item(key = "loading_state") {
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

            // 4. Content Based on Selected Tab
            if (!state.isLoading) {
                val activeList = if (selectedTab == TAB_UPCOMING) state.upcomingBookings else state.pastBookings

                if (activeList.isEmpty()) {
                    item(key = "empty_state_$selectedTab") {
                        if (selectedTab == TAB_UPCOMING) {
                            EmptyStateView(
                                icon = Icons.Outlined.BookmarkBorder,
                                title = stringResource(R.string.bookings_empty_upcoming_title),
                                subtitle = stringResource(R.string.bookings_empty_upcoming_subtitle),
                                modifier = Modifier.padding(top = 24.dp)
                            )
                        } else {
                            EmptyStateView(
                                icon = Icons.Outlined.History,
                                title = stringResource(R.string.bookings_empty_past_title),
                                subtitle = stringResource(R.string.bookings_empty_past_subtitle),
                                modifier = Modifier.padding(top = 24.dp)
                            )
                        }
                    }
                } else {
                    items(activeList, key = { it.reference }) { booking ->
                        BookingCard(
                            booking = booking,
                            isPast = selectedTab == TAB_PAST,
                            onClick = { onBookingClick(booking.reference) },
                            modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp)
                        )
                    }
                }
            }
        }
    }
}

private val previewBookingsState = BookingsUiState(
    isLoading = false,
    upcomingBookings = Samples.bookings,
    pastBookings = emptyList()
)

@Preview(
    name = "Bookings Screen • Light Theme",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
private fun BookingsScreenLightPreview() = NuzulTheme(darkTheme = false) {
    Box(Modifier.background(colorScheme.background)) {
        BookingsScreenContent(
            state = previewBookingsState,
            onBookingClick = {}
        )
    }
}

@Preview(
    name = "Bookings Screen • Dark Theme",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun BookingsScreenDarkPreview() = NuzulTheme(darkTheme = true) {
    Box(Modifier.background(colorScheme.background)) {
        BookingsScreenContent(
            state = previewBookingsState,
            onBookingClick = {}
        )
    }
}