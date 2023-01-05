package com.fca.graphviz.api.interactor

import com.fca.graphviz.entities.AdjacencyTable
import com.fca.graphviz.entities.Graph

class TopSortInteractor(
    val graph: Graph,
    val adjacencyTable: AdjacencyTable
) {

    val newIndices = MutableList(adjacencyTable.nodes.size) { NO_INDEX }

    var currentIndex = adjacencyTable.nodes.size - 1

    fun invoke(): List<Int> {
        val startIndex = graph.nodes.first { it.level == 0 }.id
        parseNode(startIndex)

        return newIndices
    }

    private fun parseNode(index: Int) {
        for (nodeIndex in adjacencyTable.nodes[index].adjacentNodes) {
            if (graph.nodes[index].level < graph.nodes[nodeIndex].level &&
                    newIndices[nodeIndex] != NO_INDEX
            ) {
                parseNode(nodeIndex)
            }
        }

        newIndices[index] = currentIndex--
    }

    companion object {
        private const val NO_INDEX = -1
    }
}