package com.fca.filtertimeestimation

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.fca.fcamobile.ui.viewmodels.FCAViewModel
import com.fca.filtertimeestimation.databinding.FragmentFirstBinding
import com.fca.graphviz.api.extensions.filter
import kotlin.system.measureTimeMillis

/**
 * A simple [Fragment] subclass as the default destination in the navigation.
 */
class FirstFragment : Fragment() {

    private var _binding: FragmentFirstBinding? = null

    private val graphViewModel: GraphViewModel by activityViewModels()

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentFirstBinding.inflate(inflater, container, false)

        graphViewModel.graph.observe(viewLifecycleOwner) { graph ->
            graph?.let {
                binding.verticesAmountTextView.text = it.nodes.count().toString()
                val naiveFilterTime = measureTimeMillis {
                    it.naiveFilter(0.5, null, null, null)
                }
                val newFilterTime = measureTimeMillis {
                    it.filter(0.5, null, null, null)
                }

                binding.naiveAlgorithmTextView.text = naiveFilterTime.toString()
                binding.newAlgorithmTextView.text = newFilterTime.toString()
            }
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}