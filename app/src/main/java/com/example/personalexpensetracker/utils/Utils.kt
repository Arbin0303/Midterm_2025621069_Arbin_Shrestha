package com.example.personalexpensetracker.utils

import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.*

object Constants {
    const val TYPE_INCOME  = "INCOME"
    const val TYPE_EXPENSE = "EXPENSE"

    val CATEGORIES = listOf(
        "Food", "Transport", "Education", "Shopping", "Salary", "Entertainment", "Other"
    )

    val CATEGORIES_INCOME  = listOf("Salary", "Other")
    val CATEGORIES_EXPENSE = listOf("Food", "Transport", "Education", "Shopping", "Entertainment", "Other")
}

object DateUtils {
    private val displayFormat = SimpleDateFormat("dd MMM yyyy", Locale.getDefault())
    private val inputFormat   = SimpleDateFormat("dd/MM/yyyy",  Locale.getDefault())

    fun formatForDisplay(epochMs: Long): String =
        displayFormat.format(Date(epochMs))

    fun formatForInput(epochMs: Long): String =
        inputFormat.format(Date(epochMs))

    fun todayEpoch(): Long = System.currentTimeMillis()

    /** Returns the start (00:00:00) and end (23:59:59) of the current month as epoch ms */
    fun currentMonthRange(): Pair<Long, Long> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.DAY_OF_MONTH, 1)
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        val start = cal.timeInMillis
        cal.set(Calendar.DAY_OF_MONTH, cal.getActualMaximum(Calendar.DAY_OF_MONTH))
        cal.set(Calendar.HOUR_OF_DAY, 23)
        cal.set(Calendar.MINUTE, 59)
        cal.set(Calendar.SECOND, 59)
        val end = cal.timeInMillis
        return Pair(start, end)
    }
}

object CurrencyUtils {
    fun format(amount: Double): String {
        val formatter = NumberFormat.getNumberInstance(Locale.getDefault())
        formatter.minimumFractionDigits = 0
        formatter.maximumFractionDigits = 2
        return "₩ ${formatter.format(amount)}"
    }
}
