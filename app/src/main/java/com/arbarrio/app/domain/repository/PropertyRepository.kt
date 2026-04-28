package com.arbarrio.app.domain.repository

import com.arbarrio.app.domain.model.Property
import kotlinx.coroutines.flow.Flow

interface PropertyRepository {
    fun getProperties(): Flow<List<Property>>
    suspend fun getPropertyById(id: String): Property?
    suspend fun toggleFavorite(propertyId: String)
}
