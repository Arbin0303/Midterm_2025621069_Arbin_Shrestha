package com.example.personalexpensetracker.data.repository

import com.example.personalexpensetracker.data.local.TransactionDao
import com.example.personalexpensetracker.data.local.TransactionEntity
import kotlinx.coroutines.flow.Flow

/**
 * Repository — single source of truth between ViewModels and Room DAO.
 * ViewModels depend on this class, not directly on the DAO.
 */
class TransactionRepository(private val dao: TransactionDao) {

    // ── Expose flows for UI observation ──────────────────────────────────────

    val allTransactions: Flow<List<TransactionEntity>> = dao.getAllTransactions()

    fun getRecentTransactions(limit: Int = 5): Flow<List<TransactionEntity>> =
        dao.getRecentTransactions(limit)

    fun getTransactionById(id: Int): Flow<TransactionEntity?> =
        dao.getTransactionById(id)

    fun getTransactionsByType(type: String): Flow<List<TransactionEntity>> =
        dao.getTransactionsByType(type)

    fun getTransactionsByCategory(category: String): Flow<List<TransactionEntity>> =
        dao.getTransactionsByCategory(category)

    fun getTransactionsByTypeAndCategory(
        type: String,
        category: String
    ): Flow<List<TransactionEntity>> =
        dao.getTransactionsByTypeAndCategory(type, category)

    fun getTransactionsByDateRange(startMs: Long, endMs: Long): Flow<List<TransactionEntity>> =
        dao.getTransactionsByDateRange(startMs, endMs)

    fun getTotalByType(type: String): Flow<Double> =
        dao.getTotalByType(type)

    fun getMonthlyTotalByType(type: String, startMs: Long, endMs: Long): Flow<Double> =
        dao.getMonthlyTotalByType(type, startMs, endMs)

    // ── Write operations (suspend — called from viewModelScope) ──────────────

    suspend fun insertTransaction(transaction: TransactionEntity) =
        dao.insertTransaction(transaction)

    suspend fun updateTransaction(transaction: TransactionEntity) =
        dao.updateTransaction(transaction)

    suspend fun deleteTransaction(transaction: TransactionEntity) =
        dao.deleteTransaction(transaction)

    suspend fun deleteById(id: Int) =
        dao.deleteById(id)
}
