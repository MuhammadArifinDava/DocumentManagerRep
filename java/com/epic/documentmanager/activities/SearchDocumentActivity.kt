package com.epic.documentmanager.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epic.documentmanager.R
import com.epic.documentmanager.adapters.DocumentAdapter
import com.epic.documentmanager.utils.Constants
import com.epic.documentmanager.viewmodels.AuthViewModel
import com.epic.documentmanager.viewmodels.DocumentViewModel

class SearchDocumentActivity : AppCompatActivity() {

    private lateinit var documentViewModel: DocumentViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var adapter: DocumentAdapter<Any>

    // Views
    private lateinit var etSearchQuery: EditText
    private lateinit var spinnerDocumentType: Spinner
    private lateinit var btnSearch: Button
    private lateinit var btnClear: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var tvNoResults: TextView
    private lateinit var tvResultCount: TextView

    private var userRole: String = Constants.ROLE_STAFF

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search_document)

        setupActionBar()
        initViews()
        setupViewModel()
        setupSpinner()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()
    }

    private fun setupActionBar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Cari Dokumen"
        }
    }

    private fun initViews() {
        etSearchQuery = findViewById(R.id.etSearchQuery)
        spinnerDocumentType = findViewById(R.id.spinnerDocumentType)
        btnSearch = findViewById(R.id.btnSearch)
        btnClear = findViewById(R.id.btnClear)
        recyclerView = findViewById(R.id.recyclerView)
        progressBar = findViewById(R.id.progressBar)
        tvNoResults = findViewById(R.id.tvNoResults)
        tvResultCount = findViewById(R.id.tvResultCount)
    }

    private fun setupViewModel() {
        documentViewModel = ViewModelProvider(this)[DocumentViewModel::class.java]
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        authViewModel.getCurrentUser()
    }

    private fun setupSpinner() {
        val documentTypes = arrayOf(
            "Semua Jenis",
            "Pembelian Rumah",
            "Renovasi Rumah",
            "Pemasangan AC",
            "Pemasangan CCTV"
        )

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            documentTypes
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerDocumentType.adapter = adapter
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
                // Handle delete - not implemented in search
                Toast.makeText(this, "Hapus dokumen tidak tersedia dari pencarian", Toast.LENGTH_SHORT).show()
            },
            canDelete = false // Disable delete from search results
        )

        recyclerView.apply {
            layoutManager = LinearLayoutManager(this@SearchDocumentActivity)
            adapter = this@SearchDocumentActivity.adapter
        }
    }

    private fun setupObservers() {
        authViewModel.currentUser.observe(this) { user ->
            user?.let {
                userRole = it.role
            }
        }

        documentViewModel.searchResults.observe(this) { results ->
            progressBar.visibility = View.GONE

            if (results.isEmpty()) {
                tvNoResults.visibility = View.VISIBLE
                tvResultCount.visibility = View.GONE
                recyclerView.visibility = View.GONE
            } else {
                tvNoResults.visibility = View.GONE
                tvResultCount.visibility = View.VISIBLE
                recyclerView.visibility = View.VISIBLE
                tvResultCount.text = "Ditemukan ${results.size} dokumen"
                adapter.updateDocuments(results)
            }
        }

        documentViewModel.loading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
        }
    }

    private fun setupClickListeners() {
        btnSearch.setOnClickListener {
            performSearch()
        }

        btnClear.setOnClickListener {
            clearSearch()
        }

        etSearchQuery.setOnEditorActionListener { _, _, _ ->
            performSearch()
            true
        }
    }

    private fun performSearch() {
        val query = etSearchQuery.text.toString().trim()
        val selectedTypeIndex = spinnerDocumentType.selectedItemPosition

        if (query.isEmpty()) {
            Toast.makeText(this, "Masukkan kata kunci pencarian", Toast.LENGTH_SHORT).show()
            return
        }

        val documentType = when (selectedTypeIndex) {
            1 -> Constants.DOC_TYPE_PEMBELIAN_RUMAH
            2 -> Constants.DOC_TYPE_RENOVASI_RUMAH
            3 -> Constants.DOC_TYPE_PEMASANGAN_AC
            4 -> Constants.DOC_TYPE_PEMASANGAN_CCTV
            else -> null // All types
        }

        documentViewModel.searchDocuments(query, documentType)
    }

    private fun clearSearch() {
        etSearchQuery.text.clear()
        spinnerDocumentType.setSelection(0)
        tvNoResults.visibility = View.GONE
        tvResultCount.visibility = View.GONE
        recyclerView.visibility = View.GONE
        adapter.updateDocuments(emptyList())
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

    private fun getDocumentType(document: Any): String {
        return when (document::class.java.simpleName) {
            "PembelianRumah" -> Constants.DOC_TYPE_PEMBELIAN_RUMAH
            "RenovasiRumah" -> Constants.DOC_TYPE_RENOVASI_RUMAH
            "PemasanganAC" -> Constants.DOC_TYPE_PEMASANGAN_AC
            "PemasanganCCTV" -> Constants.DOC_TYPE_PEMASANGAN_CCTV
            else -> Constants.DOC_TYPE_PEMBELIAN_RUMAH
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}