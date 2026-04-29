package com.arbarrio.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.dataObjects
import com.arbarrio.app.domain.model.Property
import com.arbarrio.app.domain.repository.PropertyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class PropertyRepositoryImpl(
    private val firestore: FirebaseFirestore
) : PropertyRepository {

    private val propertiesCollection = firestore.collection("properties")

    override fun getProperties(): Flow<List<Property>> {
        // We use dataObjects() extension from firebase-firestore-ktx
        return propertiesCollection.dataObjects<Property>()
    }

    override suspend fun getPropertyById(id: String): Property? {
        return try {
            propertiesCollection.document(id).get().await().toObject(Property::class.java)
        } catch (e: Exception) {
            null
        }
    }

    override suspend fun addProperty(property: Property): Result<Unit> {
        return try {
            propertiesCollection.document(property.id).set(property).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun toggleFavorite(propertyId: String) {
        // Logic for local/remote favorite toggle
    }
}
