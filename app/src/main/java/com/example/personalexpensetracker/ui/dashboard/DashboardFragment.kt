package com.example.personalexpensetracker.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.personalexpensetracker.R
import com.example.personalexpensetracker.databinding.FragmentDashboardBinding
import com.example.personalexpensetracker.ui.history.TransactionAdapter
import com.example.personalexpensetracker.utils.CurrencyUtils
import kotlinx.coroutines.launch

class DashboardFragment : Fragment() {

    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!

    private val viewModel: DashboardViewModel by viewModels()
    private lateinit var adapter: TransactionAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupRecyclerView()
        setupClickListeners()
        observeViewModel()
    }

    private fun setupRecyclerView() {
        adapter = TransactionAdapter { transaction ->
            val action = DashboardFragmentDirections
                .actionDashboardToDetail(transaction.transactionId)
            findNavController().navigate(action)
        }
        binding.rvRecentTransactions.layoutManager = LinearLayoutManager(requireContext())
        binding.rvRecentTransactions.adapter = adapter
    }

    private fun setupClickListeners() {
        // FAB — navigate to AddTransactionFragment (insert mode)
        binding.fabAddTransaction.setOnClickListener {
            val action = DashboardFragmentDirections
                .actionDashboardToAddTransaction(transactionId = -1)
            findNavController().navigate(action)
        }

        // "See All" button — navigate to TransactionHistoryFragment
        binding.btnSeeAll.setOnClickListener {
            findNavController().navigate(R.id.action_dashboard_to_history)
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                // Summary card
                launch {
                    viewModel.dashboardSummary.collect { summary ->
                        binding.tvIncome.text  = CurrencyUtils.format(summary.totalIncome)
                        binding.tvExpense.text = CurrencyUtils.format(summary.totalExpense)
                        binding.tvBalance.text = CurrencyUtils.format(summary.balance)
                    }
                }

                // Recent transactions list
                launch {
                    viewModel.recentTransactions.collect { transactions ->
                        adapter.submitList(transactions)
                        binding.tvNoRecent.visibility =
                            if (transactions.isEmpty()) View.VISIBLE else View.GONE
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
