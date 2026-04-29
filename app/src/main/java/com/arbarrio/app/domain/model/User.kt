package com.arbarrio.app.domain.model

data class User(
    val id: String,
    val email: String,
    val name: String? = null,
    val photoUrl: String? = null,
    val rating: Float = 0.0f,
    val reviewCount: Int = 0
)
