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
            stabFilterView.setTitle(requireContext().getString(R.string.filter_stab_title))
            deltaFilterView.setTitle(requireContext().getString(R.string.filter_delta_title))
            impactFilterView.setTitle(requireContext().getString(R.string.filter_impact_title))
            pValueFilterView.setTitle(requireContext().getString(R.string.filter_p_value_title))

            deltaFilterView.filterEnabled = false

            applyButton.setOnClickListener {
                with (binding) {
                    val model = FiltersModel(
                        stabFilterView.inputValue,
                        deltaFilterView.inputValue,
                        impactFilterView.inputValue,
                        pValueFilterView.inputValue,
                        stabFilterView.isChecked,
                        deltaFilterView.isChecked,
                        impactFilterView.isChecked,
                        pValueFilterView.isChecked
                    )
                    lifecycleScope.launch {
                        if (model != graphViewModel.filters.value)
                            graphViewModel.setFilter(model)
                    }
                }
            }
        }

        graphViewModel.filters.observe(viewLifecycleOwner) {
            with (binding) {
                stabFilterView.inputValue = it.stabFilterValue
                deltaFilterView.inputValue = it.deltaFilterValue
                impactFilterView.inputValue = it.impactFilterValue
                pValueFilterView.inputValue = it.pvalueFilterValue
                stabFilterView.isChecked = it.stabFilterEnabled
                deltaFilterView.isChecked = it.deltaFilterEnabled
                impactFilterView.isChecked = it.impactFilterEnabled
                pValueFilterView.isChecked = it.pvalueFilterEnabled
            }

            graphViewModel.applyFilter(it)
        }

        return binding.root
    }
}