package com.epic.documentmanager.repositories

import android.net.Uri
import com.epic.documentmanager.utils.Constants
import com.epic.documentmanager.utils.FirebaseUtils
import kotlinx.coroutines.tasks.await
import java.util.*

class StorageRepository {

    suspend fun uploadDocument(uri: Uri, documentType: String, fileName: String): Result<String> {
        return try {
            val timestamp = System.currentTimeMillis()
            val uniqueFileName = "${timestamp}_${fileName}"
            val path = "${Constants.STORAGE_DOCUMENTS}/$documentType/$uniqueFileName"

            val storageRef = FirebaseUtils.storage.reference.child(path)
            val uploadTask = storageRef.putFile(uri).await()
            val downloadUrl = uploadTask.storage.downloadUrl.await()

            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadProfileImage(uri: Uri, userId: String): Result<String> {
        return try {
            val path = "${Constants.STORAGE_PROFILE_IMAGES}/$userId.jpg"
            val storageRef = FirebaseUtils.storage.reference.child(path)
            val uploadTask = storageRef.putFile(uri).await()
            val downloadUrl = uploadTask.storage.downloadUrl.await()

            Result.success(downloadUrl.toString())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteFile(downloadUrl: String): Result<Unit> {
        return try {
            val storageRef = FirebaseUtils.storage.getReferenceFromUrl(downloadUrl)
            storageRef.delete().await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun uploadMultipleFiles(
        uris: List<Uri>,
        documentType: String,
        fileNames: List<String>
    ): Result<List<String>> {
        return try {
            val downloadUrls = mutableListOf<String>()

            uris.forEachIndexed { index, uri ->
                val fileName = fileNames.getOrNull(index) ?: "file_${index + 1}.jpg"
                val result = uploadDocument(uri, documentType, fileName)
                if (result.isSuccess) {
                    downloadUrls.add(result.getOrThrow())
                } else {
                    return Result.failure(result.exceptionOrNull() ?: Exception("Upload failed"))
                }
            }

            Result.success(downloadUrls)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
