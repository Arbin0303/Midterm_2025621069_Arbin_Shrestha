package com.example.personalexpensetracker.ui.detail

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.personalexpensetracker.R
import com.example.personalexpensetracker.databinding.FragmentTransactionDetailBinding
import com.example.personalexpensetracker.utils.Constants
import com.example.personalexpensetracker.utils.CurrencyUtils
import com.example.personalexpensetracker.utils.DateUtils
import kotlinx.coroutines.launch

class TransactionDetailFragment : Fragment() {

    private var _binding: FragmentTransactionDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel: TransactionDetailViewModel by viewModels()
    private val args: TransactionDetailFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTransactionDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        viewModel.loadTransaction(args.transactionId)
        observeViewModel()

        binding.btnEdit.setOnClickListener {
            val action = TransactionDetailFragmentDirections
                .actionDetailToAddTransaction(args.transactionId)
            findNavController().navigate(action)
        }

        binding.btnDelete.setOnClickListener {
            showDeleteConfirmation()
        }
    }

    private fun showDeleteConfirmation() {
        AlertDialog.Builder(requireContext())
            .setTitle("Delete Transaction")
            .setMessage("Are you sure you want to delete this transaction? This action cannot be undone.")
            .setPositiveButton("Delete") { _, _ ->
                viewModel.deleteTransaction(args.transactionId)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                launch {
                    viewModel.transaction.collect { entity ->
                        entity ?: return@collect
                        val isIncome = entity.type == Constants.TYPE_INCOME

                        binding.tvType.text = entity.type
                        binding.tvType.setTextColor(
                            ContextCompat.getColor(
                                requireContext(),
                                if (isIncome) R.color.income_green else R.color.expense_red
                            )
                        )
                        binding.ivTypeIcon.setImageResource(
                            if (isIncome) R.drawable.ic_income else R.drawable.ic_expense
                        )
                        binding.tvTitle.text       = entity.title
                        binding.tvAmount.text      = CurrencyUtils.format(entity.amount)
                        binding.tvCategory.text    = entity.category
                        binding.tvDate.text        = DateUtils.formatForDisplay(entity.date)
                        binding.tvDescription.text =
                            if (entity.description.isNullOrBlank()) "—" else entity.description
                    }
                }

                launch {
                    viewModel.deleted.collect { isDeleted ->
                        if (isDeleted) findNavController().popBackStack()
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
