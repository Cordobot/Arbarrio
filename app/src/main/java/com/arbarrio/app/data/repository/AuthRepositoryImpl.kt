package com.arbarrio.app.data.repository

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.arbarrio.app.domain.model.User
import com.arbarrio.app.domain.repository.AuthRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.ExperimentalCoroutinesApi

@OptIn(ExperimentalCoroutinesApi::class)
class AuthRepositoryImpl(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
) : AuthRepository {

    override val currentUser: Flow<User?> = callbackFlow {
        val listener = FirebaseAuth.AuthStateListener { auth ->
            trySend(auth.currentUser?.uid)
        }
        firebaseAuth.addAuthStateListener(listener)
        awaitClose { firebaseAuth.removeAuthStateListener(listener) }
    }.flatMapLatest { uid ->
        if (uid == null) flowOf(null)
        else callbackFlow {
            val docRef = firestore.collection("users").document(uid)
            val registration = docRef.addSnapshotListener { snapshot, _ ->
                if (snapshot != null && snapshot.exists()) {
                    val user = User(
                        id = uid,
                        email = snapshot.getString("email") ?: "",
                        name = snapshot.getString("name"),
                        lastName = snapshot.getString("lastName"),
                        phone = snapshot.getString("phone"),
                        idNumber = snapshot.getString("idNumber"),
                        age = snapshot.getLong("age")?.toInt(),
                        rating = snapshot.getDouble("rating")?.toFloat() ?: 0.0f,
                        reviewCount = snapshot.getLong("reviewCount")?.toInt() ?: 0
                    )
                    trySend(user)
                } else {
                    // If doc doesn't exist, use basic info from Auth
                    val firebaseUser = firebaseAuth.currentUser
                    trySend(firebaseUser?.let { 
                        User(id = it.uid, email = it.email ?: "", name = it.displayName) 
                    })
                }
            }
            awaitClose { registration.remove() }
        }
    }

    override suspend fun signInWithEmail(email: String, password: String): Result<User> {
        return try {
            val trimmedEmail = email.trim()
            val result = firebaseAuth.signInWithEmailAndPassword(trimmedEmail, password).await()
            val firebaseUser = result.user ?: throw Exception("User null")
            Result.success(User(id = firebaseUser.uid, email = firebaseUser.email ?: ""))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signUpWithEmail(
        email: String, 
        password: String, 
        name: String, 
        lastName: String, 
        phone: String, 
        idNumber: String
    ): Result<User> {
        return try {
            val trimmedEmail = email.trim()
            val result = firebaseAuth.createUserWithEmailAndPassword(trimmedEmail, password).await()
            val firebaseUser = result.user ?: throw Exception("User null")
            
            val user = User(
                id = firebaseUser.uid,
                email = email,
                name = name,
                lastName = lastName,
                phone = phone,
                idNumber = idNumber
            )
            
            firestore.collection("users").document(firebaseUser.uid).set(user).await()
            
            val profileUpdates = com.google.firebase.auth.userProfileChangeRequest {
                displayName = "$name $lastName"
            }
            firebaseUser.updateProfile(profileUpdates).await()
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signOut() {
        firebaseAuth.signOut()
    }

    override suspend fun updateProfile(
        name: String, 
        lastName: String, 
        phone: String, 
        idNumber: String, 
        age: Int?
    ): Result<Unit> {
        return try {
            val firebaseUser = firebaseAuth.currentUser ?: throw Exception("Usuario no autenticado")
            
            val updates = mapOf(
                "name" to name,
                "lastName" to lastName,
                "phone" to phone,
                "idNumber" to idNumber,
                "age" to age
            )
            
            // Use set with merge to create document if it doesn't exist
            firestore.collection("users").document(firebaseUser.uid)
                .set(updates, SetOptions.merge()).await()
            
            val profileUpdates = com.google.firebase.auth.userProfileChangeRequest {
                displayName = "$name $lastName"
            }
            firebaseUser.updateProfile(profileUpdates).await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            val trimmedEmail = email.trim()
            firebaseAuth.sendPasswordResetEmail(trimmedEmail).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<User> {
        return try {
            val credential = GoogleAuthProvider.getCredential(idToken, null)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = result.user ?: throw Exception("User null")
            
            val user = User(
                id = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                name = firebaseUser.displayName?.split(" ")?.getOrNull(0) ?: "",
                lastName = firebaseUser.displayName?.split(" ")?.getOrNull(1) ?: ""
            )
            
            // If it's a new user, create document in Firestore
            if (result.additionalUserInfo?.isNewUser == true) {
                firestore.collection("users").document(firebaseUser.uid).set(user).await()
            }
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    override suspend fun signInWithFacebook(accessToken: String): Result<User> {
        return try {
            val credential = FacebookAuthProvider.getCredential(accessToken)
            val result = firebaseAuth.signInWithCredential(credential).await()
            val firebaseUser = result.user ?: throw Exception("User null")
            
            val user = User(
                id = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                name = firebaseUser.displayName?.split(" ")?.getOrNull(0) ?: "",
                lastName = firebaseUser.displayName?.split(" ")?.getOrNull(1) ?: ""
            )
            
            if (result.additionalUserInfo?.isNewUser == true) {
                firestore.collection("users").document(firebaseUser.uid).set(user).await()
            }
            
            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
