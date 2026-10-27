package com.example.personalexpensetracker.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Room Entity representing a single financial transaction.
 * Maps to the "transactions" table in expense_tracker_database.
 */
@Entity(tableName = "transactions")
data class TransactionEntity(
    @PrimaryKey(autoGenerate = true)
    val transactionId: Int = 0,

    /** Short descriptive title, e.g. "Monthly Salary", "Lunch" */
    val title: String,

    /** Always a positive value; type field determines direction */
    val amount: Double,

    /** "INCOME" or "EXPENSE" */
    val type: String,

    /** One of: Food, Transport, Education, Shopping, Salary, Entertainment */
    val category: String,

    /** Unix epoch in milliseconds — enables range queries and consistent sorting */
    val date: Long,

    /** Optional user note — nullable */
    val description: String? = null
)
