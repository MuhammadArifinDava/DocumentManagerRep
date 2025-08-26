package com.epic.documentmanager.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.epic.documentmanager.R
import com.epic.documentmanager.adapters.DocumentAdapter
import com.epic.documentmanager.utils.Constants
import com.epic.documentmanager.viewmodels.AuthViewModel
import com.epic.documentmanager.viewmodels.DocumentViewModel

class DocumentListActivity : AppCompatActivity() {

    private lateinit var documentViewModel: DocumentViewModel
    private lateinit var authViewModel: AuthViewModel

    private lateinit var recyclerView: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar

    private lateinit var adapter: DocumentAdapter<Any>
    private var documentType: String? = null
    private var userRole: String = Constants.ROLE_STAFF

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_list)

        documentType = intent.getStringExtra("documentType")

        setupActionBar()
        initViews()
        setupViewModel()
        setupRecyclerView()
        setupObservers()
        setupSwipeRefresh()

        loadDocuments()
    }

    private fun setupActionBar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = when (documentType) {
                Constants.DOC_TYPE_PEMBELIAN_RUMAH -> "Dokumen Pembelian Rumah"
                Constants.DOC_TYPE_RENOVASI_RUMAH -> "Dokumen Renovasi Rumah"
                Constants.DOC_TYPE_PEMASANGAN_AC -> "Dokumen Pemasangan AC"
                Constants.DOC_TYPE_PEMASANGAN_CCTV -> "Dokumen Pemasangan CCTV"
                else -> "Semua Dokumen"
            }
        }
    }

    private fun initViews() {
        recyclerView = findViewById(R.id.recyclerView)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        progressBar = findViewById(R.id.progressBar)
    }

    private fun setupViewModel() {
        documentViewModel = ViewModelProvider(this)[DocumentViewModel::class.java]
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        // Get current user role
        authViewModel.getCurrentUser()
    }

    private fun setupRecyclerView() {
        adapter = DocumentAdapter(
            documents = emptyList(),
            onItemClick = { document ->
                openDocumentDetail(document)
            },
            onEditClick = { document ->
                editDocument(document)
            },
            onDeleteClick = { document ->
                showDeleteConfirmation(document)
            },
            canDelete = userRole == Constants.ROLE_ADMIN
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@DocumentListActivity)
            adapter = this@DocumentListActivity.adapter
        }
    }

    private fun setupObservers() {
        authViewModel.currentUser.observe(this) { user ->
            user?.let {
                userRole = it.role
                adapter = DocumentAdapter(
                    documents = adapter.documents,
                    onItemClick = { document -> openDocumentDetail(document) },
                    onEditClick = { document -> editDocument(document) },
                    onDeleteClick = { document -> showDeleteConfirmation(document) },
                    canDelete = userRole == Constants.ROLE_ADMIN
                )
                recyclerView.adapter = adapter
            }
        }

        documentViewModel.pembelianRumahList.observe(this) { documents ->
            if (documentType == Constants.DOC_TYPE_PEMBELIAN_RUMAH || documentType == null) {
                updateDocumentList(documents)
            }
        }

        documentViewModel.renovasiRumahList.observe(this) { documents ->
            if (documentType == Constants.DOC_TYPE_RENOVASI_RUMAH || documentType == null) {
                updateDocumentList(documents)
            }
        }

        documentViewModel.pemasanganACList.observe(this) { documents ->
            if (documentType == Constants.DOC_TYPE_PEMASANGAN_AC || documentType == null) {
                updateDocumentList(documents)
            }
        }

        documentViewModel.pemasanganCCTVList.observe(this) { documents ->
            if (documentType == Constants.DOC_TYPE_PEMASANGAN_CCTV || documentType == null) {
                updateDocumentList(documents)
            }
        }

        documentViewModel.deleteResult.observe(this) { result ->
            if (result.isSuccess) {
                Toast.makeText(this, "Dokumen berhasil dihapus", Toast.LENGTH_SHORT).show()
                loadDocuments()
            } else {
                Toast.makeText(this, "Gagal menghapus dokumen: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
            }
        }

        documentViewModel.loading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            swipeRefresh.isRefreshing = isLoading
        }
    }

    private fun setupSwipeRefresh() {
        swipeRefresh.setOnRefreshListener {
            loadDocuments()
        }
    }

    private fun loadDocuments() {
        when (documentType) {
            Constants.DOC_TYPE_PEMBELIAN_RUMAH -> documentViewModel.loadAllPembelianRumah()
            Constants.DOC_TYPE_RENOVASI_RUMAH -> documentViewModel.loadAllRenovasiRumah()
            Constants.DOC_TYPE_PEMASANGAN_AC -> documentViewModel.loadAllPemasanganAC()
            Constants.DOC_TYPE_PEMASANGAN_CCTV -> documentViewModel.loadAllPemasanganCCTV()
            else -> documentViewModel.loadAllDocuments()
        }
    }

    private fun updateDocumentList(documents: List<Any>) {
        if (documentType == null) {
            // Combine all documents if showing all types
            val allDocuments = mutableListOf<Any>()
            allDocuments.addAll(documentViewModel.pembelianRumahList.value ?: emptyList())
            allDocuments.addAll(documentViewModel.renovasiRumahList.value ?: emptyList())
            allDocuments.addAll(documentViewModel.pemasanganACList.value ?: emptyList())
            allDocuments.addAll(documentViewModel.pemasanganCCTVList.value ?: emptyList())
            adapter.updateDocuments(allDocuments)
        } else {
            adapter.updateDocuments(documents)
        }
    }

    private fun openDocumentDetail(document: Any) {
        val intent = Intent(this, DocumentDetailActivity::class.java)
        intent.putExtra("document", document as java.io.Serializable)
        intent.putExtra("documentType", getDocumentType(document))
        startActivity(intent)
    }

    private fun editDocument(document: Any) {
        val intent = Intent(this, EditDocumentActivity::class.java)
        intent.putExtra("document", document as java.io.Serializable)
        intent.putExtra("documentType", getDocumentType(document))
        startActivity(intent)
    }

    private fun showDeleteConfirmation(document: Any) {
        if (userRole != Constants.ROLE_ADMIN) {
            Toast.makeText(this, "Anda tidak memiliki izin untuk menghapus dokumen", Toast.LENGTH_SHORT).show()
            return
        }

        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Hapus")
            .setMessage("Apakah Anda yakin ingin menghapus dokumen ini?")
            .setPositiveButton("Hapus") { _, _ ->
                deleteDocument(document)
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteDocument(document: Any) {
        try {
            val idField = document::class.java.getDeclaredField("id")
            idField.isAccessible = true
            val id = idField.get(document) as String

            when (getDocumentType(document)) {
                Constants.DOC_TYPE_PEMBELIAN_RUMAH -> documentViewModel.deletePembelianRumah(id)
                Constants.DOC_TYPE_RENOVASI_RUMAH -> documentViewModel.deleteRenovasiRumah(id)
                Constants.DOC_TYPE_PEMASANGAN_AC -> documentViewModel.deletePemasanganAC(id)
                Constants.DOC_TYPE_PEMASANGAN_CCTV -> documentViewModel.deletePemasanganCCTV(id)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Gagal menghapus dokumen", Toast.LENGTH_SHORT).show()
        }
    }

    private fun getDocumentType(document: Any): String {
        return when (document::class.java.simpleName) {
            "PembelianRumah" -> Constants.DOC_TYPE_PEMBELIAN_RUMAH
            "RenovasiRumah" -> Constants.DOC_TYPE_RENOVASI_RUMAH
            "PemasanganAC" -> Constants.DOC_TYPE_PEMASANGAN_AC
            "PemasanganCCTV" -> Constants.DOC_TYPE_PEMASANGAN_CCTV
            else -> Constants.DOC_TYPE_PEMBELIAN_RUMAH
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_search -> {
                startActivity(Intent(this, SearchDocumentActivity::class.java))
                true
            }
            R.id.action_add -> {
                startActivity(Intent(this, UploadDocumentActivity::class.java))
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    override fun onResume() {
        super.onResume()
        loadDocuments()
    }
}