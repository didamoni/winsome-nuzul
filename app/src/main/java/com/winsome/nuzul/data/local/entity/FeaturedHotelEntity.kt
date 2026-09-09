package com.winsome.nuzul.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "featured_hotels",
    foreignKeys = [
        ForeignKey(
            entity = HotelEntity::class,
            parentColumns = ["id"],
            childColumns = ["hotelId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index(value = ["hotelId"])]
)
data class FeaturedHotelEntity(
    @PrimaryKey
    val hotelId: String,
    val displayOrder: Int
)