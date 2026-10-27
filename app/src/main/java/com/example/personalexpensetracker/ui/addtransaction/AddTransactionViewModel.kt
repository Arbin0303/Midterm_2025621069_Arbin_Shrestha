package com.example.personalexpensetracker.ui.addtransaction

import android.app.Application
import androidx.lifecycle.*
import com.example.personalexpensetracker.data.local.AppDatabase
import com.example.personalexpensetracker.data.local.TransactionEntity
import com.example.personalexpensetracker.data.repository.TransactionRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SaveState {
    object Idle    : SaveState()
    object Success : SaveState()
    data class Error(val message: String) : SaveState()
}

class AddTransactionViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: TransactionRepository

    init {
        val dao = AppDatabase.getDatabase(application).transactionDao()
        repository = TransactionRepository(dao)
    }

    private val _saveState = MutableStateFlow<SaveState>(SaveState.Idle)
    val saveState: StateFlow<SaveState> = _saveState.asStateFlow()

    private val _existingTransaction = MutableStateFlow<TransactionEntity?>(null)
    val existingTransaction: StateFlow<TransactionEntity?> = _existingTransaction.asStateFlow()

    /** Load an existing transaction when in edit mode */
    fun loadTransaction(id: Int) {
        viewModelScope.launch {
            repository.getTransactionById(id).collect { entity ->
                _existingTransaction.value = entity
            }
        }
    }

    /** Validate inputs then insert or update */
    fun saveTransaction(
        transactionId: Int,
        title: String,
        amountStr: String,
        type: String,
        category: String,
        dateMs: Long,
        description: String?
    ) {
        // ── Validation ──────────────────────────────────────────────────────
        if (title.isBlank()) {
            _saveState.value = SaveState.Error("Title cannot be empty.")
            return
        }
        val amount = amountStr.toDoubleOrNull()
        if (amount == null || amount <= 0) {
            _saveState.value = SaveState.Error("Please enter a valid amount greater than zero.")
            return
        }
        if (type.isBlank()) {
            _saveState.value = SaveState.Error("Please select a transaction type.")
            return
        }
        if (category.isBlank()) {
            _saveState.value = SaveState.Error("Please select a category.")
            return
        }

        val entity = TransactionEntity(
            transactionId = if (transactionId == -1) 0 else transactionId,
            title         = title.trim(),
            amount        = amount,
            type          = type,
            category      = category,
            date          = dateMs,
            description   = description?.trim()?.ifBlank { null }
        )

        viewModelScope.launch {
            if (transactionId == -1) {
                repository.insertTransaction(entity)
            } else {
                repository.updateTransaction(entity)
            }
            _saveState.value = SaveState.Success
        }
    }

    fun resetSaveState() {
        _saveState.value = SaveState.Idle
    }
}
