package com.epic.documentmanager.repositories

import com.epic.documentmanager.models.User
import com.epic.documentmanager.utils.Constants
import com.epic.documentmanager.utils.FirebaseUtils
import kotlinx.coroutines.tasks.await

class UserRepository {

    suspend fun getUserById(userId: String): User? {
        return try {
            val doc = FirebaseUtils.firestore.collection(Constants.USERS_COLLECTION)
                .document(userId)
                .get()
                .await()
            doc.toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun updateUser(user: User): Result<Unit> {
        return try {
            FirebaseUtils.firestore.collection(Constants.USERS_COLLECTION)
                .document(user.uid)
                .set(user.copy(updatedAt = System.currentTimeMillis()))
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getAllStaff(): List<User> {
        return try {
            val querySnapshot = FirebaseUtils.firestore.collection(Constants.USERS_COLLECTION)
                .whereIn("role", listOf(Constants.ROLE_STAFF, Constants.ROLE_MANAGER))
                .whereEqualTo("isActive", true)
                .get()
                .await()

            querySnapshot.documents.mapNotNull { it.toObject(User::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun deactivateUser(userId: String): Result<Unit> {
        return try {
            FirebaseUtils.firestore.collection(Constants.USERS_COLLECTION)
                .document(userId)
                .update("isActive", false, "updatedAt", System.currentTimeMillis())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun getUserRole(userId: String): String? {
        return try {
            val doc = FirebaseUtils.firestore.collection(Constants.USERS_COLLECTION)
                .document(userId)
                .get()
                .await()
            doc.getString("role")
        } catch (e: Exception) {
            null
        }
    }
}
