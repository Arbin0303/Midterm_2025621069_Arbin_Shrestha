package com.example.personalexpensetracker.ui.dashboard

import android.app.Application
import androidx.lifecycle.*
import com.example.personalexpensetracker.data.local.AppDatabase
import com.example.personalexpensetracker.data.local.TransactionEntity
import com.example.personalexpensetracker.data.repository.TransactionRepository
import com.example.personalexpensetracker.utils.Constants
import com.example.personalexpensetracker.utils.DateUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

data class DashboardSummary(
    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0
)

class DashboardViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TransactionRepository

    init {
        val dao = AppDatabase.getDatabase(application).transactionDao()
        repository = TransactionRepository(dao)
    }

    // ── Recent transactions (last 5) for Dashboard RecyclerView ──────────────
    val recentTransactions: StateFlow<List<TransactionEntity>> =
        repository.getRecentTransactions(5)
            .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // ── Monthly summary combining income + expense flows ─────────────────────
    val dashboardSummary: StateFlow<DashboardSummary> = run {
        val (startMs, endMs) = DateUtils.currentMonthRange()
        combine(
            repository.getMonthlyTotalByType(Constants.TYPE_INCOME, startMs, endMs),
            repository.getMonthlyTotalByType(Constants.TYPE_EXPENSE, startMs, endMs)
        ) { income, expense ->
            DashboardSummary(income, expense, income - expense)
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardSummary())
    }
}
