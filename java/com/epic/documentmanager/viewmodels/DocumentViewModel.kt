package com.epic.documentmanager.viewmodels

import android.net.Uri
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epic.documentmanager.models.*
import com.epic.documentmanager.repositories.DocumentRepository
import com.epic.documentmanager.repositories.SearchResult
import com.epic.documentmanager.repositories.StorageRepository
import kotlinx.coroutines.launch

class DocumentViewModel : ViewModel() {
    private val documentRepository = DocumentRepository()
    private val storageRepository = StorageRepository()

    private val _pembelianRumahList = MutableLiveData<List<PembelianRumah>>()
    val pembelianRumahList: LiveData<List<PembelianRumah>> = _pembelianRumahList

    private val _renovasiRumahList = MutableLiveData<List<RenovasiRumah>>()
    val renovasiRumahList: LiveData<List<RenovasiRumah>> = _renovasiRumahList

    private val _pemasanganACList = MutableLiveData<List<PemasanganAC>>()
    val pemasanganACList: LiveData<List<PemasanganAC>> = _pemasanganACList

    private val _pemasanganCCTVList = MutableLiveData<List<PemasanganCCTV>>()
    val pemasanganCCTVList: LiveData<List<PemasanganCCTV>> = _pemasanganCCTVList

    private val _saveResult = MutableLiveData<Result<String>>()
    val saveResult: LiveData<Result<String>> = _saveResult

    private val _deleteResult = MutableLiveData<Result<Unit>>()
    val deleteResult: LiveData<Result<Unit>> = _deleteResult

    private val _uploadResult = MutableLiveData<Result<List<String>>>()
    val uploadResult: LiveData<Result<List<String>>> = _uploadResult

    private val _searchResult = MutableLiveData<SearchResult>()
    val searchResult: LiveData<SearchResult> = _searchResult

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    private val _uploadProgress = MutableLiveData<Int>()
    val uploadProgress: LiveData<Int> = _uploadProgress

    // Save Documents
    fun savePembelianRumah(data: PembelianRumah) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = documentRepository.savePembelianRumah(data)
                _saveResult.value = result
                if (result.isSuccess) {
                    loadAllPembelianRumah()
                }
            } catch (e: Exception) {
                _saveResult.value = Result.failure(e)
            } finally {
                _loading.value = false
            }
        }
    }

    fun saveRenovasiRumah(data: RenovasiRumah) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = documentRepository.saveRenovasiRumah(data)
                _saveResult.value = result
                if (result.isSuccess) {
                    loadAllRenovasiRumah()
                }
            } catch (e: Exception) {
                _saveResult.value = Result.failure(e)
            } finally {
                _loading.value = false
            }
        }
    }

    fun savePemasanganAC(data: PemasanganAC) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = documentRepository.savePemasanganAC(data)
                _saveResult.value = result
                if (result.isSuccess) {
                    loadAllPemasanganAC()
                }
            } catch (e: Exception) {
                _saveResult.value = Result.failure(e)
            } finally {
                _loading.value = false
            }
        }
    }

    fun savePemasanganCCTV(data: PemasanganCCTV) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = documentRepository.savePemasanganCCTV(data)
                _saveResult.value = result
                if (result.isSuccess) {
                    loadAllPemasanganCCTV()
                }
            } catch (e: Exception) {
                _saveResult.value = Result.failure(e)
            } finally {
                _loading.value = false
            }
        }
    }

    // Load Documents
    fun loadAllPembelianRumah() {
        viewModelScope.launch {
            try {
                val result = documentRepository.getAllPembelianRumah()
                _pembelianRumahList.value = result
            } catch (e: Exception) {
                _pembelianRumahList.value = emptyList()
            }
        }
    }

    fun loadAllRenovasiRumah() {
        viewModelScope.launch {
            try {
                val result = documentRepository.getAllRenovasiRumah()
                _renovasiRumahList.value = result
            } catch (e: Exception) {
                _renovasiRumahList.value = emptyList()
            }
        }
    }

    fun loadAllPemasanganAC() {
        viewModelScope.launch {
            try {
                val result = documentRepository.getAllPemasanganAC()
                _pemasanganACList.value = result
            } catch (e: Exception) {
                _pemasanganACList.value = emptyList()
            }
        }
    }

    fun loadAllPemasanganCCTV() {
        viewModelScope.launch {
            try {
                val result = documentRepository.getAllPemasanganCCTV()
                _pemasanganCCTVList.value = result
            } catch (e: Exception) {
                _pemasanganCCTVList.value = emptyList()
            }
        }
    }

    fun loadAllDocuments() {
        loadAllPembelianRumah()
        loadAllRenovasiRumah()
        loadAllPemasanganAC()
        loadAllPemasanganCCTV()
    }

    // Delete Documents
    fun deletePembelianRumah(id: String) {
        viewModelScope.launch {
            try {
                val result = documentRepository.deletePembelianRumah(id)
                _deleteResult.value = result
                if (result.isSuccess) {
                    loadAllPembelianRumah()
                }
            } catch (e: Exception) {
                _deleteResult.value = Result.failure(e)
            }
        }
    }

    fun deleteRenovasiRumah(id: String) {
        viewModelScope.launch {
            try {
                val result = documentRepository.deleteRenovasiRumah(id)
                _deleteResult.value = result
                if (result.isSuccess) {
                    loadAllRenovasiRumah()
                }
            } catch (e: Exception) {
                _deleteResult.value = Result.failure(e)
            }
        }
    }

    fun deletePemasanganAC(id: String) {
        viewModelScope.launch {
            try {
                val result = documentRepository.deletePemasanganAC(id)
                _deleteResult.value = result
                if (result.isSuccess) {
                    loadAllPemasanganAC()
                }
            } catch (e: Exception) {
                _deleteResult.value = Result.failure(e)
            }
        }
    }

    fun deletePemasanganCCTV(id: String) {
        viewModelScope.launch {
            try {
                val result = documentRepository.deletePemasanganCCTV(id)
                _deleteResult.value = result
                if (result.isSuccess) {
                    loadAllPemasanganCCTV()
                }
            } catch (e: Exception) {
                _deleteResult.value = Result.failure(e)
            }
        }
    }

    // Upload Files
    fun uploadFiles(uris: List<Uri>, documentType: String, fileNames: List<String>) {
        viewModelScope.launch {
            _loading.value = true
            _uploadProgress.value = 0
            try {
                val result = storageRepository.uploadMultipleFiles(uris, documentType, fileNames)
                _uploadResult.value = result
                _uploadProgress.value = 100
            } catch (e: Exception) {
                _uploadResult.value = Result.failure(e)
            } finally {
                _loading.value = false
            }
        }
    }

    // Search Documents
    fun searchDocuments(query: String) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val result = documentRepository.searchDocuments(query)
                _searchResult.value = result
            } catch (e: Exception) {
                _searchResult.value = SearchResult(emptyList(), emptyList(), emptyList(), emptyList())
            } finally {
                _loading.value = false
            }
        }
    }

    fun clearSearchResult() {
        _searchResult.value = SearchResult(emptyList(), emptyList(), emptyList(), emptyList())
    }
}