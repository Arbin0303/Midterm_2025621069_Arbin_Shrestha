package com.example.personalexpensetracker.ui.detail

import android.app.Application
import androidx.lifecycle.*
import com.example.personalexpensetracker.data.local.AppDatabase
import com.example.personalexpensetracker.data.local.TransactionEntity
import com.example.personalexpensetracker.data.repository.TransactionRepository
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class TransactionDetailViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TransactionRepository

    init {
        val dao = AppDatabase.getDatabase(application).transactionDao()
        repository = TransactionRepository(dao)
    }

    private val _transaction = MutableStateFlow<TransactionEntity?>(null)
    val transaction: StateFlow<TransactionEntity?> = _transaction.asStateFlow()

    private val _deleted = MutableStateFlow(false)
    val deleted: StateFlow<Boolean> = _deleted.asStateFlow()

    fun loadTransaction(id: Int) {
        viewModelScope.launch {
            repository.getTransactionById(id).collect { entity ->
                _transaction.value = entity
            }
        }
    }

    fun deleteTransaction(id: Int) {
        viewModelScope.launch {
            repository.deleteById(id)
            _deleted.value = true
        }
    }
}
