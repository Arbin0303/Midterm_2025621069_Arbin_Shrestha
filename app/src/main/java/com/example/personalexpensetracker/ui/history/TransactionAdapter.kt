package com.example.personalexpensetracker.ui.history

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.personalexpensetracker.R
import com.example.personalexpensetracker.data.local.TransactionEntity
import com.example.personalexpensetracker.databinding.ItemTransactionBinding
import com.example.personalexpensetracker.utils.Constants
import com.example.personalexpensetracker.utils.CurrencyUtils
import com.example.personalexpensetracker.utils.DateUtils

/**
 * RecyclerView adapter for transaction lists.
 * Uses ListAdapter + DiffUtil for efficient, animated updates.
 */
class TransactionAdapter(
    private val onItemClick: (TransactionEntity) -> Unit
) : ListAdapter<TransactionEntity, TransactionAdapter.ViewHolder>(DiffCallback) {

    inner class ViewHolder(private val binding: ItemTransactionBinding) :
        RecyclerView.ViewHolder(binding.root) {

        fun bind(transaction: TransactionEntity) {
            binding.tvTitle.text    = transaction.title
            binding.tvCategory.text = transaction.category
            binding.tvDate.text     = DateUtils.formatForDisplay(transaction.date)

            val isIncome = transaction.type == Constants.TYPE_INCOME
            val amountText = if (isIncome)
                "+${CurrencyUtils.format(transaction.amount)}"
            else
                "-${CurrencyUtils.format(transaction.amount)}"

            binding.tvAmount.text = amountText

            val amountColor = ContextCompat.getColor(
                binding.root.context,
                if (isIncome) R.color.income_green else R.color.expense_red
            )
            binding.tvAmount.setTextColor(amountColor)

            val iconRes = if (isIncome) R.drawable.ic_income else R.drawable.ic_expense
            binding.ivTypeIcon.setImageResource(iconRes)

            binding.root.setOnClickListener { onItemClick(transaction) }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding = ItemTransactionBinding.inflate(
            LayoutInflater.from(parent.context), parent, false
        )
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(getItem(position))
    }

    companion object DiffCallback : DiffUtil.ItemCallback<TransactionEntity>() {
        override fun areItemsTheSame(old: TransactionEntity, new: TransactionEntity) =
            old.transactionId == new.transactionId

        override fun areContentsTheSame(old: TransactionEntity, new: TransactionEntity) =
            old == new
    }
}
