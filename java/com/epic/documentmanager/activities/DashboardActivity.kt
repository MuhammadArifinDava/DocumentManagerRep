package com.epic.documentmanager.activities

import android.content.Intent
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.ViewModelProvider
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.google.android.material.navigation.NavigationView
import com.epic.documentmanager.R
import com.epic.documentmanager.utils.Constants
import com.epic.documentmanager.viewmodels.AuthViewModel
import com.epic.documentmanager.viewmodels.DashboardViewModel

class DashboardActivity : AppCompatActivity(), NavigationView.OnNavigationItemSelectedListener {

    private lateinit var dashboardViewModel: DashboardViewModel
    private lateinit var authViewModel: AuthViewModel

    private lateinit var drawerLayout: DrawerLayout
    private lateinit var navigationView: NavigationView
    private lateinit var swipeRefresh: SwipeRefreshLayout

    // Dashboard cards
    private lateinit var cardTotalDocuments: CardView
    private lateinit var cardPembelianRumah: CardView
    private lateinit var cardRenovasiRumah: CardView
    private lateinit var cardPemasanganAC: CardView
    private lateinit var cardPemasanganCCTV: CardView

    // Dashboard text views
    private lateinit var tvWelcome: TextView
    private lateinit var tvTotalDocuments: TextView
    private lateinit var tvPembelianRumah: TextView
    private lateinit var tvRenovasiRumah: TextView
    private lateinit var tvPemasanganAC: TextView
    private lateinit var tvPemasanganCCTV: TextView

    private var userRole: String = Constants.ROLE_STAFF

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard)

        initViews()
        setupViewModel()
        setupDrawer()
        setupObservers()
        setupClickListeners()

        // Load dashboard data
        dashboardViewModel.loadDashboardData()
    }

    private fun initViews() {
        drawerLayout = findViewById(R.id.drawerLayout)
        navigationView = findViewById(R.id.navigationView)
        swipeRefresh = findViewById(R.id.swipeRefresh)

        cardTotalDocuments = findViewById(R.id.cardTotalDocuments)
        cardPembelianRumah = findViewById(R.id.cardPembelianRumah)
        cardRenovasiRumah = findViewById(R.id.cardRenovasiRumah)
        cardPemasanganAC = findViewById(R.id.cardPemasanganAC)
        cardPemasanganCCTV = findViewById(R.id.cardPemasanganCCTV)

        tvWelcome = findViewById(R.id.tvWelcome)
        tvTotalDocuments = findViewById(R.id.tvTotalDocuments)
        tvPembelianRumah = findViewById(R.id.tvPembelianRumah)
        tvRenovasiRumah = findViewById(R.id.tvRenovasiRumah)
        tvPemasanganAC = findViewById(R.id.tvPemasanganAC)
        tvPemasanganCCTV = findViewById(R.id.tvPemasanganCCTV)
    }

    private fun setupViewModel() {
        dashboardViewModel = ViewModelProvider(this)[DashboardViewModel::class.java]
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]
    }

    private fun setupDrawer() {
        setSupportActionBar(findViewById(R.id.toolbar))

        val toggle = ActionBarDrawerToggle(
            this, drawerLayout, findViewById(R.id.toolbar),
            R.string.navigation_drawer_open, R.string.navigation_drawer_close
        )
        drawerLayout.addDrawerListener(toggle)
        toggle.syncState()

        navigationView.setNavigationItemSelectedListener(this)
    }

    private fun setupObservers() {
        dashboardViewModel.currentUser.observe(this) { user ->
            user?.let {
                tvWelcome.text = "Selamat datang, ${it.fullName}!"
                userRole = it.role
                updateNavigationMenu()

                // Update header
                val headerView = navigationView.getHeaderView(0)
                val tvHeaderName: TextView = headerView.findViewById(R.id.tvHeaderName)
                val tvHeaderEmail: TextView = headerView.findViewById(R.id.tvHeaderEmail)
                val tvHeaderRole: TextView = headerView.findViewById(R.id.tvHeaderRole)

                tvHeaderName.text = it.fullName
                tvHeaderEmail.text = it.email
                tvHeaderRole.text = it.role.uppercase()
            }
        }

        dashboardViewModel.documentCounts.observe(this) { stats ->
            tvTotalDocuments.text = stats.totalDocuments.toString()
            tvPembelianRumah.text = stats.pembelianRumahCount.toString()
            tvRenovasiRumah.text = stats.renovasiRumahCount.toString()
            tvPemasanganAC.text = stats.pemasanganACCount.toString()
            tvPemasanganCCTV.text = stats.pemasanganCCTVCount.toString()
        }

        dashboardViewModel.loading.observe(this) { isLoading ->
            swipeRefresh.isRefreshing = isLoading
        }
    }

    private fun setupClickListeners() {
        swipeRefresh.setOnRefreshListener {
            dashboardViewModel.refreshData()
        }

        cardTotalDocuments.setOnClickListener {
            startActivity(Intent(this, DocumentListActivity::class.java))
        }

        cardPembelianRumah.setOnClickListener {
            val intent = Intent(this, DocumentListActivity::class.java)
            intent.putExtra("documentType", Constants.DOC_TYPE_PEMBELIAN_RUMAH)
            startActivity(intent)
        }

        cardRenovasiRumah.setOnClickListener {
            val intent = Intent(this, DocumentListActivity::class.java)
            intent.putExtra("documentType", Constants.DOC_TYPE_RENOVASI_RUMAH)
            startActivity(intent)
        }

        cardPemasanganAC.setOnClickListener {
            val intent = Intent(this, DocumentListActivity::class.java)
            intent.putExtra("documentType", Constants.DOC_TYPE_PEMASANGAN_AC)
            startActivity(intent)
        }

        cardPemasanganCCTV.setOnClickListener {
            val intent = Intent(this, DocumentListActivity::class.java)
            intent.putExtra("documentType", Constants.DOC_TYPE_PEMASANGAN_CCTV)
            startActivity(intent)
        }
    }

    private fun updateNavigationMenu() {
        val menu = navigationView.menu

        // Hide admin-only features for non-admin users
        if (userRole != Constants.ROLE_ADMIN) {
            menu.findItem(R.id.nav_manage_users)?.isVisible = false
        }

        // Hide manager-only features for staff users
        if (userRole == Constants.ROLE_STAFF) {
            menu.findItem(R.id.nav_monthly_report)?.isVisible = false
        }
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.nav_upload_document -> {
                startActivity(Intent(this, UploadDocumentActivity::class.java))
            }
            R.id.nav_view_documents -> {
                startActivity(Intent(this, DocumentListActivity::class.java))
            }
            R.id.nav_search_documents -> {
                startActivity(Intent(this, SearchDocumentActivity::class.java))
            }
            R.id.nav_monthly_report -> {
                if (userRole != Constants.ROLE_STAFF) {
                    startActivity(Intent(this, MonthlyReportActivity::class.java))
                }
            }
            R.id.nav_account_settings -> {
                startActivity(Intent(this, AccountSettingsActivity::class.java))
            }
            R.id.nav_logout -> {
                logout()
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START)
        return true
    }

    private fun logout() {
        authViewModel.logout()
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        finish()
    }

    override fun onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.START)) {
            drawerLayout.closeDrawer(GravityCompat.START)
        } else {
            super.onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        dashboardViewModel.refreshData()
    }
}