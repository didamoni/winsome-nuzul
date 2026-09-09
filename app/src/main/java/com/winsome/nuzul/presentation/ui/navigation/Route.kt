package com.winsome.nuzul.presentation.ui.navigation

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.runtime.Immutable
import androidx.navigation3.runtime.NavKey
import com.winsome.nuzul.R
import kotlinx.serialization.Serializable

@Immutable
sealed interface Route : NavKey {

    @Immutable
    sealed interface Main : Route {
        @get:StringRes val label: Int
        @get:DrawableRes val filledIcon: Int
        @get:DrawableRes val outlinedIcon: Int

        @Serializable
        data object Explore : Main {
            override val label = R.string.explore
            override val filledIcon = R.drawable.ic_compass_filled
            override val outlinedIcon = R.drawable.ic_compass_outlined
        }

        @Serializable
        data object Favorites : Main {
            override val label = R.string.favorites
            override val filledIcon = R.drawable.ic_heart_filled
            override val outlinedIcon = R.drawable.ic_heart_outlined
        }

        @Serializable
        data object Bookings : Main {
            override val label = R.string.bookings
            override val filledIcon = R.drawable.ic_clipboard_list_filled
            override val outlinedIcon = R.drawable.ic_clipboard_list_outlined
        }

        companion object {
            val entries = listOf(Explore, Favorites, Bookings)
        }
    }

    @Serializable
    data class HotelDetails(val hotelId: String) : Route

    @Serializable
    data class Booking(val hotelId: String) : Route

    @Serializable
    data class BookingDetails(val reference: String) : Route
}
