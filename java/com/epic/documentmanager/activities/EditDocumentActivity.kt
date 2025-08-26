package com.epic.documentmanager.activities

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayout
import com.epic.documentmanager.R
import com.epic.documentmanager.fragments.PembelianRumahFragment
import com.epic.documentmanager.fragments.RenovasiRumahFragment
import com.epic.documentmanager.fragments.PemasanganACFragment
import com.epic.documentmanager.fragments.PemasanganCCTVFragment
import com.epic.documentmanager.models.*
import com.epic.documentmanager.utils.Constants

class EditDocumentActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private val fragments = mutableListOf<Fragment>()

    private var document: Any? = null
    private var documentType: String = Constants.DOC_TYPE_PEMBELIAN_RUMAH

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_document)

        document = intent.getSerializableExtra("document")
        documentType = intent.getStringExtra("documentType") ?: Constants.DOC_TYPE_PEMBELIAN_RUMAH

        setupActionBar()
        initViews()
        setupTabs()

        // Load appropriate fragment based on document type
        loadEditFragment()
    }

    private fun setupActionBar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Edit Dokumen"
        }
    }

    private fun initViews() {
        tabLayout = findViewById(R.id.tabLayout)
    }

}