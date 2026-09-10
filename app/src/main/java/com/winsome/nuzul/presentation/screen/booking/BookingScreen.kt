package com.winsome.nuzul.presentation.screen.booking

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.outlined.ErrorOutline
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MaterialTheme.colorScheme
import androidx.compose.material3.MaterialTheme.typography
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.winsome.nuzul.R
import com.winsome.nuzul.domain.util.BookingCalculationEngine
import com.winsome.nuzul.domain.util.BookingDateValidator
import com.winsome.nuzul.presentation.ui.component.DateSelectionRow
import com.winsome.nuzul.presentation.ui.component.EmptyStateView
import com.winsome.nuzul.presentation.ui.component.HotelSummaryCard
import com.winsome.nuzul.presentation.ui.component.NuzulDatePickerDialog
import com.winsome.nuzul.presentation.ui.theme.NuzulTheme
import com.winsome.nuzul.util.Samples
import java.time.LocalDate
import java.time.format.DateTimeFormatter

private val dateFormatter = DateTimeFormatter.ofPattern("EEE, MMM dd, yyyy")

@Composable
fun BookingScreen(
    viewModel: BookingViewModel,
    onConfirmBooking: () -> Unit,
    onBackClick: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    // Listen for booking success event
    LaunchedEffect(viewModel.events) {
        viewModel.events.collect { event ->
            if (event is BookingEvent.BookingSuccess) {
                onConfirmBooking()
            }
        }
    }

    BookingScreenContent(
        state = state,
        onIntent = viewModel::onIntent,
        onBackClick = onBackClick
    )
}

@Composable
private fun BookingScreenContent(
    state: BookingUiState,
    onIntent: (BookingIntent) -> Unit,
    onBackClick: () -> Unit
) {
    val density = LocalDensity.current
    var bottomBarHeightDp by remember { mutableStateOf(0.dp) }
    var showConfirmDialog by remember { mutableStateOf(false) }
    var showCheckInPicker by remember { mutableStateOf(false) }
    var showCheckOutPicker by remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(colorScheme.background)
    ) {
        // 1. Loading State
        if (state.isLoadingHotel) {
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
        if (!state.isLoadingHotel && state.hotel == null) {
            EmptyStateView(
                icon = Icons.Outlined.ErrorOutline,
                title = stringResource(R.string.hotel_details_error_title),
                subtitle = stringResource(R.string.hotel_details_error_subtitle),
                actionButtonText = stringResource(R.string.hotel_details_error_back),
                onActionClick = onBackClick,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        // 3. Main Booking Form
        state.hotel?.let { hotel ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
            ) {
                // Top Bar with Back Button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Surface(
                        onClick = onBackClick,
                        shape = CircleShape,
                        color = colorScheme.surface,
                        modifier = Modifier
                            .size(42.dp)
                            .border(1.dp, colorScheme.outlineVariant, CircleShape)
                    ) {
                        Box(contentAlignment = Alignment.Center) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                contentDescription = stringResource(R.string.hotel_details_back_desc),
                                tint = colorScheme.onSurface,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                    }
                }

                // Header Titles
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp, vertical = 6.dp)
                ) {
                    Text(
                        text = stringResource(R.string.booking_brand_eyebrow),
                        style = typography.labelSmall.copy(
                            letterSpacing = 2.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = colorScheme.tertiary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.booking_title),
                        style = typography.displayMedium,
                        color = colorScheme.onBackground
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.booking_subtitle),
                        style = typography.bodyMedium,
                        color = colorScheme.onSurfaceVariant
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Hotel Overview Card
                HotelSummaryCard(
                    hotel = hotel,
                    modifier = Modifier.padding(horizontal = 20.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Dates & Rooms Configuration Card
                Card(
                    shape = MaterialTheme.shapes.large,
                    colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp)
                ) {
                    Column(modifier = Modifier.padding(18.dp)) {
                        Text(
                            text = stringResource(R.string.booking_dates_and_rooms),
                            style = typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        // Check-in Date Selector
                        DateSelectionRow(
                            label = stringResource(R.string.booking_check_in_label),
                            dateFormatted = state.checkInDate.format(dateFormatter),
                            onClick = { showCheckInPicker = true }
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Check-out Date Selector
                        DateSelectionRow(
                            label = stringResource(R.string.booking_check_out_label),
                            dateFormatted = state.checkOutDate.format(dateFormatter),
                            onClick = { showCheckOutPicker = true }
                        )

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = colorScheme.outlineVariant)
                        Spacer(modifier = Modifier.height(16.dp))

                        // Rooms Counter
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = stringResource(R.string.booking_rooms_label),
                                    style = typography.bodyMedium,
                                    fontWeight = FontWeight.SemiBold,
                                    color = colorScheme.onSurface
                                )
                                Text(
                                    text = if (state.rooms == 1) {
                                        stringResource(R.string.booking_rooms_count_single, state.rooms)
                                    } else {
                                        stringResource(R.string.booking_rooms_count_plural, state.rooms)
                                    },
                                    style = typography.bodySmall,
                                    color = colorScheme.onSurfaceVariant
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Surface(
                                    onClick = { onIntent(BookingIntent.OnDecrementRooms) },
                                    enabled = state.rooms > 1,
                                    shape = CircleShape,
                                    color = if (state.rooms > 1) colorScheme.surfaceVariant else colorScheme.surfaceVariant.copy(alpha = 0.5f),
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Remove,
                                            contentDescription = null,
                                            tint = if (state.rooms > 1) colorScheme.primary else colorScheme.onSurfaceVariant.copy(alpha = 0.4f),
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Text(
                                    text = state.rooms.toString(),
                                    style = typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = colorScheme.onSurface,
                                    modifier = Modifier.padding(horizontal = 16.dp)
                                )

                                Surface(
                                    onClick = { onIntent(BookingIntent.OnIncrementRooms) },
                                    shape = CircleShape,
                                    color = colorScheme.primary,
                                    modifier = Modifier.size(36.dp)
                                ) {
                                    Box(contentAlignment = Alignment.Center) {
                                        Icon(
                                            imageVector = Icons.Default.Add,
                                            contentDescription = null,
                                            tint = colorScheme.onPrimary,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }

                        // Validation Error Warning
                        state.dateValidationError?.let { error ->
                            Spacer(modifier = Modifier.height(14.dp))
                            val errorMessage = when (error) {
                                is BookingDateValidator.ValidationResult.Invalid.CheckInInPast ->
                                    stringResource(R.string.booking_error_checkin_past)
                                is BookingDateValidator.ValidationResult.Invalid.CheckOutNotAfterCheckIn ->
                                    stringResource(R.string.booking_error_checkout_before_checkin)
                                is BookingDateValidator.ValidationResult.Invalid.InvalidRoomCount ->
                                    stringResource(R.string.booking_error_invalid_rooms)
                            }
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = colorScheme.errorContainer.copy(alpha = 0.7f),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
                                ) {
                                    Icon(
                                        imageVector = Icons.Outlined.Info,
                                        contentDescription = null,
                                        tint = colorScheme.error,
                                        modifier = Modifier.size(16.dp)
                                    )
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text(
                                        text = errorMessage,
                                        style = typography.bodySmall,
                                        color = colorScheme.onErrorContainer
                                    )
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Price Breakdown Card
                state.pricing?.let { pricing ->
                    Card(
                        shape = MaterialTheme.shapes.large,
                        colors = CardDefaults.cardColors(containerColor = colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp)
                    ) {
                        Column(modifier = Modifier.padding(18.dp)) {
                            Text(
                                text = stringResource(R.string.booking_pricing_breakdown_title),
                                style = typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = colorScheme.onSurface
                            )

                            Spacer(modifier = Modifier.height(14.dp))

                            // Base Price Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = stringResource(
                                        R.string.booking_pricing_rate_breakdown,
                                        pricing.nights,
                                        state.rooms
                                    ),
                                    style = typography.bodyMedium,
                                    color = colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${pricing.basePrice.currency} ${pricing.basePrice.amount.toInt()}",
                                    style = typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            // 15% VAT Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = stringResource(R.string.booking_pricing_vat_label),
                                    style = typography.bodyMedium,
                                    color = colorScheme.onSurfaceVariant
                                )
                                Text(
                                    text = "${pricing.vat.currency} ${pricing.vat.amount.toInt()}",
                                    style = typography.bodyMedium,
                                    fontWeight = FontWeight.Medium,
                                    color = colorScheme.onSurface
                                )
                            }

                            Spacer(modifier = Modifier.height(12.dp))
                            HorizontalDivider(color = colorScheme.outlineVariant)
                            Spacer(modifier = Modifier.height(12.dp))

                            // Total Price Row
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = stringResource(R.string.booking_pricing_total_label),
                                    style = typography.titleMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = colorScheme.onSurface
                                )
                                Row {
                                    Text(
                                        text = "${pricing.totalPrice.currency} ${pricing.totalPrice.amount.toInt()}",
                                        style = typography.titleLarge.copy(
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 22.sp
                                        ),
                                        color = colorScheme.primary,
                                        modifier = Modifier.alignByBaseline()
                                    )
                                }
                            }
                        }
                    }
                }

                // Dynamic bottom clearance to prevent bottom bar from overlapping content
                Spacer(modifier = Modifier.height(bottomBarHeightDp + 16.dp))
            }

            // 4. Sticky Confirmation Bar
            state.pricing?.let { pricing ->
                val dividerColor = colorScheme.outlineVariant

                Surface(
                    color = colorScheme.surface,
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .drawBehind {
                            drawLine(
                                color = dividerColor,
                                start = Offset(0f, 0f),
                                end = Offset(size.width, 0f),
                                strokeWidth = 1.dp.toPx()
                            )
                        }
                        .onGloballyPositioned { coordinates ->
                            bottomBarHeightDp = with(density) { coordinates.size.height.toDp() }
                        }
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 20.dp, vertical = 14.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = stringResource(R.string.bookings_total_label),
                                style = typography.labelSmall.copy(letterSpacing = 1.sp),
                                color = colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "${pricing.totalPrice.currency} ${pricing.totalPrice.amount.toInt()}",
                                style = typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 20.sp
                                ),
                                color = colorScheme.primary
                            )
                        }

                        Button(
                            onClick = { showConfirmDialog = true },
                            enabled = state.canConfirm,
                            shape = MaterialTheme.shapes.medium,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = colorScheme.primary,
                                contentColor = colorScheme.onPrimary
                            ),
                            contentPadding = PaddingValues(horizontal = 24.dp, vertical = 12.dp),
                            modifier = Modifier.height(48.dp)
                        ) {
                            if (state.isSubmitting) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(20.dp),
                                    color = colorScheme.onPrimary,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = stringResource(R.string.booking_confirm_action),
                                    style = typography.labelLarge.copy(fontWeight = FontWeight.Bold),
                                    maxLines = 1
                                )
                            }
                        }
                    }
                }
            }
        }
    }

    // Check-in Date Picker Dialog
    if (showCheckInPicker) {
        NuzulDatePickerDialog(
            initialDate = state.checkInDate,
            minDate = LocalDate.now(),
            onDateSelected = { onIntent(BookingIntent.OnCheckInDateSelected(it)) },
            onDismiss = { showCheckInPicker = false }
        )
    }

    // Check-out Date Picker Dialog
    if (showCheckOutPicker) {
        NuzulDatePickerDialog(
            initialDate = state.checkOutDate,
            minDate = state.checkInDate.plusDays(1),
            onDateSelected = { onIntent(BookingIntent.OnCheckOutDateSelected(it)) },
            onDismiss = { showCheckOutPicker = false }
        )
    }

    // Confirmation Alert Dialog
    if (showConfirmDialog) {
        state.hotel?.let { hotel ->
            state.pricing?.let { pricing ->
                AlertDialog(
                    onDismissRequest = { showConfirmDialog = false },
                    containerColor = colorScheme.surface,
                    title = {
                        Text(
                            text = stringResource(R.string.booking_dialog_title),
                            style = typography.headlineSmall,
                            color = colorScheme.onSurface
                        )
                    },
                    text = {
                        Text(
                            text = stringResource(
                                R.string.booking_dialog_message,
                                hotel.name,
                                pricing.nights,
                                pricing.totalPrice.currency,
                                pricing.totalPrice.amount.toInt()
                            ),
                            style = typography.bodyMedium,
                            color = colorScheme.onSurfaceVariant
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = {
                                showConfirmDialog = false
                                onIntent(BookingIntent.OnConfirmBooking)
                            },
                            shape = MaterialTheme.shapes.medium,
                            colors = ButtonDefaults.buttonColors(containerColor = colorScheme.primary)
                        ) {
                            Text(stringResource(R.string.booking_dialog_confirm))
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { showConfirmDialog = false }) {
                            Text(
                                text = stringResource(R.string.booking_dialog_cancel),
                                color = colorScheme.onSurfaceVariant
                            )
                        }
                    }
                )
            }
        }
    }
}

private val previewBookingState = BookingUiState(
    hotel = Samples.hotels.first(),
    isLoadingHotel = false,
    checkInDate = LocalDate.now().plusDays(2),
    checkOutDate = LocalDate.now().plusDays(5),
    rooms = 1,
    pricing = BookingCalculationEngine.calculate(
        checkIn = LocalDate.now().plusDays(2),
        checkOut = LocalDate.now().plusDays(5),
        rooms = 1,
        pricePerNight = Samples.hotels.first().pricePerNight
    )
)

@Preview(
    name = "Booking Screen • Light Theme",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_NO
)
@Composable
private fun BookingScreenLightPreview() = NuzulTheme(darkTheme = false) {
    Box(Modifier.background(colorScheme.background)) {
        BookingScreenContent(
            state = previewBookingState,
            onIntent = {},
            onBackClick = {}
        )
    }
}

@Preview(
    name = "Booking Screen • Dark Theme",
    showBackground = true,
    showSystemUi = true,
    uiMode = Configuration.UI_MODE_NIGHT_YES
)
@Composable
private fun BookingScreenDarkPreview() = NuzulTheme(darkTheme = true) {
    Box(Modifier.background(colorScheme.background)) {
        BookingScreenContent(
            state = previewBookingState,
            onIntent = {},
            onBackClick = {}
        )
    }
}