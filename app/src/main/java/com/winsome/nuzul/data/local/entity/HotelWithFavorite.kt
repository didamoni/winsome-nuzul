package com.winsome.nuzul.data.local.entity

import androidx.room.Embedded

data class HotelWithFavorite(
    @Embedded
    val hotel: HotelEntity,
    val isFavorite: Boolean
)