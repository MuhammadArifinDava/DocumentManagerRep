package com.epic.documentmanager.activities

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.epic.documentmanager.R
import com.epic.documentmanager.adapters.DocumentImage
import com.epic.documentmanager.adapters.DocumentImageAdapter
import com.epic.documentmanager.models.*
import com.epic.documentmanager.utils.Constants
import com.epic.documentmanager.utils.DateUtils
import com.epic.documentmanager.utils.PDFGenerator
import com.epic.documentmanager.viewmodels.AuthViewModel
import com.epic.documentmanager.viewmodels.DocumentViewModel
import android.widget.TextView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class DocumentDetailActivity : AppCompatActivity() {

    private lateinit var documentViewModel: DocumentViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var imageAdapter: DocumentImageAdapter

    private var document: Any? = null
    private var documentType: String = Constants.DOC_TYPE_PEMBELIAN_RUMAH
    private var userRole: String = Constants.ROLE_STAFF

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_document_detail)

        document = intent.getSerializableExtra("document")
        documentType = intent.getStringExtra("documentType") ?: Constants.DOC_TYPE_PEMBELIAN_RUMAH

        setupActionBar()
        setupViewModel()
        setupImageRecyclerView()
        displayDocumentDetails()
    }

    private fun setupActionBar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Detail Dokumen"
        }
    }

    private fun setupViewModel() {
        documentViewModel = ViewModelProvider(this)[DocumentViewModel::class.java]
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        authViewModel.getCurrentUser()
        authViewModel.currentUser.observe(this) { user ->
            user?.let {
                userRole = it.role
                invalidateOptionsMenu()
            }
        }
    }

    private fun setupImageRecyclerView() {
        val recyclerViewImages: RecyclerView = findViewById(R.id.recyclerViewImages)
        imageAdapter = DocumentImageAdapter(
            images = emptyList(),
            onImageClick = { image, position ->
                showFullScreenImage(image)
            },
            onDeleteClick = { image, position -> },
            canDelete = false
        )

        recyclerViewImages.apply {
            layoutManager = GridLayoutManager(this@DocumentDetailActivity, 2)
            adapter = imageAdapter
        }
    }

    private fun displayDocumentDetails() {
        document?.let { doc ->
            try {
                when (documentType) {
                    Constants.DOC_TYPE_PEMBELIAN_RUMAH -> displayPembelianRumah(doc as PembelianRumah)
                    Constants.DOC_TYPE_RENOVASI_RUMAH -> displayRenovasiRumah(doc as RenovasiRumah)
                    Constants.DOC_TYPE_PEMASANGAN_AC -> displayPemasanganAC(doc as PemasanganAC)
                    Constants.DOC_TYPE_PEMASANGAN_CCTV -> displayPemasanganCCTV(doc as PemasanganCCTV)
                }
            } catch (e: Exception) {
                Toast.makeText(this, "Error displaying document details", Toast.LENGTH_SHORT).show()
                finish()
            }
        }
    }

    private fun displayPembelianRumah(data: PembelianRumah) {
        findViewById<TextView>(R.id.tvUniqueCode).text = data.uniqueCode
        findViewById<TextView>(R.id.tvNama).text = data.nama
        findViewById<TextView>(R.id.tvAlamatKTP).text = data.alamatKTP
        findViewById<TextView>(R.id.tvNIK).text = data.nik
        findViewById<TextView>(R.id.tvNPWP).text = data.npwp
        findViewById<TextView>(R.id.tvNoTelepon).text = data.noTelepon
        findViewById<TextView>(R.id.tvStatusPernikahan).text = data.statusPernikahan
        findViewById<TextView>(R.id.tvNamaPasangan).text = data.namaPasangan
        findViewById<TextView>(R.id.tvPekerjaan).text = data.pekerjaan
        findViewById<TextView>(R.id.tvGaji).text = data.gaji
        findViewById<TextView>(R.id.tvKontakDarurat).text = data.kontakDarurat
        findViewById<TextView>(R.id.tvTempatKerja).text = data.tempatKerja
        findViewById<TextView>(R.id.tvNamaPerumahan).text = data.namaPerumahan
        findViewById<TextView>(R.id.tvTipeRumah).text = data.tipeRumah
        findViewById<TextView>(R.id.tvJenisPembayaran).text = data.jenisPembayaran
        findViewById<TextView>(R.id.tvTipeRumahKategori).text = data.tipeRumahKategori
        findViewById<TextView>(R.id.tvCreatedAt).text = DateUtils.formatDateTime(data.createdAt)
        findViewById<TextView>(R.id.tvUpdatedAt).text = DateUtils.formatDateTime(data.updatedAt)

        // Load images
        val images = data.attachments.map { (name, url) ->
            DocumentImage(name, null, url)
        }
        imageAdapter.updateImages(images)
    }

    private fun displayRenovasiRumah(data: RenovasiRumah) {
        findViewById<TextView>(R.id.tvUniqueCode).text = data.uniqueCode
        findViewById<TextView>(R.id.tvNama).text = data.nama
        findViewById<TextView>(R.id.tvAlamat).text = data.alamat
        findViewById<TextView>(R.id.tvNoTelepon).text = data.noTelepon
        findViewById<TextView>(R.id.tvDeskripsiRenovasi).text = data.deskripsiRenovasi
        findViewById<TextView>(R.id.tvCreatedAt).text = DateUtils.formatDateTime(data.createdAt)
        findViewById<TextView>(R.id.tvUpdatedAt).text = DateUtils.formatDateTime(data.updatedAt)

        val images = data.attachments.map { (name, url) ->
            DocumentImage(name, null, url)
        }
        imageAdapter.updateImages(images)
    }

    private fun displayPemasanganAC(data: PemasanganAC) {
        findViewById<TextView>(R.id.tvUniqueCode).text = data.uniqueCode
        findViewById<TextView>(R.id.tvNama).text = data.nama
        findViewById<TextView>(R.id.tvAlamat).text = data.alamat
        findViewById<TextView>(R.id.tvNoTelepon).text = data.noTelepon
        findViewById<TextView>(R.id.tvJenisAC).text = data.jenisAC
        findViewById<TextView>(R.id.tvJumlahUnit).text = data.jumlahUnit.toString()
        findViewById<TextView>(R.id.tvCreatedAt).text = DateUtils.formatDateTime(data.createdAt)
        findViewById<TextView>(R.id.tvUpdatedAt).text = DateUtils.formatDateTime(data.updatedAt)

        val images = data.attachments.map { (name, url) ->
            DocumentImage(name, null, url)
        }
        imageAdapter.updateImages(images)
    }

    private fun displayPemasanganCCTV(data: PemasanganCCTV) {
        findViewById<TextView>(R.id.tvUniqueCode).text = data.uniqueCode
        findViewById<TextView>(R.id.tvNama).text = data.nama
        findViewById<TextView>(R.id.tvAlamat).text = data.alamat
        findViewById<TextView>(R.id.tvNoTelepon).text = data.noTelepon
        findViewById<TextView>(R.id.tvJumlahUnit).text = data.jumlahUnit.toString()
        findViewById<TextView>(R.id.tvCreatedAt).text = DateUtils.formatDateTime(data.createdAt)
        findViewById<TextView>(R.id.tvUpdatedAt).text = DateUtils.formatDateTime(data.updatedAt)

        val images = data.attachments.map { (name, url) ->
            DocumentImage(name, null, url)
        }
        imageAdapter.updateImages(images)
    }

    private fun showFullScreenImage(image: DocumentImage) {
        // Implement full screen image viewer
        // You can create a dialog or new activity for this
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.detail_menu, menu)

        // Hide delete option for non-admin users
        if (userRole != Constants.ROLE_ADMIN) {
            menu?.findItem(R.id.action_delete)?.isVisible = false
        }

        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_edit -> {
                editDocument()
                true
            }
            R.id.action_delete -> {
                showDeleteConfirmation()
                true
            }
            R.id.action_print -> {
                printDocument()
                true
            }
            R.id.action_download -> {
                downloadPDF()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun editDocument() {
        val intent = Intent(this, EditDocumentActivity::class.java)
        intent.putExtra("document", document as java.io.Serializable)
        intent.putExtra("documentType", documentType)
        startActivity(intent)
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(this)
            .setTitle("Konfirmasi Hapus")
            .setMessage("Apakah Anda yakin ingin menghapus dokumen ini?")
            .setPositiveButton("Hapus") { _, _ ->
                deleteDocument()
            }
            .setNegativeButton("Batal", null)
            .show()
    }

    private fun deleteDocument() {
        // Implementation similar to DocumentListActivity
        document?.let { doc ->
            try {
                val idField = doc::class.java.getDeclaredField("id")
                idField.isAccessible = true
                val id = idField.get(doc) as String

                when (documentType) {
                    Constants.DOC_TYPE_PEMBELIAN_RUMAH -> documentViewModel.deletePembelianRumah(id)
                    Constants.DOC_TYPE_RENOVASI_RUMAH -> documentViewModel.deleteRenovasiRumah(id)
                    Constants.DOC_TYPE_PEMASANGAN_AC -> documentViewModel.deletePemasanganAC(id)
                    Constants.DOC_TYPE_PEMASANGAN_CCTV -> documentViewModel.deletePemasanganCCTV(id)
                }

                documentViewModel.deleteResult.observe(this) { result ->
                    if (result.isSuccess) {
                        Toast.makeText(this, "Dokumen berhasil dihapus", Toast.LENGTH_SHORT).show()
                        finish()
                    } else {
                        Toast.makeText(this, "Gagal menghapus dokumen", Toast.LENGTH_SHORT).show()
                    }
                }

            } catch (e: Exception) {
                Toast.makeText(this, "Gagal menghapus dokumen", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun printDocument() {
        // Implementation for printing
        downloadPDF()
    }

    private fun downloadPDF() {
        document?.let { doc ->
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val pdfFile = when (documentType) {
                        Constants.DOC_TYPE_PEMBELIAN_RUMAH ->
                            PDFGenerator.generatePembelianRumahPDF(this@DocumentDetailActivity, doc as PembelianRumah)
                        Constants.DOC_TYPE_RENOVASI_RUMAH ->
                            PDFGenerator.generateRenovasiRumahPDF(this@DocumentDetailActivity, doc as RenovasiRumah)
                        Constants.DOC_TYPE_PEMASANGAN_AC ->
                            PDFGenerator.generatePemasanganACPDF(this@DocumentDetailActivity, doc as PemasanganAC)
                        Constants.DOC_TYPE_PEMASANGAN_CCTV ->
                            PDFGenerator.generatePemasanganCCTVPDF(this@DocumentDetailActivity, doc as PemasanganCCTV)
                        else -> null
                    }

                    withContext(Dispatchers.Main) {
                        pdfFile?.let { file ->
                            Toast.makeText(this@DocumentDetailActivity, "PDF berhasil dibuat: ${file.name}", Toast.LENGTH_LONG).show()
                            sharePDF(file)
                        } ?: run {
                            Toast.makeText(this@DocumentDetailActivity, "Gagal membuat PDF", Toast.LENGTH_SHORT).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(this@DocumentDetailActivity, "Error: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }

    private fun sharePDF(file: File) {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "application/pdf"
        intent.putExtra(Intent.EXTRA_STREAM, androidx.core.content.FileProvider.getUriForFile(
            this,
            "${packageName}.fileprovider",
            file
        ))
        intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        startActivity(Intent.createChooser(intent, "Share PDF"))
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}