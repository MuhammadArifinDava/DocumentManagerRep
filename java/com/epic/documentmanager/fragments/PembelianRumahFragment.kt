package com.epic.documentmanager.fragments

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.dhaval2404.imagepicker.ImagePicker
import com.epic.documentmanager.R
import com.epic.documentmanager.adapters.DocumentImage
import com.epic.documentmanager.adapters.DocumentImageAdapter
import com.epic.documentmanager.models.PembelianRumah
import com.epic.documentmanager.utils.CodeGenerator
import com.epic.documentmanager.utils.Constants
import com.epic.documentmanager.utils.ValidationUtils
import com.epic.documentmanager.viewmodels.DocumentViewModel

class PembelianRumahFragment : Fragment() {

    private val documentViewModel: DocumentViewModel by activityViewModels()
    private lateinit var imageAdapter: DocumentImageAdapter
    private val selectedImages = mutableListOf<DocumentImage>()
    private var isEditMode = false
    private var editingDocument: PembelianRumah? = null

    // Views
    private lateinit var etNama: EditText
    private lateinit var etAlamatKTP: EditText
    private lateinit var etNIK: EditText
    private lateinit var etNPWP: EditText
    private lateinit var etNoTelepon: EditText
    private lateinit var spinnerStatusPernikahan: Spinner
    private lateinit var etNamaPasangan: EditText
    private lateinit var etPekerjaan: EditText
    private lateinit var etGaji: EditText
    private lateinit var etKontakDarurat: EditText
    private lateinit var etTempatKerja: EditText
    private lateinit var etNamaPerumahan: EditText
    private lateinit var etTipeRumah: EditText
    private lateinit var spinnerJenisPembayaran: Spinner
    private lateinit var spinnerTipeRumahKategori: Spinner
    private lateinit var recyclerViewImages: RecyclerView
    private lateinit var btnAddImage: Button
    private lateinit var btnSave: Button
    private lateinit var progressBar: ProgressBar

    companion object {
        private const val REQUEST_IMAGE_PICK = 1001
        private const val ARG_EDIT_DOCUMENT = "edit_document"

        fun newInstance(document: PembelianRumah? = null): PembelianRumahFragment {
            val fragment = PembelianRumahFragment()
            val args = Bundle()
            document?.let { args.putSerializable(ARG_EDIT_DOCUMENT, it) }
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pembelian_rumah, container, false)
        initViews(view)
        setupRecyclerView()
        setupSpinners()
        setupObservers()
        setupClickListeners()

        // Check if editing
        arguments?.getSerializable(ARG_EDIT_DOCUMENT)?.let { document ->
            editingDocument = document as PembelianRumah
            isEditMode = true
            populateFields(document)
        }

        return view
    }

    private fun initViews(view: View) {
        etNama = view.findViewById(R.id.etNama)
        etAlamatKTP = view.findViewById(R.id.etAlamatKTP)
        etNIK = view.findViewById(R.id.etNIK)
        etNPWP = view.findViewById(R.id.etNPWP)
        etNoTelepon = view.findViewById(R.id.etNoTelepon)
        spinnerStatusPernikahan = view.findViewById(R.id.spinnerStatusPernikahan)
        etNamaPasangan = view.findViewById(R.id.etNamaPasangan)
        etPekerjaan = view.findViewById(R.id.etPekerjaan)
        etGaji = view.findViewById(R.id.etGaji)
        etKontakDarurat = view.findViewById(R.id.etKontakDarurat)
        etTempatKerja = view.findViewById(R.id.etTempatKerja)
        etNamaPerumahan = view.findViewById(R.id.etNamaPerumahan)
        etTipeRumah = view.findViewById(R.id.etTipeRumah)
        spinnerJenisPembayaran = view.findViewById(R.id.spinnerJenisPembayaran)
        spinnerTipeRumahKategori = view.findViewById(R.id.spinnerTipeRumahKategori)
        recyclerViewImages = view.findViewById(R.id.recyclerViewImages)
        btnAddImage = view.findViewById(R.id.btnAddImage)
        btnSave = view.findViewById(R.id.btnSave)
        progressBar = view.findViewById(R.id.progressBar)
    }

    private fun setupRecyclerView() {
        imageAdapter = DocumentImageAdapter(
            images = selectedImages,
            onImageClick = { image, position ->
                // Show full screen image
                showFullScreenImage(image)
            },
            onDeleteClick = { image, position ->
                selectedImages.removeAt(position)
                imageAdapter.notifyItemRemoved(position)
            }
        )

        recyclerViewImages.apply {
            layoutManager = GridLayoutManager(requireContext(), 2)
            adapter = imageAdapter
        }
    }

    private fun setupSpinners() {
        // Status Pernikahan
        val statusPernikahanOptions = arrayOf("Belum Menikah", "Menikah", "Cerai")
        val statusAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, statusPernikahanOptions)
        statusAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerStatusPernikahan.adapter = statusAdapter

        // Jenis Pembayaran
        val jenisPembayaranOptions = arrayOf("Cash", "KPR", "Kredit Internal")
        val pembayaranAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, jenisPembayaranOptions)
        pembayaranAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerJenisPembayaran.adapter = pembayaranAdapter

        // Tipe Rumah Kategori
        val tipeRumahOptions = arrayOf("Subsidi", "Cluster", "Secondary")
        val tipeAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, tipeRumahOptions)
        tipeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerTipeRumahKategori.adapter = tipeAdapter
    }

    private fun setupObservers() {
        documentViewModel.saveResult.observe(viewLifecycleOwner) { result ->
            progressBar.visibility = View.GONE
            btnSave.isEnabled = true

            if (result.isSuccess) {
                Toast.makeText(requireContext(), "Dokumen berhasil disimpan", Toast.LENGTH_SHORT).show()
                clearForm()
                parentFragmentManager.popBackStack()
            } else {
                Toast.makeText(requireContext(), "Gagal menyimpan dokumen: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
            }
        }

        documentViewModel.uploadResult.observe(viewLifecycleOwner) { result ->
            if (result.isSuccess) {
                val uploadedUrls = result.getOrNull() ?: emptyList()
                saveDocumentWithImages(uploadedUrls)
            } else {
                progressBar.visibility = View.GONE
                btnSave.isEnabled = true
                Toast.makeText(requireContext(), "Gagal upload gambar: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupClickListeners() {
        btnAddImage.setOnClickListener {
            pickImage()
        }

        btnSave.setOnClickListener {
            if (validateForm()) {
                saveDocument()
            }
        }
    }

    private fun pickImage() {
        ImagePicker.with(this)
            .crop()
            .compress(1024)
            .maxResultSize(1080, 1080)
            .start(REQUEST_IMAGE_PICK)
    }

    private fun showFullScreenImage(image: DocumentImage) {
        // Implement full screen image viewer
        // You can create a dialog or new activity for this
    }

    private fun populateFields(document: PembelianRumah) {
        etNama.setText(document.nama)
        etAlamatKTP.setText(document.alamatKTP)
        etNIK.setText(document.nik)
        etNPWP.setText(document.npwp)
        etNoTelepon.setText(document.noTelepon)
        etNamaPasangan.setText(document.namaPasangan)
        etPekerjaan.setText(document.pekerjaan)
        etGaji.setText(document.gaji)
        etKontakDarurat.setText(document.kontakDarurat)
        etTempatKerja.setText(document.tempatKerja)
        etNamaPerumahan.setText(document.namaPerumahan)
        etTipeRumah.setText(document.tipeRumah)

        // Set spinner selections
        setSpinnerSelection(spinnerStatusPernikahan, document.statusPernikahan)
        setSpinnerSelection(spinnerJenisPembayaran, document.jenisPembayaran)
        setSpinnerSelection(spinnerTipeRumahKategori, document.tipeRumahKategori)

        // Load existing images
        document.attachments.forEach { (name, url) ->
            selectedImages.add(DocumentImage(name, null, url))
        }
        imageAdapter.notifyDataSetChanged()

        btnSave.text = "Update Dokumen"
    }

    private fun setSpinnerSelection(spinner: Spinner, value: String) {
        val adapter = spinner.adapter as ArrayAdapter<String>
        val position = adapter.getPosition(value)
        if (position >= 0) {
            spinner.setSelection(position)
        }
    }

    private fun validateForm(): Boolean {
        val fields = mapOf(
            "Nama" to etNama.text.toString().trim(),
            "Alamat KTP" to etAlamatKTP.text.toString().trim(),
            "NIK" to etNIK.text.toString().trim(),
            "NPWP" to etNPWP.text.toString().trim(),
            "No. Telepon" to etNoTelepon.text.toString().trim(),
            "Pekerjaan" to etPekerjaan.text.toString().trim(),
            "Gaji" to etGaji.text.toString().trim(),
            "Tempat Kerja" to etTempatKerja.text.toString().trim(),
            "Nama Perumahan" to etNamaPerumahan.text.toString().trim(),
            "Tipe Rumah" to etTipeRumah.text.toString().trim()
        )

        val errors = ValidationUtils.validateForm(fields)
        if (errors.isNotEmpty()) {
            Toast.makeText(requireContext(), errors.first(), Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun saveDocument() {
        progressBar.visibility = View.VISIBLE
        btnSave.isEnabled = false

        // Upload images first if there are new local images
        val localImages = selectedImages.filter { it.uri != null }
        if (localImages.isNotEmpty()) {
            val uris = localImages.map { it.uri!! }
            val fileNames = localImages.map { it.name }
            documentViewModel.uploadFiles(uris, Constants.DOC_TYPE_PEMBELIAN_RUMAH, fileNames)
        } else {
            // No new images, save directly
            saveDocumentWithImages(emptyList())
        }
    }

    private fun saveDocumentWithImages(uploadedUrls: List<String>) {
        // Combine existing and new image URLs
        val allAttachments = mutableMapOf<String, String>()

        // Add existing images
        selectedImages.filter { it.url.isNotEmpty() }.forEach { image ->
            allAttachments[image.name] = image.url
        }

        // Add newly uploaded images
        val localImages = selectedImages.filter { it.uri != null }
        uploadedUrls.forEachIndexed { index, url ->
            val imageName = localImages.getOrNull(index)?.name ?: "image_${index + 1}"
            allAttachments[imageName] = url
        }

        val document = PembelianRumah(
            id = editingDocument?.id ?: "",
            uniqueCode = editingDocument?.uniqueCode ?: CodeGenerator.generateCodeForPembelianRumah(),
            nama = etNama.text.toString().trim(),
            alamatKTP = etAlamatKTP.text.toString().trim(),
            nik = etNIK.text.toString().trim(),
            npwp = etNPWP.text.toString().trim(),
            noTelepon = etNoTelepon.text.toString().trim(),
            statusPernikahan = spinnerStatusPernikahan.selectedItem.toString(),
            namaPasangan = etNamaPasangan.text.toString().trim(),
            pekerjaan = etPekerjaan.text.toString().trim(),
            gaji = etGaji.text.toString().trim(),
            kontakDarurat = etKontakDarurat.text.toString().trim(),
            tempatKerja = etTempatKerja.text.toString().trim(),
            namaPerumahan = etNamaPerumahan.text.toString().trim(),
            tipeRumah = etTipeRumah.text.toString().trim(),
            jenisPembayaran = spinnerJenisPembayaran.selectedItem.toString(),
            tipeRumahKategori = spinnerTipeRumahKategori.selectedItem.toString(),
            createdAt = editingDocument?.createdAt ?: System.currentTimeMillis(),
            createdBy = editingDocument?.createdBy ?: "",
            attachments = allAttachments
        )

        documentViewModel.savePembelianRumah(document)
    }

    private fun clearForm() {
        etNama.text.clear()
        etAlamatKTP.text.clear()
        etNIK.text.clear()
        etNPWP.text.clear()
        etNoTelepon.text.clear()
        etNamaPasangan.text.clear()
        etPekerjaan.text.clear()
        etGaji.text.clear()
        etKontakDarurat.text.clear()
        etTempatKerja.text.clear()
        etNamaPerumahan.text.clear()
        etTipeRumah.text.clear()

        spinnerStatusPernikahan.setSelection(0)
        spinnerJenisPembayaran.setSelection(0)
        spinnerTipeRumahKategori.setSelection(0)

        selectedImages.clear()
        imageAdapter.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                val imageName = "Document_${System.currentTimeMillis()}.jpg"
                val documentImage = DocumentImage(imageName, uri)
                selectedImages.add(documentImage)
                imageAdapter.notifyItemInserted(selectedImages.size - 1)
            }
        }
    }
}