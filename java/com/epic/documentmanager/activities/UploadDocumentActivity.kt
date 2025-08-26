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

class UploadDocumentActivity : AppCompatActivity() {

    private lateinit var tabLayout: TabLayout
    private val fragments = mutableListOf<Fragment>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_upload_document)

        setupActionBar()
        initViews()
        setupTabs()

        // Load first tab by default
        loadFragment(fragments[0])
    }

    private fun setupActionBar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Upload Dokumen"
        }
    }

    private fun initViews() {
        tabLayout = findViewById(R.id.tabLayout)
    }

    private fun setupTabs() {
        // Initialize fragments
        fragments.apply {
            add(PembelianRumahFragment.newInstance())
            add(RenovasiRumahFragment.newInstance())
            add(PemasanganACFragment.newInstance())
            add(PemasanganCCTVFragment.newInstance())
        }

        // Add tabs
        tabLayout.addTab(tabLayout.newTab().setText("Pembelian Rumah"))
        tabLayout.addTab(tabLayout.newTab().setText("Renovasi Rumah"))
        tabLayout.addTab(tabLayout.newTab().setText("Pemasangan AC"))
        tabLayout.addTab(tabLayout.newTab().setText("Pemasangan CCTV"))

        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                tab?.let {
                    loadFragment(fragments[it.position])
                }
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {}
            override fun onTabReselected(tab: TabLayout.Tab?) {}
        })
    }

    private fun loadFragment(fragment: Fragment) {
        supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragment)
            .commit()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}