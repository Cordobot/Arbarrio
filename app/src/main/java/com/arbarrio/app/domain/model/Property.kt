package com.arbarrio.app.domain.model

data class Property(
    val id: String = "",
    val title: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val imageUrl: String = "",
    val location: String = "",
    val category: String = "",
    val bedrooms: Int = 0,
    val bathrooms: Int = 0,
    val area: Int = 0,
    val ownerPhone: String? = null,
    val isFavorite: Boolean = false
)
