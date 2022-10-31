package com.fca.fcamobile.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.fca.fcamobile.databinding.FragmentFilterBinding
import com.fca.fcamobile.ui.viewmodels.FCAViewModel

class FilterFragment : Fragment() {

    private lateinit var binding: FragmentFilterBinding

    private val graphViewModel: FCAViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentFilterBinding.inflate(inflater, container, false)

        binding.stabFilterSwitch.setOnCheckedChangeListener { _, checked ->
            graphViewModel.applyFilter(checked)
        }

        graphViewModel.filters.observe(viewLifecycleOwner) {
            binding.stabFilterSwitch.isChecked = it.stabFilter
        }

        return binding.root
    }
}