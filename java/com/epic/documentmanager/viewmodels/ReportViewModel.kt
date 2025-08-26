package com.epic.documentmanager.viewmodels

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.epic.documentmanager.models.MonthlyReport
import com.epic.documentmanager.repositories.DocumentRepository
import com.epic.documentmanager.utils.DateUtils
import kotlinx.coroutines.launch

class ReportViewModel : ViewModel() {
    private val documentRepository = DocumentRepository()

    private val _monthlyReport = MutableLiveData<MonthlyReport>()
    val monthlyReport: LiveData<MonthlyReport> = _monthlyReport

    private val _availableMonths = MutableLiveData<List<String>>()
    val availableMonths: LiveData<List<String>> = _availableMonths

    private val _loading = MutableLiveData<Boolean>()
    val loading: LiveData<Boolean> = _loading

    fun generateMonthlyReport(year: Int, month: Int) {
        viewModelScope.launch {
            _loading.value = true
            try {
                val report = documentRepository.getMonthlyReportData(year, month)
                _monthlyReport.value = report
            } catch (e: Exception) {
                _monthlyReport.value = MonthlyReport()
            } finally {
                _loading.value = false
            }
        }
    }

    fun generateCurrentMonthReport() {
        val currentYear = DateUtils.getCurrentYear().toInt()
        val currentMonth = DateUtils.getCurrentMonth().toInt()
        generateMonthlyReport(currentYear, currentMonth)
    }

    fun loadAvailableMonths() {
        viewModelScope.launch {
            try {
                val months = mutableListOf<String>()
                val currentYear = DateUtils.getCurrentYear().toInt()

                // Generate last 12 months
                for (i in 0..11) {
                    val calendar = java.util.Calendar.getInstance()
                    calendar.add(java.util.Calendar.MONTH, -i)
                    val monthYear = "${calendar.get(java.util.Calendar.MONTH) + 1}/${calendar.get(java.util.Calendar.YEAR)}"
                    months.add(monthYear)
                }

                _availableMonths.value = months
            } catch (e: Exception) {
                _availableMonths.value = emptyList()
            }
        }
    }
}