package com.winsome.nuzul.presentation.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val NuzulShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),      // Chips & small badges
    medium = RoundedCornerShape(12.dp),    // Buttons & TextFields
    large = RoundedCornerShape(16.dp),     // Hotel Cards & Dialogs
    extraLarge = RoundedCornerShape(24.dp) // Bottom Sheets & Surfaces
)