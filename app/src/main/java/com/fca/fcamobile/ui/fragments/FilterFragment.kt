package com.fca.fcamobile.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import com.fca.fcamobile.R
import com.fca.fcamobile.databinding.FragmentFilterBinding
import com.fca.fcamobile.model.FiltersModel
import com.fca.fcamobile.ui.viewmodels.FCAViewModel
import kotlinx.coroutines.launch

class FilterFragment : Fragment() {

    private lateinit var binding: FragmentFilterBinding

    private val graphViewModel: FCAViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilterBinding.inflate(inflater, container, false)

        with (binding) {
            binding.stabFilterView.setTitle(requireContext().getString(R.string.filter_stab_title))
            binding.impactFilterView.setTitle(requireContext().getString(R.string.filter_impact_title))
            applyButton.setOnClickListener {
                val model = FiltersModel(
                    binding.stabFilterView.inputValue,
                    binding.impactFilterView.inputValue,
                    binding.stabFilterView.isChecked,
                    binding.impactFilterView.isChecked
                )
                lifecycleScope.launch {
                    if (model != graphViewModel.filters.value)
                        graphViewModel.setFilter(model)
                }
            }
        }

        graphViewModel.filters.observe(viewLifecycleOwner) {
            binding.stabFilterView.inputValue = it.stabFilterValue
            binding.impactFilterView.inputValue = it.impactFilterValue
            binding.stabFilterView.isChecked = it.stabFilterEnabled
            binding.impactFilterView.isChecked = it.impactFilterEnabled
            graphViewModel.applyFilter(it)
        }

        return binding.root
    }
}