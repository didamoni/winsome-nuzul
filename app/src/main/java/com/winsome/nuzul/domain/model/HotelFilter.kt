package com.winsome.nuzul.domain.model

import java.math.BigDecimal

data class HotelFilter(
    val query: String = "",
    val city: String? = null,
    val minRating: Double? = null,
    val minPrice: BigDecimal? = null,
    val maxPrice: BigDecimal? = null
)