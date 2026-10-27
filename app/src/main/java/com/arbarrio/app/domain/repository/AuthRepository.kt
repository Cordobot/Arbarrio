package com.arbarrio.app.domain.repository

import com.arbarrio.app.domain.model.User
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<User?>
    suspend fun signInWithEmail(email: String, password: String): Result<User>
    suspend fun signUpWithEmail(
        email: String, 
        password: String, 
        name: String, 
        lastName: String, 
        phone: String, 
        idNumber: String
    ): Result<User>
    suspend fun signOut()
    suspend fun updateProfile(
        name: String, 
        lastName: String, 
        phone: String, 
        idNumber: String, 
        age: Int?
    ): Result<Unit>
    suspend fun sendPasswordResetEmail(email: String): Result<Unit>
    suspend fun signInWithGoogle(idToken: String): Result<User>
    suspend fun signInWithFacebook(accessToken: String): Result<User>
}
