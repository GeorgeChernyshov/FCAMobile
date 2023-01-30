package com.fca.fcamobile.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.fca.fcamobile.R
import com.fca.fcamobile.databinding.FragmentGraphBinding
import com.fca.fcamobile.model.GraphSettingsModel
import com.fca.fcamobile.ui.viewmodels.FCAViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class GraphFragment : Fragment() {

    private lateinit var binding: FragmentGraphBinding

    private val graphViewModel: FCAViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGraphBinding.inflate(inflater, container, false)
        graphViewModel.graphUiState.observe(viewLifecycleOwner) { uiState ->
            uiState.graph?.let { binding.graphView.setGraph(it) }
        }
        graphViewModel.graphViewMode.observe(viewLifecycleOwner) {
            val drawable = AppCompatResources.getDrawable(
                requireContext(),
                when (it) {
                    GraphSettingsModel.GraphViewMode.FORCE_GRAPH -> R.drawable.ic_force_graph
                    GraphSettingsModel.GraphViewMode.NODE_TRAVERSAL -> R.drawable.ic_node_traversal
                }
            )

            binding.viewModeSelectorImageView.setImageDrawable(drawable)
            binding.graphView.setMode(it.toString())
        }

        binding.graphView.onNodeClicked {
            CoroutineScope(Dispatchers.Main).launch {
                binding.nodeInfoView.isVisible = true
                binding.nodeInfoView.setNode(it)
            }
        }.onDragEnded {
            CoroutineScope(Dispatchers.Main).launch {
                binding.nodeInfoView.isGone = true
            }
        }

        binding.viewModeSelectorImageView.setOnClickListener {
            CoroutineScope(Dispatchers.Main).launch {
                graphViewModel.switchMode()
            }
        }

        return binding.root
    }
}