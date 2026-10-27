package com.example.personalexpensetracker.ui.addtransaction

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.personalexpensetracker.databinding.FragmentAddTransactionBinding
import com.example.personalexpensetracker.utils.Constants
import com.example.personalexpensetracker.utils.DateUtils
import kotlinx.coroutines.launch
import java.util.Calendar

class AddTransactionFragment : Fragment() {

    private var _binding: FragmentAddTransactionBinding? = null
    private val binding get() = _binding!!

    private val viewModel: AddTransactionViewModel by viewModels()
    private val args: AddTransactionFragmentArgs by navArgs()

    private var selectedDateMs: Long = DateUtils.todayEpoch()
    private var selectedType: String = Constants.TYPE_EXPENSE

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddTransactionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupCategorySpinner()
        setupDatePicker()
        setupTypeToggle()
        setupButtons()
        observeViewModel()

        // Edit mode — load existing transaction
        if (args.transactionId != -1) {
            viewModel.loadTransaction(args.transactionId)
        }

        // Show today's date by default
        binding.tvSelectedDate.text = DateUtils.formatForInput(selectedDateMs)
    }

    private fun setupCategorySpinner() {
        val adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_spinner_item,
            Constants.CATEGORIES
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCategory.adapter = adapter
    }

    private fun setupDatePicker() {
        binding.btnSelectDate.setOnClickListener {
            val cal = Calendar.getInstance().apply { timeInMillis = selectedDateMs }
            DatePickerDialog(
                requireContext(),
                { _, year, month, day ->
                    val picked = Calendar.getInstance()
                    picked.set(year, month, day, 0, 0, 0)
                    picked.set(Calendar.MILLISECOND, 0)
                    selectedDateMs = picked.timeInMillis
                    binding.tvSelectedDate.text = DateUtils.formatForInput(selectedDateMs)
                },
                cal.get(Calendar.YEAR),
                cal.get(Calendar.MONTH),
                cal.get(Calendar.DAY_OF_MONTH)
            ).show()
        }
    }

    private fun setupTypeToggle() {
        binding.toggleIncome.setOnClickListener {
            selectedType = Constants.TYPE_INCOME
            binding.toggleIncome.isChecked  = true
            binding.toggleExpense.isChecked = false
        }
        binding.toggleExpense.setOnClickListener {
            selectedType = Constants.TYPE_EXPENSE
            binding.toggleExpense.isChecked = true
            binding.toggleIncome.isChecked  = false
        }
        // Default: Expense selected
        binding.toggleExpense.isChecked = true
    }

    private fun setupButtons() {
        binding.btnSave.setOnClickListener {
            viewModel.saveTransaction(
                transactionId = args.transactionId,
                title         = binding.etTitle.text.toString(),
                amountStr     = binding.etAmount.text.toString(),
                type          = selectedType,
                category      = binding.spinnerCategory.selectedItem.toString(),
                dateMs        = selectedDateMs,
                description   = binding.etDescription.text.toString()
            )
        }
        binding.btnCancel.setOnClickListener {
            findNavController().popBackStack()
        }
    }

    private fun observeViewModel() {
        viewLifecycleOwner.lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {

                // Pre-fill form in edit mode
                launch {
                    viewModel.existingTransaction.collect { entity ->
                        entity ?: return@collect
                        binding.etTitle.setText(entity.title)
                        binding.etAmount.setText(entity.amount.toString())
                        binding.etDescription.setText(entity.description ?: "")
                        selectedDateMs = entity.date
                        binding.tvSelectedDate.text = DateUtils.formatForInput(entity.date)
                        selectedType = entity.type
                        if (entity.type == Constants.TYPE_INCOME) {
                            binding.toggleIncome.isChecked  = true
                            binding.toggleExpense.isChecked = false
                        } else {
                            binding.toggleExpense.isChecked = true
                            binding.toggleIncome.isChecked  = false
                        }
                        // Set spinner to matching category
                        val catIndex = Constants.CATEGORIES.indexOf(entity.category)
                        if (catIndex >= 0) binding.spinnerCategory.setSelection(catIndex)
                    }
                }

                // Save result
                launch {
                    viewModel.saveState.collect { state ->
                        when (state) {
                            is SaveState.Success -> {
                                Toast.makeText(requireContext(), "Transaction saved!", Toast.LENGTH_SHORT).show()
                                viewModel.resetSaveState()
                                findNavController().popBackStack()
                            }
                            is SaveState.Error -> {
                                Toast.makeText(requireContext(), state.message, Toast.LENGTH_LONG).show()
                                viewModel.resetSaveState()
                            }
                            SaveState.Idle -> { /* no-op */ }
                        }
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
