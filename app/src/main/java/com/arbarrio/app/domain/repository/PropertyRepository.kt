package com.arbarrio.app.domain.repository

import com.arbarrio.app.domain.model.Property
import kotlinx.coroutines.flow.Flow

interface PropertyRepository {
    fun getProperties(): Flow<List<Property>>
    suspend fun getPropertyById(id: String): Property?
    suspend fun addProperty(property: Property): Result<Unit>
    suspend fun toggleFavorite(propertyId: String)
    suspend fun deleteProperty(propertyId: String): Result<Unit>
}
