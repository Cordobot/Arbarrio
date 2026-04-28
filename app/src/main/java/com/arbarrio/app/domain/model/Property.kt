package com.arbarrio.app.domain.model

data class Property(
    val id: String,
    val title: String,
    val description: String,
    val price: Double,
    val imageUrl: String,
    val location: String,
    val category: String,
    val bedrooms: Int,
    val bathrooms: Int,
    val area: Int,
    val isFavorite: Boolean = false
)
