package com.winsome.nuzul

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.winsome.nuzul.domain.model.Hotel
import com.winsome.nuzul.domain.model.Location
import com.winsome.nuzul.domain.model.Money
import com.winsome.nuzul.presentation.ui.component.HotelCard
import com.winsome.nuzul.presentation.ui.theme.NuzulTheme
import org.junit.Rule
import org.junit.Test
import java.math.BigDecimal

class ExploreToDetailsNavigationTest {

    @get:Rule
    val composeTestRule = createComposeRule()

    private val testHotel = Hotel(
        id = "test-1",
        name = "The Oberoi Beach Resort",
        city = "Hurghada",
        rating = 4.9,
        pricePerNight = Money(BigDecimal("1150"), "SAR"),
        images = emptyList(),
        amenities = listOf("WiFi", "Beach"),
        description = "Tranquil beach sanctuary",
        location = Location(0.0, 0.0, "Sahl Hasheesh"),
        isFavorite = false,
        isFeatured = true
    )

    @Test
    fun hotelCard_displaysNamePriceAndTriggersClick() {
        var clickedHotelId = ""

        composeTestRule.setContent {
            NuzulTheme {
                HotelCard(
                    hotel = testHotel,
                    onClick = { clickedHotelId = testHotel.id },
                    onFavoriteClick = {}
                )
            }
        }

        // 1. Verify Hotel Name is displayed
        composeTestRule.onNodeWithText("The Oberoi Beach Resort").assertIsDisplayed()

        // 2. Verify Price is displayed
        composeTestRule.onNodeWithText("SAR 1150", substring = true).assertIsDisplayed()

        // 3. Click Reserve action
        composeTestRule.onNodeWithText("Reserve").performClick()

        // 4. Verify correct hotel ID callback was triggered
        assert(clickedHotelId == "test-1")
    }
}
