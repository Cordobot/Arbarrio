package com.arbarrio.app.domain.model

import java.io.Serializable

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
    val isFavorite: Boolean = false,
    val latitude: Double = 6.2442, // Default to Medellin/Cordoba approximate
    val longitude: Double = -75.5812
) : Serializable
