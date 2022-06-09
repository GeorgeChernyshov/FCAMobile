package com.fca.fcamobile.ui.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.fca.fcamobile.databinding.FragmentGraphBinding
import com.fca.graphviz.entities.Graph
import com.fca.graphviz.entities.Link
import com.fca.graphviz.entities.Node

class GraphFragment : Fragment() {

    private lateinit var binding: FragmentGraphBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGraphBinding.inflate(inflater, container, false)

        val graph = Graph(
            nodes = listOf(Node("Myriel", 1), Node("Napoleon", 1)),
            links = listOf(Link("Napoleon", "Myriel", 1))
        )

        binding.graphView.setGraph(graph)

        return binding.root
    }
}