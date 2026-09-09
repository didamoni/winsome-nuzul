package com.winsome.nuzul.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "favorites",
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
data class FavoriteEntity(
    @PrimaryKey
    val hotelId: String,
    val savedAt: Long = System.currentTimeMillis()
)