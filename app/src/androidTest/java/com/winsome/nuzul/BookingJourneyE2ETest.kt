package com.winsome.nuzul

import androidx.compose.ui.test.ExperimentalTestApi
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.v2.createAndroidComposeRule
import androidx.compose.ui.test.onAllNodesWithText
import androidx.compose.ui.test.onFirst
import androidx.compose.ui.test.onLast
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performScrollTo
import com.winsome.nuzul.presentation.MainActivity
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalTestApi::class)
class BookingJourneyE2ETest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun testCriticalBookingUserJourney() {
        // ---------------------------------------------------------------------
        // Step 1: Wait for Explore Screen to load hotels from mock data source
        // ---------------------------------------------------------------------
        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            composeTestRule
                .onAllNodesWithText("Reserve")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeTestRule.waitForIdle()

        // ---------------------------------------------------------------------
        // Step 2: Scroll to the first hotel card and click it
        // ---------------------------------------------------------------------
        // Locate the first hotel card's Reserve button
        val firstHotelReserveButton = composeTestRule
            .onAllNodesWithText("Reserve")
            .onFirst()

        // MUST scroll into view so the click coordinates are inside the viewport!
        firstHotelReserveButton.performScrollTo()
        composeTestRule.waitForIdle()

        // Perform click once visible
        firstHotelReserveButton.performClick()
        composeTestRule.waitForIdle()

        // ---------------------------------------------------------------------
        // Step 3: Verify Hotel Details screen appears
        // ---------------------------------------------------------------------
        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            composeTestRule
                .onAllNodesWithText("About This Sanctuary")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }
        composeTestRule.onNodeWithText("About This Sanctuary").assertIsDisplayed()
        composeTestRule.waitForIdle()

        // ---------------------------------------------------------------------
        // Step 4: Click the sticky "Reserve" button on Hotel Details
        // ---------------------------------------------------------------------
        // Use .onLast() to target the Reserve button on the top active screen
        composeTestRule
            .onAllNodesWithText("Reserve")
            .onLast()
            .performClick()

        composeTestRule.waitForIdle()

        // ---------------------------------------------------------------------
        // Step 5: Verify Booking Screen appears with price calculation
        // ---------------------------------------------------------------------
        composeTestRule.waitUntil(timeoutMillis = 10_000) {
            composeTestRule
                .onAllNodesWithText("Complete Booking")
                .fetchSemanticsNodes()
                .isNotEmpty()
        }

        composeTestRule.onNodeWithText("Complete Booking").assertIsDisplayed()
        composeTestRule.onNodeWithText("Dates & Rooms").assertIsDisplayed()
        composeTestRule.onNodeWithText("Price Breakdown").assertIsDisplayed()
        composeTestRule.onNodeWithText("Confirm Reservation").assertIsDisplayed()
    }
}