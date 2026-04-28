package com.arbarrio.app.domain.model

data class User(
    val id: String,
    val email: String,
    val name: String? = null,
    val photoUrl: String? = null
)
