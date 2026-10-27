package com.example.personalexpensetracker.ui.history

import android.app.Application
import androidx.lifecycle.*
import com.example.personalexpensetracker.data.local.AppDatabase
import com.example.personalexpensetracker.data.local.TransactionEntity
import com.example.personalexpensetracker.data.repository.TransactionRepository
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch

class TransactionHistoryViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TransactionRepository

    init {
        val dao = AppDatabase.getDatabase(application).transactionDao()
        repository = TransactionRepository(dao)
    }

    // Current filter state
    private val _filterType     = MutableStateFlow("All")
    private val _filterCategory = MutableStateFlow("All")

    val filterType:     StateFlow<String> = _filterType.asStateFlow()
    val filterCategory: StateFlow<String> = _filterCategory.asStateFlow()

    /** Filtered list reacts to filter changes automatically */
    val filteredTransactions: StateFlow<List<TransactionEntity>> =
        combine(_filterType, _filterCategory) { type, category ->
            Pair(type, category)
        }.flatMapLatest { (type, category) ->
            when {
                type == "All" && category == "All" -> repository.allTransactions
                type == "All"                      -> repository.getTransactionsByCategory(category)
                category == "All"                  -> repository.getTransactionsByType(type)
                else                               -> repository.getTransactionsByTypeAndCategory(type, category)
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setTypeFilter(type: String)         { _filterType.value = type }
    fun setCategoryFilter(category: String) { _filterCategory.value = category }

    fun deleteById(id: Int) {
        viewModelScope.launch { repository.deleteById(id) }
    }
}
