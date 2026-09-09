package com.winsome.nuzul.presentation.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.hilt.lifecycle.viewmodel.compose.hiltViewModel
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.winsome.nuzul.presentation.screen.booking.BookingScreen
import com.winsome.nuzul.presentation.screen.booking.BookingViewModel
import com.winsome.nuzul.presentation.screen.bookings.BookingDetailsScreen
import com.winsome.nuzul.presentation.screen.bookings.BookingDetailsViewModel
import com.winsome.nuzul.presentation.screen.bookings.BookingsScreen
import com.winsome.nuzul.presentation.screen.explore.ExploreScreen
import com.winsome.nuzul.presentation.screen.favorites.FavoritesScreen
import com.winsome.nuzul.presentation.screen.hotel.HotelDetailsScreen
import com.winsome.nuzul.presentation.screen.hotel.HotelDetailsViewModel

@Composable
fun NuzulNavDisplay(
    backStack: NavBackStack<NavKey>,
    modifier: Modifier = Modifier
) = NavDisplay(
    modifier = modifier,
    backStack = backStack,
    entryDecorators = listOf(
        rememberSaveableStateHolderNavEntryDecorator(),
        rememberViewModelStoreNavEntryDecorator()
    ),
    entryProvider = entryProvider {
        entry<Route.Main.Explore> {
            ExploreScreen(
                viewModel = hiltViewModel(),
                onHotelClick = { backStack.add(Route.HotelDetails(it)) }
            )
        }
        entry<Route.Main.Favorites> {
            FavoritesScreen(
                viewModel = hiltViewModel(),
                onHotelClick = { backStack.add(Route.HotelDetails(it)) }
            )
        }
        entry<Route.Main.Bookings> {
            BookingsScreen(
                viewModel = hiltViewModel(),
                onBookingClick = { backStack.add(Route.BookingDetails(it)) }
            )
        }

        entry<Route.HotelDetails> { route ->
            HotelDetailsScreen(
                viewModel = hiltViewModel { factory: HotelDetailsViewModel.Factory ->
                    factory.create(hotelId = route.hotelId)
                },
                onBookClick = { backStack.add(Route.Booking(it)) },
                onBackClick = { backStack.removeLastOrNull() }
            )
        }
        entry<Route.Booking> { route ->
            BookingScreen(
                viewModel = hiltViewModel { factory: BookingViewModel.Factory ->
                    factory.create(hotelId = route.hotelId)
                },
                onConfirmBooking = {
                    backStack.clear()
                    backStack.addAll(listOf(Route.Main.Explore, Route.Main.Bookings))
                },
                onBackClick = { backStack.removeLastOrNull() }
            )
        }
        entry<Route.BookingDetails> { route ->
            BookingDetailsScreen(
                viewModel = hiltViewModel { factory: BookingDetailsViewModel.Factory ->
                    factory.create(reference = route.reference)
                },
                onBackClick = { backStack.removeLastOrNull() }
            )
        }
    }
)
