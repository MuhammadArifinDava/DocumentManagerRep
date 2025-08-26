package com.epic.documentmanager.utils

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import java.util.*

object FirebaseUtils {

    val auth: FirebaseAuth = FirebaseAuth.getInstance()
    val firestore: FirebaseFirestore = FirebaseFirestore.getInstance()
    val storage: FirebaseStorage = FirebaseStorage.getInstance()

    // Auth helpers
    fun getCurrentUserId(): String? {
        return auth.currentUser?.uid
    }

    fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }

    fun isUserLoggedIn(): Boolean {
        return auth.currentUser != null
    }

    // Firestore helpers
    fun getUsersCollection() = firestore.collection(Constants.USERS_COLLECTION)

    fun getPembelianRumahCollection() = firestore.collection(Constants.PEMBELIAN_RUMAH_COLLECTION)

    fun getRenovasiRumahCollection() = firestore.collection(Constants.RENOVASI_RUMAH_COLLECTION)

    fun getPemasanganACCollection() = firestore.collection(Constants.PEMASANGAN_AC_COLLECTION)

    fun getPemasanganCCTVCollection() = firestore.collection(Constants.PEMASANGAN_CCTV_COLLECTION)

    fun getDocumentCollection(type: String) = when (type) {
        Constants.DOC_TYPE_PEMBELIAN_RUMAH -> getPembelianRumahCollection()
        Constants.DOC_TYPE_RENOVASI_RUMAH -> getRenovasiRumahCollection()
        Constants.DOC_TYPE_PEMASANGAN_AC -> getPemasanganACCollection()
        Constants.DOC_TYPE_PEMASANGAN_CCTV -> getPemasanganCCTVCollection()
        else -> firestore.collection("documents")
    }

    // Storage helpers
    fun getDocumentsStorageRef(): StorageReference {
        return storage.reference.child(Constants.STORAGE_DOCUMENTS)
    }

    fun getProfileImagesStorageRef(): StorageReference {
        return storage.reference.child(Constants.STORAGE_PROFILE_IMAGES)
    }

    fun getDocumentStorageRef(documentType: String): StorageReference {
        return getDocumentsStorageRef().child(documentType)
    }

    fun getFileStorageRef(documentType: String, fileName: String): StorageReference {
        return getDocumentStorageRef(documentType).child(fileName)
    }

    // Utility functions
    fun generateDocumentId(): String {
        return firestore.collection("temp").document().id
    }

    fun getTimestamp(): Long {
        return System.currentTimeMillis()
    }

    fun createMap(vararg pairs: Pair<String, Any?>): Map<String, Any> {
        return mapOf(*pairs).filterValues { it != null } as Map<String, Any>
    }

    // File upload helpers
    fun generateFileName(originalName: String, documentType: String): String {
        val timestamp = System.currentTimeMillis()
        val extension = originalName.substringAfterLast(".", "")
        return "${documentType}_${timestamp}${if (extension.isNotEmpty()) ".$extension" else ""}"
    }

    fun isValidImageFile(fileName: String): Boolean {
        val validExtensions = listOf("jpg", "jpeg", "png", "gif", "webp")
        val extension = fileName.substringAfterLast(".", "").lowercase(Locale.getDefault())
        return extension in validExtensions
    }

    fun isValidDocumentFile(fileName: String): Boolean {
        val validExtensions = listOf("pdf", "doc", "docx", "txt")
        val extension = fileName.substringAfterLast(".", "").lowercase(Locale.getDefault())
        return extension in validExtensions
    }

    // Search helpers
    fun createSearchQuery(field: String, query: String): String {
        return query.lowercase(Locale.getDefault())
    }

    // Date range helpers
    fun getStartOfDay(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        return calendar.timeInMillis
    }

    fun getEndOfDay(timestamp: Long): Long {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        return calendar.timeInMillis
    }
}