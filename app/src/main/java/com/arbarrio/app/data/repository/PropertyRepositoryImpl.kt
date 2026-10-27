package com.arbarrio.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.dataObjects
import com.google.firebase.firestore.snapshots
import com.arbarrio.app.domain.model.Property
import com.arbarrio.app.domain.repository.PropertyRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await

class PropertyRepositoryImpl(
    private val firebaseAuth: com.google.firebase.auth.FirebaseAuth,
    private val firestore: FirebaseFirestore
) : PropertyRepository {

    private val propertiesCollection = firestore.collection("properties")
    private val favoritesCollection = firestore.collection("favorites")

    override fun getProperties(): Flow<List<Property>> {
        val userId = firebaseAuth.currentUser?.uid
        if (userId == null) return propertiesCollection.dataObjects<Property>()

        val userFavoritesFlow = favoritesCollection.document(userId)
            .collection("propertyIds")
            .snapshots()
            .map { snapshot -> snapshot.documents.map { it.id }.toSet() }

        return propertiesCollection.dataObjects<Property>().combine(userFavoritesFlow) { allProps, favIds ->
            allProps.map { it.copy(isFavorite = favIds.contains(it.id)) }
        }
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
        val userId = firebaseAuth.currentUser?.uid ?: return
        val favDocRef = favoritesCollection.document(userId).collection("propertyIds").document(propertyId)
        
        try {
            val doc = favDocRef.get().await()
            if (doc.exists()) {
                favDocRef.delete().await()
            } else {
                favDocRef.set(mapOf("exists" to true)).await()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override suspend fun deleteProperty(propertyId: String): Result<Unit> {
        return try {
            propertiesCollection.document(propertyId).delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
