package com.epic.documentmanager.repositories

import com.google.firebase.firestore.Query
import com.epic.documentmanager.models.*
import com.epic.documentmanager.utils.Constants
import com.epic.documentmanager.utils.DateUtils
import com.epic.documentmanager.utils.FirebaseUtils
import kotlinx.coroutines.tasks.await

class DocumentRepository {

    // Pembelian Rumah
    suspend fun savePembelianRumah(data: PembelianRumah): Result<String> {
        return try {
            val docRef = if (data.id.isEmpty()) {
                FirebaseUtils.firestore.collection(Constants.PEMBELIAN_RUMAH_COLLECTION).document()
            } else {
                FirebaseUtils.firestore.collection(Constants.PEMBELIAN_RUMAH_COLLECTION).document(data.id)
            }

            val dataToSave = data.copy(
                id = docRef.id,
                updatedAt = System.currentTimeMillis(),
                updatedBy = FirebaseUtils.getCurrentUserId() ?: ""
            )

            docRef.set(dataToSave).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Renovasi Rumah
    suspend fun saveRenovasiRumah(data: RenovasiRumah): Result<String> {
        return try {
            val docRef = if (data.id.isEmpty()) {
                FirebaseUtils.firestore.collection(Constants.RENOVASI_RUMAH_COLLECTION).document()
            } else {
                FirebaseUtils.firestore.collection(Constants.RENOVASI_RUMAH_COLLECTION).document(data.id)
            }

            val dataToSave = data.copy(
                id = docRef.id,
                updatedAt = System.currentTimeMillis(),
                updatedBy = FirebaseUtils.getCurrentUserId() ?: ""
            )

            docRef.set(dataToSave).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Pemasangan AC
    suspend fun savePemasanganAC(data: PemasanganAC): Result<String> {
        return try {
            val docRef = if (data.id.isEmpty()) {
                FirebaseUtils.firestore.collection(Constants.PEMASANGAN_AC_COLLECTION).document()
            } else {
                FirebaseUtils.firestore.collection(Constants.PEMASANGAN_AC_COLLECTION).document(data.id)
            }

            val dataToSave = data.copy(
                id = docRef.id,
                updatedAt = System.currentTimeMillis(),
                updatedBy = FirebaseUtils.getCurrentUserId() ?: ""
            )

            docRef.set(dataToSave).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Pemasangan CCTV
    suspend fun savePemasanganCCTV(data: PemasanganCCTV): Result<String> {
        return try {
            val docRef = if (data.id.isEmpty()) {
                FirebaseUtils.firestore.collection(Constants.PEMASANGAN_CCTV_COLLECTION).document()
            } else {
                FirebaseUtils.firestore.collection(Constants.PEMASANGAN_CCTV_COLLECTION).document(data.id)
            }

            val dataToSave = data.copy(
                id = docRef.id,
                updatedAt = System.currentTimeMillis(),
                updatedBy = FirebaseUtils.getCurrentUserId() ?: ""
            )

            docRef.set(dataToSave).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Get Documents
    suspend fun getAllPembelianRumah(): List<PembelianRumah> {
        return try {
            val querySnapshot = FirebaseUtils.firestore.collection(Constants.PEMBELIAN_RUMAH_COLLECTION)
                .whereEqualTo("status", "active")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            querySnapshot.documents.mapNotNull { it.toObject(PembelianRumah::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getAllRenovasiRumah(): List<RenovasiRumah> {
        return try {
            val querySnapshot = FirebaseUtils.firestore.collection(Constants.RENOVASI_RUMAH_COLLECTION)
                .whereEqualTo("status", "active")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            querySnapshot.documents.mapNotNull { it.toObject(RenovasiRumah::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getAllPemasanganAC(): List<PemasanganAC> {
        return try {
            val querySnapshot = FirebaseUtils.firestore.collection(Constants.PEMASANGAN_AC_COLLECTION)
                .whereEqualTo("status", "active")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            querySnapshot.documents.mapNotNull { it.toObject(PemasanganAC::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    suspend fun getAllPemasanganCCTV(): List<PemasanganCCTV> {
        return try {
            val querySnapshot = FirebaseUtils.firestore.collection(Constants.PEMASANGAN_CCTV_COLLECTION)
                .whereEqualTo("status", "active")
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .await()

            querySnapshot.documents.mapNotNull { it.toObject(PemasanganCCTV::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }

    // Delete Documents
    suspend fun deletePembelianRumah(id: String): Result<Unit> {
        return try {
            FirebaseUtils.firestore.collection(Constants.PEMBELIAN_RUMAH_COLLECTION)
                .document(id)
                .update("status", "deleted", "updatedAt", System.currentTimeMillis())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deleteRenovasiRumah(id: String): Result<Unit> {
        return try {
            FirebaseUtils.firestore.collection(Constants.RENOVASI_RUMAH_COLLECTION)
                .document(id)
                .update("status", "deleted", "updatedAt", System.currentTimeMillis())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deletePemasanganAC(id: String): Result<Unit> {
        return try {
            FirebaseUtils.firestore.collection(Constants.PEMASANGAN_AC_COLLECTION)
                .document(id)
                .update("status", "deleted", "updatedAt", System.currentTimeMillis())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun deletePemasanganCCTV(id: String): Result<Unit> {
        return try {
            FirebaseUtils.firestore.collection(Constants.PEMASANGAN_CCTV_COLLECTION)
                .document(id)
                .update("status", "deleted", "updatedAt", System.currentTimeMillis())
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    // Search Documents
    suspend fun searchDocuments(query: String): SearchResult {
        val pembelianRumah = mutableListOf<PembelianRumah>()
        val renovasiRumah = mutableListOf<RenovasiRumah>()
        val pemasanganAC = mutableListOf<PemasanganAC>()
        val pemasanganCCTV = mutableListOf<PemasanganCCTV>()

        try {
            // Search Pembelian Rumah
            val prQuery = FirebaseUtils.firestore.collection(Constants.PEMBELIAN_RUMAH_COLLECTION)
                .whereEqualTo("status", "active")
                .get()
                .await()

            prQuery.documents.forEach { doc ->
                val data = doc.toObject(PembelianRumah::class.java)
                if (data != null && (
                            data.nama.contains(query, ignoreCase = true) ||
                                    data.uniqueCode.contains(query, ignoreCase = true)
                            )) {
                    pembelianRumah.add(data)
                }
            }

            // Search other collections similarly...
            // (Implementation for other collections would be similar)

        } catch (e: Exception) {
            // Handle error
        }

        return SearchResult(pembelianRumah, renovasiRumah, pemasanganAC, pemasanganCCTV)
    }

    // Get Monthly Report Data
    suspend fun getMonthlyReportData(year: Int, month: Int): MonthlyReport {
        val startTime = DateUtils.getStartOfMonth(year, month)
        val endTime = DateUtils.getEndOfMonth(year, month)

        var pembelianCount = 0
        var renovasiCount = 0
        var acCount = 0
        var cctvCount = 0

        try {
            // Count Pembelian Rumah
            val prQuery = FirebaseUtils.firestore.collection(Constants.PEMBELIAN_RUMAH_COLLECTION)
                .whereGreaterThanOrEqualTo("createdAt", startTime)
                .whereLessThanOrEqualTo("createdAt", endTime)
                .whereEqualTo("status", "active")
                .get()
                .await()
            pembelianCount = prQuery.size()

            // Count other collections similarly...

        } catch (e: Exception) {
            // Handle error
        }

        val totalCount = pembelianCount + renovasiCount + acCount + cctvCount

        return MonthlyReport(
            month = month.toString().padStart(2, '0'),
            year = year.toString(),
            totalDocuments = totalCount,
            pembelianRumahCount = pembelianCount,
            renovasiRumahCount = renovasiCount,
            pemasanganACCount = acCount,
            pemasanganCCTVCount = cctvCount,
            generatedAt = System.currentTimeMillis(),
            generatedBy = FirebaseUtils.getCurrentUserId() ?: ""
        )
    }
}

data class SearchResult(
    val pembelianRumah: List<PembelianRumah>,
    val renovasiRumah: List<RenovasiRumah>,
    val pemasanganAC: List<PemasanganAC>,
    val pemasanganCCTV: List<PemasanganCCTV>
)