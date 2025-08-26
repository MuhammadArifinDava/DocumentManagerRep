package com.epic.documentmanager.activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import com.epic.documentmanager.R
import com.epic.documentmanager.adapters.ReportAdapter
import com.epic.documentmanager.models.MonthlyReport
import com.epic.documentmanager.utils.Constants
import com.epic.documentmanager.utils.DateUtils
import com.epic.documentmanager.utils.PDFGenerator
import com.epic.documentmanager.viewmodels.AuthViewModel
import com.epic.documentmanager.viewmodels.ReportViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File

class MonthlyReportActivity : AppCompatActivity() {

    private lateinit var reportViewModel: ReportViewModel
    private lateinit var authViewModel: AuthViewModel
    private lateinit var reportAdapter: ReportAdapter

    // Views
    private lateinit var spinnerMonth: Spinner
    private lateinit var spinnerYear: Spinner
    private lateinit var btnGenerateReport: Button
    private lateinit var recyclerViewReports: RecyclerView
    private lateinit var swipeRefresh: SwipeRefreshLayout
    private lateinit var progressBar: ProgressBar
    private lateinit var llReportSummary: LinearLayout
    private lateinit var tvTotalDocuments: TextView
    private lateinit var tvPembelianRumah: TextView
    private lateinit var tvRenovasiRumah: TextView
    private lateinit var tvPemasanganAC: TextView
    private lateinit var tvPemasanganCCTV: TextView
    private lateinit var btnDownloadPDF: Button

    private var currentReport: MonthlyReport? = null
    private var userRole: String = Constants.ROLE_STAFF

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_monthly_report)

        setupActionBar()
        initViews()
        setupViewModel()
        setupSpinners()
        setupRecyclerView()
        setupObservers()
        setupClickListeners()

        loadReports()
    }

    private fun setupActionBar() {
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            title = "Laporan Bulanan"
        }
    }

    private fun initViews() {
        spinnerMonth = findViewById(R.id.spinnerMonth)
        spinnerYear = findViewById(R.id.spinnerYear)
        btnGenerateReport = findViewById(R.id.btnGenerateReport)
        recyclerViewReports = findViewById(R.id.recyclerViewReports)
        swipeRefresh = findViewById(R.id.swipeRefresh)
        progressBar = findViewById(R.id.progressBar)
        llReportSummary = findViewById(R.id.llReportSummary)
        tvTotalDocuments = findViewById(R.id.tvTotalDocuments)
        tvPembelianRumah = findViewById(R.id.tvPembelianRumah)
        tvRenovasiRumah = findViewById(R.id.tvRenovasiRumah)
        tvPemasanganAC = findViewById(R.id.tvPemasanganAC)
        tvPemasanganCCTV = findViewById(R.id.tvPemasanganCCTV)
        btnDownloadPDF = findViewById(R.id.btnDownloadPDF)
    }

    private fun setupViewModel() {
        reportViewModel = ViewModelProvider(this)[ReportViewModel::class.java]
        authViewModel = ViewModelProvider(this)[AuthViewModel::class.java]

        authViewModel.getCurrentUser()
    }

    private fun setupSpinners() {
        // Month spinner
        val months = arrayOf(
            "01" to "Januari", "02" to "Februari", "03" to "Maret",
            "04" to "April", "05" to "Mei", "06" to "Juni",
            "07" to "Juli", "08" to "Agustus", "09" to "September",
            "10" to "Oktober", "11" to "November", "12" to "Desember"
        )

        val monthAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            months.map { it.second }
        )
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMonth.adapter = monthAdapter

        // Set current month
        val currentMonth = DateUtils.getCurrentMonth().toInt() - 1
        spinnerMonth.setSelection(currentMonth)

        // Year spinner
        val currentYear = DateUtils.getCurrentYear().toInt()
        val years = (currentYear - 5..currentYear).map { it.toString() }.reversed()

        val yearAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            years
        )
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerYear.adapter = yearAdapter
    }

    private fun setupRecyclerView() {
        reportAdapter = ReportAdapter(
            reports = emptyList(),
            onItemClick = { report ->
                showReportDetails(report)
            }
        )

        recyclerViewReports.apply {
            layoutManager = LinearLayoutManager(this@MonthlyReportActivity)
            adapter = reportAdapter
        }
    }

    private fun setupObservers() {
        authViewModel.currentUser.observe(this) { user ->
            user?.let {
                userRole = it.role
            }
        }

        reportViewModel.reportList.observe(this) { reports ->
            reportAdapter.updateReports(reports)
        }

        reportViewModel.currentReport.observe(this) { report ->
            currentReport = report
            report?.let {
                showReportSummary(it)
            }
        }

        reportViewModel.loading.observe(this) { isLoading ->
            progressBar.visibility = if (isLoading) View.VISIBLE else View.GONE
            swipeRefresh.isRefreshing = isLoading
        }

        reportViewModel.generateResult.observe(this) { result ->
            if (result.isSuccess) {
                Toast.makeText(this, "Laporan berhasil digenerate", Toast.LENGTH_SHORT).show()
                loadReports()
            } else {
                Toast.makeText(this, "Gagal generate laporan: ${result.exceptionOrNull()?.message}", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun setupClickListeners() {
        btnGenerateReport.setOnClickListener {
            generateReport()
        }

        swipeRefresh.setOnRefreshListener {
            loadReports()
        }

        btnDownloadPDF.setOnClickListener {
            currentReport?.let { downloadReportPDF(it) }
        }
    }

    private fun loadReports() {
        reportViewModel.loadAllReports()
    }

    private fun generateReport() {
        val selectedMonth = String.format("%02d", spinnerMonth.selectedItemPosition + 1)
        val selectedYear = spinnerYear.selectedItem.toString()

        reportViewModel.generateMonthlyReport(selectedMonth, selectedYear)
    }

    private fun showReportDetails(report: MonthlyReport) {
        showReportSummary(report)
        currentReport = report
    }

    private fun showReportSummary(report: MonthlyReport) {
        llReportSummary.visibility = View.VISIBLE
        tvTotalDocuments.text = report.totalDocuments.toString()
        tvPembelianRumah.text = report.pembelianRumahCount.toString()
        tvRenovasiRumah.text = report.renovasiRumahCount.toString()
        tvPemasanganAC.text = report.pemasanganACCount.toString()
        tvPemasanganCCTV.text = report.pemasanganCCTVCount.toString()
        btnDownloadPDF.visibility = View.VISIBLE
    }

    private fun downloadReportPDF(report: MonthlyReport) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val pdfFile = PDFGenerator.generateMonthlyReportPDF(this@MonthlyReportActivity, report)

                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MonthlyReportActivity,
                        "PDF berhasil dibuat: ${pdfFile.name}",
                        Toast.LENGTH_LONG
                    ).show()
                    sharePDF(pdfFile)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MonthlyReportActivity,
                        "Error: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
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