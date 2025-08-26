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
import com.epic.documentmanager.models.PemasanganCCTV
import com.epic.documentmanager.utils.CodeGenerator
import com.epic.documentmanager.utils.Constants
import com.epic.documentmanager.utils.Constants.REQUEST_IMAGE_PICK
import com.epic.documentmanager.utils.ValidationUtils
import com.epic.documentmanager.viewmodels.DocumentViewModel

class PemasanganCCTVFragment : Fragment() {

    private val documentViewModel: DocumentViewModel by activityViewModels()
    private lateinit var imageAdapter: DocumentImageAdapter
    private val selectedImages = mutableListOf<DocumentImage>()
    private var isEditMode = false
    private var editingDocument: PemasanganCCTV? = null

    // Views
    private lateinit var etNama: EditText
    private lateinit var etAlamat: EditText
    private lateinit var etNoTelepon: EditText
    private lateinit var etJumlahUnit: EditText
    private lateinit var recyclerViewImages: RecyclerView
    private lateinit var btnAddImage: Button
    private lateinit var btnSave: Button
    private lateinit var progressBar: ProgressBar

    companion object {
        private const val REQUEST_IMAGE_PICK = 1004
        private const val ARG_EDIT_DOCUMENT = "edit_document"

        fun newInstance(document: PemasanganCCTV? = null): PemasanganCCTVFragment {
            val fragment = PemasanganCCTVFragment()
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
        val view = inflater.inflate(R.layout.fragment_pemasangan_cctv, container, false)
        initViews(view)
        setupRecyclerView()
        setupObservers()
        setupClickListeners()

        arguments?.getSerializable(ARG_EDIT_DOCUMENT)?.let { document ->
            editingDocument = document as PemasanganCCTV
            isEditMode = true
            populateFields(document)
        }

        return view
    }

    private fun initViews(view: View) {
        etNama = view.findViewById(R.id.etNama)
        etAlamat = view.findViewById(R.id.etAlamat)
        etNoTelepon = view.findViewById(R.id.etNoTelepon)
        etJumlahUnit = view.findViewById(R.id.etJumlahUnit)
        recyclerViewImages = view.findViewById(R.id.recyclerViewImages)
        btnAddImage = view.findViewById(R.id.btnAddImage)
        btnSave = view.findViewById(R.id.btnSave)
        progressBar = view.findViewById(R.id.progressBar)
    }

    private fun setupRecyclerView() {
        imageAdapter = DocumentImageAdapter(
            images = selectedImages,
            onImageClick = { image, position -> showFullScreenImage(image) },
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
        btnAddImage.setOnClickListener { pickImage() }
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
    }

    private fun populateFields(document: PemasanganCCTV) {
        etNama.setText(document.nama)
        etAlamat.setText(document.alamat)
        etNoTelepon.setText(document.noTelepon)
        etJumlahUnit.setText(document.jumlahUnit.toString())

        document.attachments.forEach { (name, url) ->
            selectedImages.add(DocumentImage(name, null, url))
        }
        imageAdapter.notifyDataSetChanged()

        btnSave.text = "Update Dokumen"
    }

    private fun validateForm(): Boolean {
        val fields = mapOf(
            "Nama" to etNama.text.toString().trim(),
            "Alamat" to etAlamat.text.toString().trim(),
            "No. Telepon" to etNoTelepon.text.toString().trim(),
            "Jumlah Unit" to etJumlahUnit.text.toString().trim()
        )

        val errors = ValidationUtils.validateForm(fields)
        if (errors.isNotEmpty()) {
            Toast.makeText(requireContext(), errors.first(), Toast.LENGTH_SHORT).show()
            return false
        }

        try {
            etJumlahUnit.text.toString().toInt()
        } catch (e: NumberFormatException) {
            Toast.makeText(requireContext(), "Jumlah unit harus berupa angka", Toast.LENGTH_SHORT).show()
            return false
        }

        return true
    }

    private fun saveDocument() {
        progressBar.visibility = View.VISIBLE
        btnSave.isEnabled = false

        val localImages = selectedImages.filter { it.uri != null }
        if (localImages.isNotEmpty()) {
            val uris = localImages.map { it.uri!! }
            val fileNames = localImages.map { it.name }
            documentViewModel.uploadFiles(uris, Constants.DOC_TYPE_PEMASANGAN_CCTV, fileNames)
        } else {
            saveDocumentWithImages(emptyList())
        }
    }

    private fun saveDocumentWithImages(uploadedUrls: List<String>) {
        val allAttachments = mutableMapOf<String, String>()

        selectedImages.filter { it.url.isNotEmpty() }.forEach { image ->
            allAttachments[image.name] = image.url
        }

        val localImages = selectedImages.filter { it.uri != null }
        uploadedUrls.forEachIndexed { index, url ->
            val imageName = localImages.getOrNull(index)?.name ?: "image_${index + 1}"
            allAttachments[imageName] = url
        }

        val document = PemasanganCCTV(
            id = editingDocument?.id ?: "",
            uniqueCode = editingDocument?.uniqueCode ?: CodeGenerator.generateCodeForPemasanganCCTV(),
            nama = etNama.text.toString().trim(),
            alamat = etAlamat.text.toString().trim(),
            noTelepon = etNoTelepon.text.toString().trim(),
            jumlahUnit = etJumlahUnit.text.toString().toInt(),
            createdAt = editingDocument?.createdAt ?: System.currentTimeMillis(),
            createdBy = editingDocument?.createdBy ?: "",
            attachments = allAttachments
        )

        documentViewModel.savePemasanganCCTV(document)
    }

    private fun clearForm() {
        etNama.text.clear()
        etAlamat.text.clear()
        etNoTelepon.text.clear()
        etJumlahUnit.text.clear()
        selectedImages.clear()
        imageAdapter.notifyDataSetChanged()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK) {
            data?.data?.let { uri ->
                val imageName = "CCTV_${System.currentTimeMillis()}.jpg"
                val documentImage = DocumentImage(imageName, uri)
                selectedImages.add(documentImage)
                imageAdapter.notifyItemInserted(selectedImages.size - 1)
            }
        }
    }
} ?: "",
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
