package com.epic.documentmanager.repositories

import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.epic.documentmanager.models.User
import com.epic.documentmanager.utils.Constants
import com.epic.documentmanager.utils.FirebaseUtils
import kotlinx.coroutines.tasks.await

class AuthRepository {

    suspend fun loginUser(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = FirebaseUtils.auth.signInWithEmailAndPassword(email, password).await()
            Result.success(result.user!!)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun registerUser(email: String, password: String, fullName: String, role: String): Result<FirebaseUser> {
        return try {
            val result = FirebaseUtils.auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user!!

            // Create user profile in Firestore
            val userProfile = User(
                uid = user.uid,
                email = email,
                fullName = fullName,
                role = role,
                createdAt = System.currentTimeMillis(),
                updatedAt = System.currentTimeMillis(),
                isActive = true
            )

            FirebaseUtils.firestore.collection(Constants.USERS_COLLECTION)
                .document(user.uid)
                .set(userProfile)
                .await()

            Result.success(user)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getCurrentUser(): User? {
        return try {
            val currentUser = FirebaseUtils.auth.currentUser
            if (currentUser != null) {
                val doc = FirebaseUtils.firestore.collection(Constants.USERS_COLLECTION)
                    .document(currentUser.uid)
                    .get()
                    .await()
                doc.toObject(User::class.java)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updatePassword(newPassword: String): Result<Unit> {
        return try {
            FirebaseUtils.auth.currentUser?.updatePassword(newPassword)?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun logout() {
        FirebaseUtils.auth.signOut()
    }

    fun isUserLoggedIn(): Boolean {
        return FirebaseUtils.auth.currentUser != null
    }
}