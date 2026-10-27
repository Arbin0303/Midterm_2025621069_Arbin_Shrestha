package com.example.personalexpensetracker.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object for the transactions table.
 * All write operations are suspend functions (run on IO dispatcher via coroutines).
 * Read operations return Flow so the UI updates automatically on data changes.
 */
@Dao
interface TransactionDao {

    // ──────────────────────────────────────────────
    //  Write operations
    // ──────────────────────────────────────────────

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTransaction(transaction: TransactionEntity)

    @Update
    suspend fun updateTransaction(transaction: TransactionEntity)

    @Delete
    suspend fun deleteTransaction(transaction: TransactionEntity)

    @Query("DELETE FROM transactions WHERE transactionId = :id")
    suspend fun deleteById(id: Int)

    // ──────────────────────────────────────────────
    //  Read operations
    // ──────────────────────────────────────────────

    /** All transactions, newest first */
    @Query("SELECT * FROM transactions ORDER BY date DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    /** Single transaction by ID (for detail / edit screens) */
    @Query("SELECT * FROM transactions WHERE transactionId = :id")
    fun getTransactionById(id: Int): Flow<TransactionEntity?>

    /** Recent N transactions for Dashboard preview */
    @Query("SELECT * FROM transactions ORDER BY date DESC LIMIT :limit")
    fun getRecentTransactions(limit: Int = 5): Flow<List<TransactionEntity>>

    // ──────────────────────────────────────────────
    //  Filtered queries
    // ──────────────────────────────────────────────

    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY date DESC")
    fun getTransactionsByType(type: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE category = :category ORDER BY date DESC")
    fun getTransactionsByCategory(category: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE type = :type AND category = :category ORDER BY date DESC")
    fun getTransactionsByTypeAndCategory(type: String, category: String): Flow<List<TransactionEntity>>

    @Query("SELECT * FROM transactions WHERE date BETWEEN :startMs AND :endMs ORDER BY date DESC")
    fun getTransactionsByDateRange(startMs: Long, endMs: Long): Flow<List<TransactionEntity>>

    // ──────────────────────────────────────────────
    //  Aggregate queries for Dashboard
    // ──────────────────────────────────────────────

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM transactions WHERE type = :type")
    fun getTotalByType(type: String): Flow<Double>

    @Query("SELECT COALESCE(SUM(amount), 0.0) FROM transactions WHERE type = :type AND date BETWEEN :startMs AND :endMs")
    fun getMonthlyTotalByType(type: String, startMs: Long, endMs: Long): Flow<Double>
}
