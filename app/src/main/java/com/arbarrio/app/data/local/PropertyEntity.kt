package com.arbarrio.app.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "properties")
data class PropertyEntity(
    @PrimaryKey val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val location: String,
    val isFavorite: Boolean = false
)
