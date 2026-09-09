package com.winsome.nuzul.data.local.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import java.math.BigDecimal

@Entity(
    tableName = "hotels",
    indices = [
        Index(value = ["city"]),
        Index(value = ["rating"]),
        Index(value = ["priceAmount"])
    ]
)
data class HotelEntity(
    @PrimaryKey
    val id: String,
    val name: String,
    val city: String,
    val rating: Double,
    val priceAmount: BigDecimal,
    val currency: String,
    val images: List<String>,
    val amenities: List<String>,
    val description: String,
    val latitude: Double,
    val longitude: Double,
    val address: String,
    val isFeatured: Boolean
)