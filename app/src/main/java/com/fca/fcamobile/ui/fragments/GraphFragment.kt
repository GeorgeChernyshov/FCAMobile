package com.fca.fcamobile.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.viewModels
import androidx.core.view.isGone
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.fca.fcamobile.databinding.FragmentGraphBinding
import com.fca.fcamobile.ui.viewmodels.GraphViewModel
import com.fca.graphviz.entities.Graph
import com.fca.graphviz.entities.Link
import com.fca.graphviz.entities.Node
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.coroutineContext

class GraphFragment : Fragment() {

    private lateinit var binding: FragmentGraphBinding

    private val graphViewModel: GraphViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGraphBinding.inflate(inflater, container, false)
        graphViewModel.graph.observe(viewLifecycleOwner, Observer { graph ->
            graph?.let { binding.graphView.setGraph(it) }
        })

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

        return binding.root
    }
}